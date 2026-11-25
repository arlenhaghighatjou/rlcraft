package com.dhanantry.scapeandrunparasites.proxy;

import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.handlers.SRPRegistryHandlers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent e) {
      super.preInit(e);
      SRPRegistryHandlers.initRenders();
   }

   public void spreadBiome(BlockPos pos, boolean convert) {
      if (convert) {
         ParasiteEventWorld.positionToParasiteBiome(Minecraft.getMinecraft().world, pos);
      } else {
         ParasiteEventWorld.positionToBiome(Minecraft.getMinecraft().world, pos);
      }

   }

   public void playMovingSound(int sound, float v) {
      switch(sound) {
      case -1:
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SRPSounds.BHEART, 1.0F, v * SRPConfigWorld.biomeHeartVol));
      case 0:
      case 9:
      default:
         break;
      case 1:
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SRPSounds.EVPHASE_1, 1.0F, 1.0F));
         break;
      case 2:
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SRPSounds.EVPHASE_2, 1.0F, 1.0F));
         break;
      case 3:
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SRPSounds.EVPHASE_3, 1.0F, 1.0F));
         break;
      case 4:
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SRPSounds.EVPHASE_4, 1.0F, 1.0F));
         break;
      case 5:
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SRPSounds.EVPHASE_5, 1.0F, 1.0F));
         break;
      case 6:
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SRPSounds.EVPHASE_6, 1.0F, 1.0F));
         break;
      case 7:
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SRPSounds.EVPHASE_7, 1.0F, 1.0F));
         break;
      case 8:
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SRPSounds.EVPHASE_8, 1.0F, 1.0F));
         break;
      case 10:
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SRPSounds.NODE_1, 1.0F, 1.0F));
         break;
      case 11:
         Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SRPSounds.COLONY_1, 1.0F, 1.0F));
      }

   }

   public boolean handleMaterialAcceleration(Entity entityIn, Material materialIn) {
      World parWorld = entityIn.world;
      AxisAlignedBB bb = entityIn.getEntityBoundingBox().grow(0.0D, -0.4000000059604645D, 0.0D).shrink(0.001D);
      int j2 = MathHelper.floor(bb.minX);
      int k2 = MathHelper.ceil(bb.maxX);
      int l2 = MathHelper.floor(bb.minY);
      int i3 = MathHelper.ceil(bb.maxY);
      int j3 = MathHelper.floor(bb.minZ);
      int k3 = MathHelper.ceil(bb.maxZ);
      boolean flag = false;
      Vec3d vec3d = Vec3d.ZERO;
      PooledMutableBlockPos blockpos$pooledmutableblockpos = PooledMutableBlockPos.retain();

      for(int l3 = j2; l3 < k2; ++l3) {
         for(int i4 = l2; i4 < i3; ++i4) {
            for(int j4 = j3; j4 < k3; ++j4) {
               blockpos$pooledmutableblockpos.setPos(l3, i4, j4);
               IBlockState iblockstate1 = parWorld.getBlockState(blockpos$pooledmutableblockpos);
               Block block = iblockstate1.getBlock();
               Boolean result = block.isEntityInsideMaterial(parWorld, blockpos$pooledmutableblockpos, iblockstate1, entityIn, (double)i3, materialIn, false);
               if (result != null && result.booleanValue()) {
                  flag = true;
                  vec3d = block.modifyAcceleration(parWorld, blockpos$pooledmutableblockpos, entityIn, vec3d);
               } else if ((result == null || result.booleanValue()) && iblockstate1.getMaterial() == materialIn) {
                  double d0 = (double)((float)(i4 + 1) - BlockLiquid.getLiquidHeightPercent(((Integer)iblockstate1.getValue(BlockLiquid.LEVEL)).intValue()));
                  if ((double)i3 >= d0) {
                     flag = true;
                     vec3d = block.modifyAcceleration(parWorld, blockpos$pooledmutableblockpos, entityIn, vec3d);
                  }
               }
            }
         }
      }

      blockpos$pooledmutableblockpos.release();
      if (vec3d.lengthVector() > 0.0D && entityIn.isPushedByWater()) {
         vec3d = vec3d.normalize();
         double d1 = 0.014D;
         entityIn.motionX += vec3d.x * d1;
         entityIn.motionY += vec3d.y * d1;
         entityIn.motionZ += vec3d.z * d1;
      }

      entityIn.fallDistance = 0.0F;
      return flag;
   }
}
