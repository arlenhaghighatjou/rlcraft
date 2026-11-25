package vazkii.quark.world.block;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.feature.TreeVariants;

public class BlockVariantLeaves extends BlockLeaves implements IQuarkBlock, IBlockColorProvider {
   public static final PropertyEnum<BlockVariantLeaves.Variant> VARIANT = PropertyEnum.create("variant", BlockVariantLeaves.Variant.class);
   private final String[] variants = new String[]{"swamp_leaves", "sakura_leaves"};
   private final String bareName = "variant_leaves";

   public BlockVariantLeaves() {
      this.setUnlocalizedName(this.bareName);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockVariantLeaves.Variant.SWAMP_LEAVES).withProperty(CHECK_DECAY, Boolean.valueOf(true)).withProperty(DECAYABLE, Boolean.valueOf(true)));
   }

   @Nonnull
   public Block setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      this.register(name);
      return this;
   }

   public int damageDropped(IBlockState state) {
      return ((BlockVariantLeaves.Variant)state.getValue(VARIANT)).ordinal();
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(TreeVariants.variant_sapling);
   }

   @Nonnull
   public ItemStack getItem(World worldIn, BlockPos pos, @Nonnull IBlockState state) {
      return new ItemStack(this, 1, ((BlockVariantLeaves.Variant)state.getValue(VARIANT)).ordinal());
   }

   @Nonnull
   protected ItemStack getSilkTouchDrop(@Nonnull IBlockState state) {
      return new ItemStack(this, 1, ((BlockVariantLeaves.Variant)state.getValue(VARIANT)).ordinal());
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockVariantLeaves.Variant.values()[meta & 1]).withProperty(DECAYABLE, Boolean.valueOf((meta & 4) != 0)).withProperty(CHECK_DECAY, Boolean.valueOf((meta & 8) > 0));
   }

   public int getMetaFromState(IBlockState state) {
      int i = ((BlockVariantLeaves.Variant)state.getValue(VARIANT)).ordinal() & 1;
      if (((Boolean)state.getValue(DECAYABLE)).booleanValue()) {
         i |= 4;
      }

      if (((Boolean)state.getValue(CHECK_DECAY)).booleanValue()) {
         i |= 8;
      }

      return i;
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT, CHECK_DECAY, DECAYABLE});
   }

   @Nonnull
   public EnumType getWoodType(int meta) {
      return EnumType.OAK;
   }

   public void harvestBlock(@Nonnull World worldIn, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
         player.addStat(StatList.getBlockStats(this));
         spawnAsEntity(worldIn, pos, this.getSilkTouchDrop(state));
      } else {
         super.harvestBlock(worldIn, player, pos, state, te, stack);
      }

   }

   @Nonnull
   public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
      return NonNullList.withSize(1, new ItemStack(this, 1, ((BlockVariantLeaves.Variant)world.getBlockState(pos).getValue(VARIANT)).ordinal()));
   }

   public String getBareName() {
      return this.bareName;
   }

   public String[] getVariants() {
      return this.variants;
   }

   @SideOnly(Side.CLIENT)
   public ItemMeshDefinition getCustomMeshDefinition() {
      return null;
   }

   public EnumRarity getBlockRarity(ItemStack stack) {
      return EnumRarity.COMMON;
   }

   public IProperty[] getIgnoredProperties() {
      return new IProperty[]{CHECK_DECAY, DECAYABLE};
   }

   public IProperty getVariantProp() {
      return VARIANT;
   }

   public Class getVariantEnum() {
      return BlockVariantLeaves.Variant.class;
   }

   @SideOnly(Side.CLIENT)
   public IItemColor getItemColor() {
      return (stack, i) -> {
         return stack.getMetadata() == 0 ? 6975545 : 16777215;
      };
   }

   @SideOnly(Side.CLIENT)
   public IBlockColor getBlockColor() {
      return (state, world, pos, i) -> {
         return state.getValue(VARIANT) == BlockVariantLeaves.Variant.SWAMP_LEAVES ? 6975545 : 16777215;
      };
   }

   public static enum Variant implements IStringSerializable {
      SWAMP_LEAVES,
      SAKURA_LEAVES;

      private Variant() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
