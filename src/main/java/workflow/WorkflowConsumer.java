package workflow;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;
import common.Consumer;

public class WorkflowConsumer {

	public static void main(String[] args) throws Exception {
		WorkflowConsumer consumer = new WorkflowConsumer();
		Channel channel = AMQPCommon.connect();
		Connection connection = channel.getConnection();
		consumer.start(connection);
	}

	public void start(Connection connection) throws Exception {
		Channel channel = connection.createChannel();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		setupChannel(channel, consumer);

		while (true) {
			QueueingConsumer.Delivery msg = consumer.nextDelivery();
			try {
				processTrade(msg);
				channel.basicAck(msg.getEnvelope().getDeliveryTag(), false);

			} catch (Exception e) {
				System.out.println("error with trade: " + e.getMessage());
				System.out.println("sending to workflow");
				channel.basicPublish("", "workflow.q", null, msg.getBody());
			}
		}			
	}

	private void processTrade(QueueingConsumer.Delivery msg) throws InterruptedException {
		String message = new String(msg.getBody());
		System.out.println("message received: " + message);
		String[] parts = message.split(",");
		long shares = new Long(parts[2]).longValue();
		Thread.sleep(1000);
	}

	private static void setupChannel(Channel channel, QueueingConsumer consumer) throws IOException {
		channel.basicQos(1);
		channel.basicConsume("trade.eq.q", false, consumer);
	}

}
