package net.minecraft.network;

import net.minecraft.util.IThreadListener;

public final class PacketThreadUtil
{
	private PacketThreadUtil()
	{
	}

	public static <T extends INetHandler> void checkThreadAndEnqueue(Packet<T> packet, T handler, IThreadListener listener)
	{
		if (!listener.isCallingFromMinecraftThread())
		{
			listener.addScheduledTask(() -> packet.processPacket(handler));
			throw DeferredPacket.INSTANCE;
		}
	}

	public static final class DeferredPacket extends RuntimeException
	{
		private static final DeferredPacket INSTANCE = new DeferredPacket();

		private DeferredPacket()
		{
			super(null, null, false, false);
		}
	}
}
