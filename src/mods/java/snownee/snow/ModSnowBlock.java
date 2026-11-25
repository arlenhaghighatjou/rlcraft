package snownee.snow;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.BiPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModSnowBlock extends BlockSnow {
   protected static final AxisAlignedBB[] SNOW_AABB_MAGIC = new AxisAlignedBB[]{new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D)};
   public static final PropertyBool TILE = PropertyBool.create("tile");

   public ModSnowBlock() {
      if (ModConfig.placeSnowInBlock) {
         this.setDefaultState(this.blockState.getBaseState().withProperty(LAYERS, Integer.valueOf(1)).withProperty(TILE, Boolean.valueOf(false)));
      }

      this.setRegistryName("minecraft", "snow_layer");
      this.setHardness(0.1F);
      this.setSoundType(SoundType.SNOW);
      this.setUnlocalizedName("snow");
      this.setLightOpacity(0);
   }

   protected BlockStateContainer createBlockState() {
      return ModConfig.placeSnowInBlock ? new BlockStateContainer(this, new IProperty[]{LAYERS, TILE}) : super.createBlockState();
   }

   public boolean hasTileEntity(IBlockState state) {
      return ModConfig.placeSnowInBlock ? ((Boolean)state.getValue(TILE)).booleanValue() : false;
   }

   public TileEntity createTileEntity(World world, IBlockState state) {
      return ModConfig.placeSnowInBlock ? new SnowTile() : null;
   }

   public IBlockState getStateFromMeta(int meta) {
      if (ModConfig.placeSnowInBlock) {
         IBlockState state = super.getStateFromMeta(meta);
         if (meta >= 8) {
            state = state.withProperty(TILE, Boolean.valueOf(true));
         }

         return state;
      } else {
         return super.getStateFromMeta(meta);
      }
   }

   public int getMetaFromState(IBlockState state) {
      int meta = super.getMetaFromState(state);
      if (ModConfig.placeSnowInBlock && ((Boolean)state.getValue(TILE)).booleanValue()) {
         meta += 8;
      }

      return meta;
   }

   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      if (!ModConfig.thinnerBoundingBox) {
         return super.getCollisionBoundingBox(blockState, worldIn, pos);
      } else {
         int layers = ((Integer)blockState.getValue(LAYERS)).intValue();
         BlockPos posUp = pos.up();
         return layers == 8 && worldIn.getBlockState(posUp).getCollisionBoundingBox(worldIn, posUp) != null ? FULL_BLOCK_AABB : SNOW_AABB_MAGIC[layers - 1];
      }
   }

   public RayTraceResult collisionRayTrace(IBlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
      RayTraceResult hit1 = null;
      if (ModConfig.placeSnowInBlock && ((Boolean)state.getValue(TILE)).booleanValue()) {
         hit1 = this.rayTrace(pos, start, end, this.getContainedState(worldIn, pos).getBoundingBox(worldIn, pos));
      }

      RayTraceResult hit2 = super.collisionRayTrace(state, worldIn, pos, start, end);
      if (hit1 == null) {
         return hit2;
      } else if (hit2 == null) {
         return hit1;
      } else {
         Vec3d vec1 = hit1.hitVec;
         Vec3d vec2 = hit2.hitVec;
         return start.squareDistanceTo(vec1) < start.squareDistanceTo(vec2) ? hit1 : hit2;
      }
   }

   public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
      AxisAlignedBB aabb = super.getSelectedBoundingBox(state, worldIn, pos);
      if (ModConfig.placeSnowInBlock && ((Boolean)state.getValue(TILE)).booleanValue()) {
         IBlockState contained = this.getContainedState(worldIn, pos);
         if (contained.getBlock() != Blocks.AIR) {
            aabb = aabb.union(contained.getSelectedBoundingBox(worldIn, pos));
         }
      }

      return aabb;
   }

   public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      if (state.getBlock() != this) {
         return false;
      } else {
         return ((Integer)state.getValue(LAYERS)).intValue() == 8;
      }
   }

   public int tickRate(World worldIn) {
      return 2;
   }

   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (ModConfig.snowGravity && !BlockFalling.fallInstantly) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }

   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!ModConfig.snowGravity) {
         super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
      } else if (!BlockFalling.fallInstantly) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }

   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (!worldIn.isRemote) {
         this.checkFallable(worldIn, pos, state);
      }
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos.down());
      Block block = iblockstate.getBlock();
      if (ModConfig.snowOnIce || block != Blocks.ICE && block != Blocks.PACKED_ICE && block != Blocks.BARRIER) {
         BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP);
         return blockfaceshape == BlockFaceShape.SOLID || iblockstate.getBlock().isLeaves(iblockstate, worldIn, pos.down()) || block == this && ((Integer)iblockstate.getValue(LAYERS)).intValue() == 8;
      } else {
         return false;
      }
   }

   public void randomTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom random) {
      if (!ModConfig.snowNeverMelt && worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11) {
         worldIn.setBlockToAir(pos);
      } else if ((ModConfig.snowAccumulationDuringSnowfall || ModConfig.snowAccumulationDuringSnowstorm) && random.nextInt(8) <= 0 && worldIn.canSeeSky(pos.up())) {
         boolean flag = false;
         if (worldIn.isRaining()) {
            if (ModConfig.snowAccumulationDuringSnowfall) {
               flag = true;
            } else if (ModConfig.snowAccumulationDuringSnowstorm && worldIn.isThundering()) {
               flag = true;
            }
         }

         int layers = ((Integer)state.getValue(LAYERS)).intValue();
         if (worldIn.canSnowAt(pos, false)) {
            if (flag) {
               accumulate(worldIn, pos, state, (w, p) -> {
                  return !(w.getBlockState(p.down()).getBlock() instanceof ModSnowBlock) && w.getLightFor(EnumSkyBlock.BLOCK, p) < 10;
               }, true);
            } else if (layers > 1 && !worldIn.isRaining() && worldIn.getBlockState(pos.up()).getBlock() != this) {
               accumulate(worldIn, pos, state, (w, p) -> {
                  return !(w.getBlockState(p.up()).getBlock() instanceof ModSnowBlock);
               }, false);
            }
         }

      }
   }

   private static void accumulate(World world, BlockPos pos, IBlockState centerState, BiPredicate<World, BlockPos> filter, boolean accumulate) {
      int i = ((Integer)centerState.getValue(LAYERS)).intValue();
      int j = 0;

      while(j < 8) {
         int k = j / 2;
         EnumFacing direction = EnumFacing.getHorizontal(k);
         BlockPos pos2 = pos.offset(direction);
         if (j % 2 == 1) {
            pos2 = pos2.offset(EnumFacing.getHorizontal(k + 1));
         }

         int l;
         label95: {
            if (world.isBlockLoaded(pos2) && filter.test(world, pos2)) {
               label96: {
                  IBlockState state = world.getBlockState(pos2);
                  boolean isAir = state.getBlock().isAir(state, world, pos2);
                  int height = world.getPrecipitationHeight(pos2).getY();
                  if (!isAir && state.getBlock() != Blocks.SNOW_LAYER) {
                     if (!canContainState(state) || height != pos2.getY() && height != pos2.getY() + 1) {
                        break label96;
                     }
                  } else if (height != pos2.getY()) {
                     break label96;
                  }

                  if (Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos2)) {
                     if (state.getBlock() instanceof ModSnowBlock) {
                        l = ((Integer)state.getValue(LAYERS)).intValue();
                     } else {
                        l = 0;
                     }

                     if (accumulate) {
                        if (i > l) {
                           break label95;
                        }
                     } else if (i < l) {
                        break label95;
                     }
                  }
               }
            }

            ++j;
            continue;
         }

         i = l;
         pos = pos2;
         break;
      }

      if (accumulate) {
         if (i < 8 && placeLayersOn(world, pos, 1, false, false, 2)) {
            AxisAlignedBB aabb = new AxisAlignedBB(pos);

            for(Entity entity : world.getEntitiesWithinAABBExcludingEntity((Entity)null, aabb)) {
               entity.setPositionAndUpdate(entity.posX, entity.posY + (ModConfig.thinnerBoundingBox ? 0.0625D : 0.125D), entity.posZ);
            }
         }
      } else {
         world.setBlockState(pos, centerState.withProperty(LAYERS, Integer.valueOf(i - 1)), 2);
      }

   }

   private boolean checkFallable(World worldIn, BlockPos pos, IBlockState state) {
      BlockPos posDown = pos.down();
      if ((worldIn.isAirBlock(posDown) || canFallThrough(worldIn.getBlockState(posDown), worldIn, posDown)) && pos.getY() >= 0) {
         if (!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
            if (!worldIn.isRemote) {
               worldIn.setBlockToAir(pos);
               FallingSnowEntity entityfallingblock = new FallingSnowEntity(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, ((Integer)state.getValue(LAYERS)).intValue());
               worldIn.spawnEntity(entityfallingblock);
            }
         } else {
            worldIn.setBlockToAir(pos);

            BlockPos blockpos;
            for(blockpos = pos.down(); blockpos.getY() > 0; blockpos = blockpos.down()) {
               IBlockState state2 = worldIn.getBlockState(blockpos);
               if (state2.getBlock() instanceof BlockSnow) {
                  return true;
               }

               if (!canFallThrough(state2, worldIn, blockpos)) {
                  break;
               }
            }

            if (blockpos.getY() > 0) {
               worldIn.setBlockState(blockpos.up(), state);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean placeLayersOn(World world, BlockPos pos, int layers, boolean falling, boolean playSound, int flags) {
      if (!Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos)) {
         return false;
      } else {
         layers = MathHelper.clamp(layers, 1, 8);
         IBlockState state = world.getBlockState(pos);
         int originLayers = 0;
         boolean flag = false;
         IBlockState base;
         if (state.getBlock() == Blocks.SNOW_LAYER) {
            originLayers = ((Integer)state.getValue(LAYERS)).intValue();
            base = state;
         } else {
            base = Blocks.SNOW_LAYER.getDefaultState();
            if (canContainState(state)) {
               flag = true;
               base = base.withProperty(TILE, Boolean.valueOf(true));
            }
         }

         if (!flag && state.getBlock() != Blocks.SNOW_LAYER && !state.getBlock().isReplaceable(world, pos)) {
            return false;
         } else {
            world.setBlockState(pos, base.withProperty(LAYERS, Integer.valueOf(MathHelper.clamp(originLayers + layers, 1, 8))), flags);
            if (flag) {
               TileEntity tile = world.getTileEntity(pos);
               if (tile instanceof SnowTile) {
                  ((SnowTile)tile).setState(state);
               }
            }

            if (falling) {
               world.addBlockEvent(pos, Blocks.SNOW_LAYER, originLayers, layers);
            } else if (playSound) {
               SoundType soundtype = Blocks.SNOW_LAYER.getSoundType(state, world, pos, (Entity)null);
               world.playSound((EntityPlayer)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
            }

            if (originLayers + layers > 8) {
               pos = pos.up();
               if (world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
                  world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState().withProperty(LAYERS, Integer.valueOf(MathHelper.clamp(originLayers + layers - 8, 1, 8))), flags);
               }
            }

            return true;
         }
      }
   }

   public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int originLayers, int layers) {
      double offsetY = (double)originLayers / 8.0D;
      layers = layers * 10;

      for(int i = 0; i < layers; ++i) {
         double d0 = RANDOM.nextGaussian() * 0.2D;
         double d1 = RANDOM.nextGaussian() * 0.02D;
         double d2 = RANDOM.nextGaussian() * 0.2D;
         worldIn.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, (double)((float)pos.getX() + RANDOM.nextFloat()), (double)pos.getY() + offsetY, (double)((float)pos.getZ() + RANDOM.nextFloat()), d0, d1, d2, new int[0]);
      }

      SoundType soundtype = this.getSoundType(state, worldIn, pos, (Entity)null);
      worldIn.playSound((EntityPlayer)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
      return true;
   }

   public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
      IBlockState state = worldIn.getBlockState(pos);
      if (((Integer)state.getValue(LAYERS)).intValue() == 8) {
         return false;
      } else if (ModConfig.placeSnowInBlock && state.getBlock() == this && ((Boolean)state.getValue(TILE)).booleanValue()) {
         return this.getContainedState(worldIn, pos).getMaterial().isReplaceable();
      } else {
         return ModConfig.snowAlwaysReplaceable || super.isReplaceable(worldIn, pos);
      }
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      if (ModConfig.particleThroughLeaves && rand.nextInt(31) == 1) {
         IBlockState stateDown = worldIn.getBlockState(pos.down());
         if (stateDown.getBlock().isLeaves(stateDown, worldIn, pos.down())) {
            double d0 = (double)pos.getX() + rand.nextDouble();
            double d1 = (double)pos.getY() - 0.05D;
            double d2 = (double)pos.getZ() + rand.nextDouble();
            worldIn.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
         }

      }
   }

   public static boolean canContainState(IBlockState state) {
      if (ModConfig.placeSnowInBlock && !state.getBlock().hasTileEntity(state)) {
         Block block = state.getBlock();
         if (!(block instanceof BlockTallGrass) && !(block instanceof BlockFlower) && !(block instanceof BlockSapling) && !(block instanceof BlockMushroom) && !(block instanceof BlockDeadBush)) {
            return !(block instanceof BlockFence) && !(block instanceof BlockWall) && (!(block instanceof BlockPane) || block instanceof BlockStainedGlassPane) ? isCompatBlock(block) : true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public IBlockState getContainedState(IBlockAccess world, BlockPos pos) {
      TileEntity tile = world.getTileEntity(pos);
      return tile instanceof SnowTile ? ((SnowTile)tile).getState() : Blocks.AIR.getDefaultState();
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      if (ModConfig.placeSnowInBlock && ((Boolean)state.getValue(TILE)).booleanValue()) {
         IBlockState stateIn = this.getContainedState(worldIn, pos);
         Block block = stateIn.getBlock();
         if (!(block instanceof BlockFence) && !(block instanceof BlockWall) && !(block instanceof BlockPane)) {
            return stateIn.getBlockFaceShape(worldIn, pos, face);
         } else {
            return face == EnumFacing.UP ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
         }
      } else {
         return super.getBlockFaceShape(worldIn, state, pos, face);
      }
   }

   public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing) {
      return this.getContainedState(world, pos).getBlock().canBeConnectedTo(world, pos, facing);
   }

   public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
      addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getCollisionBoundingBox(worldIn, pos));
      if (ModConfig.placeSnowInBlock && ((Boolean)state.getValue(TILE)).booleanValue()) {
         this.getContainedState(worldIn, pos).addCollisionBoxToList(worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
      }

   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (worldIn.getBlockState(pos).getMaterial() == Material.AIR && this.hasTileEntity(state)) {
         TileEntity tile = worldIn.getTileEntity(pos);
         if (tile instanceof SnowTile) {
            IBlockState newState = ((SnowTile)tile).getState();
            worldIn.removeTileEntity(pos);
            if (worldIn.setBlockState(pos, newState)) {
               newState.getBlock().neighborChanged(newState, worldIn, pos, newState.getBlock(), pos.down());
            }
         } else {
            worldIn.removeTileEntity(pos);
         }
      }

   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return ModConfig.placeSnowInBlock ? this.getContainedState(worldIn, pos).getBlock().isPassable(worldIn, pos) : super.isPassable(worldIn, pos);
   }

   public EnumPushReaction getMobilityFlag(IBlockState state) {
      return this.hasTileEntity(state) ? EnumPushReaction.BLOCK : EnumPushReaction.DESTROY;
   }

   public static boolean canFallThrough(IBlockState state, World worldIn, BlockPos pos) {
      return state.getCollisionBoundingBox(worldIn, pos) == null;
   }

   public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
      if (ModConfig.placeSnowInBlock && (layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.CUTOUT_MIPPED) && ((Boolean)state.getValue(TILE)).booleanValue()) {
         return true;
      } else {
         return layer == BlockRenderLayer.SOLID;
      }
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (ModConfig.placeSnowInBlock) {
         if (((Boolean)state.getValue(TILE)).booleanValue()) {
            IBlockState contained = this.getContainedState(worldIn, pos);
            if (isCompatBlock(contained.getBlock())) {
               try {
                  if (contained.getBlock().onBlockActivated(worldIn, pos, contained, playerIn, hand, facing, hitX, hitY, hitZ)) {
                     return worldIn.setBlockState(pos, state.withProperty(TILE, Boolean.valueOf(false)));
                  }
               } catch (Throwable var14) {
                  ;
               }
            }
         } else {
            ItemStack stack = playerIn.getHeldItem(hand);
            Block block = Block.getBlockFromItem(stack.getItem());
            if (block != null && block != Blocks.AIR) {
               IBlockState state2 = block.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, stack.getMetadata(), playerIn, hand);
               if (state2 != null && canContainState(state2) && state2.getBlock().canPlaceBlockOnSide(worldIn, pos, EnumFacing.UP)) {
                  if (!worldIn.isRemote) {
                     worldIn.setBlockState(pos, state2, 48);
                     int i = ((Integer)state.getValue(LAYERS)).intValue();
                     if (placeLayersOn(worldIn, pos, i, false, true, 3) && !playerIn.isCreative()) {
                        stack.shrink(1);
                     }
                  }

                  return true;
               }
            }
         }
      }

      return false;
   }

   public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer player) {
      if (!worldIn.isRemote) {
         IBlockState state = worldIn.getBlockState(pos);
         if (ModConfig.placeSnowInBlock && ((Boolean)state.getValue(TILE)).booleanValue()) {
            try {
               IBlockState contained = this.getContainedState(worldIn, pos);
               if (contained.getBlockHardness(worldIn, pos) == 0.0F) {
                  worldIn.playEvent(2001, pos, Block.getStateId(contained));
                  contained.getBlock().harvestBlock(worldIn, player, pos, contained, (TileEntity)null, player.getHeldItemMainhand());
                  worldIn.setBlockState(pos, state.withProperty(TILE, Boolean.valueOf(false)));
               }
            } catch (Exception var6) {
               ;
            }
         }

      }
   }

   public static boolean isCompatBlock(Block block) {
      return ModConfig.snowLoggableBlocksSet.contains(block.getRegistryName());
   }
}
