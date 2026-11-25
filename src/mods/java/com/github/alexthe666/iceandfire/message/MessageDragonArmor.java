package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageDragonArmor extends AbstractMessage<MessageDragonArmor> {
   public int dragonId;
   public int armor_index;
   public int armor_type;

   public MessageDragonArmor(int dragonId, int armor_index, int armor_type) {
      this.dragonId = dragonId;
      this.armor_index = armor_index;
      this.armor_type = armor_type;
   }

   public MessageDragonArmor() {
   }

   public void fromBytes(ByteBuf buf) {
      this.dragonId = buf.readInt();
      this.armor_index = buf.readInt();
      this.armor_type = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.dragonId);
      buf.writeInt(this.armor_index);
      buf.writeInt(this.armor_type);
   }

   @SideOnly(Side.CLIENT)
   public void onClientReceived(Minecraft client, MessageDragonArmor message, EntityPlayer player, MessageContext messageContext) {
   }

   public void onServerReceived(MinecraftServer server, MessageDragonArmor message, EntityPlayer player, MessageContext messageContext) {
      Entity entity = player.world.getEntityByID(message.dragonId);
      if (entity != null && entity instanceof EntityDragonBase) {
         EntityDragonBase dragon = (EntityDragonBase)entity;
         dragon.setArmorInSlot(message.armor_index, message.armor_type);
      }

   }
}
