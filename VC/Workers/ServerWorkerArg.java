package Workers;

import java.net.Socket;

public class ServerWorkerArg {
	public Socket socket;
	public String dir_path;
	public ServerWorkerArg(Socket socket, String dir_path)
{
		this.socket = socket;
		this.dir_path = dir_path;
	}
}
