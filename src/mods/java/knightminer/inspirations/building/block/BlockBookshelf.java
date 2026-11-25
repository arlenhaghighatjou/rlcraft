package knightminer.inspirations.building.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import knightminer.inspirations.Inspirations;
import knightminer.inspirations.building.InspirationsBuilding;
import knightminer.inspirations.building.tileentity.TileBookshelf;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.PropertyUnlistedInteger;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.util.TextureBlockUtil;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.block.BlockInventory;
import slimeknights.mantle.property.PropertyString;

public class BlockBookshelf extends BlockInventory implements ITileEntityProvider {
   public static final PropertyEnum<BlockBookshelf.BookshelfType> TYPE = PropertyEnum.create("type", BlockBookshelf.BookshelfType.class);
   public static final PropertyDirection FACING = BlockHorizontal.FACING;
   public static final PropertyString TEXTURE = TextureBlockUtil.TEXTURE_PROP;
   public static final IUnlistedProperty<Integer> BOOKS = new PropertyUnlistedInteger("books");
   private static final ImmutableMap<EnumFacing, AxisAlignedBB> BOUNDS;
   private static final ImmutableMap<EnumFacing, AxisAlignedBB[]> TRACE_BOUNDS;

   public BlockBookshelf() {
      super(Material.WOOD);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setCreativeTab(CreativeTabs.DECORATIONS);
      this.setHardness(2.0F);
      this.setResistance(5.0F);
      this.setSoundType(SoundType.WOOD);
   }

   protected ExtendedBlockState createBlockState() {
      return new ExtendedBlockState(this, new IProperty[]{TYPE, FACING}, new IUnlistedProperty[]{TEXTURE, BOOKS});
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPE, BlockBookshelf.BookshelfType.fromMeta(meta & 3)).withProperty(FACING, EnumFacing.getHorizontal(meta >> 2));
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getHorizontalIndex() << 2 | ((BlockBookshelf.BookshelfType)state.getValue(TYPE)).getMeta();
   }

   /** @deprecated */
   @Deprecated
   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getStateFromMeta(meta).withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(world, pos, state, placer, stack);
      TextureBlockUtil.placeTextureBlock(world, pos, stack);
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileBookshelf();
   }

   protected boolean openGui(EntityPlayer player, World world, BlockPos pos) {
      player.openGui(Inspirations.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
      return true;
   }

   public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float clickX, float clickY, float clickZ) {
      EnumFacing facing = (EnumFacing)state.getValue(FACING);
      if (facing != side) {
         return false;
      } else if (player.isSneaking()) {
         return world.isRemote || this.openGui(player, world, pos);
      } else {
         int book = bookClicked(facing, clickX, clickY, clickZ);
         if (book != -1) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileBookshelf) {
               if (((TileBookshelf)te).interact(player, hand, book)) {
                  return true;
               }

               if (InspirationsRegistry.isBook(player.getHeldItemOffhand())) {
                  return false;
               }
            }

            return true;
         } else {
            return world.isRemote || this.openGui(player, world, pos);
         }
      }
   }

   private static int bookClicked(EnumFacing facing, float clickX, float clickY, float clickZ) {
      if ((double)clickY >= 0.0625D && (double)clickY <= 0.9375D) {
         int shelf = 0;
         if ((double)clickY <= 0.4375D) {
            shelf = 7;
         } else if ((double)clickY < 0.5625D) {
            return -1;
         }

         int offX = facing.getFrontOffsetX();
         int offZ = facing.getFrontOffsetZ();
         double x1 = offX == -1 ? 0.625D : 0.0625D;
         double z1 = offZ == -1 ? 0.625D : 0.0625D;
         double x2 = offX == 1 ? 0.375D : 0.9375D;
         double z2 = offZ == 1 ? 0.375D : 0.9375D;
         if ((double)clickX >= x1 && (double)clickX <= x2 && (double)clickZ >= z1 && (double)clickZ <= z2) {
            EnumFacing dir = facing.rotateYCCW();
            double clicked = (double)((float)dir.getFrontOffsetX() * clickX + (float)dir.getFrontOffsetZ() * clickZ) - 0.0625D;
            if (clicked < 0.0D) {
               ++clicked;
            }

            return shelf + Math.min((int)(clicked * 8.0D), 7);
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   /** @deprecated */
   @Deprecated
   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return (AxisAlignedBB)BOUNDS.get(state.getValue(FACING));
   }

   /** @deprecated */
   @Deprecated
   public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
      List<RayTraceResult> list = new ArrayList(6);

      for(AxisAlignedBB bound : (AxisAlignedBB[])TRACE_BOUNDS.get(state.getValue(FACING))) {
         list.add(this.rayTrace(pos, start, end, bound));
      }

      return Util.closestResult(list, end);
   }

   public void breakBlock(World world, BlockPos pos, IBlockState state) {
      if (this.getPower(state, world, pos) > 0) {
         world.notifyNeighborsOfStateChange(pos, this, false);
         world.notifyNeighborsOfStateChange(pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite()), this, false);
      }

      super.breakBlock(world, pos, state);
   }

   /** @deprecated */
   @Deprecated
   public int getWeakPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return this.getPower(state, blockAccess, pos);
   }

   /** @deprecated */
   @Deprecated
   public int getStrongPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
      return state.getValue(FACING) != side ? 0 : this.getPower(state, blockAccess, pos);
   }

   private int getPower(IBlockState state, IBlockAccess blockAccess, BlockPos pos) {
      if (InspirationsBuilding.redstoneBook == null) {
         return 0;
      } else {
         TileEntity te = blockAccess.getTileEntity(pos);
         return te instanceof TileBookshelf ? ((TileBookshelf)te).getPower() : 0;
      }
   }

   /** @deprecated */
   @Deprecated
   public boolean canProvidePower(IBlockState state) {
      return InspirationsBuilding.redstoneBook != null;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   @Nonnull
   public IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
      IExtendedBlockState extendedState = (IExtendedBlockState)state;
      TileEntity te = world.getTileEntity(pos);
      return te instanceof TileBookshelf ? ((TileBookshelf)te).writeExtendedBlockState(extendedState) : super.getExtendedState(state, world, pos);
   }

   /** @deprecated */
   @Deprecated
   public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
      return side == ((EnumFacing)state.getValue(FACING)).getOpposite() ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   /** @deprecated */
   @Deprecated
   public boolean isFullCube(IBlockState state) {
      return false;
   }

   /** @deprecated */
   @Deprecated
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
   public IBlockState withMirror(IBlockState state, Mirror mirror) {
      return state.withRotation(mirror.toRotation((EnumFacing)state.getValue(FACING)));
   }

   public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
      for(BlockBookshelf.BookshelfType type : BlockBookshelf.BookshelfType.values()) {
         TextureBlockUtil.addBlocksFromOredict("slabWood", this, type.getMeta(), list);
      }

   }

   public int damageDropped(IBlockState state) {
      return ((BlockBookshelf.BookshelfType)state.getValue(TYPE)).getMeta();
   }

   @Nonnull
   public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
      return TextureBlockUtil.getBlockItemStack(world, pos, state);
   }

   public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
      this.onBlockDestroyedByPlayer(world, pos, state);
      if (willHarvest) {
         this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
      }

      world.setBlockToAir(pos);
      return false;
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      drops.add(TextureBlockUtil.getBlockItemStack(world, pos, state));
   }

   public float getEnchantPowerBonus(World world, BlockPos pos) {
      if (!Config.bookshelvesBoostEnchanting) {
         return 0.0F;
      } else {
         TileEntity te = world.getTileEntity(pos);
         return te instanceof TileBookshelf ? ((TileBookshelf)te).getEnchantPower() : 0.0F;
      }
   }

   static {
      Builder<EnumFacing, AxisAlignedBB> bounds = ImmutableMap.builder();
      Builder<EnumFacing, AxisAlignedBB[]> builder = ImmutableMap.builder();

      for(EnumFacing side : EnumFacing.HORIZONTALS) {
         int offX = side.getFrontOffsetX();
         int offZ = side.getFrontOffsetZ();
         double x1 = offX == -1 ? 0.5D : 0.0D;
         double z1 = offZ == -1 ? 0.5D : 0.0D;
         double x2 = offX == 1 ? 0.5D : 1.0D;
         double z2 = offZ == 1 ? 0.5D : 1.0D;
         bounds.put(side, new AxisAlignedBB(x1, 0.0D, z1, x2, 1.0D, z2));
         builder.put(side, new AxisAlignedBB[]{new AxisAlignedBB(x1, 0.0D, z1, x2, 0.0625D, z2), new AxisAlignedBB(x1, 0.4375D, z1, x2, 0.5625D, z2), new AxisAlignedBB(x1, 0.9375D, z1, x2, 1.0D, z2), new AxisAlignedBB(offX == -1 ? 0.625D : 0.0D, 0.0D, offZ == -1 ? 0.625D : 0.0D, offX == 1 ? 0.375D : 1.0D, 1.0D, offZ == 1 ? 0.375D : 1.0D), new AxisAlignedBB(x1, 0.0D, z1, offX == 0 ? 0.0625D : x2, 1.0D, offZ == 0 ? 0.0625D : z2), new AxisAlignedBB(offX == 0 ? 0.9375D : x1, 0.0D, offZ == 0 ? 0.9375D : z1, x2, 1.0D, z2)});
      }

      BOUNDS = bounds.build();
      TRACE_BOUNDS = builder.build();
   }

   public static enum BookshelfType implements IStringSerializable {
      NORMAL,
      RAINBOW,
      TOMES,
      ANCIENT;

      private int meta = this.ordinal();

      private BookshelfType() {
      }

      public String getName() {
         return this.name().toLowerCase(Locale.US);
      }

      public int getMeta() {
         return this.meta;
      }

      public static BlockBookshelf.BookshelfType fromMeta(int meta) {
         if (meta < 0 || meta >= values().length) {
            meta = 0;
         }

         return values()[meta];
      }
   }
}
