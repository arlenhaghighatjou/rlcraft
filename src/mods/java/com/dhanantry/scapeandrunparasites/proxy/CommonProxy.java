package com.dhanantry.scapeandrunparasites.proxy;

import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPFluids;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.init.SRPSpawning;
import com.dhanantry.scapeandrunparasites.network.SRPCommandColony;
import com.dhanantry.scapeandrunparasites.network.SRPCommandEvolution;
import com.dhanantry.scapeandrunparasites.network.SRPCommandNode;
import com.dhanantry.scapeandrunparasites.network.SRPCommandRoot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.handlers.SRPPacketHandler;
import com.dhanantry.scapeandrunparasites.util.handlers.SRPRegistryHandlers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy {
   public static Configuration config;
   public static Configuration configMobs;
   public static Configuration configSistems;
   public static Configuration configWorld;

   public CommonProxy() {
   }

   public void preInit(FMLPreInitializationEvent e) {
      SRPFluids.init();
      SRPRegistryHandlers.initEvents();
      SRPConfig.initConfig(e);
      SRPConfigSystems.initConfig(e);
      SRPConfigMobs.initConfig(e);
      SRPConfigWorld.initConfig(e);
      SRPAttributes.reset();
      SRPAttributes.init();
      SRPSounds.init();
      SRPPacketHandler.init();
      SRPEntities.registerTileEntities();
   }

   public void init(FMLInitializationEvent e) {
   }

   public void postInit(FMLPostInitializationEvent e) {
      config.save();
      configMobs.save();
      configSistems.save();
      configWorld.save();
      SRPSpawning.init();
      SRPBlocks.init();
      SRPBiomes.clearMobSpawnList();
      SRPSpawning.initBiome();
   }

   public void serverInit(FMLServerStartingEvent e) {
      e.registerServerCommand(new SRPCommandEvolution());
      e.registerServerCommand(new SRPCommandNode());
      e.registerServerCommand(new SRPCommandColony());
      e.registerServerCommand(new SRPCommandRoot());
   }

   public EntityPlayerMP getPlayerEntityFromContext(MessageContext ctx) {
      return ctx.getServerHandler().player;
   }

   public void spreadBiome(BlockPos pos, boolean convert) {
   }

   public void playMovingSound(int sound, float v) {
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
