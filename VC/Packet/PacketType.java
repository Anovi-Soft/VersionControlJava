package Packet;

public enum PacketType {
	Command(0),
	Answer(1),
	Data(2),
	Exception(3);
	private final int value;
	PacketType(int value)
{
		this.value = value;
	}
	public int getValue()
{
		return value;
	}
}
