#!/bin/sh

# 환경 변수를 템플릿 파일에 반영
#envsubst < /etc/prometheus/prometheus.yml.template > /etc/prometheus/prometheus.yml

# Prometheus 실행
exec /bin/prometheus --config.file=/etc/prometheus/prometheus.yml --enable-feature=expand-external-labels