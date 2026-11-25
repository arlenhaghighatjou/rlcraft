package prospector.traverse.blocks.base;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import prospector.traverse.core.TraverseTab;
import prospector.traverse.shadow.shootingstar.ShootingStar;
import prospector.traverse.shadow.shootingstar.model.ModelCompound;

public abstract class BlockTraverseSlab extends BlockSlab {
   public static final PropertyEnum<BlockTraverseSlab.Variant> VARIANT = PropertyEnum.create("variant", BlockTraverseSlab.Variant.class);
   public final String name;
   public Block halfslab;

   public BlockTraverseSlab(String name, Material material, SoundType soundType) {
      super(material, material.getMaterialMapColor());
      this.name = name;
      IBlockState iblockstate = this.blockState.getBaseState();
      if (!this.isDouble()) {
         iblockstate = iblockstate.withProperty(HALF, EnumBlockHalf.BOTTOM);
         this.setRegistryName(new ResourceLocation("traverse", name + "_slab"));
         this.halfslab = this;
      } else {
         this.setRegistryName(new ResourceLocation("traverse", name + "_double_slab"));
      }

      this.setCreativeTab(TraverseTab.TAB);
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setHarvestLevel("pickaxe", 0);
      this.setHardness(2.0F);
      this.setResistance(10.0F);
      this.setSoundType(soundType);
      this.setDefaultState(iblockstate.withProperty(VARIANT, BlockTraverseSlab.Variant.DEFAULT));
      this.useNeighborBrightness = true;
      ShootingStar.registerModel(new ModelCompound("traverse", this, "slab", new IProperty[0]));
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(this.halfslab);
   }

   public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(this.halfslab);
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockTraverseSlab.Variant.DEFAULT);
      if (!this.isDouble()) {
         iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
      }

      return iblockstate;
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      if (!this.isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP) {
         i |= 8;
      }

      return i;
   }

   protected BlockStateContainer createBlockState() {
      return this.isDouble() ? new BlockStateContainer(this, new IProperty[]{VARIANT}) : new BlockStateContainer(this, new IProperty[]{HALF, VARIANT});
   }

   public String getUnlocalizedName(int meta) {
      return super.getUnlocalizedName();
   }

   public IProperty<?> getVariantProperty() {
      return VARIANT;
   }

   public Comparable<?> getTypeForItem(ItemStack stack) {
      return BlockTraverseSlab.Variant.DEFAULT;
   }

   public static class Double extends BlockTraverseSlab {
      public Double(String name, Material material, SoundType soundType, Block half) {
         super(name, material, soundType);
         this.halfslab = half;
      }

      public boolean isDouble() {
         return true;
      }
   }

   public static class Half extends BlockTraverseSlab {
      public Half(String name, Material material, SoundType soundType) {
         super(name, material, soundType);
      }

      public boolean isDouble() {
         return false;
      }
   }

   public static enum Variant implements IStringSerializable {
      DEFAULT;

      private Variant() {
      }

      public String getName() {
         return "default";
      }
   }
}
