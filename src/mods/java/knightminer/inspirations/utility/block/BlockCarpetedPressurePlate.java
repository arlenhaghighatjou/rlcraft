package knightminer.inspirations.utility.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockPressurePlate.Sensitivity;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCarpetedPressurePlate extends BlockPressurePlate {
   protected static final AxisAlignedBB PRESSED_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.078125D, 1.0D);
   protected static final AxisAlignedBB UNPRESSED_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.09375D, 1.0D);
   public static final PropertyEnum<EnumDyeColor> COLOR1;
   public static final PropertyEnum<EnumDyeColor> COLOR2;
   private boolean lastHalf;

   public BlockCarpetedPressurePlate(boolean lastHalf) {
      super(Material.ROCK, Sensitivity.MOBS);
      this.setHardness(0.5F);
      this.setSoundType(SoundType.CLOTH);
      this.lastHalf = lastHalf;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      boolean flag = this.getRedstoneStrength(state) > 0;
      return flag ? PRESSED_AABB : UNPRESSED_AABB;
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(Blocks.CARPET);
   }

   public int damageDropped(IBlockState state) {
      return ((EnumDyeColor)state.getValue(this.lastHalf ? COLOR2 : COLOR1)).getMetadata();
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      drops.add(new ItemStack(Blocks.CARPET, 1, this.damageDropped(state)));
      drops.add(new ItemStack(Blocks.STONE_PRESSURE_PLATE));
   }

   /** @deprecated */
   @Deprecated
   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(Blocks.CARPET, 1, this.damageDropped(state));
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState state = this.getDefaultState().withProperty(POWERED, Boolean.valueOf((meta & 1) == 1));
      if (this.lastHalf) {
         state = state.withProperty(COLOR2, EnumDyeColor.byMetadata((meta >> 1) + 8));
      } else {
         state = state.withProperty(COLOR1, EnumDyeColor.byMetadata(meta >> 1));
      }

      return state;
   }

   public int getMetaFromState(IBlockState state) {
      int meta = ((Boolean)state.getValue(POWERED)).booleanValue() ? 1 : 0;
      meta = meta + (((EnumDyeColor)state.getValue(this.lastHalf ? COLOR2 : COLOR1)).getMetadata() % 8 << 1);
      return meta;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{POWERED, COLOR1});
   }

   static {
      EnumDyeColor[] colors1 = new EnumDyeColor[8];
      EnumDyeColor[] colors2 = new EnumDyeColor[8];

      for(int i = 0; i < 8; ++i) {
         colors1[i] = EnumDyeColor.byMetadata(i);
         colors2[i] = EnumDyeColor.byMetadata(i + 8);
      }

      COLOR1 = PropertyEnum.create("color", EnumDyeColor.class, colors1);
      COLOR2 = PropertyEnum.create("color", EnumDyeColor.class, colors2);
   }

   public static class BlockCarpetedPressurePlate2 extends BlockCarpetedPressurePlate {
      public BlockCarpetedPressurePlate2() {
         super(true);
      }

      protected BlockStateContainer createBlockState() {
         return new BlockStateContainer(this, new IProperty[]{POWERED, COLOR2});
      }
   }
}
