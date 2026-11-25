package com.lycanitesmobs.core.block.effect;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.block.BlockBase;
import com.lycanitesmobs.core.config.ConfigBase;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPoisonCloud extends BlockBase {
   public BlockPoisonCloud() {
      super(Material.PLANTS);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
      this.group = LycanitesMobs.modInfo;
      this.blockName = "poisoncloud";
      this.setup();
      this.tickRate = ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Poison Clouds", true) ? 200 : 1;
      this.removeOnTick = true;
      this.loopTicks = true;
      this.canBeCrushed = true;
      this.noEntityCollision = true;
      this.noBreakCollision = true;
      this.isOpaque = false;
      this.setBlockUnbreakable();
      this.setLightOpacity(1);
   }

   public boolean canRemove(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      return world.getBlockState(pos.down()).getBlock() == Blocks.MYCELIUM ? false : super.canRemove(world, pos, state, rand);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(AGE)).intValue();
   }

   public BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{AGE});
   }

   public Item getItemDropped(IBlockState blockState, EaglercraftRandom random, int fortune) {
      return null;
   }

   public int damageDropped(IBlockState blockState) {
      return 0;
   }

   public int quantityDropped(EaglercraftRandom random) {
      return 1;
   }

   public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      super.onEntityCollidedWithBlock(world, pos, state, entity);
      if (entity instanceof EntityLivingBase) {
         ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.POISON, 100, 0));
      }

   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, EaglercraftRandom random) {
      super.randomDisplayTick(state, world, pos, random);
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      if (random.nextInt(24) == 0) {
         world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("poisoncloud"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
      }

      for(int particleCount = 0; particleCount < 12; ++particleCount) {
         float particleX = (float)x + random.nextFloat();
         float particleY = (float)y + random.nextFloat() * 0.5F;
         float particleZ = (float)z + random.nextFloat();
         world.spawnParticle(EnumParticleTypes.PORTAL, (double)particleX, (double)particleY, (double)particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
         world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)particleX, (double)particleY, (double)particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }
}
