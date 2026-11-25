package noppes.vc.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import noppes.vc.VariedCommodities;
import noppes.vc.blocks.tiles.TileCarpentryBench;
import noppes.vc.constants.GuiType;

public class BlockCarpentryBench extends BlockBasicContainer {
   public static final PropertyInteger TYPE = PropertyInteger.create("type", 0, 1);
   public static final PropertyInteger ROTATION = PropertyInteger.create("rotation", 0, 3);

   public BlockCarpentryBench() {
      super(Material.WOOD);
      this.setSoundType(SoundType.WOOD);
   }

   public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!par1World.isRemote) {
         player.openGui(VariedCommodities.Instance, GuiType.CARPENTRY_BENCH.ordinal(), par1World, pos.getX(), pos.getY(), pos.getZ());
      }

      return true;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public int damageDropped(IBlockState state) {
      return ((Integer)state.getValue(TYPE)).intValue();
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(ROTATION)).intValue() + ((Integer)state.getValue(TYPE)).intValue() * 4;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPE, Integer.valueOf(meta.intValue() >> 2)).withProperty(ROTATION, Integer.valueOf(meta % 4));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{TYPE, ROTATION});
   }

   public void getSubBlocks(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
      par3List.add(new ItemStack(this, 1, 0));
      par3List.add(new ItemStack(this, 1, 1));
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      int var6 = MathHelper.floor((double)(entity.rotationYaw / 90.0F) + 0.5D) & 3;
      world.setBlockState(pos, state.withProperty(TYPE, Integer.valueOf(stack.getItemDamage())).withProperty(ROTATION, Integer.valueOf(var6)), 2);
   }

   public TileEntity createNewTileEntity(World var1, int var2) {
      return new TileCarpentryBench();
   }
}
