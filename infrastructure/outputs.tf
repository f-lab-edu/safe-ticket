output "public_ips" {
  description = "EC2 인스턴스들의 Public IP 목록"
  value       = aws_instance.servers[*].public_ip
}

output "instance_ids" {
  description = "EC2 인스턴스들의 ID"
  value       = aws_instance.servers[*].id
}

