package ivorius.reccomplex.world.gen.feature;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.utils.RCStructureBoundingBoxes;
import ivorius.reccomplex.utils.accessor.SafeReflector;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class HeightMapFreezer {
   protected static Method relightBlock;
   protected static Method propagateSkylightOcclusion;
   protected StructureBoundingBox boundingBox;
   protected World world;
   protected ChunkPos chunkMin;
   protected int[] chunkSize;
   protected TIntObjectMap<HeightMapFreezer.Entry> chunks = new TIntObjectHashMap();

   public HeightMapFreezer(StructureBoundingBox boundingBox, World world) {
      this.boundingBox = new StructureBoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
      this.world = world;
      this.chunkMin = new ChunkPos(StructureBoundingBoxes.min(boundingBox));
      ChunkPos chunkMax = new ChunkPos(StructureBoundingBoxes.max(boundingBox));
      this.chunkSize = new int[]{chunkMax.x - this.chunkMin.x + 1, chunkMax.z - this.chunkMin.z + 1};
   }

   public static HeightMapFreezer freeze(StructureBoundingBox boundingBox, World world) {
      HeightMapFreezer freezer = new HeightMapFreezer(boundingBox, world);
      freezer.initialize();
      return freezer;
   }

   protected void initialize() {
      for(ChunkPos pos : RCStructureBoundingBoxes.rasterize(this.boundingBox, false)) {
         this.chunks.put(this.index(pos).intValue(), new HeightMapFreezer.Entry(this.world.getChunkFromChunkCoords(pos.x, pos.z)));
      }

      RCStructureBoundingBoxes.streamMutablePositions(this.boundingBox).forEach((posx) -> {
         this.entry(pos).chunk.getHeightMap()[this.chunkSurfaceIndex(pos)] = this.world.getHeight() + 1;
      });
   }

   private HeightMapFreezer.Entry entry(BlockPos pos) {
      return pos.getX() >= this.boundingBox.minX && pos.getZ() >= this.boundingBox.minZ && pos.getX() <= this.boundingBox.maxX && pos.getZ() <= this.boundingBox.maxZ ? (HeightMapFreezer.Entry)this.chunks.get(this.index(new ChunkPos(pos)).intValue()) : null;
   }

   private Integer index(ChunkPos pos) {
      return (pos.x - this.chunkMin.x) * this.chunkSize[1] + (pos.z - this.chunkMin.z);
   }

   private int chunkSurfaceIndex(BlockPos pos) {
      int i = pos.getX() & 15;
      int k = pos.getZ() & 15;
      return k << 4 | i;
   }

   public void setBlockState(BlockPos pos, IBlockState state, int flags) {
      this.world.setBlockState(pos, state, flags);
      this.markBlock(pos, state);
   }

   public void markBlock(BlockPos pos, IBlockState state) {
      HeightMapFreezer.Entry entry = this.entry(pos);
      if (entry != null) {
         int light = state.getLightOpacity(this.world, pos);
         int surfaceIndex = this.chunkSurfaceIndex(pos);
         if (light > 0 && pos.getY() >= entry.heightMap[surfaceIndex]) {
            entry.heightMap[surfaceIndex] = pos.getY();
         }
      }

   }

   public void melt() {
      if (relightBlock == null) {
         relightBlock = ReflectionHelper.findMethod(Chunk.class, "relightBlock", "relightBlock", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE});
      }

      if (propagateSkylightOcclusion == null) {
         propagateSkylightOcclusion = ReflectionHelper.findMethod(Chunk.class, "propagateSkylightOcclusion", "propagateSkylightOcclusion", new Class[]{Integer.TYPE, Integer.TYPE});
      }

      RCStructureBoundingBoxes.streamMutablePositions(this.boundingBox).forEach((pos) -> {
         HeightMapFreezer.Entry entry = this.entry(pos);
         int surfaceIndex = this.chunkSurfaceIndex(pos);
         entry.chunk.getHeightMap()[surfaceIndex] = entry.original[surfaceIndex];
      });
      RCStructureBoundingBoxes.streamMutablePositions(this.boundingBox).forEach((pos) -> {
         HeightMapFreezer.Entry entry = this.entry(pos);
         int surfaceIndex = this.chunkSurfaceIndex(pos);
         if (entry.heightMap[surfaceIndex] >= entry.original[surfaceIndex]) {
            SafeReflector.invoke(entry.chunk, relightBlock, (Object)null, pos.getX() & 15, entry.heightMap[surfaceIndex] + 1, pos.getZ() & 15);
            SafeReflector.invoke(entry.chunk, propagateSkylightOcclusion, (Object)null, pos.getX() & 15, pos.getZ() & 15);
            if (this.world.provider.hasSkyLight()) {
               this.world.checkLight(new BlockPos(pos.getX(), entry.heightMap[surfaceIndex], pos.getZ()));
            }
         }

      });
   }

   private class Entry {
      public Chunk chunk;
      public int[] heightMap;
      public int[] original;

      public Entry(Chunk chunk) {
         this.chunk = chunk;
         this.original = (int[])chunk.getHeightMap().clone();
         this.heightMap = new int[this.original.length];
         Arrays.fill(this.heightMap, -1);
      }
   }
}
