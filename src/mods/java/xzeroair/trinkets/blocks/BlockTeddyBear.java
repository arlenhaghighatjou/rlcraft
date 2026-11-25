package xzeroair.trinkets.blocks;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.blocks.materials.TrinketBlockMaterial;
import xzeroair.trinkets.blocks.tileentities.TileEntityTeddyBear;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.util.interfaces.IsModelLoaded;

public class BlockTeddyBear extends BlockHorizontal implements IsModelLoaded {
   protected static final AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(0.2D, 0.0D, 0.2D, 0.8D, 0.8D, 0.8D);
   protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.2D, 0.0D, 0.15D, 0.8D, 0.8D, 0.85D);
   protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.2D, 0.0D, 0.15D, 0.8D, 0.8D, 0.85D);
   protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.15D, 0.0D, 0.2D, 0.85D, 0.8D, 0.8D);
   protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.15D, 0.0D, 0.2D, 0.85D, 0.8D, 0.8D);
   private static final AxisAlignedBB HITBOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);

   public BlockTeddyBear() {
      super(TrinketBlockMaterial.ClothTeddyBear);
      this.setSoundType(SoundType.CLOTH);
      this.setRegistryName("xat", "teddy_bear");
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setLightLevel(0.0F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public BlockTeddyBear(String name) {
      super(TrinketBlockMaterial.ClothTeddyBear);
      this.setSoundType(SoundType.CLOTH);
      this.setRegistryName("xat", "teddy_bear_" + name);
      this.setUnlocalizedName(this.getRegistryName().toString());
      this.setLightLevel(0.0F);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public TileEntity createTileEntity(World world, IBlockState state) {
      return new TileEntityTeddyBear();
   }

   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, world, tooltip, flagIn);
   }

   public boolean canPlaceBlockAt(World world, BlockPos pos) {
      IBlockState blockBelow = world.getBlockState(pos.down());
      return blockBelow.isSideSolid(world, pos.down(), EnumFacing.UP) ? super.canPlaceBlockAt(world, pos) : false;
   }

   public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
      this.checkAndDropBlock(world, pos, state);
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
   }

   public void updateTick(World world, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      this.checkAndDropBlock(world, pos, state);
   }

   protected void checkAndDropBlock(World world, BlockPos pos, IBlockState state) {
      if (!this.canBlockStay(world, pos, state)) {
         this.dropBlockAsItem(world, pos, state, 0);
         world.setBlockToAir(pos);
      }

   }

   public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
      if (state.getBlock() == this) {
         IBlockState down = world.getBlockState(pos.down());
         if (!down.isSideSolid(world, pos, EnumFacing.UP)) {
            return false;
         }
      }

      return true;
   }

   public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
      return willHarvest ? true : super.removedByPlayer(state, world, pos, player, willHarvest);
   }

   public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
      super.harvestBlock(world, player, pos, state, te, stack);
      world.setBlockToAir(pos);
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      TileEntity TileEntity = worldIn.getTileEntity(pos);
      if (TileEntity instanceof TileEntityTeddyBear) {
         ((TileEntityTeddyBear)TileEntity).setTeddyBear(stack);
      }

   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      TileEntity TileEntity = world.getTileEntity(pos);
      if (TileEntity instanceof TileEntityTeddyBear) {
         ItemStack stack = ((TileEntityTeddyBear)TileEntity).getTeddyBear();
         if (stack != null && !stack.isEmpty()) {
            drops.add(stack);
         }
      } else {
         System.out.println("Something went wrong, Please Report this with the circumstances prior to it happening");
      }

   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return ModItems.trinkets.TrinketTeddyBear;
   }

   public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState state = this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
      return state;
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
      return i;
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      switch((EnumFacing)state.getValue(FACING)) {
      case UP:
      default:
         return DEFAULT_AABB;
      case NORTH:
         return NORTH_AABB;
      case SOUTH:
         return SOUTH_AABB;
      case WEST:
         return WEST_AABB;
      case EAST:
         return EAST_AABB;
      }
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
      return Block.NULL_AABB;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   /** @deprecated */
   @Deprecated
   public IBlockState withRotation(IBlockState state, Rotation rot) {
      return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
   }

   /** @deprecated */
   @Deprecated
   public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
      return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING});
   }

   @SideOnly(Side.CLIENT)
   public void registerModels() {
      ModelLoader.setCustomStateMapper(this, (new Builder()).build());
   }
}
