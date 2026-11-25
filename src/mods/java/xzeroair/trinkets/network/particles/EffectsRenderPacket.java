package xzeroair.trinkets.network.particles;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.ThreadSafePacket;

public class EffectsRenderPacket extends ThreadSafePacket {
   double x;
   double y;
   double z;
   double x2;
   double y2;
   double z2;
   private int effectID;
   private int color;
   private float alpha;
   private float intensity;

   public EffectsRenderPacket() {
   }

   public EffectsRenderPacket(Entity entity, double x, double y, double z, double x2, double y2, double z2, int color, int effectID, float alpha, float intensity) {
      this.entityID = entity.getEntityId();
      this.effectID = effectID;
      this.x = x;
      this.y = y;
      this.z = z;
      this.x2 = x2;
      this.y2 = y2;
      this.z2 = z2;
      this.color = color;
      this.alpha = alpha;
      this.intensity = intensity;
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityID);
      buf.writeInt(this.effectID);
      buf.writeInt(this.color);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeDouble(this.x2);
      buf.writeDouble(this.y2);
      buf.writeDouble(this.z2);
      buf.writeFloat(this.alpha);
      buf.writeFloat(this.intensity);
   }

   public void fromBytes(ByteBuf buf) {
      this.entityID = buf.readInt();
      this.effectID = buf.readInt();
      this.color = buf.readInt();
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.x2 = buf.readDouble();
      this.y2 = buf.readDouble();
      this.z2 = buf.readDouble();
      this.alpha = buf.readFloat();
      this.intensity = buf.readFloat();
   }

   public void handleClientSafe(NetHandlerPlayClient client) {
      EntityPlayerSP clientPlayer = Minecraft.getMinecraft().player;
      World world = clientPlayer.getEntityWorld();

      try {
         Entity entity = clientPlayer.getEntityWorld().getEntityByID(this.entityID);
         if (entity != null) {
            world = entity.getEntityWorld();
         }

         Trinkets.proxy.renderEffect(this.effectID, world, this.x, this.y, this.z, this.x2, this.y2, this.z2, this.color, this.alpha, this.intensity);
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public void handleServerSafe(NetHandlerPlayServer server) {
      EntityPlayerMP serverPlayer = server.player;
      WorldServer world = serverPlayer.getServerWorld();

      try {
         Entity entity = serverPlayer.getEntityWorld().getEntityByID(this.entityID);
         if (entity != null) {
            if (entity instanceof EntityPlayerMP) {
               world = ((EntityPlayerMP)entity).getServerWorld();
            }
         } else {
            entity = serverPlayer;
         }

         NetworkHandler.sendToClients(world, new BlockPos(this.x, this.y, this.z), new EffectsRenderPacket(entity, this.x, this.y, this.z, this.x2, this.y2, this.z2, this.color, this.effectID, this.alpha, this.intensity));
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }
}
