package tschipp.tschipplib.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CTileEntitySyncPacket implements IMessage{
	

	public NBTTagCompound data;
	
	public CTileEntitySyncPacket() {
	}
	
	
	public CTileEntitySyncPacket(NBTTagCompound data)
	{
		this.data = data;
	}
	
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, data);
		
	}

}
