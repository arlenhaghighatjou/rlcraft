package com.endertech.minecraft.forge.world;

import com.endertech.common.CommonTime;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.api.ISmokeContainer;
import com.endertech.minecraft.forge.api.IWind;
import com.endertech.minecraft.forge.blocks.BlockState;
import com.endertech.minecraft.forge.blocks.ForgeBlock;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.items.ForgeItem;
import com.endertech.minecraft.forge.math.ForgeMath;
import com.endertech.minecraft.forge.math.Vect3d;
import com.endertech.minecraft.forge.units.ItemState;
import com.endertech.minecraft.forge.units.UnitId;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public final class ForgeWorld {
   public ForgeWorld() {
   }

   public static boolean isOreBlock(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      ItemStack stack = state.getBlock().getItem(world, pos, state);

      for(String name : ForgeItem.getOreDictNamesFor(stack)) {
         if (name.startsWith("ore")) {
            return true;
         }
      }

      return false;
   }

   public static IWind getWindAt(World world, BlockPos pos) {
      IWind wind = (IWind)Wind.Weather2.get(world, pos).orElse((Object)null);
      if (wind != null) {
         return wind;
      } else {
         ForgeWorld.WorldData data = getData(world);
         Biome biome = world.getBiome(pos);
         return data.getWind(biome);
      }
   }

   public static void explodeBlock(World world, BlockPos pos, float explosionSize, boolean fire, boolean dropAsItem, @Nullable Entity exploder) {
      IBlockState state = world.getBlockState(pos);
      if (dropAsItem) {
         state.getBlock().dropBlockAsItem(world, pos, state, 0);
      }

      world.newExplosion(exploder, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), explosionSize, fire, fire);
      world.setBlockToAir(pos);
   }

   public static void scheduleBlockExplosion(World world, BlockPos pos, CommonTime.Interval delay, float size, boolean fire, boolean dropAsItem, @Nullable Entity exploder) {
      ForgeWorld.WorldData data = getData(world);
      ForgeWorld.ScheduledExplosion explosion = new ForgeWorld.ScheduledExplosion(world, pos, delay, size, fire, dropAsItem, exploder);
      data.scheduledExplosions.put(pos, explosion);
   }

   public static boolean isServerSide(World world) {
      return world != null && !world.isRemote;
   }

   public static boolean isClientSide(World world) {
      return world != null && world.isRemote;
   }

   @Nullable
   public static Entity findEntity(World world, int id) {
      return world != null ? world.getEntityByID(id) : null;
   }

   public static Block getBlock(World world, BlockPos pos) {
      return world != null && pos != null ? world.getBlockState(pos).getBlock() : Blocks.AIR;
   }

   public static Vect3d getBlockCenter(BlockPos pos) {
      return Vect3d.from(pos).add(ForgeMath.getBBCenter(Block.FULL_BLOCK_AABB));
   }

   public static int getBlockMeta(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      return getBlock(world, pos).getMetaFromState(state);
   }

   public static BlockState getBlockState(World world, BlockPos pos) {
      return BlockState.from(world.getBlockState(pos));
   }

   public static BlockState getBlockDroppedState(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      ItemStack droppedItem = block.getItem(world, pos, state);
      int meta = droppedItem.getMetadata();
      return BlockState.from(block, meta);
   }

   public static ItemState getItemStateOf(World world, BlockPos pos, IBlockState state) {
      ItemStack stack = state.getBlock().getItem(world, pos, state);
      return ItemState.from(stack);
   }

   public static Chunk getChunk(World world, ChunkPos pos) {
      return world.getChunkFromChunkCoords(pos.x, pos.z);
   }

   public static boolean isAirBlock(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      return isAirBlock(state);
   }

   public static boolean isAirBlock(IBlockState state) {
      return state.getBlock() == Blocks.AIR;
   }

   public static boolean isForgeBlock(World world, BlockPos pos) {
      Block block = world.getBlockState(pos).getBlock();
      return block instanceof ForgeBlock;
   }

   public static boolean isVerticalOpaque(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      return state.isSideSolid(world, pos, EnumFacing.UP) && state.isSideSolid(world, pos, EnumFacing.DOWN);
   }

   public static boolean isHorizOpaque(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      return state.isSideSolid(world, pos, EnumFacing.WEST) && state.isSideSolid(world, pos, EnumFacing.EAST) && state.isSideSolid(world, pos, EnumFacing.SOUTH) && state.isSideSolid(world, pos, EnumFacing.NORTH);
   }

   @Nullable
   public static TileEntity getTileEntity(World world, BlockPos pos) {
      return world != null && pos != null ? world.getTileEntity(pos) : null;
   }

   public static void spawnParticle(World world, EnumParticleTypes type, Vect3d pos, Vect3d motion) {
      if (world != null) {
         world.spawnParticle(type, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z, new int[0]);
      }

   }

   /** @deprecated */
   @Nullable
   @Deprecated
   public static BlockPos findAroundInHorizPlane(World world, BlockState state, BlockPos startPos, int maxRadius) {
      BlockPos pos = startPos;
      int radius = 1;
      int turnsCount = 0;

      while(radius <= maxRadius) {
         for(EnumFacing facing : ForgeWorld.Direction.CLOCKWISE_HORIZONTALS) {
            if (turnsCount > 1) {
               ++radius;
               turnsCount = 0;
            }

            for(int n = 1; n <= radius; ++n) {
               pos = pos.offset(facing);
               if (getBlockState(world, pos).matches(state)) {
                  return pos;
               }
            }

            ++turnsCount;
         }
      }

      return null;
   }

   @Nullable
   public static RayTraceResult rayTraceBlocks(World world, Vect3d curPos, Vect3d nextPos, boolean includeLiquids) {
      return world.rayTraceBlocks(curPos.toVec3d(), nextPos.toVec3d(), includeLiquids);
   }

   @Nullable
   public static RayTraceResult rayTraceBlocks(final World world, Vec3d vecStart, final Vec3d vecEnd, final boolean includeLiquids, final BiPredicate<World, BlockPos> ignore) {
      if (!ForgeMath.isNaN(vecStart) && !ForgeMath.isNaN(vecEnd)) {
         BlockPos posStart = new BlockPos(vecStart);
         BlockPos posEnd = new BlockPos(vecEnd);
         class 1Helper {
            private RayTraceResult collision;
            final double max = 999.0D;
            final double min = -1.0E-4D;

            _Helper/* $FF was: 1Helper*/() {
            }

            @Nullable
            public RayTraceResult getLastCollision() {
               return this.collision;
            }

            public boolean collisionWithBlock(Vec3d vecStart, BlockPos pos) {
               IBlockState state = world.getBlockState(pos);
               if (state.getBlock().canCollideCheck(state, includeLiquids) && !ignore.test(world, pos)) {
                  this.collision = state.collisionRayTrace(world, pos, vecStart, vecEnd);
               }

               return this.collision != null;
            }

            public double calcFrom(int posStart, int posEnd) {
               if (posStart < posEnd) {
                  return (double)(posStart + 1);
               } else {
                  return posStart > posEnd ? (double)posStart : 999.0D;
               }
            }

            public double calcDir(int posStart, int posEnd, double vecStart, double vecEndx) {
               double dir = 999.0D;
               if (posStart != posEnd) {
                  dir = (this.calcFrom(posStart, posEnd) - vecStart) / (vecEnd - vecStart);
               }

               if (dir == -0.0D) {
                  dir = -1.0E-4D;
               }

               return dir;
            }
         }

         1Helper helper = new 1Helper();
         if (helper.collisionWithBlock(vecStart, posStart)) {
            return helper.getLastCollision();
         } else {
            for(int i = 0; i < 200; ++i) {
               if (ForgeMath.isNaN(vecStart) || posStart.equals(posEnd)) {
                  return null;
               }

               double fromX = helper.calcFrom(posStart.getX(), posEnd.getX());
               double fromY = helper.calcFrom(posStart.getY(), posEnd.getY());
               double fromZ = helper.calcFrom(posStart.getZ(), posEnd.getZ());
               double dirX = helper.calcDir(posStart.getX(), posEnd.getX(), vecStart.x, vecEnd.x);
               double dirY = helper.calcDir(posStart.getY(), posEnd.getY(), vecStart.y, vecEnd.y);
               double dirZ = helper.calcDir(posStart.getZ(), posEnd.getZ(), vecStart.z, vecEnd.z);
               double lenX = vecEnd.x - vecStart.x;
               double lenY = vecEnd.y - vecStart.y;
               double lenZ = vecEnd.z - vecStart.z;
               EnumFacing facing;
               if (dirX < dirY && dirX < dirZ) {
                  facing = posEnd.getX() > posStart.getX() ? EnumFacing.WEST : EnumFacing.EAST;
                  vecStart = new Vec3d(fromX, vecStart.y + lenY * dirX, vecStart.z + lenZ * dirX);
               } else if (dirY < dirZ) {
                  facing = posEnd.getY() > posStart.getY() ? EnumFacing.DOWN : EnumFacing.UP;
                  vecStart = new Vec3d(vecStart.x + lenX * dirY, fromY, vecStart.z + lenZ * dirY);
               } else {
                  facing = posEnd.getZ() > posStart.getZ() ? EnumFacing.NORTH : EnumFacing.SOUTH;
                  vecStart = new Vec3d(vecStart.x + lenX * dirZ, vecStart.y + lenY * dirZ, fromZ);
               }

               posStart = (new BlockPos(vecStart)).add(facing == EnumFacing.EAST ? -1 : 0, facing == EnumFacing.UP ? -1 : 0, facing == EnumFacing.SOUTH ? -1 : 0);
               if (helper.collisionWithBlock(vecStart, posStart)) {
                  return helper.getLastCollision();
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   @Nullable
   public static RayTraceResult rayTraceEntities(World world, Vec3d start, Vec3d end, Predicate<Entity> ignore) {
      List<Entity> entities = world.getEntitiesInAABBexcluding((Entity)null, (new AxisAlignedBB(start, end)).grow(1.0D), (ent) -> {
         return ent.canBeCollidedWith() && EntitySelectors.NOT_SPECTATING.test(ent) && !ignore.test(ent);
      });
      List<RayTraceResult> hits = new ArrayList();

      for(Entity entity : entities) {
         AxisAlignedBB bb = entity.getEntityBoundingBox().grow((double)entity.getCollisionBorderSize());
         RayTraceResult hit = bb.calculateIntercept(start, end);
         if (hit != null) {
            hits.add(new RayTraceResult(entity, hit.hitVec));
         }
      }

      return getClosestHitTo(start, hits);
   }

   @Nullable
   public static RayTraceResult rayTraceAll(World world, Vec3d start, Vec3d end, boolean includeLiquids, BiPredicate<World, BlockPos> ignoreBlocks, Predicate<Entity> ignoreEntities) {
      RayTraceResult blockHit = rayTraceBlocks(world, start, end, includeLiquids, ignoreBlocks);
      RayTraceResult entityHit = rayTraceEntities(world, start, end, ignoreEntities);
      return getClosestHitTo(start, Arrays.asList(blockHit, entityHit));
   }

   @Nullable
   public static RayTraceResult getClosestHitTo(Vec3d point, Collection<RayTraceResult> hits) {
      RayTraceResult closestHit = null;
      double minDistance = Double.MAX_VALUE;

      for(RayTraceResult hit : hits) {
         if (hit != null) {
            double distance = point.squareDistanceTo(hit.hitVec);
            if (distance < minDistance) {
               minDistance = distance;
               closestHit = hit;
            }
         }
      }

      return closestHit;
   }

   /** @deprecated */
   @Deprecated
   public static void hitThroughBlock(LeftClickBlock event, BiPredicate<World, BlockPos> predicate) {
      World world = event.getWorld();
      event.setCanceled(true);
      if (!isServerSide(world)) {
         EntityPlayer player = event.getEntityPlayer();
         Minecraft mc = Minecraft.getMinecraft();
         PlayerControllerMP controller = mc.playerController;
         double reachDistance = (double)controller.getBlockReachDistance();
         Vec3d vecEyes = player.getPositionEyes(1.0F);
         Vec3d vecLook = player.getLook(1.0F);
         Vec3d vecEnd = vecEyes.add(vecLook.scale(reachDistance));
         RayTraceResult hit = rayTraceAll(world, vecEyes, vecEnd, false, predicate, (ent) -> {
            return (void)(ent == player);
         });
         if (hit != null) {
            switch(hit.typeOfHit) {
            case ENTITY:
               controller.attackEntity(player, hit.entityHit);
               break;
            case BLOCK:
               BlockPos hitpos = hit.getBlockPos();
               if (event.getPos().equals(hitpos) || world.isAirBlock(hitpos)) {
                  return;
               }

               if (controller.onPlayerDamageBlock(hitpos, hit.sideHit)) {
                  mc.effectRenderer.addBlockHitEffects(hitpos, hit);
               }

               controller.clickBlock(hitpos, hit.sideHit);
            case MISS:
            }

         }
      }
   }

   @Nonnull
   public static ForgeWorld.WorldData getData(World world) {
      ForgeWorld.WorldData data = (ForgeWorld.WorldData)ForgeWorld.WorldData.DATA_MAP.get(world);
      if (data == null) {
         data = new ForgeWorld.WorldData(world);
         ForgeWorld.WorldData.DATA_MAP.put(world, data);
      }

      return data;
   }

   public static class Direction {
      public static final EnumFacing[] CLOCKWISE_HORIZONTALS = new EnumFacing[]{EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.NORTH};
      private static final List<EnumFacing> SHUFFLED_HORIZONTALS = Arrays.asList(CLOCKWISE_HORIZONTALS.clone());
      private static final List<EnumFacing> SHUFFLED_ALL = Arrays.asList(EnumFacing.VALUES.clone());

      public Direction() {
      }

      public static List<EnumFacing> getShuffledAll() {
         Collections.shuffle(SHUFFLED_ALL);
         return Collections.unmodifiableList(SHUFFLED_ALL);
      }

      public static List<EnumFacing> getShuffledHorizontals() {
         Collections.shuffle(SHUFFLED_HORIZONTALS);
         return Collections.unmodifiableList(SHUFFLED_HORIZONTALS);
      }
   }

   public static class Position {
      public Position() {
      }

      public static List<BlockPos> getAroundHoriz(BlockPos centerPos, boolean includeCorners, BlockPos... positions) {
         List<BlockPos> blocks = new ArrayList();
         blocks.add(centerPos.west());
         blocks.add(centerPos.east());
         blocks.add(centerPos.north());
         blocks.add(centerPos.south());
         if (includeCorners) {
            blocks.add(centerPos.west().north());
            blocks.add(centerPos.west().south());
            blocks.add(centerPos.east().north());
            blocks.add(centerPos.east().south());
         }

         for(BlockPos pos : positions) {
            blocks.add(pos);
         }

         return blocks;
      }

      public static List<ChunkPos> getAroundHoriz(ChunkPos centerPos, boolean includeCorners, ChunkPos... positions) {
         List<BlockPos> blocks = getAroundHoriz(new BlockPos(centerPos.x, 0, centerPos.z), includeCorners);
         List<ChunkPos> chunks = new ArrayList();

         for(BlockPos pos : blocks) {
            chunks.add(new ChunkPos(pos.getX(), pos.getZ()));
         }

         for(ChunkPos pos : positions) {
            chunks.add(pos);
         }

         return chunks;
      }

      public static List<BlockPos> getAroundHoriz(BlockPos centerPos, int radius, boolean includeCorners) {
         List<BlockPos> blocks = new ArrayList();
         if (radius > 0) {
            int diameter = radius * 2 + (!includeCorners ? 1 : 0);
            blocks.addAll(getLine(centerPos.west(radius).north(radius), EnumFacing.EAST, diameter, !includeCorners));
            blocks.addAll(getLine(centerPos.north(radius).east(radius), EnumFacing.SOUTH, diameter, !includeCorners));
            blocks.addAll(getLine(centerPos.east(radius).south(radius), EnumFacing.WEST, diameter, !includeCorners));
            blocks.addAll(getLine(centerPos.south(radius).west(radius), EnumFacing.NORTH, diameter, !includeCorners));
         } else {
            blocks.add(centerPos);
         }

         return blocks;
      }

      public static List<ChunkPos> getAroundHoriz(ChunkPos centerPos, int radius, boolean includeCorners) {
         List<ChunkPos> chunks = new ArrayList();

         for(BlockPos pos : getAroundHoriz(new BlockPos(centerPos.x, 0, centerPos.z), radius, includeCorners)) {
            chunks.add(new ChunkPos(pos.getX(), pos.getZ()));
         }

         return chunks;
      }

      public static List<BlockPos> getLine(BlockPos startPos, EnumFacing direction, int length, boolean excludeEnds) {
         List<BlockPos> blocks = new ArrayList();

         for(int i = 0; i < length; ++i) {
            if (!excludeEnds || i != 0 && i != length - 1) {
               BlockPos pos = startPos.offset(direction, i);
               blocks.add(pos);
            }
         }

         return blocks;
      }

      public static List<BlockPos> getHorizPlane(BlockPos centerPos, int radius, boolean includeCorners) {
         List<BlockPos> blocks = new ArrayList();

         for(int r = radius; r >= 1; --r) {
            blocks.addAll(getAroundHoriz(centerPos, r, includeCorners));
         }

         blocks.add(centerPos);
         return blocks;
      }

      public static List<BlockPos> getAroundCube(BlockPos startPos) {
         return getAroundHoriz(startPos, false, startPos.up(), startPos.down());
      }

      public static List<BlockPos> getAroundCube(World world, BlockPos centerPos, BiPredicate<World, BlockPos> validPos) {
         List<BlockPos> validPositions = new ArrayList();

         for(BlockPos pos : getAroundCube(centerPos)) {
            if (validPos.test(world, pos)) {
               validPositions.add(pos);
            }
         }

         return validPositions;
      }

      public static List<BlockPos> getAroundCube(BlockPos centerPos, int radius, boolean includeEdges) {
         List<BlockPos> blocks = new ArrayList();

         for(int offset = -radius; offset <= radius; ++offset) {
            BlockPos pos = centerPos.offset(EnumFacing.UP, offset);
            if (Math.abs(offset) == Math.abs(radius)) {
               blocks.addAll(getHorizPlane(pos, radius, includeEdges));
            } else {
               blocks.addAll(getAroundHoriz(pos, radius, includeEdges));
            }
         }

         return blocks;
      }

      public static double getDistance(BlockPos posA, BlockPos posB) {
         return Vect3d.distance(Vect3d.from(posA), Vect3d.from(posB));
      }

      public static BlockPos getLastInLine(World world, BlockPos startPos, BiPredicate<World, BlockPos> validation, EnumFacing direction) {
         int offset;
         for(offset = 1; validation.test(world, startPos.offset(direction, offset)); ++offset) {
            ;
         }

         return startPos.offset(direction, offset - 1);
      }

      public static BlockPos withY(BlockPos pos, int y) {
         return new BlockPos(pos.getX(), y, pos.getZ());
      }

      public static BlockPos withY(BlockPos pos, double y) {
         return new BlockPos((double)pos.getX(), y, (double)pos.getZ());
      }
   }

   public static class ScheduledExplosion {
      public final World world;
      public final BlockPos pos;
      public final CommonTime.Interval delay;
      public final float size;
      public final boolean fire;
      public final boolean dropAsItem;
      @Nullable
      public final Entity exploder;
      protected final CommonTime.Stamp stamp = CommonTime.Stamp.now();

      public ScheduledExplosion(World world, BlockPos pos, CommonTime.Interval delay, float size, boolean fire, boolean dropAsItem, Entity exploder) {
         this.world = world;
         this.pos = pos;
         this.delay = delay;
         this.size = size;
         this.fire = fire;
         this.dropAsItem = dropAsItem;
         this.exploder = exploder;
      }

      public boolean timePast() {
         return CommonTime.Interval.passedFrom(this.stamp).moreThan(this.delay);
      }
   }

   public static class SmokeContainers<C extends ISmokeContainer> {
      public static int maxVentPipeLength = 32;
      public static int ventInhaleDistance = 4;
      public static int maxBlocksInMultiblock = 81;
      private static final Logger LOGGER = ForgeMain.instance.getLogger();
      private static final String FORMAT_REGISTERED_AS = "registered: <{}> as {}";
      private static final String FORMAT_ALREADY_REGISTERED_AS = "already registered: <{}> as {}!";
      private static final String FORMAT_SKIPPED_NO_RELATED_BLOCK = "SKIPPED: <{}> because no related block found.";
      private final Map<BlockState, C> map = new ConcurrentHashMap();
      private final String name;

      public SmokeContainers(Class<C> containerClass) {
         this.name = containerClass.getSimpleName();
      }

      @Nullable
      public C find(World world, BlockPos pos) {
         BlockState state = ForgeWorld.getBlockState(world, pos);
         return (C)this.find(state);
      }

      @Nullable
      public C find(BlockState state) {
         C container = this.map.get(state);
         return (C)(container != null ? container : (ISmokeContainer)this.map.get(state.withMetaAll()));
      }

      public void register(C container) {
         if (container != null) {
            UnitId id = container.getRelatedId();
            if (id.getBlockState().exists()) {
               if (this.map.putIfAbsent(id.getBlockState(), container) == null) {
                  LOGGER.debug("registered: <{}> as {}", id, container);
               } else {
                  LOGGER.warn("already registered: <{}> as {}!", id, this.name);
               }
            } else {
               LOGGER.debug("SKIPPED: <{}> because no related block found.", id);
            }
         } else {
            LOGGER.error("{} is NULL!", this.name);
         }

      }

      public static boolean isChimney(World world, BlockPos pos) {
         IBlockState state = world.getBlockState(pos);
         Block block = state.getBlock();
         return block instanceof ISmokeContainer && ((ISmokeContainer)block).isChimney(state);
      }

      public static boolean isVent(World world, BlockPos pos) {
         IBlockState state = world.getBlockState(pos);
         Block block = state.getBlock();
         return block instanceof ISmokeContainer && ((ISmokeContainer)block).isVent(state);
      }

      public static boolean isPump(World world, BlockPos pos) {
         IBlockState state = world.getBlockState(pos);
         Block block = state.getBlock();
         return block instanceof ISmokeContainer && ((ISmokeContainer)block).isPump(state);
      }

      public static boolean isActive(World world, BlockPos pos) {
         IBlockState state = world.getBlockState(pos);
         Block block = state.getBlock();
         return block instanceof ISmokeContainer && ((ISmokeContainer)block).isActive(world, pos);
      }

      public static boolean isActivePump(World world, BlockPos pos) {
         return isPump(world, pos) && isActive(world, pos);
      }

      public static boolean isCoveredByChimney(World world, BlockPos pos) {
         return isChimney(world, pos.up());
      }

      public static BlockPos getTopmostChimney(World world, BlockPos startPos) {
         return ForgeWorld.Position.getLastInLine(world, startPos, ForgeWorld.SmokeContainers::isChimney, EnumFacing.UP);
      }

      public static BlockPos getBottommostChimney(World world, BlockPos startPos) {
         return ForgeWorld.Position.getLastInLine(world, startPos, ForgeWorld.SmokeContainers::isChimney, EnumFacing.DOWN);
      }

      public static List<BlockPos> getVentsAround(World world, BlockPos centerPos) {
         List<BlockPos> vents = new ArrayList();

         for(BlockPos pos : ForgeWorld.Position.getAroundHoriz(centerPos, false)) {
            if (isVent(world, pos)) {
               vents.add(pos);
            }
         }

         return vents;
      }

      /** @deprecated */
      @Deprecated
      public static int addSmokeOutletsAround(World world, BlockPos startPos, int maxPathLength, int maxOutletsAmount, List<BlockPos> outlets, BiPredicate<World, BlockPos> isOutlet) {
         int outletsCount = 0;
         List<BlockPos> conductors = new ArrayList();
         conductors.add(startPos);
         int startIndex = 0;

         for(int pathLength = 1; pathLength <= maxPathLength; ++pathLength) {
            int endIndex = conductors.size() - 1;

            for(int index = startIndex; index <= endIndex; ++index) {
               if (outletsCount >= maxOutletsAmount) {
                  return outletsCount;
               }

               BlockPos pos = (BlockPos)conductors.get(index);

               for(BlockPos checkPos : ForgeWorld.Position.getAroundHoriz(pos, false)) {
                  if (!checkPos.equals(startPos)) {
                     if (isVent(world, checkPos) && !conductors.contains(checkPos)) {
                        conductors.add(checkPos);
                     } else if (!outlets.contains(checkPos) && isOutlet.test(world, checkPos)) {
                        outlets.add(checkPos);
                     }
                  }
               }
            }

            startIndex = endIndex + 1;
         }

         return outletsCount;
      }
   }

   public static class SnowMelter {
      public SnowMelter() {
      }

      public static boolean isSnow(World world, BlockPos pos) {
         return world.getBlockState(pos).getBlock() == Blocks.SNOW_LAYER;
      }

      private static BlockPos getSnowTopPos(World world, BlockPos startPos) {
         int offset;
         for(offset = 1; isSnow(world, startPos.up(offset)); ++offset) {
            ;
         }

         return startPos.up(offset - 1);
      }

      public static void meltLayerAt(World world, BlockPos pos) {
         if (isSnow(world, pos)) {
            BlockPos meltPos = getSnowTopPos(world, pos);
            IBlockState state = world.getBlockState(meltPos);
            int layersAmount = ((Integer)state.getValue(BlockSnow.LAYERS)).intValue();
            if (layersAmount > 1) {
               world.setBlockState(meltPos, state.withProperty(BlockSnow.LAYERS, Integer.valueOf(layersAmount - 1)));
            } else {
               world.setBlockToAir(meltPos);
            }
         }

      }
   }

   @EventBusSubscriber
   public static class WorldData {
      static final Map<World, ForgeWorld.WorldData> DATA_MAP = new ConcurrentHashMap();
      public int smokeParticlesCount = 0;
      protected final Map<Integer, Wind> biomeWindMap = new ConcurrentHashMap();
      protected final Map<BlockPos, ForgeWorld.ScheduledExplosion> scheduledExplosions = new ConcurrentHashMap();
      private final World world;

      public WorldData(World world) {
         this.world = world;
      }

      public World getWorld() {
         return this.world;
      }

      @SubscribeEvent
      public static void onWorldLoad(Load event) {
         World world = event.getWorld();
         if (ForgeWorld.isServerSide(world)) {
            ForgeWorld.WorldData data = ForgeWorld.getData(world);
            data.initBiomesWinds();
         }

      }

      @SubscribeEvent
      public static void onWorldUnload(Unload event) {
         World world = event.getWorld();
         DATA_MAP.remove(world);
      }

      @SubscribeEvent
      public static void onWorldTick(WorldTickEvent event) {
         if (event.phase == Phase.START) {
            World world = event.world;
            ForgeWorld.WorldData data = ForgeWorld.getData(world);
            Wind.defaultWind.update(world);

            for(Wind wind : data.biomeWindMap.values()) {
               wind.update(world);
            }

            if (ForgeWorld.isServerSide(world)) {
               Iterator<ForgeWorld.ScheduledExplosion> iterator = data.scheduledExplosions.values().iterator();

               while(iterator.hasNext()) {
                  ForgeWorld.ScheduledExplosion explosion = (ForgeWorld.ScheduledExplosion)iterator.next();
                  if (explosion.timePast()) {
                     ForgeWorld.explodeBlock(world, explosion.pos, explosion.size, explosion.fire, explosion.dropAsItem, explosion.exploder);
                     iterator.remove();
                     break;
                  }
               }
            }

         }
      }

      @SubscribeEvent
      public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
         EntityPlayer player = event.player;
         World world = player.getEntityWorld();
         if (ForgeWorld.isServerSide(world) && player instanceof EntityPlayerMP) {
            ForgeWorld.WorldData data = ForgeWorld.getData(world);

            for(Wind wind : data.biomeWindMap.values()) {
               Wind.WindMsg message = new Wind.WindMsg(wind);
               ForgeMain.instance.getConnection().sendToPlayer(message, (EntityPlayerMP)player);
            }
         }

      }

      @SubscribeEvent
      public static void onPlayerTick(PlayerTickEvent event) {
         World world = event.player.getEntityWorld();
         if (ForgeWorld.isClientSide(world)) {
            Wind.defaultWind.update(world);
            ForgeWorld.WorldData data = ForgeWorld.getData(world);

            for(Wind wind : data.biomeWindMap.values()) {
               wind.update(world);
            }
         }

      }

      protected void initBiomesWinds() {
         ForgeRegistries.BIOMES.getEntries().stream().findFirst().map((entry) -> {
            return (Biome)entry.getValue();
         }).ifPresent((biome) -> {
            Wind.from(Biomes.createConfigFor(ForgeMain.instance, biome), Biome.getIdForBiome(biome));
         });

         for(Path path : UnitConfig.listCustomConfigs(Biomes.getConfigsBaseDir(ForgeMain.instance), (Class)null)) {
            UnitConfig config = new UnitConfig(path.toFile());
            int biomeId = Biomes.getId(config);
            boolean enabled = Biomes.isConfigEnabled(config);
            Wind wind = Wind.from(config, biomeId);
            if (enabled && !wind.equalsDefault()) {
               this.biomeWindMap.put(Integer.valueOf(biomeId), wind);
            }
         }

      }

      @Nonnull
      public Wind getWind(Biome biome) {
         int id = Biome.getIdForBiome(biome);
         return (Wind)this.biomeWindMap.getOrDefault(Integer.valueOf(id), Wind.defaultWind);
      }
   }
}
