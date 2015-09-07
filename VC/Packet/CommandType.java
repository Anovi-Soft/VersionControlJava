package Packet;

public enum CommandType {
	OK((byte)0),
	Add((byte)1),
	Clone((byte)2),
	Update((byte)3),
	Commit((byte)4),
	Revert((byte)5),
	Log((byte)6),
	DirSize((byte)7),
	ZipSize((byte)8),
	Login((byte)9),
	Password((byte)10),
	Exception((byte)11),
	NeedLogin((byte)12),
	NeedReg((byte)13),
	Quit((byte)14),
	Version((byte)15),
	Port((byte)16);
	private final byte value;
	CommandType(byte value)
{
		this.value = value;
	}
	public byte getValue()
{
		return value;
	}
	public static boolean isCommand(IPacket pack)
	{
		return pack.getType().equals(PacketType.Command);
	}
	public static boolean isItComand(IPacket pack, CommandType ct)
	{
		return isCommand(pack) && ((CommandType)((PackCommand)pack).getObject()).equals(ct);
	}
}
