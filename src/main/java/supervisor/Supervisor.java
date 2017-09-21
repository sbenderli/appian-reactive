package supervisor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import common.AMQPCommon;
import common.Consumer;

public class Supervisor {
	
	private List<Consumer> consumers = new ArrayList<>();
	Connection connection;
	
	public void run() throws Exception {
		System.out.println("Starting supervisor");
		Channel channel = AMQPCommon.connect();
		connection = channel.getConnection();
		createOneConsumer();
		while (true) {
			long queueSize = getQueueSize(channel);

			// Fancy algorithm
			long consumersNeeded = howManyConsumersNeeded(queueSize);

			long diff = Math.abs(consumersNeeded - consumers.size());
			for (int i=0;i<diff;i++) {
				if (consumersNeeded > consumers.size()) 
					createOneConsumer();
				else 
					killOneConsumer();
			}			
			Thread.sleep(1000);
		}
	}

	private long getQueueSize(Channel channel) throws IOException {
		return channel.messageCount("trade.eq.q");
	}

	private long howManyConsumersNeeded(long queueDepth) {
		return (long)Math.ceil(new Double(queueDepth)/2);
	}

	private void createOneConsumer() {
		System.out.println("Starting consumer...");
		Consumer consumer = new Consumer();
		consumers.add(consumer);
	    new Thread(() -> {
        try {
          consumer.start(connection);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }).start();
	}
	
	private void killOneConsumer() {
		if (consumers.size() >= 2) {
			System.out.println("Removing consumer...");
			Consumer consumer = getLastConsumer();
			consumer.shutdown();
			consumers.remove(consumer);
		}
	}

	private Consumer getLastConsumer() {
		return consumers.get(consumers.size() - 1);
	}

	public static void main(String[] args) throws Exception {
		Supervisor app = new Supervisor();
		app.run();
	}
}
