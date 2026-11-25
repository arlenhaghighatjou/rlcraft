package jaredbgreat.dldungeons.tputils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class Teleportation extends Teleporter {
   private final WorldServer world;
   private double x;
   private double y;
   private double z;

   public Teleportation(WorldServer world, double x, double y, double z) {
      super(world);
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void placeInPortal(Entity entity, float rotationYaw) {
      this.world.getBlockState(new BlockPos((int)this.x, (int)this.y, (int)this.z));
      entity.setPosition(this.x, this.y, this.z);
      entity.motionX = 0.0D;
      entity.motionY = 0.0D;
      entity.motionZ = 0.0D;
   }

   public static void tpToDimension(EntityPlayer player, int dimension, double x, double y, double z) {
      int startDimension = player.getEntityWorld().provider.getDimension();
      EntityPlayerMP playerMP = (EntityPlayerMP)player;
      MinecraftServer server = player.getEntityWorld().getMinecraftServer();
      WorldServer worldServer = server.getWorld(dimension);
      if (worldServer != null && server != null) {
         worldServer.getMinecraftServer().getPlayerList().transferPlayerToDimension(playerMP, dimension, new Teleportation(worldServer, x, y, z));
         player.setPositionAndUpdate(x, y, z);
      } else {
         throw new IllegalArgumentException("ERROR: Trying to teleport to non-existent dimension " + dimension + "!");
      }
   }

   public static void tpToBlockPos(EntityPlayer player, int dimension, int ix, int iy, int iz) {
      tpToDimension(player, dimension, (double)ix + 0.5D, (double)iy, (double)iz + 0.5D);
   }

   public static void tpToBlockPos(EntityPlayer player, int dimension, BlockPos pos) {
      tpToDimension(player, dimension, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
   }
}
