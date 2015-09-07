package Workers;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import Auth.IServerAuth;
import Auth.StockServerAuth;
import Exceptions.AuthException;
import Exceptions.UnknownCommandException;
import Exceptions.VersionControlException;
import Packer.StockPacker;
import Packet.CommandType;
import Packet.PackCommand;
import Provider.FolderProvider;
import Provider.IDataProvider;
import Util.FileHelper;
import Util.ProSocket;
import Util.ZipperOriginal;
import Util.ZipperProvider;
import Version.IVersion;
import Version.VersionType;


public class ServerWorkerV1 implements IWorker{
	private ProSocket sock;
	private IDataProvider provider;
	private String login;
	@Override
	public void work(Object args) {
		if (!(args instanceof Socket))
{
			System.out.println("����������� �������� ��� �������� �������");
			return;
		}
		this.sock = new ProSocket((Socket)args, new StockPacker());
		hello();
		loop();		
	}
	
	protected void hello()
{
		IServerAuth auth = new StockServerAuth("auth/auth.info");
		
		boolean reg = false, check = false;
		PackCommand com = sock.getPackCommand(new CommandType[]{CommandType.NeedLogin, CommandType.NeedReg});
		if (CommandType.isItComand(com, CommandType.NeedReg))
			reg = true;
		do {
			login = sock.getPackCommand(CommandType.Login).getArgs();
			String password = sock.getPackCommand(CommandType.Password).getArgs();
			try {
				check = reg ? auth.AddUser(login, password) : auth.Login(login, password);
				if (!check)
					throw new AuthException("Wrong Login or Password");
				else
					sock.sendPacket(new PackCommand(CommandType.OK, ""));					
			} catch (VersionControlException e) {
				sock.sendPacket(new PackCommand(CommandType.Exception, e.getMessage()));
			}
		}while (!check);		
		
		provider = new FolderProvider("auth" + "/" + login);
	}
	
	protected void loop()
{
		while(true)
		{
			PackCommand com = sock.getPackCommand(new CommandType[]{CommandType.Add, 
					CommandType.Clone,
					CommandType.Update,
					CommandType.Commit,
					CommandType.Revert,
					CommandType.Log,
					CommandType.Quit});
			if(CommandType.isItComand(com, CommandType.Quit))
				return;
			try {
				Cmd(com);
			} catch (VersionControlException e) {
				sock.sendPacket(new PackCommand(CommandType.Exception, e.getMessage()));
			}
		}
	}
	
	private void Cmd(PackCommand pc) throws VersionControlException{
		switch ((CommandType)pc.getObject())
{
		case Add:ADD(pc.getArgs());break;
		case Clone:CLONE(pc.getArgs());break;
		case Update:UPDATE(pc.getArgs());break;
		case Commit:COMMIT(pc.getArgs());break;
		case Revert:REVERT(pc.getArgs());break;
		case Log:LOG(pc.getArgs());break;
		default: throw new UnknownCommandException();
		}
	}
	private void ADD(String line) throws VersionControlException{
		String[] splt = line.split(" ");
		if (splt.length != 2)
		{
			sock.sendPacket(new PackCommand(CommandType.Exception, "Bad arguments"));
			return;
		}
		if (this.provider.existsProject(splt[1]))
		{
			sock.sendPacket(new PackCommand(CommandType.Exception, "Proect allready exists"));
			return;
		}
		this.provider.createProject(splt[1], VersionType.Stock);
		sock.sendPacket(new PackCommand(CommandType.OK, ""));
		System.out.print("Add project\n");
	}
	private void CLONE(String line)
	{
		String[] splt = line.split(" ");
		if (splt.length > 4 || splt.length < 3) {
			sock.sendPacket(new PackCommand(CommandType.Exception, "Bad arguments"));
			return;
		}
		if (!this.provider.existsProject(splt[2]))
		{
			sock.sendPacket(new PackCommand(CommandType.Exception, "Proect not exists"));
			return;
		}
		sock.sendPacket(new PackCommand(CommandType.OK, ""));
		System.out.print("Start send project");
		try
		{
			new ZipperOriginal().zip(FileHelper.concat("auth/"+login, splt[2]), login+".zip");
			sock.sendFile(login+".zip");
			new File(login + ".zip").delete();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.print("End of sending project\n");
	}
	private void UPDATE(String line)
{
		
	}
	private void COMMIT(String line)
	{
		String[] splt = line.split(" ");
		if (splt.length != 3)
		{
			sock.sendPacket(new PackCommand(CommandType.Exception, "Bad arguments"));
			return;
		}
		if (!this.provider.existsProject(splt[2]))
		{
			sock.sendPacket(new PackCommand(CommandType.Exception, "Proect not exists"));
			return;
		}
		sock.sendPacket(new PackCommand(CommandType.OK, ""));
		try{
			System.out.println("Start download project:" + splt[2]);
			sock.getFile(login + ".zip");
			IVersion version = provider.maxProjectVersion(splt[2]).update();
			new ZipperProvider().unzip(login + ".zip", provider, splt[2], version);
			new File(login + ".zip").delete();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.print("Commit done\n");
	}
	private void REVERT(String line)
	{//revert path repoName version flags
		String[] splt = line.split(" ");
		if (splt.length > 5 || splt.length < 4) {
			sock.sendPacket(new PackCommand(CommandType.Exception, "Bad arguments"));
			return;
		}
		if (!this.provider.existsProject(splt[2]))
		{
			sock.sendPacket(new PackCommand(CommandType.Exception, "Proect not exists"));
			return;
		}
		IVersion version = provider.maxProjectVersion(splt[2]).parse(splt[3]);
		if (!this.provider.existsVersion(splt[2], version))
		{
			sock.sendPacket(new PackCommand(CommandType.Exception, "Version not exists"));
			return;
		}
		sock.sendPacket(new PackCommand(CommandType.OK, ""));
		System.out.print("Start send project\n");
		try
		{
			new ZipperOriginal().zip(FileHelper.concat("auth/"+login, splt[2]+"/"+version), login+".zip");
			sock.sendFile(login+".zip");
			new File(login + ".zip").delete();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.print("End of sending project\n");
	}
	private void LOG(String line)
{
		
	}

	@Override
	public String name() {
		return null;
	}
	@Override
	public boolean rightArg(Object args) {
		return args instanceof Socket; 
	}

}
