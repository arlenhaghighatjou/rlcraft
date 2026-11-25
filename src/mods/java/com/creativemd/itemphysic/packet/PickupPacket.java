package com.creativemd.itemphysic.packet;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import com.creativemd.itemphysic.EventHandler;
import com.creativemd.itemphysic.physics.ServerPhysic;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;

public class PickupPacket extends CreativeCorePacket {
   public EaglercraftUUID uuid;
   public boolean rightClick;

   public PickupPacket() {
   }

   public PickupPacket(EaglercraftUUID uuid, boolean rightClick) {
      this.uuid = uuid;
      this.rightClick = rightClick;
   }

   public void writeBytes(ByteBuf buf) {
      buf.writeLong(this.uuid.getLeastSignificantBits());
      buf.writeLong(this.uuid.getMostSignificantBits());
      buf.writeBoolean(this.rightClick);
   }

   public void readBytes(ByteBuf buf) {
      long least = buf.readLong();
      long most = buf.readLong();
      this.uuid = new EaglercraftUUID(most, least);
      this.rightClick = buf.readBoolean();
   }

   public void executeClient(EntityPlayer player) {
   }

   public void executeServer(EntityPlayer player) {
      if (this.rightClick) {
         EventHandler.toCancel.add(player);
      }

      Entity item = ((WorldServer)player.world).getEntityFromUuid(this.uuid);
      if (item != null && item instanceof EntityItem && !item.isDead) {
         ServerPhysic.processInitialInteract((EntityItem)item, player, player.getHeldItem(EnumHand.MAIN_HAND), EnumHand.MAIN_HAND);
         player.swingArm(EnumHand.MAIN_HAND);
      }

   }
}
