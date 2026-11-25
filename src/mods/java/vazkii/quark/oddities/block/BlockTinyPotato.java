package vazkii.quark.oddities.block;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.oddities.item.ItemBlockTinyPotato;
import vazkii.quark.oddities.tile.TileTinyPotato;

public class BlockTinyPotato extends BlockMod implements IQuarkBlock {
   public static final PropertyEnum<EnumFacing> FACING = BlockHorizontal.FACING;
   private static final AxisAlignedBB AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.375D, 0.625D);

   public BlockTinyPotato() {
      super("tiny_potato", Material.CLOTH, new String[0]);
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setHardness(0.25F);
      this.setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH));
   }

   public ItemBlock createItemBlock(ResourceLocation res) {
      return new ItemBlockTinyPotato(this, res);
   }

   @Nonnull
   public BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING});
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
   }

   public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      TileEntity inv = world.getTileEntity(pos);
      if (inv instanceof IInventory) {
         InventoryHelper.dropInventoryItems(world, pos, (IInventory)inv);
      }

      super.breakBlock(world, pos, state);
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
      return AABB;
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing par6, float par7, float par8, float par9) {
      TileEntity tile = world.getTileEntity(pos);
      if (tile instanceof TileTinyPotato) {
         ((TileTinyPotato)tile).interact(player, hand, player.getHeldItem(hand), par6);
         world.spawnParticle(EnumParticleTypes.HEART, (double)pos.getX() + AABB.minX + Math.random() * (AABB.maxX - AABB.minX), (double)pos.getY() + AABB.maxY, (double)pos.getZ() + AABB.minZ + Math.random() * (AABB.maxZ - AABB.minZ), 0.0D, 0.0D, 0.0D, new int[0]);
      }

      return true;
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()));
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileTinyPotato) {
         if (stack.hasDisplayName()) {
            ((TileTinyPotato)te).name = stack.getDisplayName();
         }

         ((TileTinyPotato)te).angery = ItemNBTHelper.getBoolean(stack, "angery", false);
      }

   }

   public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
      if (willHarvest) {
         this.onBlockHarvested(world, pos, state, player);
         return true;
      } else {
         return super.removedByPlayer(state, world, pos, player, false);
      }
   }

   public void harvestBlock(@Nonnull World world, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, TileEntity te, ItemStack stack) {
      super.harvestBlock(world, player, pos, state, te, stack);
      world.setBlockToAir(pos);
   }

   public void getDrops(@Nonnull NonNullList<ItemStack> list, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
      TileEntity tile = world.getTileEntity(pos);
      if (tile != null) {
         ItemStack stack = new ItemStack(this);
         String name = ((TileTinyPotato)tile).name;
         if (!name.isEmpty()) {
            stack.setStackDisplayName(name);
         }

         if (((TileTinyPotato)tile).angery) {
            ItemNBTHelper.setBoolean(stack, "angery", true);
         }

         list.add(stack);
      }

   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @Nonnull
   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   @Nonnull
   public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
      return new TileTinyPotato();
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return BlockFaceShape.UNDEFINED;
   }
}
