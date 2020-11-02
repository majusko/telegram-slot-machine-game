#!/usr/bin/env bash

docker volume create slot-machine-volume
docker run -d \
  -h redis \
  -e REDIS_PASSWORD="$TELEGRAM_BOT_REDIS_PASS" \
  -v slot-machine-volume:/data \
  -p 6379:6379 \
  --name redis \
  --restart always \
  redis:5.0.5-alpine3.9 /bin/sh -c 'redis-server --appendonly yes --requirepass ${REDIS_PASSWORD}'