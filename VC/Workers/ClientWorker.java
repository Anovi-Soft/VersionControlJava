package Workers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import Auth.*;
import Exceptions.VersionControlException;
import Util.FileHelper;
import Packer.StockPacker;
import Packet.CommandType;
import Packet.PackCommand;
import Util.ProSocket;
import Util.ZipperOriginal;
import Version.StockVersion;


public class ClientWorker implements IWorker{
	private StockVersion clientVersion = new StockVersion("1.0.0");
	private ProSocket sock;
	private String address, port;
	@Override
	public void work(Object args) {
		System.out.println("Please input address");
		address = "127.0.0.1";//readLine();
		System.out.println("Please input port");
		port = "11568";//readLine();
		
		this.sock = new ProSocket(address, Integer.parseInt(port), new StockPacker());
		hello();
		loop();
	}

	protected void hello()
{
		port = sock.getPackCommand(CommandType.Port).getArgs();
		
		sock.sendPacket(new PackCommand(CommandType.Version, clientVersion.toString()));
		PackCommand pack = sock.getPackCommand(new CommandType[]{CommandType.OK, CommandType.Exception});		
		if (CommandType.isItComand(pack, CommandType.Exception))
		{
			System.out.println(pack.getArgs());
			return;
		}		
		sock.close();
		sock = new ProSocket(address, Integer.parseInt(port), new StockPacker());
		
		
		IClientAuth auth = new StockClientAuth(sock);
		boolean uncheck = false;
		System.out.println("do you need new accountant? (y/n)");
		do
		{
			switch(readLine().toLowerCase())
			{
			case "y":
				auth.Reg();
				uncheck = false;
				break;
			case "n":
				auth.Auth();
				uncheck = false;
				break;
			default :
				uncheck = true;
				System.out.println("Unknown Input");
				break;
			}
		}while(uncheck);
	}
	
	protected void loop()
{
		while (true)
			try {
				String line = readLine().toLowerCase();
				if (line.startsWith("quit"))
				{
					sock.sendPacket(new PackCommand(CommandType.Quit, ""));
					return;
				}
				Cmd(line);
			} catch (VersionControlException e) {
				System.out.println(e.getMessage());
			}	
	}
	
	@Override
	public String name() {
		return null;
	}

	@Override
	public boolean rightArg(Object args) {
		return args instanceof Socket; 
	}

	private void Cmd(String line) throws VersionControlException{
		String[] splt = line.toLowerCase().split(" ");
		switch (splt[0])
{
		case "add":ADD(line);break;
		case "clone":CLONE(line);break;
		case "update":UPDATE(line);break;
		case "commit":COMMIT(line);break;
		case "revert":REVERT(line);break;
		case "log":LOG(line);break;
		default: throw new VersionControlException("Unknown Command");
		}
	}
	private void ADD(String line) throws VersionControlException{
		sock.sendPacket(new PackCommand(CommandType.Add, line));
		PackCommand com = sock.getPackCommand(new CommandType[]{CommandType.OK, CommandType.Exception});
		if (CommandType.isItComand(com, CommandType.Exception))
			System.out.println(com.getArgs());
		else
			System.out.println(line + " is Ok");
	}
	private void CLONE(String line) throws VersionControlException
	{
		sock.sendPacket(new PackCommand(CommandType.Clone, line));
		PackCommand com = sock.getPackCommand(new CommandType[]{CommandType.OK, CommandType.Exception});
		if (CommandType.isItComand(com, CommandType.Exception)) {
			System.out.println(com.getArgs());
			return;
		}
		String[] splt = line.replace("/", "\\").split(" ");
		FileHelper.freeFolder(splt[1]);
		if (splt[1].endsWith("\\"))
			splt[1] = splt[1].substring(0, splt[1].length()-1);
		String path = splt[1];
		FileHelper.makeFolders(path);
		boolean flag = false;
		if (splt.length == 4 && splt[3].equals("."))
			flag = true;
		if (flag)
			path = splt[1].substring(0, splt[1].lastIndexOf("\\"));
		if (new File("tmp.zip").exists())
			new File("tmp.zip").delete();
		try {
			sock.getFile("tmp.zip");
			new ZipperOriginal().unzip("tmp.zip", path);
			if (flag) {
				File pathTMP = new File(FileHelper.concat(path, new File(splt[1]).getName()));
				FileHelper.freeFolder(pathTMP);
				new File(FileHelper.concat(path, splt[2]))
						.renameTo(pathTMP);
			}
			new File("tmp.zip").delete();
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.print("Clone complete\n");
	}
	private void UPDATE(String line) throws VersionControlException
	{
		
	}
	private void COMMIT(String line) throws VersionControlException
	{
		final String[] splt = line.split(" ");
		try {
			if (!(new File(splt[1])).exists()) {
				System.out.println("Bad path");
				return;
			}
		}catch (IndexOutOfBoundsException e){}
		sock.sendPacket(new PackCommand(CommandType.Commit, line));
		PackCommand com = sock.getPackCommand(new CommandType[]{CommandType.OK, CommandType.Exception});
		if (CommandType.isItComand(com, CommandType.Exception))
		{
			System.out.println(com.getArgs());
			return;
		}
		try
		{
			System.out.print("Folder packed\n");
			if (new File("tmp.zip").exists())
				new File("tmp.zip").delete();
			new ZipperOriginal().zip(splt[1], "tmp.zip");
			System.out.print("Folder send\n");
			sock.sendFile("tmp.zip");
			new File("tmp.zip").delete();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.print("Commit is done\n");
	}
	private void REVERT(String line) throws VersionControlException
	{//revert path repoName version flags
		String[] splt = line.split(" ");
		sock.sendPacket(new PackCommand(CommandType.Revert, line));
		PackCommand com = sock.getPackCommand(new CommandType[]{CommandType.OK, CommandType.Exception});
		if (CommandType.isItComand(com, CommandType.Exception))
		{
			System.out.println(com.getArgs());
			return;
		}
		String path = splt[1].replace("/", "\\");
		boolean flag = false;
		if (splt.length == 5 && splt[4].equals("."))
			flag = true;
		if (flag)
			path = path.substring(0, path.lastIndexOf("\\"));
		if (new File("tmp.zip").exists())
			new File("tmp.zip").delete();
		try {
			sock.getFile("tmp.zip");
			new ZipperOriginal().unzip("tmp.zip", path);
			if (flag)
			{
				File pathTMP = new File(FileHelper.concat(path, new File(splt[1]).getName()));
				FileHelper.freeFolder(pathTMP);
				FileHelper.makeFolders(pathTMP+"/1");
				new File(FileHelper.concat(path, splt[3]))
						.renameTo(pathTMP);
			}
			new File("tmp.zip").delete();
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.print("Revert complete\n");
	}
	private void LOG(String line) throws VersionControlException
	{
		
	}
	public static String readLine() {
		try {
			return new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (IOException e) {
			return new String();
		}
	}
}
