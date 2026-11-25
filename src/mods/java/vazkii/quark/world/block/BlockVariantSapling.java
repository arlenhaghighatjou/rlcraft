package vazkii.quark.world.block;

import java.util.Locale;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.event.terraingen.TerrainGen;
import vazkii.arl.item.ItemModBlock;
import vazkii.quark.base.block.BlockQuarkBush;
import vazkii.quark.world.world.tree.WorldGenSakuraTree;
import vazkii.quark.world.world.tree.WorldGenSwampTree;

public class BlockVariantSapling extends BlockQuarkBush implements IGrowable {
   public static final PropertyEnum<BlockVariantSapling.Variant> VARIANT = PropertyEnum.create("variant", BlockVariantSapling.Variant.class);
   public static final PropertyInteger STAGE = BlockSapling.STAGE;
   protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.1D, 0.0D, 0.1D, 0.9D, 0.8D, 0.9D);
   private static final String[] VARIANTS = new String[]{"swamp_sapling", "sakura_sapling"};

   public BlockVariantSapling() {
      super("variant_sapling", VARIANTS);
      this.setHardness(0.0F);
      this.setSoundType(SoundType.PLANT);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockVariantSapling.Variant.SWAMP_SAPLING).withProperty(STAGE, Integer.valueOf(0)));
      this.setCreativeTab(CreativeTabs.DECORATIONS);
   }

   public ItemBlock createItemInstance(ResourceLocation regName) {
      return new ItemModBlock(this, regName) {
         public int getItemBurnTime(ItemStack itemStack) {
            return 100;
         }
      };
   }

   public void updateTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         super.updateTick(worldIn, pos, state, rand);
         if (!worldIn.isAreaLoaded(pos, 1)) {
            return;
         }

         if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
            this.grow(worldIn, pos, state, rand);
         }
      }

   }

   public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (((Integer)state.getValue(STAGE)).intValue() == 0) {
         worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
      } else {
         this.generateTree(worldIn, pos, state, rand);
      }

   }

   public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
         WorldGenAbstractTree generator = (WorldGenAbstractTree)(state.getValue(VARIANT) == BlockVariantSapling.Variant.SWAMP_SAPLING ? new WorldGenSwampTree(false) : new WorldGenSakuraTree(true));
         worldIn.setBlockToAir(pos);
         if (!generator.generate(worldIn, rand, pos)) {
            worldIn.setBlockState(pos, state, 4);
         }

      }
   }

   public boolean canGrow(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, boolean isClient) {
      return true;
   }

   public boolean canUseBonemeal(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      return (double)worldIn.rand.nextFloat() < 0.45D;
   }

   public void grow(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      this.grow(worldIn, pos, state, rand);
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return SAPLING_AABB;
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockVariantSapling.Variant.values()[meta & 1]).withProperty(STAGE, Integer.valueOf((meta & 8) >> 3));
   }

   public int getMetaFromState(IBlockState state) {
      int i = ((BlockVariantSapling.Variant)state.getValue(VARIANT)).ordinal() & 1;
      i = i | ((Integer)state.getValue(STAGE)).intValue() << 3;
      return i;
   }

   public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
      return EnumPlantType.Plains;
   }

   public int damageDropped(IBlockState state) {
      return ((BlockVariantSapling.Variant)state.getValue(VARIANT)).ordinal();
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT, STAGE});
   }

   public IProperty getVariantProp() {
      return VARIANT;
   }

   public Class getVariantEnum() {
      return BlockVariantSapling.Variant.class;
   }

   public IProperty[] getIgnoredProperties() {
      return new IProperty[]{STAGE};
   }

   public boolean useBlockstateForItem() {
      return false;
   }

   private static enum Variant implements IStringSerializable {
      SWAMP_SAPLING,
      SAKURA_SAPLING;

      private Variant() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
