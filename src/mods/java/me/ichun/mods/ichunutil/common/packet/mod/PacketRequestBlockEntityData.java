package me.ichun.mods.ichunutil.common.packet.mod;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class PacketRequestBlockEntityData extends AbstractPacket {
   public int id;

   public PacketRequestBlockEntityData() {
   }

   public PacketRequestBlockEntityData(EntityBlock block) {
      this.id = block.getEntityId();
   }

   public void writeTo(ByteBuf buffer) {
      buffer.writeInt(this.id);
   }

   public void readFrom(ByteBuf buffer) {
      this.id = buffer.readInt();
   }

   public void execute(Side side, EntityPlayer player) {
      Entity ent = player.getEntityWorld().getEntityByID(this.id);
      if (ent instanceof EntityBlock) {
         NBTTagCompound tag = new NBTTagCompound();
         ent.writeToNBT(tag);
         iChunUtil.channel.sendTo(new PacketBlockEntityData(this.id, tag), player);
      }

   }

   public Side receivingSide() {
      return Side.SERVER;
   }
}
