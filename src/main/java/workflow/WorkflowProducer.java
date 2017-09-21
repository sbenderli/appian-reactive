package workflow;

import java.util.Random;

import com.rabbitmq.client.Channel;
import common.AMQPCommon;

/**
 * Continuously produce messages every 1 second
 */
public class WorkflowProducer {
	
	public static void main(String[] args) throws Exception {
		WorkflowProducer app = new WorkflowProducer();
		app.produceMessages(args);
	}
	
	private void produceMessages(String[] args) throws Exception {		
		Channel channel = AMQPCommon.connect();
		while (true) {
			for (int i = 0; i < 10; i++) {
				String text = generateNewTrade(i);
				byte[] message = text.getBytes();
				System.out.println("sending trade: " + text);
				channel.basicPublish("", "trade.eq.q", null, message);
			}
			Thread.sleep(10000);
		}
	}

	private String generateNewTrade(int i) {
		String shares = i == 7 ? "XXXX" : new Long(Math.abs(new Random().nextInt(4000))).toString();
		String text = "BUY,AAPL," + shares;
		if (i == 4) text += " shares";
		return text;
	}
}









