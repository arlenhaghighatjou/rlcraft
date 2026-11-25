package vazkii.quark.world.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkPane;

public class BlockCrystalPane extends BlockQuarkPane {
   public static final PropertyEnum<BlockCrystal.Variants> VARIANT = PropertyEnum.create("variant", BlockCrystal.Variants.class);

   private static String[] buildVariants() {
      BlockCrystal.Variants[] variantTypes = BlockCrystal.Variants.values();
      String[] variants = new String[variantTypes.length];

      for(int i = 0; i < variantTypes.length; ++i) {
         variants[i] = variantTypes[i].getName() + "_pane";
      }

      return variants;
   }

   public BlockCrystalPane() {
      super("crystal_pane", Material.GLASS, buildVariants());
      this.setHardness(3.0F);
      this.setSoundType(SoundType.GLASS);
      this.setLightLevel(0.73333335F);
      this.setLightOpacity(0);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setHarvestLevel("pickaxe", 0);
   }

   @Nonnull
   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{NORTH, EAST, WEST, SOUTH, VARIANT});
   }

   @SideOnly(Side.CLIENT)
   public IStateMapper getStateMapper() {
      return (new Builder()).withName(VARIANT).withSuffix("_pane").ignore(this.getIgnoredProperties()).build();
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockCrystal.Variants)state.getValue(VARIANT)).ordinal();
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      BlockCrystal.Variants[] variantTypes = BlockCrystal.Variants.values();
      return this.getDefaultState().withProperty(VARIANT, variantTypes[meta % variantTypes.length]);
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   @Nullable
   public float[] getBeaconColorMultiplier(IBlockState state, World world, BlockPos pos, BlockPos beaconPos) {
      return ((BlockCrystal.Variants)state.getValue(VARIANT)).color;
   }

   @Nonnull
   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public boolean canPaneConnectTo(IBlockAccess world, BlockPos pos, EnumFacing dir) {
      return super.canPaneConnectTo(world, pos, dir) || world.getBlockState(pos.offset(dir)).getBlock() instanceof BlockCrystal;
   }
}
