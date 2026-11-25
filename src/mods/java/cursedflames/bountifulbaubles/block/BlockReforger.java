package cursedflames.bountifulbaubles.block;

import cursedflames.bountifulbaubles.BountifulBaubles;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockReforger extends GenericTileBlock {
   public static final int GUI_ID = 1;
   public static final PropertyDirection FACING = PropertyDirection.create("facing");

   public BlockReforger() {
      super("bountifulbaubles", "reforger", TileReforger.class, BountifulBaubles.TAB, Material.IRON, 3.0F, 100.0F);
      this.setLightLevel(0.8125F).setLightOpacity(0);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (world.isRemote) {
         return true;
      } else {
         TileEntity te = world.getTileEntity(pos);
         if (!(te instanceof TileReforger)) {
            return false;
         } else {
            player.openGui(BountifulBaubles.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
         }
      }
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      if (!world.isRemote) {
         TileEntity te = world.getTileEntity(pos);
         if (te instanceof TileReforger) {
            ItemStack stack = ((IItemHandler)((TileReforger)te).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null)).getStackInSlot(0);
            world.spawnEntity(new EntityItem(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), stack));
         }
      }

      super.breakBlock(world, pos, state);
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return NULL_AABB;
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      try {
         return (TileEntity)this.tileEntity.newInstance();
      } catch (IllegalAccessException | InstantiationException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
      super.onBlockPlacedBy(world, pos, state, entity, stack);
      world.setBlockState(pos, state.withProperty(FACING, EnumFacing.getFacingFromVector((float)(entity.posX - (double)pos.getX()), 0.0F, (float)(entity.posZ - (double)pos.getZ()))), 2);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getIndex();
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING});
   }
}
