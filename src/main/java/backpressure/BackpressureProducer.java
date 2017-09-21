package backpressure;

import java.util.Random;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

/**
 * Continuously produce messages with backpressure enabled
 */
public class BackpressureProducer {
	private static Long DELAY_BP = 3000l;
	private static Long DELAY_NO_BP = 500l;
	private Long delay = DELAY_NO_BP;
	private Connection connection;

	public static void main(String[] args) throws Exception {
		BackpressureProducer app = new BackpressureProducer();
		app.connection = AMQPCommon.connect().getConnection();
		app.startListener();
		app.produceMessages();
	}

	private void produceMessages() throws Exception {
		Channel channel = connection.createChannel();
		while (true) {
			long shares = ((long) ((new Random().nextDouble() * 4000) + 1));
			String text = "BUY,AAPL," + shares;
			byte[] message = text.getBytes();
			String routingKey = "trade.eq.q";
			System.out.println("sending trade: " + text);
			channel.basicPublish("", routingKey, null, message);
			Thread.sleep(delay);
		}
	}

	private void startListener() {
	    new Thread(() -> {
        try {
          Channel channel = connection.createChannel();
          QueueingConsumer consumer = new QueueingConsumer(channel);
          channel.basicQos(1);
          channel.basicConsume("flow.q", false, consumer);
          while (true) {
            QueueingConsumer.Delivery msg = consumer.nextDelivery();
            boolean controlFlow = new Boolean(new String(msg.getBody())).booleanValue();
            channel.basicAck(msg.getEnvelope().getDeliveryTag(), false);
            if (controlFlow) {
              System.out.println("notification received to slow down...");
              synchronized(delay) {
                delay = DELAY_BP;
              }
            } else {
              System.out.println("system back to normal...");
              synchronized(delay) {
                delay = DELAY_NO_BP;
              }
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
    }).start();
	}
}









