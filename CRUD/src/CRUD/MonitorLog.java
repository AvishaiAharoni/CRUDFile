package CRUD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Function;

import Observer.Dispatcher;

public class MonitorLog {
	private Dispatcher<String> dispatcher;
	private File srcFile;
	private Thread th;
	
	/**
	 * constructor to init the vars, and create the thread
	 * @param name - the path of the file to watch
	 * @throws IOException
	 */
	public MonitorLog(String name, boolean startOver) throws IOException {
		this.dispatcher = new Dispatcher<>();
		this.srcFile = new File(name);
		this.srcFile.createNewFile();
		
		if (startOver) {
			byte[] empty = new byte[0];
			com.google.common.io.Files.write(empty, this.srcFile);	
		}
		
		this.th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try (FileReader fr = new FileReader(srcFile);
					BufferedReader br = new BufferedReader(fr)) {
					String res;

					while (!Thread.interrupted()) {
						// there is a new line in the file
						if ((res = br.readLine()) != null) {
							MonitorLog.this.dispatcher.notifyAllObservers(res);
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * to start the thread to watch on the file
	 */
	public void start() {
		this.th.start();
	}
	
	/**
	 * to stop the thread
	 */
	public void stop() {
		this.th.interrupt();
	}
	
	/**
	 * to subscribe to the monitor service on the file
	 * @param func - the function to insert to the dispatcher to do when the file is midified
	 */
	public void subscribe(Function<String, Void> func) {
		dispatcher.subscribe(func);
	}
	
	/**
	 * to unsubscribe to the monitor service on the file
	 * @param func - the function to remove from the dispatcher
	 */
	public void unsubscribe(Function<String, Void> func) {
		dispatcher.unSubscribe(func);
	}
}
