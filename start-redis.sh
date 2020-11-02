#!/usr/bin/env bash

docker volume create slot-machine-volume
docker create -v slot-machine-volume:/data --name my-redis-container -p 6379:6379 dockerfile/redis
docker start my-redis-container