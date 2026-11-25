package vazkii.quark.decoration.block;

import java.util.Locale;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockCustomBookshelf extends BlockMetaVariants<BlockCustomBookshelf.Variants> implements IQuarkBlock {
   public BlockCustomBookshelf() {
      super("custom_bookshelf", Material.WOOD, BlockCustomBookshelf.Variants.class);
      this.setHardness(1.5F);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 20;
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return 5;
   }

   public float getEnchantPowerBonus(World world, BlockPos pos) {
      return 1.0F;
   }

   public int quantityDropped(Random random) {
      return 3;
   }

   public int damageDropped(IBlockState state) {
      return 0;
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.BOOK;
   }

   public static enum Variants implements IStringSerializable {
      BOOKSHELF_SPRUCE,
      BOOKSHELF_BIRCH,
      BOOKSHELF_JUNGLE,
      BOOKSHELF_ACACIA,
      BOOKSHELF_DARK_OAK;

      private Variants() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
