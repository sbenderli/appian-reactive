package threshold;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;

public class AMQPConsumer {

	private List<Long> responseTimes = Arrays.asList(
			new Long(200), 
			new Long(800), 
			new Long(300), 
			new Long(900), 
			new Long(1200), 
			new Long(700), 
			new Long(200), 
			new Long(300), 
			new Long(1100), 
			new Long(1600) 
	);
	private int index = 0;
	public void execute() throws Exception {
		
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("trade.request.q", true, consumer);
		index = 0;

		while (true) {
			processTrade(channel, getNextTradeRequest(consumer));
		}
	}

	private String getNextTradeRequest(QueueingConsumer consumer) throws InterruptedException {
		QueueingConsumer.Delivery message = consumer.nextDelivery();
		String msg = new String(message.getBody());
		System.out.println("trade received: " + msg);
		return "response";
	}

	private void processTrade(Channel channel, String tradeRequest) throws InterruptedException, IOException {
		byte[] bmsg = tradeRequest.getBytes();
		Thread.sleep(responseTimes.get(index));
		channel.basicPublish("", "trade.response.q", null, bmsg);
		index++;
	}

	public static void main(String[] args) throws Exception {
		new AMQPConsumer().execute();
	}
}









