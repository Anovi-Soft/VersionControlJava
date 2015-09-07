package Packer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import Packet.*;

public class StockPacker implements ICommandPacker {

	@Override
	public byte[] toByte(IPacket packet) {
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		bytes.add((byte)packet.getType().getValue());
		switch (packet.getType())
{
		case Command:
			bytes.add(((CommandType)packet.getObject()).getValue());
			for (byte bt : ((PackCommand)packet).getArgs().getBytes())
				bytes.add(bt);
			break;
		case Data:
			for (byte bt : (byte[])packet.getObject())
				bytes.add(bt);
			break;
		default:
			break;
		}
		
		byte[] result = new byte[bytes.size()];
		int j=0;
		for(Byte b: bytes)
		    result[j++] = b.byteValue();
		return result;
	}

	@Override
	public IPacket toPacket(byte[] array) {
		PacketType type = PacketType.values()[array[0]];
		byte[] sub_array = subArray(array);
		try{
			switch (type)
{
			case Command:
				return new PackCommand(CommandType.values()[sub_array[0]],
						new String(subArray(sub_array), "UTF-8"));
			case Data:
				return new PackData(sub_array);
			default:
				break;
			}	
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private byte[] subArray(byte[] array)
	{
		byte[] sub_array = new byte[array.length-1];
		for (int i = 0; i < array.length-1; i++)
			sub_array[i] = array[i+1];
		return sub_array;
	}
}
