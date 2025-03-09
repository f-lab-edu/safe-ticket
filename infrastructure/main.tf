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
    { name = "monitoring-server", instance_type = "t2.micro", ami = "ami-0891aeb92f786d7a2" },
    { name = "ngrinder-server", instance_type = "t2.micro", ami = "ami-0891aeb92f786d7a2" }
  ]
}

# 키페어 설정
resource "aws_key_pair" "ticket_server_key" {
   key_name   = "ticket-server-key"
   public_key = file("./ticket-server.pub")
}

# 보안 그룹
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
  key_name      = aws_key_pair.ticket_server_key.key_name
  security_groups = [aws_security_group.allow_prometheus_grafana_spring.name]

  tags = {
    Name = var.instances[count.index].name
  }
}

output "ec2_instances" {
  value = aws_instance.servers[*].public_ip
}
