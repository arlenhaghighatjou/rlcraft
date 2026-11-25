package xzeroair.trinkets.network.vip;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.Vip.VipStatus;
import xzeroair.trinkets.network.ThreadSafePacket;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.vip.VIPHandler;

public class VipStatusPacket extends ThreadSafePacket {
   public VipStatusPacket() {
   }

   public VipStatusPacket(EntityPlayer entity, NBTTagCompound tag) {
      super(entity.getEntityId(), tag);
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
      EntityPlayerSP clientPlayer = Minecraft.getMinecraft().player;
      World world = clientPlayer.getEntityWorld();
      Entity entity = world.getEntityByID(this.entityID);
      Capabilities.getVipStatus(entity, (vip) -> {
         if (TrinketsConfig.SERVER.misc.retrieveVIP) {
            vip.confirmedStatus();
         }

         vip.loadFromNBT(this.tag);
      });
   }

   public void handleServerSafe(NetHandlerPlayServer server) {
      if (TrinketsConfig.SERVER.misc.retrieveVIP) {
         EntityPlayerMP serverPlayer = server.player;

         try {
            if (this.tag == null) {
               return;
            }

            if (!this.tag.hasKey("uuid")) {
               return;
            }

            String id = this.tag.getString("uuid");
            if (VIPHandler.Vips.containsKey(id.replaceAll("-", ""))) {
               Entity entity = serverPlayer.getServerWorld().getEntityFromUuid(UUID.fromString(id));
               if (entity != null && entity instanceof EntityPlayerMP) {
                  EntityPlayerMP vip = (EntityPlayerMP)entity;
                  VipStatus status = Capabilities.getVipStatus(vip);
                  if (status != null) {
                     status.confirmedStatus();
                  }
               }
            }
         } catch (Exception var7) {
            ;
         }

      }
   }
}
