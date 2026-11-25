package com.charles445.simpledifficulty.block;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.tileentity.TileEntityTemperature;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTemperature extends BlockContainer {
   public static final PropertyBool ENABLED = PropertyBool.create("enabled");
   private final float temperature;

   public BlockTemperature(float temperature) {
      super(Material.IRON);
      this.setHardness(0.5F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(ENABLED, Boolean.valueOf(false)));
      this.setSoundType(SoundType.METAL);
      this.temperature = temperature;
   }

   public float getActiveTemperatureMult() {
      return this.temperature;
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      if (!world.isRemote) {
         boolean enabled = ((Boolean)state.getValue(ENABLED)).booleanValue();
         boolean powered = world.isBlockPowered(pos);
         if (enabled && !powered) {
            this.turnOff(world, pos, state);
         } else if (!enabled && powered) {
            this.turnOn(world, pos, state);
         }
      }

   }

   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
      if (!world.isRemote) {
         boolean enabled = ((Boolean)state.getValue(ENABLED)).booleanValue();
         boolean powered = world.isBlockPowered(pos);
         if (enabled && !powered) {
            world.scheduleUpdate(pos, this, 4);
         } else if (!enabled && powered) {
            this.turnOn(world, pos, state);
         }
      }

   }

   public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
      if (!world.isRemote && ((Boolean)state.getValue(ENABLED)).booleanValue() && !world.isBlockPowered(pos)) {
         this.turnOff(world, pos, state);
      }

   }

   private void turnOff(World world, BlockPos pos, IBlockState state) {
      world.setBlockState(pos, state.withProperty(ENABLED, Boolean.valueOf(false)), 2);
   }

   private void turnOn(World world, BlockPos pos, IBlockState state) {
      world.setBlockState(pos, state.withProperty(ENABLED, Boolean.valueOf(true)), 2);
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
      super.randomDisplayTick(state, world, pos, rand);
      if (((Boolean)state.getValue(ENABLED)).booleanValue() && rand.nextFloat() <= 0.33F) {
         this.createRandomParticle(world, pos, rand);
      }

   }

   private void createRandomParticle(World world, BlockPos pos, Random rand) {
      double x_a = 0.125D;
      double z_a = 0.125D;
      double x_b = 0.0D;
      double z_b = 0.0D;
      double endoff = 0.875D;
      double thickness = 0.2D;
      switch(rand.nextInt(4)) {
      case 0:
         x_b = endoff;
         z_b = thickness;
         break;
      case 1:
         x_a = endoff - thickness;
         x_b = endoff;
         z_b = endoff;
         break;
      case 2:
         x_b = endoff;
         z_a = endoff - thickness;
         z_b = endoff;
         break;
      case 3:
         x_b = thickness;
         z_b = endoff;
      }

      double x_r = rand.nextDouble() * (x_b - x_a);
      double z_r = rand.nextDouble() * (z_b - z_a);
      if (ModConfig.client.heaterParticles) {
         SimpleDifficulty.proxy.spawnClientParticle(world, this.temperature >= 0.0F ? "HEATER" : "CHILLER", x_a + x_r + (double)pos.getX(), 0.775D + (double)pos.getY(), z_a + z_r + (double)pos.getZ(), 0.0D, 0.05D, 0.0D);
      }

   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   @SideOnly(Side.CLIENT)
   public int getPackedLightmapCoords(IBlockState state, IBlockAccess source, BlockPos pos) {
      return ((Boolean)state.getValue(ENABLED)).booleanValue() ? 15728880 : super.getPackedLightmapCoords(state, source, pos);
   }

   public TileEntity createNewTileEntity(World world, int meta) {
      return new TileEntityTemperature();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(ENABLED, Boolean.valueOf(meta > 0));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Boolean)state.getValue(ENABLED)).booleanValue() ? 1 : 0;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{ENABLED});
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   @SideOnly(Side.CLIENT)
   public float getAmbientOcclusionLightValue(IBlockState state) {
      return 1.0F;
   }

   public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
      return ((Boolean)state.getValue(ENABLED)).booleanValue() ? 7 : 0;
   }
}
