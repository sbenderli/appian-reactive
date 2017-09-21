package threshold;

import java.io.IOException;
import java.util.Random;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

public class AMQPProducer {

	public void execute() throws Exception {
		long threshold = 2000;
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("trade.response.q", true, consumer);
		for (int i=0;i<10;i++) {
			long start = System.currentTimeMillis();
			placeTrade(channel, consumer);
			long duration = System.currentTimeMillis() - start;
			System.out.println("trade confirmation received in " + duration + " ms");

			if ((duration*2) > threshold) {
				updateThreshold(channel, duration);
			}
			System.out.println("");
			Thread.sleep(1000);
		}
		channel.close();
		System.exit(0);
	}

	private void updateThreshold(Channel channel, long duration) throws IOException {
		long threshold;
		threshold = duration*2;
		String msg = "" + threshold;
		byte[] configMsg = msg.getBytes();
		System.out.println("updating threshold: " + msg);
		channel.basicPublish("", "config.q", null, configMsg);
	}

	private void placeTrade(Channel channel, QueueingConsumer consumer)
			throws IOException, InterruptedException {
		long shares = ((long) ((new Random().nextDouble() * 4000) + 1));
		String text = "BUY,AAPL," + shares;
		byte[] message = text.getBytes();
		System.out.println("sending trade: " + text);
		channel.basicPublish("", "trade.request.q", null, message);

		consumer.nextDelivery();
	}

	public static void main(String[] args) throws Exception {
		new AMQPProducer().execute();
	}
}





