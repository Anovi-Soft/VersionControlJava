package Packer;

import Packet.IPacket;

public interface ICommandPacker {
	public byte[] toByte(IPacket packet);
	public IPacket toPacket(byte[] array);
}
