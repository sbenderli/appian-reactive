package common;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

public class Consumer {

  private Boolean active = false;

  public static void main(String[] args) throws Exception {
    Consumer consumer = new Consumer();
    Channel channel = AMQPCommon.connect();
    Connection connection = channel.getConnection();
    consumer.start(connection);
  }

  public void start(Connection connection) {
    try {
      active = true;
      Channel channel = connection.createChannel();
      QueueingConsumer consumer = new QueueingConsumer(channel);
      setupChannel(channel, consumer);

      while (active) {
        Delivery msg = getNextMessage(consumer);
        System.out.println("message received: " + new String(msg.getBody()));
        processTrade(msg);
        acknowledgeMessageProcessed(channel, msg);
      }
      channel.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void setupChannel(Channel channel, QueueingConsumer consumer) throws IOException {
    channel.basicQos(1);
    channel.basicConsume("trade.eq.q", false, consumer);
  }

  private static Delivery getNextMessage(QueueingConsumer consumer) throws InterruptedException {
    return consumer.nextDelivery();
  }

  private static void acknowledgeMessageProcessed(Channel channel, Delivery msg) throws IOException {
    channel.basicAck(msg.getEnvelope().getDeliveryTag(), false);
  }

  private static void processTrade(Delivery msg) throws InterruptedException {
    String message = new String(msg.getBody());
    String[] parts = message.split(",");
    long shares = new Long(parts[2]).longValue();
    Thread.sleep(1000);
  }

  public void shutdown() {
    synchronized (active) {
      active = false;
    }
  }
}





