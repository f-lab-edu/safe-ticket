provider "aws" {
  region = "ap-northeast-2" # 서울
}

variable "instances" {
  type = list(object({
    name          = string
    instance_type = string
    ami           = string
  }))
  default = [
    { name = "spring-server", instance_type = "t2.micro", ami = "ami-0891aeb92f786d7a2" },
    { name = "spring-server", instance_type = "t2.micro", ami = "ami-0891aeb92f786d7a2" },
    { name = "monitoring-server", instance_type = "t2.micro", ami = "ami-0891aeb92f786d7a2" },
    { name = "ngrinder-server", instance_type = "t2.micro", ami = "ami-0891aeb92f786d7a2" }
  ]
}

variable "github_actions_ips" {
  description = "Github Action IP"
  type        = list(string)
}

# VPC 정보 가져오기
data "aws_vpc" "default" {
  default = true
}

# Alb 서브넷 정보 가져오기
data "aws_subnet" "subnet_a" {
  filter {
    name   = "availability-zone"
    values = ["ap-northeast-2a"]
  }
}

data "aws_subnet" "subnet_c" {
  filter {
    name   = "availability-zone"
    values = ["ap-northeast-2c"]
  }
}

# ALB 보안 그룹
resource "aws_security_group" "alb_sg" {
  vpc_id = data.aws_vpc.default.id
  name   = "alb-secure-group"

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# EC2 보안 그룹
resource "aws_security_group" "ec2_sg" {
  vpc_id = data.aws_vpc.default.id
  name   = "ec2-secure-group"

  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb_sg.id] # ALB에서만 허용
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# ALB 생성
resource "aws_lb" "ticket_lb" {
  name               = "ticket-lb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_sg.id]
  subnets           = [data.aws_subnet.subnet_a.id, data.aws_subnet.subnet_c.id]
}

# ALB Target Group
resource "aws_lb_target_group" "ticket_tg" {
  name     = "ticket-target-group"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = data.aws_vpc.default.id
}

# ALB Target Group Attachment
resource "aws_lb_target_group_attachment" "spring_server_attachment" {
  for_each = { for idx, instance in aws_instance.servers : idx => instance if instance.tags["Name"] == "spring-server" }
  target_group_arn = aws_lb_target_group.ticket_tg.arn
  target_id        = each.value.id
  port             = 8080
}

# ALB Listener (80번 포트로 요청 받음)
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.ticket_lb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ticket_tg.arn
  }
}

# 모니터링 보안 그룹
resource "aws_security_group" "allow_prometheus_grafana_spring" {
  name        = "allow-prometheus-grafana-spring"
  description = "Allow access to Prometheus (9090) and Grafana (3000) and ticket-app(8080), SSH (22)"

  ingress {
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "servers" {
  count         = length(var.instances)
  ami           = var.instances[count.index].ami
  instance_type = var.instances[count.index].instance_type
  key_name      = "ticket-server-key"
  security_groups = [aws_security_group.allow_prometheus_grafana_spring.name]

  tags = {
    Name = var.instances[count.index].name
  }
}

output "ec2_instances" {
  value = aws_instance.servers[*].public_ip
}
