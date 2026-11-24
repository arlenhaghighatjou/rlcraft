package com.arlenh7.eaglercraft.network;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;

public interface INetworkManager {

	void sendPacket(Packet<?> pkt);

	void closeChannel(ITextComponent reason);

	boolean isLocalChannel();

	boolean isChannelOpen();

	void setConnectionState(EnumConnectionState state);

	INetHandler getNetHandler();

	void setNetHandler(INetHandler handler);

	NetworkDispatcher getDispatcher();

	void setDispatcher(NetworkDispatcher dispatcher);

	boolean hasFMLMarker();

	default Packet<?> dispatchInbound(Packet<?> pkt) {
		NetworkDispatcher dispatcher = getDispatcher();
		return dispatcher != null ? dispatcher.handleInbound(pkt) : pkt;
	}

}
