package me.ichun.mods.ichunutil.common.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public abstract class AbstractPacket {
   public AbstractPacket() {
   }

   public abstract void writeTo(ByteBuf var1);

   public abstract void readFrom(ByteBuf var1);

   public abstract void execute(Side var1, EntityPlayer var2);

   public abstract Side receivingSide();

   public boolean requiresMainThread() {
      return true;
   }
}
