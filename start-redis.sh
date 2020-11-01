#!/usr/bin/env bash

docker run --name local-database -p 6379:6379 -d redis:alpine