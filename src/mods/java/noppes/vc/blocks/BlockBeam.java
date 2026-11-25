package noppes.vc.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.blocks.tiles.TileBasicRotation;
import noppes.vc.blocks.tiles.TileBeam;

public class BlockBeam extends BlockBasicRotated {
   public static final AxisAlignedBB AABB0 = new AxisAlignedBB(0.33000001311302185D, 0.33000001311302185D, 0.25D, 0.6700000166893005D, 0.6700000166893005D, 1.0D);
   public static final AxisAlignedBB AABB1 = new AxisAlignedBB(0.0D, 0.33000001311302185D, 0.33000001311302185D, 0.75D, 0.6700000166893005D, 0.6700000166893005D);
   public static final AxisAlignedBB AABB2 = new AxisAlignedBB(0.33000001311302185D, 0.33000001311302185D, 0.0D, 0.6700000166893005D, 0.6700000166893005D, 0.75D);
   public static final AxisAlignedBB AABB3 = new AxisAlignedBB(0.25D, 0.33000001311302185D, 0.33000001311302185D, 1.0D, 0.6700000166893005D, 0.6700000166893005D);

   public BlockBeam() {
      super(Blocks.PLANKS);
      this.setSoundType(SoundType.WOOD);
   }

   public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
      par3List.add(new ItemStack(this, 1, 0));
      par3List.add(new ItemStack(this, 1, 1));
      par3List.add(new ItemStack(this, 1, 2));
      par3List.add(new ItemStack(this, 1, 3));
      par3List.add(new ItemStack(this, 1, 4));
      par3List.add(new ItemStack(this, 1, 5));
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(DAMAGE)).intValue();
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      TileEntity tileentity = world.getTileEntity(pos);
      if (!(tileentity instanceof TileBasicRotation)) {
         return super.getBoundingBox(state, world, pos);
      } else {
         TileBasicRotation tile = (TileBasicRotation)tileentity;
         if (tile.rotation == 0) {
            return AABB0;
         } else if (tile.rotation == 2) {
            return AABB2;
         } else if (tile.rotation == 3) {
            return AABB3;
         } else {
            return tile.rotation == 1 ? AABB1 : super.getBoundingBox(state, world, pos);
         }
      }
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      world.setBlockState(pos, state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())), 2);
      super.onBlockPlacedBy(world, pos, state, entity, stack);
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileBeam();
   }
}
