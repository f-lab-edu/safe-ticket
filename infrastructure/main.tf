provider "aws" {
  region = "ap-northeast-2" # 서울 리전
}

variable "instances" {
  type = list(object({
    name          = string
    instance_type = string
    ami           = string
  }))
  default = [
    { name = "spring-server", instance_type = "t2.micro", ami = "ami-0891aeb92f786d7a2" },
    { name = "monitoring-server", instance_type = "t2.micro", ami = "ami-0891aeb92f786d7a2" },
    { name = "ngrinder-server", instance_type = "t2.micro", ami = "ami-0891aeb92f786d7a2" }
  ]
}

variable "github_actions_ips" {
  type    = list(string)
  default = []
}

# 키페어 설정
resource "aws_key_pair" "ticket_server_key" {
   key_name   = "ticket-server-key"
   public_key = file("./ticket-server.pub")
}

# 기본 VPC 가져오기
data "aws_vpc" "default" {
  default = true
}

# 서브넷 정보 가져오기
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

# EC2 보안 그룹 (Spring 서버 + Monitoring 서버 간 통신 허용)
resource "aws_security_group" "ec2_sg" {
  vpc_id = data.aws_vpc.default.id
  name   = "ec2-secure-group"

  # Spring 서버 8080 포트 오픈 (ALB에서 접근 가능)
  ingress {
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.alb_sg.id] # ALB에서만 허용
  }

  # SSH 포트 22 동적 오픈
  dynamic "ingress" {
    for_each = var.github_actions_ips
    content {
      from_port   = 22
      to_port     = 22
      protocol    = "tcp"
      cidr_blocks = [ingress.value]
    }
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Monitoring 보안 그룹 (Prometheus, Grafana 접근 가능)
resource "aws_security_group" "monitoring_sg" {
  vpc_id = data.aws_vpc.default.id
  name   = "monitoring-secure-group"

  # Prometheus (9090) 허용
  ingress {
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Grafana (3000) 허용
  ingress {
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # SSH 포트 22 동적 오픈
  dynamic "ingress" {
    for_each = var.github_actions_ips
    content {
      from_port   = 22
      to_port     = 22
      protocol    = "tcp"
      cidr_blocks = [ingress.value]
    }
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Monitoring 서버에서 Spring 서버(8080) 접근 허용
resource "aws_security_group_rule" "monitoring_to_spring" {
  type                     = "ingress"
  from_port                = 8080
  to_port                  = 8080
  protocol                 = "tcp"
  security_group_id        = aws_security_group.ec2_sg.id
  source_security_group_id = aws_security_group.monitoring_sg.id
}

# ALB 생성
resource "aws_lb" "ticket_lb" {
  name               = "ticket-lb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_sg.id]
  subnets           = [data.aws_subnet.subnet_a.id, data.aws_subnet.subnet_c.id]
}

# ALB Target Group (Spring 서버 대상)
resource "aws_lb_target_group" "ticket_tg" {
  name     = "ticket-target-group"
  port     = 8080
  protocol = "HTTP"
  vpc_id   = data.aws_vpc.default.id
}

# ALB Listener (80번 포트)
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.ticket_lb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ticket_tg.arn
  }
}

# EC2 인스턴스 생성
resource "aws_instance" "servers" {
  count         = length(var.instances)
  ami           = var.instances[count.index].ami
  instance_type = var.instances[count.index].instance_type
  key_name      = aws_key_pair.ticket_server_key.key_name
  security_groups = [aws_security_group.ec2_sg.name]

  tags = {
    Name = var.instances[count.index].name
  }
}

# Spring 서버를 ALB Target Group에 등록
resource "aws_lb_target_group_attachment" "spring_attachment" {
  count            = 2
  target_group_arn = aws_lb_target_group.ticket_tg.arn
  target_id        = aws_instance.servers[0].id
  port             = 8080
}

# 출력
output "alb_dns" {
  value = aws_lb.ticket_lb.dns_name
}

output "ec2_instances" {
  value = { for instance in aws_instance.servers : instance.tags.Name => instance.public_ip }
}
