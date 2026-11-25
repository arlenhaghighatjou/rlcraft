package c4.corpsecomplex.common.modules.spawning;

import c4.corpsecomplex.network.NetworkHandler;
import c4.corpsecomplex.network.TeleportEffectMessage;
import c4.corpsecomplex.network.TeleportSoundMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class ScrollTeleporter {
   public ScrollTeleporter() {
   }

   public static void sendTeleportEffect(World world, BlockPos pos) {
      NetworkHandler.INSTANCE.sendToAllAround(new TeleportEffectMessage(pos), new TargetPoint(world.provider.getDimension(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 64.0D));
   }

   public static void teleportToPosition(EntityPlayer player, BlockPos pos, EnumFacing facing, int dimensionId) {
      sendTeleportEffect(player.world, new BlockPos(player));
      if (dimensionId != player.getEntityWorld().provider.getDimension()) {
         MinecraftServer server = player.world.getMinecraftServer();
         if (server != null) {
            transferPlayerToDimension((EntityPlayerMP)player, dimensionId, server.getPlayerList());
         }
      } else {
         if (player.isBeingRidden()) {
            player.removePassengers();
         }

         if (player.isRiding()) {
            player.dismountRidingEntity();
         }
      }

      player.rotationYaw = getRotationYaw(facing);
      pos = new BlockPos((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);
      player.setPositionAndUpdate((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
      sendTeleportEffect(player.world, pos);
      if (player instanceof EntityPlayerMP) {
         NetworkHandler.INSTANCE.sendTo(new TeleportSoundMessage(), (EntityPlayerMP)player);
      }

   }

   private static float getRotationYaw(EnumFacing facing) {
      switch(facing) {
      case NORTH:
         return 180.0F;
      case SOUTH:
         return 0.0F;
      case WEST:
         return 90.0F;
      case EAST:
         return -90.0F;
      default:
         return 0.0F;
      }
   }

   private static void transferPlayerToDimension(EntityPlayerMP player, int dimension, PlayerList manager) {
      int oldDim = player.dimension;
      WorldServer worldserver = manager.getServerInstance().getWorld(player.dimension);
      player.dimension = dimension;
      WorldServer worldserver1 = manager.getServerInstance().getWorld(player.dimension);
      player.connection.sendPacket(new SPacketRespawn(player.dimension, player.world.getDifficulty(), player.world.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
      worldserver.removeEntityDangerously(player);
      if (player.isBeingRidden()) {
         player.removePassengers();
      }

      if (player.isRiding()) {
         player.dismountRidingEntity();
      }

      player.isDead = false;
      transferEntityToWorld(player, worldserver, worldserver1);
      manager.preparePlayer(player, worldserver);
      player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
      player.interactionManager.setWorld(worldserver1);
      manager.updateTimeAndWeatherForPlayer(player, worldserver1);
      manager.syncPlayerInventory(player);

      for(PotionEffect potioneffect : player.getActivePotionEffects()) {
         player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
      }

      FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDim, dimension);
   }

   private static void transferEntityToWorld(Entity entity, WorldServer oldWorld, WorldServer newWorld) {
      WorldProvider oldWorldProvider = oldWorld.provider;
      WorldProvider newWorldProvider = newWorld.provider;
      double moveFactor = oldWorldProvider.getMovementFactor() / newWorldProvider.getMovementFactor();
      double x = entity.posX * moveFactor;
      double z = entity.posZ * moveFactor;
      oldWorld.profiler.startSection("placing");
      x = MathHelper.clamp(x, -2.9999872E7D, 2.9999872E7D);
      z = MathHelper.clamp(z, -2.9999872E7D, 2.9999872E7D);
      if (entity.isEntityAlive()) {
         entity.setLocationAndAngles(x, entity.posY, z, entity.rotationYaw, entity.rotationPitch);
         newWorld.spawnEntity(entity);
         newWorld.updateEntityWithOptionalForce(entity, false);
      }

      oldWorld.profiler.endSection();
      entity.setWorld(newWorld);
   }
}
