package cursedflames.bountifulbaubles.wormhole;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

public class TeleporterRecall implements ITeleporter {
   public TeleporterRecall() {
   }

   public void placeEntity(World world, Entity entity, float yaw) {
      if (entity instanceof EntityPlayer) {
         BlockPos spawnPoint = ((EntityPlayer)entity).getBedLocation();
         if (spawnPoint != null) {
            spawnPoint = EntityPlayer.getBedSpawnLocation(world, spawnPoint, ((EntityPlayer)entity).isSpawnForced(world.provider.getDimension()));
         }

         if (spawnPoint == null) {
            spawnPoint = world.provider.getRandomizedSpawnPoint();
         }

         if (spawnPoint != null) {
            if (entity instanceof EntityPlayerMP) {
               ((EntityPlayerMP)entity).connection.setPlayerLocation((double)spawnPoint.getX() + 0.5D, (double)spawnPoint.getY(), (double)spawnPoint.getZ() + 0.5D, entity.rotationYaw, entity.rotationPitch);
            } else {
               entity.setLocationAndAngles((double)spawnPoint.getX() + 0.5D, (double)spawnPoint.getY(), (double)spawnPoint.getZ() + 0.5D, entity.rotationYaw, entity.rotationPitch);
            }

            if (entity.fallDistance > 0.0F) {
               entity.fallDistance = 0.0F;
            }

            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
         }
      }
   }
}
