package vazkii.quark.decoration.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockChest.Type;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.decoration.feature.VariedChests;
import vazkii.quark.decoration.item.ItemChestBlock;
import vazkii.quark.decoration.tile.TileCustomChest;

public class BlockCustomChest extends BlockChest implements IQuarkBlock {
   private final String[] variants;
   private final String bareName;

   public BlockCustomChest(String name, Type type) {
      super(type);
      this.variants = new String[]{name};
      this.bareName = name;
      this.setUnlocalizedName(name);
      this.setHardness(2.5F);
      this.setSoundType(SoundType.WOOD);
      this.setCreativeTab(type == VariedChests.CUSTOM_TYPE_QUARK_TRAP ? CreativeTabs.REDSTONE : CreativeTabs.DECORATIONS);
   }

   @Nonnull
   public Block setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      String key = "quark:" + name;
      this.setRegistryName(key);
      ProxyRegistry.register(this);
      ProxyRegistry.register(new ItemChestBlock(this, new ResourceLocation(key)));
      return this;
   }

   public String getBareName() {
      return this.bareName;
   }

   public String[] getVariants() {
      return this.variants;
   }

   public EnumRarity getBlockRarity(ItemStack stack) {
      return EnumRarity.COMMON;
   }

   public IProperty[] getIgnoredProperties() {
      return (IProperty[])this.createBlockState().getProperties().toArray(new IProperty[0]);
   }

   public IProperty getVariantProp() {
      return null;
   }

   public Class getVariantEnum() {
      return null;
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      VariedChests.ChestType myType = this.getCustomType(source, pos);
      return this.getCustomType(source, pos.north()) == myType ? NORTH_CHEST_AABB : (this.getCustomType(source, pos.south()) == myType ? SOUTH_CHEST_AABB : (this.getCustomType(source, pos.west()) == myType ? WEST_CHEST_AABB : (this.getCustomType(source, pos.east()) == myType ? EAST_CHEST_AABB : NOT_CONNECTED_AABB)));
   }

   public void onBlockAdded(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
   }

   @Nonnull
   public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      EnumFacing facing = EnumFacing.getHorizontal(MathHelper.floor((double)(placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3).getOpposite();
      state = state.withProperty(FACING, facing);
      BlockPos northPos = pos.north();
      BlockPos southPos = pos.south();
      BlockPos westPos = pos.west();
      BlockPos eastPos = pos.east();
      VariedChests.ChestType myType = this.getCustomType(stack);
      boolean northChest = myType == this.getCustomType(worldIn, northPos);
      boolean southChest = myType == this.getCustomType(worldIn, southPos);
      boolean westChest = myType == this.getCustomType(worldIn, westPos);
      boolean eastChest = myType == this.getCustomType(worldIn, eastPos);
      if (!northChest && !southChest && !westChest && !eastChest) {
         worldIn.setBlockState(pos, state, 3);
      } else if (facing.getAxis() != Axis.X || !northChest && !southChest) {
         if (facing.getAxis() == Axis.Z && (westChest || eastChest)) {
            if (westChest) {
               this.setState(worldIn, westPos, state, 3);
            } else {
               this.setState(worldIn, eastPos, state, 3);
            }

            worldIn.setBlockState(pos, state, 3);
         } else {
            EnumFacing corrected = facing.rotateY();
            this.setState(worldIn, pos, state.withProperty(FACING, corrected), 3);
            if (northChest) {
               this.setState(worldIn, northPos, state.withProperty(FACING, corrected), 3);
            } else if (southChest) {
               this.setState(worldIn, southPos, state.withProperty(FACING, corrected), 3);
            } else if (westChest) {
               this.setState(worldIn, westPos, state.withProperty(FACING, corrected), 3);
            } else {
               this.setState(worldIn, eastPos, state.withProperty(FACING, corrected), 3);
            }
         }
      } else {
         if (northChest) {
            this.setState(worldIn, northPos, state, 3);
         } else {
            this.setState(worldIn, southPos, state, 3);
         }

         worldIn.setBlockState(pos, state, 3);
      }

      TileEntity te = worldIn.getTileEntity(pos);
      if (te instanceof TileCustomChest) {
         TileCustomChest chest = (TileCustomChest)te;
         if (stack.hasDisplayName()) {
            chest.setCustomName(stack.getDisplayName());
         }

         chest.chestType = myType;
      }

      this.onBlockAdded(worldIn, pos, state);
   }

   public void setState(World worldIn, BlockPos pos, IBlockState state, int flag) {
      TileEntity te = worldIn.getTileEntity(pos);
      worldIn.setBlockState(pos, state, flag);
      if (te != null) {
         te.validate();
         worldIn.setTileEntity(pos, te);
         if (te instanceof TileCustomChest) {
            ((TileCustomChest)te).adjacentChestChecked = false;
         }
      }

   }

   public boolean canProvidePower(IBlockState state) {
      return this.chestType == VariedChests.CUSTOM_TYPE_QUARK_TRAP;
   }

   /** @deprecated */
   @Nonnull
   @Deprecated
   public IBlockState checkForSurroundingChests(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      return state;
   }

   /** @deprecated */
   @Nonnull
   @Deprecated
   public IBlockState correctFacing(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
      return this.correctFacing(worldIn, pos, state, VariedChests.ChestType.NONE);
   }

   public IBlockState correctFacing(World worldIn, BlockPos pos, IBlockState state, VariedChests.ChestType myType) {
      EnumFacing facing = null;

      for(EnumFacing horizontal : Plane.HORIZONTAL) {
         if (this.getCustomType(worldIn, pos.offset(horizontal)) == myType) {
            return state;
         }

         if (worldIn.getBlockState(pos.offset(horizontal)).isFullBlock()) {
            if (facing != null) {
               facing = null;
               break;
            }

            facing = horizontal;
         }
      }

      if (facing != null) {
         return state.withProperty(FACING, facing.getOpposite());
      } else {
         EnumFacing stateFacing = (EnumFacing)state.getValue(FACING);
         if (worldIn.getBlockState(pos.offset(stateFacing)).isFullBlock()) {
            stateFacing = stateFacing.getOpposite();
         }

         if (worldIn.getBlockState(pos.offset(stateFacing)).isFullBlock()) {
            stateFacing = stateFacing.rotateY();
         }

         if (worldIn.getBlockState(pos.offset(stateFacing)).isFullBlock()) {
            stateFacing = stateFacing.getOpposite();
         }

         return state.withProperty(FACING, stateFacing);
      }
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return true;
   }

   public boolean isDoubleChest(World worldIn, BlockPos pos, VariedChests.ChestType myType) {
      if (this.getCustomType(worldIn, pos) != myType) {
         return false;
      } else {
         VariedChests.ChestType theType = this.getCustomType(worldIn, pos);

         for(EnumFacing enumfacing : Plane.HORIZONTAL) {
            if (this.getCustomType(worldIn, pos.offset(enumfacing)) == theType) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
      return willHarvest || super.removedByPlayer(state, world, pos, player, false);
   }

   public void harvestBlock(@Nonnull World worldIn, EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
      super.harvestBlock(worldIn, player, pos, state, te, stack);
      worldIn.setBlockToAir(pos);
      if (te instanceof TileCustomChest) {
         te.invalidate();
      }

   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileCustomChest();
   }

   public VariedChests.ChestType getCustomType(IBlockAccess source, BlockPos pos) {
      if (source.getBlockState(pos).getBlock() == this) {
         TileEntity te = source.getTileEntity(pos);
         if (te instanceof TileCustomChest) {
            return ((TileCustomChest)te).chestType;
         }
      }

      return VariedChests.ChestType.NONE;
   }

   public VariedChests.ChestType getCustomType(ItemStack stack) {
      return VariedChests.ChestType.values()[Math.min(5, stack.getItemDamage() + 1)];
   }

   public ItemStack setCustomType(ItemStack stack, VariedChests.ChestType type) {
      stack.setItemDamage(type.ordinal() - 1);
      return stack;
   }

   public ILockableContainer getContainer(World world, @Nonnull BlockPos pos, boolean locked) {
      TileEntity tile = world.getTileEntity(pos);
      if (!(tile instanceof TileCustomChest)) {
         return null;
      } else {
         ILockableContainer myChest = (TileCustomChest)tile;
         VariedChests.ChestType myType = ((TileCustomChest)tile).chestType;
         if (!locked && this.isBlocked(world, pos)) {
            return null;
         } else {
            for(EnumFacing facing : Plane.HORIZONTAL) {
               BlockPos adjPos = pos.offset(facing);
               TileEntity adjTile = world.getTileEntity(adjPos);
               if (world.getBlockState(adjPos).getBlock() == this && adjTile instanceof TileCustomChest && ((TileCustomChest)adjTile).chestType == myType) {
                  if (this.isBlocked(world, adjPos)) {
                     return null;
                  }

                  if (facing != EnumFacing.WEST && facing != EnumFacing.NORTH) {
                     myChest = new InventoryLargeChest("container.chestDouble", myChest, (TileCustomChest)adjTile);
                  } else {
                     myChest = new InventoryLargeChest("container.chestDouble", (TileCustomChest)adjTile, myChest);
                  }
               }
            }

            return myChest;
         }
      }
   }

   private boolean isBlocked(World worldIn, BlockPos pos) {
      return this.isBelowSolidBlock(worldIn, pos) || this.isOcelotSittingOnChest(worldIn, pos);
   }

   private boolean isBelowSolidBlock(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.up()).doesSideBlockChestOpening(worldIn, pos.up(), EnumFacing.DOWN);
   }

   private boolean isOcelotSittingOnChest(World worldIn, BlockPos pos) {
      for(Entity entity : worldIn.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB((double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 2), (double)(pos.getZ() + 1)))) {
         EntityOcelot cat = (EntityOcelot)entity;
         if (cat.isSitting()) {
            return true;
         }
      }

      return false;
   }

   public void getDrops(@Nonnull NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
      drops.add(this.setCustomType(new ItemStack(this, 1), this.getCustomType(world, pos)));
   }

   @Nonnull
   public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
      return this.setCustomType(new ItemStack(this, 1), this.getCustomType(world, pos));
   }
}
