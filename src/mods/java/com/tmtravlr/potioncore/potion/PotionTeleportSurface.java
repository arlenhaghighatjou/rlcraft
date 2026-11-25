package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class PotionTeleportSurface extends PotionCorePotion {
   public static final String NAME = "teleport_surface";
   public static final PotionTeleportSurface INSTANCE = new PotionTeleportSurface();

   public PotionTeleportSurface() {
      super("teleport_surface", false, 65433);
   }

   public boolean isInstant() {
      return true;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      BlockPos pos = entity.world.getTopSolidOrLiquidBlock(entity.getPosition());
      if (entity.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK) {
         pos = entity.getPosition();
      }

      this.teleportTo(entity, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D);
   }

   private boolean teleportTo(EntityLivingBase entity, double x, double y, double z) {
      EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0.0F);
      if (MinecraftForge.EVENT_BUS.post(event)) {
         return false;
      } else {
         double d0 = entity.posX;
         double d1 = entity.posY;
         double d2 = entity.posZ;
         entity.posX = event.getTargetX();
         entity.posY = event.getTargetY();
         entity.posZ = event.getTargetZ();
         boolean flag = false;
         BlockPos blockpos = new BlockPos(entity.posX, entity.posY, entity.posZ);
         if (entity.world.isBlockLoaded(blockpos)) {
            entity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
            if (entity.world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()) {
               flag = true;
            }
         }

         if (!flag) {
            entity.setPosition(d0, d1, d2);
            return false;
         } else {
            if (entity.world instanceof WorldServer) {
               ((WorldServer)entity.world).spawnParticle(EnumParticleTypes.PORTAL, true, entity.posX, entity.posY, entity.posZ, 128, 1.0D, 2.0D, 1.0D, 0.0D, new int[0]);
            }

            entity.world.playSound((EntityPlayer)null, d0, d1, d2, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
            entity.world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
            return true;
         }
      }
   }
}
