package knightminer.inspirations.building.block;

import java.util.Locale;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMulch extends BlockFalling {
   protected static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);
   public static final PropertyEnum<BlockMulch.MulchColor> COLOR = PropertyEnum.create("color", BlockMulch.MulchColor.class);

   public BlockMulch() {
      super(Material.WOOD);
      this.setHarvestLevel("shovel", -1);
      this.setSoundType(SoundType.GROUND);
      this.setHardness(0.6F);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{COLOR});
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(COLOR, BlockMulch.MulchColor.fromMeta(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockMulch.MulchColor)state.getValue(COLOR)).getMeta();
   }

   @SideOnly(Side.CLIENT)
   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
      for(BlockMulch.MulchColor type : BlockMulch.MulchColor.values()) {
         list.add(new ItemStack(this, 1, type.getMeta()));
      }

   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
      return plantable.getPlantType(world, pos.offset(direction)) == EnumPlantType.Plains && !(plantable instanceof BlockSapling);
   }

   public static enum MulchColor implements IStringSerializable {
      PLAIN,
      BROWN,
      YELLOW,
      AMBER,
      RUBY,
      RED,
      BLACK,
      BLUE;

      private int meta = this.ordinal();

      private MulchColor() {
      }

      public int getMeta() {
         return this.meta;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.US);
      }

      public static BlockMulch.MulchColor fromMeta(int i) {
         if (i < 0 || i >= values().length) {
            i = 0;
         }

         return values()[i];
      }
   }
}
