package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasite;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInfestedStain extends BlockBase {
   public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 5);

   public BlockInfestedStain(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
      super(material, name, hardness, creative, tickRandom);
      this.setHarvestLevel("shovel", 0);
      this.setSoundType(new SoundType(1.0F, 0.5F, SRPSounds.BLOCKINFEST_BREAK, SRPSounds.BLOCKINFEST_STEP, SRPSounds.BLOCKINFEST_PLACE, SRPSounds.BLOCKINFEST_HIT, SoundEvents.BLOCK_SLIME_FALL));
      this.setDefaultState(this.blockState.getBaseState().withProperty(STAGE, Integer.valueOf(0)));
   }

   public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
      boolean flag = super.removedByPlayer(state, world, pos, player, willHarvest);
      if (world.isRemote) {
         return flag;
      } else {
         if (SRPConfigSystems.useEvolution && this.getMetaFromState(state) > 0) {
            SRPWorldData data = SRPWorldData.get(world);
            data.setTotalKills(-SRPConfigSystems.valueLossBlockStain, true, world, false);
         }

         return flag;
      }
   }

   public void randomTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (worldIn.isAreaLoaded(pos, 3)) {
         if (rand.nextDouble() < 0.5D) {
            if (worldIn.getBiome(pos) instanceof BiomeParasite) {
               worldIn.setBlockState(pos, SRPBlocks.ParasiteStain.getDefaultState());
            } else {
               int heart = ParasiteEventWorld.canBiomeStillExist(worldIn, pos, true);
               if (heart > 0) {
                  ParasiteEventWorld.SpreadBiome(worldIn, pos, heart);
               }
            }
         } else if (rand.nextDouble() <= 0.05D) {
            if (!worldIn.isUpdateScheduled(pos, state.getBlock())) {
               this.updateTick(worldIn, pos, state, rand);
            }
         } else if (!worldIn.isUpdateScheduled(pos, state.getBlock())) {
            worldIn.updateBlockTick(pos, state.getBlock(), ParasiteEventWorld.getTimer(this.getMetaFromState(state)), 5);
         }

      }
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (worldIn.isAreaLoaded(pos, 3)) {
         if (!worldIn.isRemote) {
            ParasiteEventWorld.canInfestBlock(worldIn, pos, rand, this.getMetaFromState(state), true);
         }

      }
   }

   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
      if (!worldIn.isRemote) {
         if (entityIn.ticksExisted % 20 != 0) {
            return;
         }

         if (!(entityIn instanceof EntityParasiteBase) && entityIn instanceof EntityLivingBase) {
            int meta = this.getMetaFromState(worldIn.getBlockState(pos));
            ParasiteEventEntity.damageEntity(worldIn, pos, entityIn, meta);
            if (entityIn.ticksExisted % 400 == 0) {
               if (entityIn instanceof EntityPlayer) {
                  EntityPlayer in = (EntityPlayer)entityIn;
                  if (in.capabilities.disableDamage) {
                     return;
                  }
               }

               EaglercraftRandom rand = new EaglercraftRandom();
               if (rand.nextInt(4) == 0) {
                  ParasiteEventEntity.spawnTurrets((EntityLivingBase)entityIn, 4, (byte)2, meta);
               } else {
                  ParasiteEventEntity.spawnTurrets((EntityLivingBase)entityIn, 10, (byte)1, meta);
               }
            }
         }
      }

      super.onEntityWalk(worldIn, pos, entityIn);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{STAGE});
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(STAGE)).intValue();
   }

   public IBlockState getStateFromMeta(int meta) {
      if (meta >= 5) {
         meta = 5;
      }

      return this.getDefaultState().withProperty(STAGE, Integer.valueOf(meta & 7));
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      if (worldIn.getBlockState(pos.up()).getBlock() == Blocks.AIR || worldIn.getBlockState(pos.up()).getBlock() == SRPBlocks.ParasiteBush) {
         int stage = this.getMetaFromState(stateIn);
         if (stage == 2) {
            if (rand.nextDouble() <= (double)SRPConfigSystems.rsBlockParticleS) {
               double d0 = (double)pos.getX() + rand.nextDouble();
               double d1 = (double)pos.getY() + 2.5D;
               double d2 = (double)pos.getZ() + rand.nextDouble();
               ParticleSpawner.spawnParticle(SRPEnumParticle.SPORE, d0, d1, d2, 0.0D, 0.0D, 0.0D, 0, 0, 0);
               return;
            }
         } else if (stage == 3) {
            if (rand.nextDouble() <= (double)SRPConfigSystems.rsBlockParticleF) {
               double d0 = (double)pos.getX() + rand.nextDouble();
               double d1 = (double)pos.getY() + 1.5D;
               double d2 = (double)pos.getZ() + rand.nextDouble();
               int g = 200;
               if (rand.nextInt(3) == 0) {
                  g += 50;
               }

               ParticleSpawner.spawnParticle(SRPEnumParticle.FOG, d0, d1, d2, 0.0D, 1.0E-4D, 0.0D, 0, g, 0);
            }

            if (rand.nextDouble() <= (double)SRPConfigSystems.rsBlockParticleS) {
               double d0 = (double)pos.getX() + rand.nextDouble();
               double d1 = (double)pos.getY() + 2.5D;
               double d2 = (double)pos.getZ() + rand.nextDouble();
               ParticleSpawner.spawnParticle(SRPEnumParticle.SPORE, d0, d1, d2, 0.0D, 0.0D, 0.0D, 0, 0, 0);
            }
         }

      }
   }
}
