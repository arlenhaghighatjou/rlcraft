package svenhjol.charm.automation.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneLight;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import svenhjol.charm.automation.feature.VariableRedstoneLamp;
import svenhjol.meson.iface.IMesonBlock;

public class BlockVariableRedstoneLight extends BlockRedstoneLight implements IMesonBlock {
   public static PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

   public BlockVariableRedstoneLight() {
      super(false);
      this.register();
      this.setCreativeTab(CreativeTabs.REDSTONE);
      this.setSoundType(SoundType.GLASS);
      this.setHardness(0.3F);
      this.setLightLevel(1.0F);
   }

   public String getModId() {
      return "charm";
   }

   public String getName() {
      return "variable_redstone_lamp";
   }

   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         if (((Integer)state.getValue(LEVEL)).intValue() > 0 && !worldIn.isBlockPowered(pos)) {
            this.updateState(worldIn, pos, state, 0);
         } else if (worldIn.isBlockPowered(pos)) {
            int power = worldIn.isBlockIndirectlyGettingPowered(pos);
            this.updateState(worldIn, pos, state, power);
         }
      }

   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.isRemote) {
         if (((Integer)state.getValue(LEVEL)).intValue() > 0 && !worldIn.isBlockPowered(pos)) {
            worldIn.scheduleUpdate(pos, this, 4);
         } else if (worldIn.isBlockPowered(pos)) {
            int power = worldIn.isBlockPowered(pos) ? worldIn.isBlockIndirectlyGettingPowered(pos) : 0;
            this.updateState(worldIn, pos, state, power);
         }
      }

   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && ((Integer)state.getValue(LEVEL)).intValue() > 0) {
         int power = worldIn.isBlockPowered(pos) ? worldIn.isBlockIndirectlyGettingPowered(pos) : 0;
         this.updateState(worldIn, pos, state, power);
      }

   }

   public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
      return ((Integer)state.getValue(LEVEL)).intValue();
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(LEVEL)).intValue();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(LEVEL, Integer.valueOf(meta));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{LEVEL});
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(this);
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(VariableRedstoneLamp.block);
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return new ItemStack(this);
   }

   private void updateState(World world, BlockPos pos, IBlockState state, int power) {
      world.setBlockState(pos, state.withProperty(LEVEL, Integer.valueOf(power)), 2);
      world.checkLight(pos);
   }
}
