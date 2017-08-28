package common;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class AMQPCommon {

	public static Channel connect() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setHost("192.168.99.100");
		factory.setPort(5672);
		Connection conn = factory.newConnection();
		return conn.createChannel();
	}
	
	public static void close(Channel channel) throws Exception {
		channel.close();
		channel.getConnection().close();
	}

}

