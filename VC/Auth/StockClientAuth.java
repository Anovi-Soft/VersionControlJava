package Auth;

import Packet.CommandType;
import Packet.PackCommand;
import Util.ProSocket;
import Workers.ClientWorker;


public class StockClientAuth implements IClientAuth {
	private ProSocket sock;
	public StockClientAuth(ProSocket sock)
	{
		this.sock = sock;
	}
	@Override
	public void Auth() {
		sock.sendPacket(new PackCommand(CommandType.NeedLogin,""));
		Input();
	}

	@Override
	public void Reg() {
		System.out.println("Start Registration");
		sock.sendPacket(new PackCommand(CommandType.NeedReg,""));
		Input();
	}
	
	private void Input()
	{
		System.out.println("Please input Login and password");
		boolean unCheck = true;
		do
		{
			sock.sendPacket(new PackCommand(CommandType.Login, ClientWorker.readLine()));
			sock.sendPacket(new PackCommand(CommandType.Password, ClientWorker.readLine()));

			PackCommand com = sock.getPackCommand(new CommandType[]{CommandType.OK, CommandType.Exception});
			if (CommandType.isItComand(com, CommandType.Exception))
				System.out.println(com.getArgs());
			else
			{
				System.out.println("Auth is Ok\nHere we go");
				unCheck = false;
			}
		}while(unCheck);
	}

}
