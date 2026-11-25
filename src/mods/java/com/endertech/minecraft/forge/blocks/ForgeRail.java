package com.endertech.minecraft.forge.blocks;

import com.endertech.minecraft.forge.ForgeBounds;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.materials.ForgeMaterial;
import com.endertech.minecraft.forge.units.IForgeUnit;
import com.endertech.minecraft.forge.units.UnitEnabled;
import com.endertech.minecraft.forge.units.UnitId;
import com.endertech.minecraft.forge.units.UnitVariant;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ForgeRail extends BlockRailBase implements IForgeUnit {
   public static final PropertyBool POWERED = BlockRailPowered.POWERED;
   private final ForgeMaterial material;
   private final ForgeMod mod;
   private final ForgeConfig config;
   private final UnitEnabled unitEnabled;
   private final UnitId id;
   private final float maxSpeed;
   private final int signalMaxDistance;
   private final int metaShift;
   private final UnitVariant defaultVariant;
   private Map<Object, UnitVariant> variants = new HashMap();

   public ForgeRail(ForgeMod mod, UnitConfig config, ForgeMaterial material, EnumRailDirection direction, int metaShift) {
      super(true);
      this.material = material;
      this.mod = mod;
      this.id = this.createId(mod.getId(), material.getName());
      this.config = config;
      this.unitEnabled = new UnitEnabled(config, this.getUnitCategory(), true);
      this.maxSpeed = material.getStrength();
      this.signalMaxDistance = ForgeBounds.RAIL_SIGNAL_DISTANCE.getIntBounds().approxUp(this.getMaterial().getMagicability()).intValue();
      this.metaShift = metaShift;
      this.defaultVariant = UnitVariant.from(this.id, IForgeUnit.Variants.NORMAL);
      this.setDefaultState(this.blockState.getBaseState().withProperty(this.getShapeProperty(), direction).withProperty(POWERED, Boolean.valueOf(false)));
      this.setRegistryName(this.getId().toResLoc());
      this.setHardness(0.7F);
      this.setHarvestLevel("pickaxe", ForgeMaterial.calculateHarvestLevel(material));
      this.setUnlocalizedName(this.getId().getRegName());
      this.register();
   }

   public static ForgeRail.Posture getPosture(EnumRailDirection direction) {
      switch(direction) {
      case NORTH_SOUTH:
      case ASCENDING_NORTH:
      case ASCENDING_SOUTH:
         return ForgeRail.Posture.LONGWISE;
      case EAST_WEST:
      case ASCENDING_EAST:
      case ASCENDING_WEST:
         return ForgeRail.Posture.LATERAL;
      default:
         return ForgeRail.Posture.CURVED;
      }
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{this.getShapeProperty(), POWERED});
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(this.getShapeProperty(), EnumRailDirection.byMetadata((meta & 7) + this.metaShift)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
   }

   public int getMetaFromState(IBlockState state) {
      int meta = ((EnumRailDirection)state.getValue(this.getShapeProperty())).getMetadata() - this.metaShift;
      if (((Boolean)state.getValue(POWERED)).booleanValue()) {
         meta |= 8;
      }

      return meta;
   }

   public float getRailMaxSpeed(World world, EntityMinecart cart, BlockPos pos) {
      return this.maxSpeed;
   }

   public boolean isReadyToRegister() {
      return true;
   }

   public ForgeConfig getConfig() {
      return this.config;
   }

   public UnitId getId() {
      return this.id;
   }

   public ForgeMod getMod() {
      return this.mod;
   }

   public boolean isEnabled() {
      return this.unitEnabled.isEnabled();
   }

   public boolean isRegistered() {
      return ForgeBlock.isRegistered(this);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(ChatFormatting.YELLOW + "Max Speed: " + this.getMaxSpeed());
      tooltip.add(ChatFormatting.RED + "Signal Max Distance: " + this.getSignalMaxDistance());
      super.addInformation(stack, world, tooltip, advanced);
   }

   public ForgeMaterial getMaterial() {
      return this.material;
   }

   public float getMaxSpeed() {
      return this.maxSpeed;
   }

   public int getSignalMaxDistance() {
      return this.signalMaxDistance;
   }

   protected static BlockPos nextPosition(BlockPos pos, EnumRailDirection direction, boolean forward) {
      switch(direction) {
      case NORTH_SOUTH:
         return forward ? pos.south() : pos.north();
      case ASCENDING_NORTH:
         return forward ? pos.south() : pos.north().up();
      case ASCENDING_SOUTH:
         return forward ? pos.south().up() : pos.north();
      case EAST_WEST:
         return forward ? pos.east() : pos.west();
      case ASCENDING_EAST:
         return forward ? pos.east().up() : pos.west();
      case ASCENDING_WEST:
         return forward ? pos.east() : pos.west().up();
      case NORTH_EAST:
         return forward ? pos.east() : pos.north();
      case NORTH_WEST:
         return forward ? pos.west() : pos.north();
      case SOUTH_EAST:
         return forward ? pos.east() : pos.south();
      case SOUTH_WEST:
         return forward ? pos.west() : pos.south();
      default:
         return pos;
      }
   }

   protected boolean findPowerSource(World world, BlockPos startPos, BlockPos lastPos, BlockPos curPos, int distance) {
      if (distance > this.getSignalMaxDistance()) {
         return false;
      } else if (world.isBlockPowered(curPos)) {
         return true;
      } else {
         boolean found = false;
         IBlockState state = world.getBlockState(curPos);
         if (isRailBlock(state)) {
            EnumRailDirection direction = getDirection(state);

            for(BlockPos checkPos : this.getConnectedNeightbors(world, curPos, direction)) {
               if (!checkPos.equals(startPos) && !checkPos.equals(lastPos) && isPowerConductiveRail(world, checkPos)) {
                  found = this.findPowerSource(world, startPos, curPos, checkPos, distance + 1);
                  if (found) {
                     break;
                  }
               }
            }
         }

         return found;
      }
   }

   public static boolean isAscendingSide(EnumRailDirection direction, EnumFacing side) {
      switch(side) {
      case NORTH:
         return direction == EnumRailDirection.ASCENDING_NORTH;
      case EAST:
         return direction == EnumRailDirection.ASCENDING_EAST;
      case SOUTH:
         return direction == EnumRailDirection.ASCENDING_SOUTH;
      case WEST:
         return direction == EnumRailDirection.ASCENDING_WEST;
      default:
         return false;
      }
   }

   public static boolean isConnectableSide(EnumRailDirection direction, EnumFacing side) {
      switch(getPosture(direction)) {
      case LONGWISE:
         return side == EnumFacing.NORTH || side == EnumFacing.SOUTH;
      case LATERAL:
         return side == EnumFacing.WEST || side == EnumFacing.EAST;
      case CURVED:
         switch(direction) {
         case NORTH_EAST:
            return side == EnumFacing.NORTH || side == EnumFacing.EAST;
         case NORTH_WEST:
            return side == EnumFacing.NORTH || side == EnumFacing.WEST;
         case SOUTH_EAST:
            return side == EnumFacing.SOUTH || side == EnumFacing.EAST;
         case SOUTH_WEST:
            return side == EnumFacing.SOUTH || side == EnumFacing.WEST;
         }
      default:
         return false;
      }
   }

   protected static EnumRailDirection getDirection(IBlockState state) {
      BlockRailBase block = (BlockRailBase)state.getBlock();
      return (EnumRailDirection)state.getValue(block.getShapeProperty());
   }

   public static boolean isPoweredRail(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      return isPoweredRail(state);
   }

   public static boolean isPoweredRail(IBlockState state) {
      Block block = state.getBlock();
      if (block instanceof ForgeRail) {
         return ((Boolean)state.getValue(POWERED)).booleanValue();
      } else if (block instanceof BlockRailPowered) {
         return ((Boolean)state.getValue(BlockRailPowered.POWERED)).booleanValue();
      } else {
         return block instanceof BlockRailDetector ? ((Boolean)state.getValue(BlockRailDetector.POWERED)).booleanValue() : false;
      }
   }

   public static boolean isPowerConductiveRail(World world, BlockPos pos) {
      Block block = world.getBlockState(pos).getBlock();
      return block instanceof ForgeRail || block instanceof BlockRailPowered || block instanceof BlockRailDetector;
   }

   protected boolean isRailWithConnectableSide(World world, BlockPos pos, EnumFacing side) {
      IBlockState state = world.getBlockState(pos);
      return isRailBlock(state) && isConnectableSide(getDirection(state), side);
   }

   /** @deprecated */
   @Deprecated
   protected int countConnectionsFor(World world, BlockPos pos, EnumRailDirection direction) {
      int connections = 0;

      for(EnumFacing side : EnumFacing.HORIZONTALS) {
         if (isConnectableSide(direction, side)) {
            BlockPos checkPos = pos.offset(side);
            if (isAscendingSide(direction, side)) {
               checkPos = checkPos.up();
            }

            if (this.isRailWithConnectableSide(world, checkPos, side.getOpposite())) {
               ++connections;
            }
         }
      }

      return connections;
   }

   protected IBlockState updateDir(World world, BlockPos pos, IBlockState state, boolean initialPlacement) {
      if (this.isClientSide(world)) {
         return state;
      } else {
         EnumRailDirection bestDirection = getDirection(state);
         ForgeRail.Posture bestPosture = getPosture(bestDirection);
         int maxFoundConnections = this.getConnectedNeightbors(world, pos, bestDirection).size();

         for(EnumRailDirection direction : this.getShapeProperty().getAllowedValues()) {
            int connections = this.getConnectedNeightbors(world, pos, direction).size();
            if (connections >= maxFoundConnections && (!direction.isAscending() || this.hasGroundSupport(world, pos, direction)) && (connections != maxFoundConnections || bestPosture != ForgeRail.Posture.CURVED && getPosture(direction) == bestPosture)) {
               maxFoundConnections = connections;
               bestDirection = direction;
            }
         }

         IBlockState newState = state.withProperty(this.getShapeProperty(), bestDirection);
         if (!state.equals(newState) && world.setBlockState(pos, newState)) {
            return newState;
         } else {
            return state;
         }
      }
   }

   protected static List<BlockPos> getSupportPoints(BlockPos pos, EnumRailDirection direction) {
      List<BlockPos> points = new ArrayList(2);
      points.add(pos.down());
      switch(direction) {
      case ASCENDING_NORTH:
         points.add(pos.north());
         break;
      case ASCENDING_SOUTH:
         points.add(pos.south());
      case EAST_WEST:
      default:
         break;
      case ASCENDING_EAST:
         points.add(pos.east());
         break;
      case ASCENDING_WEST:
         points.add(pos.west());
      }

      return points;
   }

   protected boolean hasGroundSupport(World world, BlockPos pos, EnumRailDirection direction) {
      boolean hasSupport = false;

      for(BlockPos checkPos : getSupportPoints(pos, direction)) {
         IBlockState state = world.getBlockState(checkPos);
         if (!state.isSideSolid(world, checkPos, EnumFacing.UP)) {
            return false;
         }

         hasSupport = true;
      }

      return hasSupport;
   }

   public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
      if (this.isServerSide(world)) {
         state.neighborChanged(world, pos, this, pos);
         state = world.getBlockState(pos);
         EnumRailDirection direction = getDirection(state);

         for(BlockPos checkPos : this.getPossibleNeighbors(world, pos, direction)) {
            IBlockState checkState = world.getBlockState(checkPos);
            if (isRailBlock(checkState)) {
               checkState.neighborChanged(world, checkPos, checkState.getBlock(), pos);
            }
         }
      }

   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
   }

   protected List<BlockPos> getPossibleNeighbors(World world, BlockPos pos, EnumRailDirection direction) {
      List<BlockPos> neighbors = new ArrayList(4);

      for(EnumFacing side : EnumFacing.HORIZONTALS) {
         if (isConnectableSide(direction, side)) {
            BlockPos offsetPos = pos.offset(side);
            if (isAscendingSide(direction, side)) {
               neighbors.add(offsetPos.up());
            } else {
               neighbors.add(offsetPos);
               neighbors.add(offsetPos.down());
            }
         }
      }

      return neighbors;
   }

   protected List<BlockPos> getConnectedNeightbors(World world, BlockPos pos, EnumRailDirection direction) {
      List<BlockPos> neighbors = new ArrayList(2);

      for(BlockPos checkPos : this.getPossibleNeighbors(world, pos, direction)) {
         BlockPos vec = checkPos.subtract(pos);
         EnumFacing checkSide = EnumFacing.getFacingFromVector((float)vec.getX(), 0.0F, (float)vec.getZ()).getOpposite();
         if (this.isRailWithConnectableSide(world, checkPos, checkSide)) {
            neighbors.add(checkPos);
         }
      }

      return neighbors;
   }

   protected void updateState(IBlockState state, World world, BlockPos pos, Block block) {
      state = this.updateDir(world, pos, state, false);
      boolean powered = this.findPowerSource(world, pos, pos, pos, 0);
      if (powered != ((Boolean)state.getValue(POWERED)).booleanValue()) {
         world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(powered)));
         world.notifyNeighborsOfStateChange(pos.down(), this, false);
         if (getDirection(state).isAscending()) {
            world.notifyNeighborsOfStateChange(pos.up(), this, false);
         }
      }

   }

   public IBlockState withRotation(IBlockState state, Rotation rotation) {
      EnumRailDirection direction = (EnumRailDirection)state.getValue(this.getShapeProperty());
      switch(rotation) {
      case CLOCKWISE_180:
         switch(direction) {
         case ASCENDING_NORTH:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_NORTH);
         case EAST_WEST:
         default:
            return state;
         case ASCENDING_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_WEST);
         case ASCENDING_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_EAST);
         case NORTH_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.SOUTH_WEST);
         case NORTH_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.SOUTH_EAST);
         case SOUTH_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.NORTH_WEST);
         case SOUTH_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.NORTH_EAST);
         }
      case COUNTERCLOCKWISE_90:
         switch(direction) {
         case NORTH_SOUTH:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.EAST_WEST);
         case ASCENDING_NORTH:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_EAST);
         case EAST_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.NORTH_SOUTH);
         case ASCENDING_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_SOUTH);
         case NORTH_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.NORTH_WEST);
         case NORTH_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.SOUTH_WEST);
         case SOUTH_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.NORTH_EAST);
         case SOUTH_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.SOUTH_EAST);
         }
      case CLOCKWISE_90:
         switch(direction) {
         case NORTH_SOUTH:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.EAST_WEST);
         case ASCENDING_NORTH:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_WEST);
         case EAST_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.NORTH_SOUTH);
         case ASCENDING_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_NORTH);
         case NORTH_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.SOUTH_EAST);
         case NORTH_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.NORTH_EAST);
         case SOUTH_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.SOUTH_WEST);
         case SOUTH_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.NORTH_WEST);
         }
      default:
         return state;
      }
   }

   public IBlockState withMirror(IBlockState state, Mirror mirror) {
      EnumRailDirection direction = (EnumRailDirection)state.getValue(this.getShapeProperty());
      switch(mirror) {
      case LEFT_RIGHT:
         switch(direction) {
         case ASCENDING_NORTH:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_NORTH);
         case EAST_WEST:
         case ASCENDING_EAST:
         case ASCENDING_WEST:
         default:
            return state;
         case NORTH_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.SOUTH_EAST);
         case NORTH_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.SOUTH_WEST);
         case SOUTH_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.NORTH_EAST);
         case SOUTH_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.NORTH_WEST);
         }
      case FRONT_BACK:
         switch(direction) {
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         case EAST_WEST:
         default:
            break;
         case ASCENDING_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_WEST);
         case ASCENDING_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.ASCENDING_EAST);
         case NORTH_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.NORTH_WEST);
         case NORTH_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.NORTH_EAST);
         case SOUTH_EAST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.SOUTH_WEST);
         case SOUTH_WEST:
            return state.withProperty(this.getShapeProperty(), EnumRailDirection.SOUTH_EAST);
         }
      default:
         return state;
      }
   }

   public Map<Object, UnitVariant> getUnitVariants() {
      return this.variants;
   }

   public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
      IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
      EnumFacing placerFacing = placer.getHorizontalFacing();
      Rotation rotation = Rotation.NONE;
      switch(placerFacing) {
      case EAST:
         rotation = Rotation.CLOCKWISE_90;
         break;
      case SOUTH:
         rotation = Rotation.CLOCKWISE_180;
         break;
      case WEST:
         rotation = Rotation.COUNTERCLOCKWISE_90;
      }

      state = this.withRotation(state, rotation);
      return state;
   }

   public <E extends Enum<E> & IForgeEnum> void setUnitVariants(Class<E> clazz) {
      this.variants = this.createUnitVariants(clazz);
   }

   public UnitVariant getDefaultVariant() {
      return this.defaultVariant;
   }

   public static enum Posture {
      LONGWISE,
      LATERAL,
      CURVED;

      private Posture() {
      }
   }
}
