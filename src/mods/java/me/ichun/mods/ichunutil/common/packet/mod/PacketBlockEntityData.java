package me.ichun.mods.ichunutil.common.packet.mod;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class PacketBlockEntityData extends AbstractPacket {
   public int id;
   public NBTTagCompound tag;

   public PacketBlockEntityData() {
   }

   public PacketBlockEntityData(int id, NBTTagCompound tag) {
      this.id = id;
      this.tag = tag;
   }

   public void writeTo(ByteBuf buffer) {
      buffer.writeInt(this.id);
      ByteBufUtils.writeTag(buffer, this.tag);
   }

   public void readFrom(ByteBuf buffer) {
      this.id = buffer.readInt();
      this.tag = ByteBufUtils.readTag(buffer);
   }

   public void execute(Side side, EntityPlayer player) {
      Entity ent = player.getEntityWorld().getEntityByID(this.id);
      if (ent instanceof EntityBlock) {
         EntityBlock block = (EntityBlock)ent;
         block.readFromNBT(this.tag);
         block.setup = true;
      }

   }

   public Side receivingSide() {
      return Side.CLIENT;
   }
}
