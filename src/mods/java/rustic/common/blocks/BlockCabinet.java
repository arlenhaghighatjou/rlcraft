package rustic.common.blocks;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.renderer.CabinetRenderer;
import rustic.common.tileentity.TileEntityCabinet;
import rustic.core.Rustic;

public class BlockCabinet extends BlockRotatable implements ITileEntityProvider {
   public static final PropertyBool MIRROR = PropertyBool.create("mirror");
   public static final PropertyBool TOP = PropertyBool.create("top");
   public static final PropertyBool BOTTOM = PropertyBool.create("bottom");
   public static final int GUI_ID = 3;

   public BlockCabinet() {
      super(Material.WOOD, "cabinet");
      this.setHardness(2.5F);
      this.setSoundType(SoundType.WOOD);
      this.setDefaultState(this.getDefaultState().withProperty(MIRROR, Boolean.valueOf(false)).withProperty(TOP, Boolean.valueOf(false)).withProperty(BOTTOM, Boolean.valueOf(false)));
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
      super.addInformation(stack, player, tooltip, advanced);
      if (stack.hasTagCompound()) {
         NBTTagCompound nbttagcompound = stack.getTagCompound();
         if (nbttagcompound.hasKey("material", 10)) {
            ItemStack material = new ItemStack(nbttagcompound.getCompoundTag("material"));
            if (!material.isEmpty()) {
               tooltip.add(material.getDisplayName());
            }
         }
      }

   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
      super.eventReceived(state, worldIn, pos, id, param);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (world.isRemote) {
         return true;
      } else {
         TileEntity te = world.getTileEntity(pos);
         if (!(te instanceof TileEntityCabinet)) {
            return false;
         } else if (facing == state.getValue(FACING)) {
            player.openGui(Rustic.instance, 3, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
         } else {
            return false;
         }
      }
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityCabinet) {
         ((TileEntityCabinet)tileentity).breakBlock(worldIn, pos, state);
      }

      worldIn.removeTileEntity(pos);
      if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockCabinet && ((Boolean)worldIn.getBlockState(pos.up()).getValue(TOP)).booleanValue()) {
         worldIn.setBlockState(pos.up(), worldIn.getBlockState(pos.up()).withProperty(TOP, Boolean.valueOf(false)));
      }

      super.breakBlock(worldIn, pos, state);
   }

   public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      ArrayList<ItemStack> items = new ArrayList();
      return items;
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityCabinet();
   }

   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      EnumFacing f = placer.getHorizontalFacing().getOpposite();
      boolean m = false;
      boolean t = false;
      if (f.equals(EnumFacing.NORTH)) {
         m = (double)hitX >= 0.5D;
      } else if (f.equals(EnumFacing.SOUTH)) {
         m = (double)hitX < 0.5D;
      } else if (f.equals(EnumFacing.EAST)) {
         m = (double)hitZ >= 0.5D;
      } else {
         m = (double)hitZ < 0.5D;
      }

      t = worldIn.getBlockState(pos.down()).getBlock() instanceof BlockCabinet && !((Boolean)worldIn.getBlockState(pos.down()).getValue(TOP)).booleanValue() && ((Boolean)worldIn.getBlockState(pos.down()).getValue(MIRROR)).booleanValue() == m && ((EnumFacing)worldIn.getBlockState(pos.down()).getValue(FACING)).equals(f);
      if (t && worldIn.getTileEntity(pos.down()) instanceof TileEntityCabinet) {
         ((TileEntityCabinet)worldIn.getTileEntity(pos.down())).doubleCabinetHandler = null;
      }

      return this.getDefaultState().withProperty(FACING, f).withProperty(MIRROR, Boolean.valueOf(m)).withProperty(TOP, Boolean.valueOf(t));
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (stack.hasTagCompound()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityCabinet) {
            TileEntityCabinet te = (TileEntityCabinet)tileentity;
            if (stack.hasDisplayName()) {
               te.setCustomName(stack.getDisplayName());
            }

            NBTTagCompound tag = stack.getTagCompound();
            if (tag.hasKey("material")) {
               ItemStack material = new ItemStack(tag.getCompoundTag("material"));
               te.material = material;
            }

            te.markDirty();
         }
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState state = this.getDefaultState().withProperty(MIRROR, Boolean.valueOf((meta & 4) > 0));
      state = state.withProperty(TOP, Boolean.valueOf((meta & 8) > 0));
      return state.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
   }

   public int getMetaFromState(IBlockState state) {
      int meta = 0;
      if (((Boolean)state.getValue(TOP)).booleanValue()) {
         meta |= 8;
      }

      if (((Boolean)state.getValue(MIRROR)).booleanValue()) {
         meta |= 4;
      }

      meta = meta | 5 - ((EnumFacing)state.getValue(FACING)).getIndex();
      return meta;
   }

   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      state = state.withProperty(BOTTOM, Boolean.valueOf(!((Boolean)state.getValue(TOP)).booleanValue() && worldIn.getBlockState(pos.up()).getBlock() instanceof BlockCabinet && ((Boolean)worldIn.getBlockState(pos.up()).getValue(TOP)).booleanValue()));
      return state;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, MIRROR, TOP, BOTTOM});
   }

   @SideOnly(Side.CLIENT)
   public void initModel() {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
      ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCabinet.class, new CabinetRenderer());
      Item.getItemFromBlock(this).setTileEntityItemStackRenderer(new CabinetRenderer.CabinetTEISR());
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return side == state.getValue(FACING) ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
   }
}
