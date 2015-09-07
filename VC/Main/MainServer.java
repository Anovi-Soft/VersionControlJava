package Main;


public class MainServer {

	public static void main(String[] args) {	
		MultiThreadServerWorker server = MultiThreadServerWorker.getInstance((short) 11568);
		server.MainLop();
		while(true){}
	}
}