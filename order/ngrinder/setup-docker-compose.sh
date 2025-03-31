#!/bin/bash

# Docker 설치
sudo yum update -y
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose

# Docker 그룹에 현재 사용자 추가
sudo usermod -aG docker $USER
newgrp docker

# docker-compose.yml 파일이 있는 디렉토리로 이동
cd /home/ec2-user

# Docker Compose 실행
docker-compose up -d