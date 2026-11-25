package vazkii.quark.building.block;

import java.util.Locale;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockCarvedWood extends BlockMetaVariants<BlockCarvedWood.Variants> implements IQuarkBlock {
   public BlockCarvedWood() {
      super("carved_wood", Material.WOOD, BlockCarvedWood.Variants.class);
      this.setHardness(2.0F);
      this.setResistance(5.0F);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 20;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 5;
   }

   public static enum Variants implements IStringSerializable {
      CARVED_OAK_WOOD,
      CARVED_SPRUCE_WOOD,
      CARVED_BIRCH_WOOD,
      CARVED_JUNGLE_WOOD,
      CARVED_ACACIA_WOOD,
      CARVED_DARK_OAK_WOOD;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
