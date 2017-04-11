#!/usr/bin/env bash
docker run -d --hostname rabit-mq-instance --name some-rabbit -p 15672:15672 -p 5672:5672 -p 5671:5671 rabbitmq:3-management