package Util;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

import Packer.ICommandPacker;
import Packet.CommandType;
import Packet.IPacket;
import Packet.PackCommand;
import Packet.PackData;
import sun.misc.IOUtils;

public class ProSocket {
	private Socket socket = null;
	private ICommandPacker packer;
	public DataInputStream in;
    public DataOutputStream out;
	public ProSocket (String adress, int port, ICommandPacker packer)
	{
		try
		{
			this.packer = packer;
			this.socket = new Socket(adress, port);
			this.in = new DataInputStream(this.socket.getInputStream());
			this.out = new DataOutputStream(this.socket.getOutputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public ProSocket(Socket socket, ICommandPacker packer)
	{
		try
		{
			this.packer = packer;
			this.socket = socket;
			this.in = new DataInputStream(this.socket.getInputStream());
			this.out = new DataOutputStream(this.socket.getOutputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public boolean sendUTF(String data)
	{
		try
		{
			out.writeUTF(data);
			out.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	public String getUTF()
	{
		try
		{
			return in.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public boolean sendBytes(byte[] data)
	{
		try
		{
			out.write(data);
			out.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	public byte[] getBytes(int count)
	{
		try
		{
			byte[] buf = new byte[count];
			int _count = in.read(buf);
			byte[] result = new byte[_count];
			for (int i=0; i<_count; i++)
				result[i] = buf[i];
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public IPacket getPacket()
{
		return this.packer.toPacket(getBytes(1024));
	}
	public boolean sendPacket(IPacket pack)
	{
		return sendBytes(this.packer.toByte(pack));
	}
	public boolean sendFile(String srcFile) throws IOException
	{
		byte[] buf = new byte[512];
        Long size = new File(srcFile).length();
        sendPacket(new PackCommand(CommandType.ZipSize, size.toString()));
		FileInputStream fis = new FileInputStream(srcFile);
		while (fis.read(buf) != -1)
		{
			sendPacket(new PackData(buf));
			getPackCommand(CommandType.OK);
		}
        return true;
	}
    public boolean getFile(String scrFile) throws IOException
    {
        long size = Long.parseLong(getPackCommand(CommandType.ZipSize).getArgs());
		File file = new File(scrFile);
		if (file.exists())
			file.delete();
        FileOutputStream fos = new FileOutputStream(scrFile, true);
        IPacket pack = getPacket();
        for(int i = 0; i < size - 512; i+=512)
        {
            fos.write((byte[]) pack.getObject());
			sendPacket(new PackCommand(CommandType.OK, ""));
			pack = getPacket();
        }
		fos.write((byte[]) pack.getObject(), 0, (int)(size % 512));
		sendPacket(new PackCommand(CommandType.OK, ""));
		fos.close();
        return true;
    }
	public PackCommand getPackCommand(CommandType ct)
	{
		IPacket pack = getPacket();
		while (!(CommandType.isItComand(pack, ct)))
		{
			sendPacket(new PackCommand(CommandType.Exception,"Incorect args"));
			pack = getPacket();
		}
		return (PackCommand)pack;
	}
	public PackCommand getPackCommand(CommandType[] ct)
	{
		IPacket pack = getPacket();
		boolean check = false;
		while (!check)
		{
			for (CommandType c : ct)
				if (CommandType.isItComand(pack, c))
				{
					check = true;
					break;
				}
			if (!check)
			{
				sendPacket(new PackCommand(CommandType.Exception, "Incorect args"));
				pack = getPacket();
			}
		}
		return (PackCommand)pack;
	}
	public void close()
	{
		try
		{
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
