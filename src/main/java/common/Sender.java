package common;

import java.io.IOException;
import java.util.Random;

import com.rabbitmq.client.Channel;

public class Sender {

	public static void main(String[] args) throws Exception {
		Channel channel = AMQPCommon.connect();

		int numMsgs = args.length > 0 ? new Integer(args[0]).intValue() : 1;
		for (int i=0; i<numMsgs; i++) {
			publishNewTrade(channel);
		}
		AMQPCommon.close(channel);
	}

	private static void publishNewTrade(Channel channel) throws IOException {
		long shares = ((long) ((new Random().nextDouble() * 4000) + 1));
		String text = "BUY,AAPL," + shares;
		byte[] message = text.getBytes();
		System.out.println("sending trade: " + text);
		channel.basicPublish("", "trade.eq.q", null, message);
	}
}









