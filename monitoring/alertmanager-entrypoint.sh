#!/bin/sh

# 환경 변수를 템플릿 파일에 반영
envsubst < /etc/alertmanager/alertmanager.yml.template > /etc/alertmanager/alertmanager.yml

# Alertmanager 실행
exec /bin/alertmanager --config.file=/etc/alertmanager/alertmanager.yml
