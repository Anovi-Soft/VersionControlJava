package Packet;


public class PackCommand implements IPacket {
	private CommandType data;
	private String args;
	public PackCommand(CommandType data, String args)
{
		this.data = data;
		this.args = args;
	}
	public PackCommand(byte data)
{
		this.data = CommandType.values()[data];
	}
	@Override
	public PacketType getType() {
		return PacketType.Command;
	}
	@Override
	public Object getObject() {
		return data;
	}
	public String getArgs()
	{
		return args;
	}

}
