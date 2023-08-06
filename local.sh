#!/bin/sh

LOCAL_REDIS_PORT=6379
LOCAL_REDIS_CONTAINER='local-redis'

docker run --rm --name $LOCAL_REDIS_CONTAINER -d \
  -p $LOCAL_REDIS_PORT:6379 \
	redis:7.0-alpine