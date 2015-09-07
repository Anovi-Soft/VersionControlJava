package Main;

import Workers.ClientWorker;

public class MainClient {

	public static void main(String[] args) {
		ClientWorker worker = new ClientWorker();
		worker.work(null);
	}

}
