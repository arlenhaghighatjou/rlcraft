package dev.satyrn.wolfarmor.common.network.packets;

import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import dev.satyrn.wolfarmor.common.network.MessageBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class WolfDropChestMessage extends MessageBase.ServerMessageBase<WolfDropChestMessage> {
   private int entityId;

   public WolfDropChestMessage() {
   }

   public WolfDropChestMessage(int entityId) {
      this.entityId = entityId;
   }

   protected void read(PacketBuffer buffer) {
      this.entityId = buffer.readInt();
   }

   protected void write(PacketBuffer buffer) {
      buffer.writeInt(this.entityId);
   }

   protected IMessage process(EntityPlayer player, Side side) {
      World world = player.getEntityWorld();
      Entity entity = world.getEntityByID(this.entityId);
      if (entity instanceof IArmoredWolf) {
         IArmoredWolf armoredWolf = (IArmoredWolf)entity;
         armoredWolf.dropChest();
         armoredWolf.dropInventoryContents();
      }

      return null;
   }
}
