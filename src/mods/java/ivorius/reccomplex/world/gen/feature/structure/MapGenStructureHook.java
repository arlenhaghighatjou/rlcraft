package ivorius.reccomplex.world.gen.feature.structure;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import ivorius.reccomplex.world.gen.feature.decoration.RCBiomeDecorator;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.VanillaDecorationGeneration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.tuple.Pair;

public class MapGenStructureHook extends MapGenStructure {
   public MapGenStructure base;
   public RCBiomeDecorator.DecorationType decorationType;

   public MapGenStructureHook(MapGenStructure base, RCBiomeDecorator.DecorationType decorationType) {
      this.base = base;
      this.decorationType = decorationType;
   }

   public MapGenStructureHook(MapGenStructure base) {
      this.base = base;
   }

   public static Long2ObjectMap<StructureStart> getStructureMap(MapGenStructure gen) {
      return (Long2ObjectMap)ReflectionHelper.getPrivateValue(MapGenStructure.class, gen, "structureMap", "structureMap");
   }

   public static void initializeStructureData(MapGenStructure gen, World world) {
      Method method = ReflectionHelper.findMethod(MapGenStructure.class, "initializeStructureData", "initializeStructureData", new Class[]{World.class});

      try {
         method.invoke(gen, world);
      } catch (InvocationTargetException | IllegalAccessException var4) {
         var4.printStackTrace();
      }

   }

   public static void setStructureStart(MapGenStructure gen, int x, int z, StructureStart start) {
      Method method = ReflectionHelper.findMethod(MapGenStructure.class, "setStructureStart", "setStructureStart", new Class[]{Integer.TYPE, Integer.TYPE, StructureStart.class});

      try {
         method.invoke(gen, x, z, start);
      } catch (InvocationTargetException | IllegalAccessException var6) {
         var6.printStackTrace();
      }

   }

   public static StructureStart getStructureStart(MapGenStructure gen, ChunkPos chunkPos) {
      return (StructureStart)getStructureMap(gen).get(ChunkPos.asLong(chunkPos.x, chunkPos.z));
   }

   public String getStructureName() {
      String name = this.base != null ? this.base.getStructureName() : null;
      return name != null ? name : "";
   }

   public boolean generateStructure(World worldIn, EaglercraftRandom randomIn, ChunkPos chunkCoord) {
      return this.base.generateStructure(worldIn, randomIn, chunkCoord);
   }

   public boolean isInsideStructure(BlockPos pos) {
      return this.base.isInsideStructure(pos);
   }

   public boolean isPositionInStructure(World worldIn, BlockPos pos) {
      return this.base.isPositionInStructure(worldIn, pos);
   }

   @Nullable
   public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
      return this.base.getNearestStructurePos(worldIn, pos, findUnexplored);
   }

   public boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
      Method method = ReflectionHelper.findMethod(MapGenStructure.class, "canSpawnStructureAtCoords", "canSpawnStructureAtCoords", new Class[]{Integer.TYPE, Integer.TYPE});

      try {
         return ((Boolean)method.invoke(this.base, chunkX, chunkZ)).booleanValue();
      } catch (InvocationTargetException | IllegalAccessException var5) {
         var5.printStackTrace();
         return false;
      }
   }

   protected StructureStart getStructureStart(int chunkX, int chunkZ) {
      return null;
   }

   public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
      this.world = worldIn;
      WorldServer server = (WorldServer)worldIn;
      this.rand.setSeed(worldIn.getSeed());
      long j = this.rand.nextLong();
      long k = this.rand.nextLong();
      initializeStructureData(this.base, this.world);
      Long2ObjectMap<StructureStart> map = getStructureMap(this.base);
      LongSet before = new LongOpenHashSet(map.keySet());
      this.base.generate(worldIn, x, z, primer);
      Sets.newHashSet(Collections2.filter(map.keySet(), Predicates.not(Predicates.in(before)))).forEach((key) -> {
         StructureStart start = (StructureStart)map.get(key);
         if (start.isSizeableStructure()) {
            this.rand.setSeed(j * (long)start.getChunkPosX() ^ k * (long)start.getChunkPosZ() ^ worldIn.getSeed());
            Pair<Structure<?>, VanillaDecorationGeneration> selected = RCBiomeDecorator.selectDecoration(server, this.rand, new BlockPos(start.getChunkPosX() * 16, 0, start.getChunkPosZ() * 16), this.getDecorationType(start));
            if (selected != null) {
               start.getComponents().clear();
               setStructureStart(this.base, start.getChunkPosX(), start.getChunkPosZ(), start);
               RCBiomeDecorator.generate(selected, server, new ChunkPos(start.getChunkPosX(), start.getChunkPosZ()), this.rand);
            }
         }

      });
   }

   public RCBiomeDecorator.DecorationType getDecorationType(StructureStart start) {
      return this.decorationType;
   }
}
