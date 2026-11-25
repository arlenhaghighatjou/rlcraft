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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.blocks.tiles.TileBanner;
import noppes.vc.blocks.tiles.TileBasicRotation;
import noppes.vc.blocks.tiles.TileSign;

public class BlockSign extends BlockBasicRotated {
   public static final AxisAlignedBB AABB2 = new AxisAlignedBB(0.0D, 0.30000001192092896D, 0.30000001192092896D, 1.0D, 1.0D, 0.699999988079071D);
   public static final AxisAlignedBB AABB1 = new AxisAlignedBB(0.30000001192092896D, 0.30000001192092896D, 0.0D, 0.699999988079071D, 1.0D, 1.0D);

   public BlockSign() {
      super(Blocks.PLANKS);
      this.setSoundType(SoundType.WOOD);
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      world.setBlockState(pos, state.withProperty(DAMAGE, Integer.valueOf(stack.getItemDamage())), 2);
      super.onBlockPlacedBy(world, pos, state, entity, stack);
      TileSign tile = (TileSign)world.getTileEntity(pos);
      tile.time = System.currentTimeMillis();
      if (entity instanceof EntityPlayer && world.isRemote) {
         ((EntityPlayer)entity).sendStatusMessage(new TextComponentTranslation("block.editIcon", new Object[0]), true);
      }

   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      TileBanner tile = (TileBanner)world.getTileEntity(pos);
      return tile.canEdit();
   }

   public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
      par3List.add(new ItemStack(this, 1, 0));
      par3List.add(new ItemStack(this, 1, 1));
      par3List.add(new ItemStack(this, 1, 2));
      par3List.add(new ItemStack(this, 1, 3));
      par3List.add(new ItemStack(this, 1, 4));
      par3List.add(new ItemStack(this, 1, 5));
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      TileEntity tileentity = world.getTileEntity(pos);
      if (!(tileentity instanceof TileBasicRotation)) {
         return super.getBoundingBox(state, world, pos);
      } else {
         TileBasicRotation tile = (TileBasicRotation)tileentity;
         return tile.rotation % 2 == 1 ? AABB1 : AABB2;
      }
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(DAMAGE)).intValue();
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileSign();
   }
}
