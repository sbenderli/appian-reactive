package dispatcherpool;

import java.util.UUID;

public class POJOThreadPoolProcessor implements Runnable {

	private String message;
	private AMQPThreadPoolDispatcher dispatcher;

	public POJOThreadPoolProcessor(AMQPThreadPoolDispatcher dispatcher, String message) {
		this.message = message;
		this.dispatcher = dispatcher;
	}
	
	public void run() {
		try {
			String id = UUID.randomUUID().toString();
			id = id.substring(id.length() - 4);
			Thread.sleep(2000);
			System.out.println(id + ": Trade placed: " + message);
			dispatcher.tradeComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
