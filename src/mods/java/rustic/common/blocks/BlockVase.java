package rustic.common.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.tileentity.TileEntityVase;
import rustic.core.Rustic;

public class BlockVase extends BlockBase implements ITileEntityProvider {
   public static final int GUI_ID = 1;
   public static final int MIN_VARIANT = 0;
   public static final int MAX_VARIANT = 5;
   public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 5);
   protected static final AxisAlignedBB VASE_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

   public BlockVase() {
      super(Material.ROCK, "vase", false);
      this.setHardness(0.5F);
      ItemBlock item = new ItemBlock(this) {
         public int getMetadata(int damage) {
            return damage;
         }
      };
      item.setMaxDamage(0);
      item.setRegistryName(this.getRegistryName());
      this.register(item);
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      super.eventReceived(state, worldIn, pos, id, param);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityVase();
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityVase) {
         ((TileEntityVase)tileentity).breakBlock(worldIn, pos, state);
      }

      worldIn.removeTileEntity(pos);
      super.breakBlock(worldIn, pos, state);
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (world.isRemote) {
         return true;
      } else {
         TileEntity te = world.getTileEntity(pos);
         if (!(te instanceof TileEntityVase)) {
            return false;
         } else {
            player.openGui(Rustic.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
         }
      }
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return VASE_AABB;
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(VARIANT, Integer.valueOf(meta));
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityVase) {
            ((TileEntityVase)tileentity).setCustomName(stack.getDisplayName());
            ((TileEntityVase)tileentity).markDirty();
         }
      }

   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(VARIANT)).intValue();
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state.withProperty(VARIANT, Integer.valueOf(0)));
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, Integer.valueOf(meta));
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{VARIANT});
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return side == EnumFacing.DOWN ? BlockFaceShape.CENTER_BIG : BlockFaceShape.UNDEFINED;
   }

   public void initModel() {
      for(int i = 0; i <= 5; ++i) {
         ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(this.getRegistryName().toString() + "_" + i, "inventory"));
      }

   }
}
