package common;

import java.io.IOException;
import java.util.Random;

import com.rabbitmq.client.Channel;

public class Producer {

	public static void main(String[] args) throws Exception {
		Channel channel = AMQPCommon.connect();
		try {
			while (true) {
				publishNewTrade(channel);
			}
		} catch (Exception e) {
			AMQPCommon.close(channel);
		}
	}

	private static void publishNewTrade(Channel channel) throws IOException, InterruptedException {
		long shares = ((long) ((new Random().nextDouble() * 4000) + 1));
		String text = "BUY,AAPL," + shares;
		byte[] message = text.getBytes();
		System.out.println("sending trade: " + text);
		channel.basicPublish("", "trade.eq.q", null, message);
		Thread.sleep(1000);
	}
}









