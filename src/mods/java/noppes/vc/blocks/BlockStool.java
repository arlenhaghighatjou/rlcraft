package noppes.vc.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.blocks.tiles.TileStool;

public class BlockStool extends BlockBasicRotated {
   public static final AxisAlignedBB AABB_NORMAL = new AxisAlignedBB(0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.8999999761581421D, 0.6000000238418579D, 0.8999999761581421D);

   public BlockStool() {
      super(Blocks.PLANKS);
      this.setSoundType(SoundType.WOOD);
   }

   public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
      return new AxisAlignedBB((double)((float)pos.getX() + 0.1F), (double)pos.getY(), (double)((float)pos.getZ() + 0.1F), (double)((float)pos.getX() + 0.9F), (double)((float)pos.getY() + 0.5F), (double)((float)pos.getZ() + 0.9F));
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      return AABB_NORMAL;
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

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      return BlockChair.MountBlock(world, pos, player);
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      world.setBlockState(pos, state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())), 2);
      super.onBlockPlacedBy(world, pos, state, entity, stack);
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileStool();
   }
}
