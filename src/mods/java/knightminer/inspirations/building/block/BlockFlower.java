package knightminer.inspirations.building.block;

import java.util.Locale;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFlower extends BlockBush implements IGrowable {
   public static final PropertyEnum<BlockFlower.FlowerType> TYPE = PropertyEnum.create("type", BlockFlower.FlowerType.class);

   public BlockFlower() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockFlower.FlowerType.ROSE));
      this.setHardness(0.0F);
      this.setSoundType(SoundType.PLANT);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{TYPE});
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPE, BlockFlower.FlowerType.fromMeta(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockFlower.FlowerType)state.getValue(TYPE)).getMeta();
   }

   public int damageDropped(IBlockState state) {
      return ((BlockFlower.FlowerType)state.getValue(TYPE)).getMeta();
   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
      for(BlockFlower.FlowerType type : BlockFlower.FlowerType.values()) {
         items.add(new ItemStack(this, 1, type.getMeta()));
      }

   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return super.getBoundingBox(state, source, pos).offset(state.getOffset(source, pos));
   }

   public EnumOffsetType getOffsetType() {
      return EnumOffsetType.XZ;
   }

   public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
      return state.getValue(TYPE) != BlockFlower.FlowerType.CYAN;
   }

   public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      EnumPlantType type = ((BlockFlower.FlowerType)state.getValue(TYPE)).getDouble();
      if (type != null) {
         if (worldIn.isAirBlock(pos.up())) {
            Blocks.DOUBLE_PLANT.placeAt(worldIn, pos, type, 2);
         }

      }
   }

   public static enum FlowerType implements IStringSerializable {
      ROSE(EnumPlantType.ROSE),
      SYRINGA(EnumPlantType.SYRINGA),
      PAEONIA(EnumPlantType.PAEONIA),
      CYAN((EnumPlantType)null);

      private final EnumPlantType big;
      private final int meta = this.ordinal();

      private FlowerType(EnumPlantType big) {
         this.big = big;
      }

      public int getMeta() {
         return this.meta;
      }

      public static BlockFlower.FlowerType fromMeta(int meta) {
         if (meta < 0 || meta >= values().length) {
            meta = 0;
         }

         return values()[meta];
      }

      @Nullable
      public EnumPlantType getDouble() {
         return this.big;
      }

      @Nullable
      public static BlockFlower.FlowerType fromDouble(@Nonnull EnumPlantType big) {
         for(BlockFlower.FlowerType type : values()) {
            if (big == type.getDouble()) {
               return type;
            }
         }

         return null;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.US);
      }
   }
}
