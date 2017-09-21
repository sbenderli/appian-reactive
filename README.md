# Reactive Patterns

This is a fork of Mark Richards' reactive repo: https://github.com/wmr513/reactive

I refactored the classes, mostly renaming them, to help me present the workflows I was interested in. 

## Getting Started
1. Start local RabbitMQ instance
docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
2. Get docker-machine ip `docker-machine ip <machine-name>`
3. Set AMPQCommon to the correct ip and port:5672
4. Run AMPQInitialize


