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

resource "aws_instance" "servers" {
  count         = length(var.instances)
  ami           = var.instances[count.index].ami
  instance_type = var.instances[count.index].instance_type

  tags = {
    Name = var.instances[count.index].name
  }
}

output "ec2_instances" {
  value = aws_instance.servers[*].public_ip
}

