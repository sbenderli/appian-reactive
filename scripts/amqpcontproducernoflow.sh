#!/usr/bin/env bash
HOME=$(dirname $0)/../
 
CLASSPATH=$HOME/src/main/resources
CLASSPATH=$CLASSPATH:$HOME/target/classes/
CLASSPATH=$CLASSPATH:$HOME/libraries/slf4j-api-1.7.21.jar
CLASSPATH=$CLASSPATH:$HOME/libraries/amqp-client-4.1.0.jar

java -cp $CLASSPATH producerflow.AMQPContProducerNoFlow
