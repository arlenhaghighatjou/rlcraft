package xzeroair.trinkets.network.mana;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.network.ThreadSafePacket;

public class SyncManaStatsPacket extends ThreadSafePacket {
   private int entityID;
   private NBTTagCompound tag;

   public SyncManaStatsPacket() {
   }

   public void fromBytes(ByteBuf buf) {
      this.entityID = buf.readInt();
      this.tag = ByteBufUtils.readTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityID);
      ByteBufUtils.writeTag(buf, this.tag);
   }

   public SyncManaStatsPacket(EntityLivingBase entity, MagicStats magic) {
      this.entityID = entity.getEntityId();
      this.tag = new NBTTagCompound();
      magic.saveToNBT(this.tag);
   }

   public void handleClientSafe(NetHandlerPlayClient client) {
      Entity player = Minecraft.getMinecraft().player.getEntityWorld().getEntityByID(this.entityID);
      MagicStats cap = Capabilities.getMagicStats(player);
      if (cap != null) {
         cap.loadFromNBT(this.tag);
      }

   }

   public void handleServerSafe(NetHandlerPlayServer server) {
   }
}
