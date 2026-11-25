package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFrostfire extends BlockFireBase {
   public BlockFrostfire() {
      super(Material.FIRE, LycanitesMobs.modInfo, "frostfire");
      this.tickRate = 30;
      this.dieInRain = false;
      this.triggerTNT = false;
      this.agingRate = 3;
      this.spreadChance = 1.0F;
      this.removeOnTick = !ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Frostfire", true);
      this.removeOnNoFireTick = ConfigBase.getConfig(this.group, "general").getBool("Features", "Remove Frostfire on No Fire Tick", false);
      this.setLightOpacity(1);
      this.setLightLevel(0.0F);
   }

   public Item getItemDropped(IBlockState state, Random random, int zero) {
      return null;
   }

   public boolean canCatchFire(IBlockAccess world, BlockPos pos, EnumFacing face) {
      Block block = world.getBlockState(pos).getBlock();
      return block == Blocks.ICE;
   }

   public boolean isBlockFireSource(Block block, World world, BlockPos pos, EnumFacing side) {
      return block == Blocks.SNOW;
   }

   public int getBlockFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      Block block = world.getBlockState(pos).getBlock();
      return block == Blocks.ICE ? 20 : 0;
   }

   protected boolean canDie(World world, BlockPos pos) {
      return false;
   }

   public void burnBlockReplace(World world, BlockPos pos, int newFireAge) {
      if (world.getBlockState(pos).getBlock() == Blocks.ICE) {
         world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState(), 3);
      } else {
         super.burnBlockReplace(world, pos, newFireAge);
      }
   }

   public void burnBlockDestroy(World world, BlockPos pos) {
      if (world.getBlockState(pos).getBlock() == Blocks.ICE) {
         world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState(), 3);
      } else {
         super.burnBlockDestroy(world, pos);
      }
   }

   public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      super.onEntityCollidedWithBlock(world, pos, state, entity);
      if (!(entity instanceof EntityItem)) {
         if (entity.isBurning()) {
            entity.extinguish();
         }

         PotionEffect effectSlowness = new PotionEffect(MobEffects.SLOWNESS, 100, 0);
         if (entity instanceof BaseCreatureEntity) {
            BaseCreatureEntity entityCreature = (BaseCreatureEntity)entity;
            if (!entityCreature.canFreeze()) {
               return;
            }

            entityCreature.addPotionEffect(effectSlowness);
         }

         if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLiving = (EntityLivingBase)entity;
            entityLiving.addPotionEffect(effectSlowness);
         }

         entity.attackEntityFrom(BlockFireBase.COLD_FIRE, 2.0F);
      }
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
      super.randomDisplayTick(state, world, pos, random);
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      if (random.nextInt(24) == 0) {
         world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("frostfire"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
      }

      for(int particleCount = 0; particleCount < 12; ++particleCount) {
         float particleX = (float)x + random.nextFloat();
         float particleY = (float)y + random.nextFloat() * 0.5F;
         float particleZ = (float)z + random.nextFloat();
         world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, (double)particleX, (double)particleY, (double)particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }
}
