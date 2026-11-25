package knightminer.inspirations.common.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import slimeknights.mantle.network.AbstractPacketThreadsafe;

public class MilkablePacket extends AbstractPacketThreadsafe {
   private int entityID;
   private boolean milkable;

   public MilkablePacket() {
   }

   public MilkablePacket(Entity entity, boolean milkable) {
      this.entityID = entity.getEntityId();
      this.milkable = milkable;
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityID);
      buf.writeBoolean(this.milkable);
   }

   public void fromBytes(ByteBuf buf) {
      this.entityID = buf.readInt();
      this.milkable = buf.readBoolean();
   }

   public void handleClientSafe(NetHandlerPlayClient netHandler) {
      Entity entity = Minecraft.getMinecraft().world.getEntityByID(this.entityID);
      if (entity != null) {
         NBTTagCompound tags = entity.getEntityData();
         tags.setShort("milk_cooldown", (short)(this.milkable ? 0 : 100));
      }
   }

   public void handleServerSafe(NetHandlerPlayServer netHandler) {
      throw new UnsupportedOperationException("Clientside only");
   }
}
