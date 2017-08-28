package dispatcherpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

public class AMQPThreadPoolDispatcher {

  ExecutorService executor = new ThreadPoolExecutor(1, 1,
      0L, TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<Runnable>(1));

	private Long numThreads = 0l;
	
	public void dispatchMessages() throws Exception {

		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicQos(1);
		channel.basicConsume("trade.eq.q", false, consumer);

		while (true) {
			QueueingConsumer.Delivery msg = consumer.nextDelivery();
			channel.basicAck(msg.getEnvelope().getDeliveryTag(), false);
      POJOThreadPoolProcessor task = new POJOThreadPoolProcessor(
          			this, new String(msg.getBody()));
			executor.execute(task);
      // new Thread().start();
			//numThreads++;
			//System.out.println("Threads: " + numThreads);
		}


	}	
	
	public void tradeComplete() {
		synchronized(numThreads) {
			numThreads = numThreads-1;
		}
	}
	
	public static void main(String[] args) throws Exception {
		new AMQPThreadPoolDispatcher().dispatchMessages();
	}
}
