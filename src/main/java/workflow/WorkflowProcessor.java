package workflow;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

public class WorkflowProcessor {

	public void execute() throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("workflow.q", true, consumer);

		while (true) {
			QueueingConsumer.Delivery message = consumer.nextDelivery();
			String msg = new String(message.getBody());
			System.out.println("\n\nmessage received: " + msg);
			fixMessageAndRepublish(channel, msg);
		}
	}

	private void fixMessageAndRepublish(Channel channel, String msg) {
		try {
			String newMsg = msg.substring(0, msg.indexOf(" shares"));
			byte[] bmsg = newMsg.getBytes();
			System.out.println("Trade fixed: " + newMsg);
			channel.basicPublish("", "trade.eq.q", null, bmsg);
		} catch (Exception e) {
			System.out.println("Failed to fix trade: " + msg);
			System.out.println("Sending for manual processing.");
		}
	}

	public static void main(String[] args) throws Exception {
		new WorkflowProcessor().execute();
	}
}





