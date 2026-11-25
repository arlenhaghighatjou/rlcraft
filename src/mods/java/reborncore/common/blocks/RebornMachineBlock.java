package reborncore.common.blocks;

import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import reborncore.api.ToolManager;
import reborncore.api.tile.IMachineGuiHandler;
import reborncore.api.tile.IUpgrade;
import reborncore.api.tile.IUpgradeable;
import reborncore.api.tile.IWrenchable;
import reborncore.common.RebornCoreConfig;
import reborncore.common.fluids.RebornFluidTank;
import reborncore.common.items.WrenchHelper;
import reborncore.common.tile.RebornMachineTile;
import reborncore.common.util.InventoryHelper;
import reborncore.common.util.Utils;

public abstract class RebornMachineBlock extends Block implements ITileEntityProvider, IWrenchable {
   public static ItemStack basicFrameStack;
   public static ItemStack advancedFrameStack;
   boolean hasCustomStates;
   public static final IProperty<Boolean> ACTIVE = PropertyBool.create("active");
   public static final IProperty<EnumFacing> FACING = PropertyDirection.create("facing");

   public RebornMachineBlock() {
      this(false);
   }

   public RebornMachineBlock(boolean hasCustomStates) {
      this(hasCustomStates, Utils.HORIZONTAL_FACINGS);
   }

   public RebornMachineBlock(boolean hasCustomStates, Set<EnumFacing> supportedFacings) {
      super(Material.IRON);
      this.hasCustomStates = hasCustomStates;
      this.setHardness(2.0F);
      this.setSoundType(SoundType.METAL);
      if (!hasCustomStates) {
         this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(ACTIVE, Boolean.valueOf(false)));
      }

      BlockWrenchEventHandler.wrenableBlocks.add(this);
   }

   public abstract IMachineGuiHandler getGui();

   protected EnumFacing getFacingForPlacement(EntityLivingBase placer) {
      if (this.hasCustomStates) {
         return EnumFacing.NORTH;
      } else {
         return placer == null ? EnumFacing.NORTH : placer.getHorizontalFacing().getOpposite();
      }
   }

   public boolean isActive(IBlockState state) {
      return this.hasCustomStates ? false : ((Boolean)state.getValue(ACTIVE)).booleanValue();
   }

   public void setActive(Boolean active, World world, BlockPos pos) {
      if (!this.hasCustomStates) {
         world.setBlockState(pos, world.getBlockState(pos).withProperty(ACTIVE, active), 3);
      }
   }

   public boolean isAdvanced() {
      return false;
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{FACING, ACTIVE});
   }

   public boolean hasTileEntity() {
      return true;
   }

   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      EnumFacing newFacing = this.getFacingForPlacement(placer);
      if (this.canSetFacing(worldIn, pos, newFacing, (EntityPlayer)null)) {
         this.setFacing(worldIn, pos, newFacing, (EntityPlayer)null);
      }
   }

   public boolean canCreatureSpawn(IBlockState state, IBlockAccess world, BlockPos pos, SpawnPlacementType type) {
      return false;
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      InventoryHelper.dropInventoryItems(worldIn, pos);
      super.breakBlock(worldIn, pos, state);
   }

   public int getMetaFromState(IBlockState state) {
      int facingInt = this.getSideFromEnum((EnumFacing)state.getValue(FACING));
      int activeInt = ((Boolean)state.getValue(ACTIVE)).booleanValue() ? 0 : 4;
      return facingInt + activeInt;
   }

   public IBlockState getStateFromMeta(int meta) {
      boolean active = false;
      int facingInt = meta;
      if (meta > 4) {
         active = true;
         facingInt = meta - 4;
      }

      EnumFacing facing = this.getSideFromint(facingInt);
      return this.getDefaultState().withProperty(FACING, facing).withProperty(ACTIVE, Boolean.valueOf(active));
   }

   public EnumFacing getSideFromint(int i) {
      if (i == 0) {
         return EnumFacing.NORTH;
      } else if (i == 1) {
         return EnumFacing.SOUTH;
      } else if (i == 2) {
         return EnumFacing.EAST;
      } else {
         return i == 3 ? EnumFacing.WEST : EnumFacing.NORTH;
      }
   }

   public int getSideFromEnum(EnumFacing facing) {
      if (facing == EnumFacing.NORTH) {
         return 0;
      } else if (facing == EnumFacing.SOUTH) {
         return 1;
      } else if (facing == EnumFacing.EAST) {
         return 2;
      } else {
         return facing == EnumFacing.WEST ? 3 : 0;
      }
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      if (RebornCoreConfig.wrenchRequired) {
         drops.add(this.isAdvanced() ? advancedFrameStack.copy() : basicFrameStack.copy());
      } else {
         super.getDrops(drops, world, pos, state, fortune);
      }

   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      ItemStack stack = playerIn.getHeldItem(hand);
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity == null) {
         return false;
      } else {
         if (tileEntity instanceof RebornMachineTile) {
            RebornFluidTank tank = ((RebornMachineTile)tileEntity).getTank();
            if (tank != null && FluidUtil.interactWithFluidHandler(playerIn, hand, tank)) {
               return true;
            }
         }

         if (!stack.isEmpty()) {
            if (ToolManager.INSTANCE.canHandleTool(stack)) {
               if (WrenchHelper.handleWrench(stack, worldIn, pos, playerIn, side)) {
                  return true;
               }
            } else if (stack.getItem() instanceof IUpgrade && tileEntity instanceof IUpgradeable) {
               IUpgradeable upgradeableEntity = (IUpgradeable)tileEntity;
               if (upgradeableEntity.canBeUpgraded() && InventoryHelper.testInventoryInsertion(upgradeableEntity.getUpgradeInventory(), stack, (EnumFacing)null) > 0) {
                  InventoryHelper.insertItemIntoInventory(upgradeableEntity.getUpgradeInventory(), stack);
                  playerIn.setHeldItem(EnumHand.MAIN_HAND, stack);
                  return true;
               }
            }
         }

         if (this.getGui() != null && !playerIn.isSneaking()) {
            this.getGui().open(playerIn, pos, worldIn);
            return true;
         } else {
            return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
         }
      }
   }

   public TileEntity createNewTileEntity(World world, int meta) {
      return null;
   }

   public EnumFacing getFacing(World world, BlockPos pos) {
      return this.hasCustomStates ? EnumFacing.NORTH : (EnumFacing)world.getBlockState(pos).getValue(FACING);
   }

   public boolean canSetFacing(World world, BlockPos pos, EnumFacing newFacing, EntityPlayer player) {
      return !this.hasCustomStates;
   }

   public boolean setFacing(World world, BlockPos pos, EnumFacing newFacing, EntityPlayer player) {
      return this.hasCustomStates ? false : world.setBlockState(pos, world.getBlockState(pos).withProperty(FACING, newFacing));
   }
}
