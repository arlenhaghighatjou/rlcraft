package xzeroair.trinkets.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import xzeroair.trinkets.capabilities.Capabilities;

public class SyncRaceDataPacket extends ThreadSafePacket {
   private int entityID;
   private NBTTagCompound tag;

   public SyncRaceDataPacket() {
   }

   public SyncRaceDataPacket(EntityLivingBase entity, NBTTagCompound tag) {
      this.entityID = entity.getEntityId();
      this.tag = tag;
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityID);
      ByteBufUtils.writeTag(buf, this.tag);
   }

   public void fromBytes(ByteBuf buf) {
      this.entityID = buf.readInt();
      this.tag = ByteBufUtils.readTag(buf);
   }

   public void handleClientSafe(NetHandlerPlayClient client) {
      Minecraft mc = Minecraft.getMinecraft();
      World world = mc.player.getEntityWorld();
      Entity entity = world.getEntityByID(this.entityID);
      Capabilities.getEntityProperties(entity, (prop) -> {
         prop.loadFromNBT(this.tag);
      });
   }

   public void handleServerSafe(NetHandlerPlayServer server) {
      Entity entity = server.player.getEntityWorld().getEntityByID(this.entityID);
      Capabilities.getEntityProperties(entity, (prop) -> {
         prop.loadFromNBT(this.tag);
         prop.scheduleResync();
      });
   }
}
