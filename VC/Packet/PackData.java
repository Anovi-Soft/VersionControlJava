package Packet;


public class PackData implements IPacket {
	private byte[] data;
	public PackData(byte[] data)
{
		this.data = data;
	}
	@Override
	public PacketType getType() {
		return PacketType.Data;
	}
	@Override
	public Object getObject() {
		return data;
	}

}
