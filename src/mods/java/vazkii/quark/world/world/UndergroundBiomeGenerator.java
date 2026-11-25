package vazkii.quark.world.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import vazkii.quark.base.handler.BiomeTypeConfigHandler;
import vazkii.quark.world.feature.UndergroundBiomes;

public class UndergroundBiomeGenerator extends MultiChunkFeatureGenerator {
   public final UndergroundBiomes.UndergroundBiomeInfo info;
   private final long seedXor;

   public UndergroundBiomeGenerator(UndergroundBiomes.UndergroundBiomeInfo info) {
      this.info = info;
      this.seedXor = (long)info.biome.getClass().toString().hashCode();
   }

   public boolean canGenerate(World world) {
      return this.info.enabled && this.info.dims.canSpawnHere(world);
   }

   public int getFeatureRadius() {
      return (int)Math.ceil((double)Math.max(this.info.minXSize + this.info.xVariation, this.info.minZSize + this.info.zVariation));
   }

   public void generateChunkPart(BlockPos src, EaglercraftRandom random, int chunkX, int chunkZ, World world) {
      int radiusX = this.info.minXSize + random.nextInt(this.info.xVariation);
      int radiusY = this.info.minYSize + random.nextInt(this.info.yVariation);
      int radiusZ = this.info.minZSize + random.nextInt(this.info.zVariation);
      this.apply(world, src, random, chunkX, chunkZ, radiusX, radiusY, radiusZ);
   }

   public BlockPos[] getSourcesInChunk(EaglercraftRandom random, int chunkX, int chunkZ) {
      return this.info.rarity > 0 && random.nextInt(this.info.rarity) == 0 ? new BlockPos[]{new BlockPos(chunkX * 16 + random.nextInt(16), this.info.minY + random.nextInt(this.info.maxY - this.info.minY), chunkZ * 16 + random.nextInt(16))} : new BlockPos[0];
   }

   public long modifyWorldSeed(long seed) {
      return seed ^ this.seedXor;
   }

   public boolean isSourceValid(World world, BlockPos pos) {
      Biome biome = world.getBiome(pos);
      return BiomeTypeConfigHandler.biomeTypeIntersectCheck(this.info.types, biome) && this.info.biome.isValidBiome(biome);
   }

   public void apply(World world, BlockPos center, EaglercraftRandom random, int chunkX, int chunkZ, int radiusX, int radiusY, int radiusZ) {
      int centerX = center.getX();
      int centerY = center.getY();
      int centerZ = center.getZ();
      double radiusX2 = (double)(radiusX * radiusX);
      double radiusY2 = (double)(radiusY * radiusY);
      double radiusZ2 = (double)(radiusZ * radiusZ);
      UndergroundBiomeGenerator.UndergroundBiomeGenerationContext context = new UndergroundBiomeGenerator.UndergroundBiomeGenerationContext();
      this.forEachChunkBlock(chunkX, chunkZ, center.getY() - radiusY, center.getY() + radiusY, (posx) -> {
         int x = pos.getX() - center.getX();
         int y = pos.getY() - center.getY();
         int z = pos.getZ() - center.getZ();
         double distX = (double)(x * x);
         double distY = (double)(y * y);
         double distZ = (double)(z * z);
         boolean inside = distX / radiusX2 + distY / radiusY2 + distZ / radiusZ2 <= 1.0D;
         if (inside) {
            this.info.biome.fill(world, center.add(x, y, z), context);
         }

      });
      context.floorList.forEach((posx) -> {
         this.info.biome.finalFloorPass(world, pos);
      });
      context.ceilingList.forEach((posx) -> {
         this.info.biome.finalCeilingPass(world, pos);
      });
      context.wallMap.keySet().forEach((posx) -> {
         this.info.biome.finalWallPass(world, pos);
      });
      context.insideList.forEach((posx) -> {
         this.info.biome.finalInsidePass(world, pos);
      });
      if (this.info.biome.hasDungeon() && world instanceof WorldServer && random.nextDouble() < this.info.biome.dungeonChance) {
         List<BlockPos> candidates = new ArrayList(context.wallMap.keySet());
         candidates.removeIf((posx) -> {
            BlockPos down = pos.down();
            IBlockState state = world.getBlockState(down);
            return this.info.biome.isWall(world, down, state) || state.getBlock().isAir(state, world, down);
         });
         if (!candidates.isEmpty()) {
            BlockPos pos = (BlockPos)candidates.get(world.rand.nextInt(candidates.size()));
            EnumFacing border = (EnumFacing)context.wallMap.get(pos);
            if (border != null) {
               this.info.biome.spawnDungeon((WorldServer)world, pos, border);
            }
         }
      }

   }

   public static class UndergroundBiomeGenerationContext {
      public final List<BlockPos> floorList = new LinkedList();
      public final List<BlockPos> ceilingList = new LinkedList();
      public final List<BlockPos> insideList = new LinkedList();
      public final Map<BlockPos, EnumFacing> wallMap = new HashMap();

      public UndergroundBiomeGenerationContext() {
      }
   }
}
