package net.mcft.copy.backpacks.network;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class BackpacksChannel extends SimpleNetworkWrapper {
   public BackpacksChannel() {
      super("wearablebackpacks");
      this.registerMessage(new MessageBackpackUpdate.Handler(), MessageBackpackUpdate.class, MessageBackpackUpdate::new, 1, Side.CLIENT);
      this.registerMessage(new MessageOpenGui.Handler(), MessageOpenGui.class, MessageOpenGui::new, 2, Side.CLIENT);
      this.registerMessage(new MessageOpenBackpack.Handler(), MessageOpenBackpack.class, MessageOpenBackpack::new, 3, Side.SERVER);
   }

   public void sendTo(IMessage message, EntityPlayer player) {
      this.sendTo(message, (EntityPlayerMP)player);
   }

   public void sendToAll(IMessage message, EntityPlayer except) {
      this.sendToAll(message, (player) -> {
         return player != except;
      });
   }

   public void sendToAll(IMessage message, Predicate<EntityPlayer> filter) {
      for(EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
         if (filter.test(player)) {
            this.sendTo(message, player);
         }
      }

   }

   public void sendToAllAround(IMessage message, World world, double x, double y, double z, double distance) {
      this.sendToAllAround(message, new TargetPoint(world.provider.getDimension(), x, y, z, distance));
   }

   public void sendToAllAround(IMessage message, World world, double x, double y, double z, double distance, EntityPlayer except) {
      this.sendToAllAround(message, world, x, y, z, distance, (player) -> {
         return player != except;
      });
   }

   public void sendToAllAround(IMessage message, World world, double x, double y, double z, double distance, Predicate<EntityPlayer> filter) {
      for(EntityPlayer player : world.playerEntities) {
         if (filter.test(player)) {
            double dx = x - player.posX;
            double dy = y - player.posY;
            double dz = z - player.posZ;
            if (dx * dx + dy * dy + dz * dz < distance * distance) {
               this.sendTo(message, player);
            }
         }
      }

   }

   public void sendToAllTracking(IMessage message, Entity entity, boolean sendToEntity) {
      ((WorldServer)entity.world).getEntityTracker().sendToTracking(entity, this.getPacketFrom(message));
      if (sendToEntity && entity instanceof EntityPlayer) {
         this.sendTo(message, (EntityPlayer)entity);
      }

   }
}
