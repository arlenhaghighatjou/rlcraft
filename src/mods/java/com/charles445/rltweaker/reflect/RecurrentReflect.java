package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;

public class RecurrentReflect {
   public final Class c_WorldStructureGenerationData = Class.forName("ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData");
   public final Method m_WorldStructureGenerationData_get;
   public final Method m_WorldStructureGenerationData_structureEntriesIn;
   public final Method m_WorldStructureGenerationData_checkChunk;
   public final Field f_WorldStructureGenerationData_entryMap;
   public final Method m_WorldStructureGenerationData_removeEntry;
   public final Class c_WorldGenStructures;
   public final Method m_WorldGenStructures_complementStructuresInChunk;
   public final Method m_WorldGenStructures_planStaticStructuresInChunk;
   public final Method m_WorldGenStructures_distance;
   public final Method m_WorldGenStructures_randomSurfacePos;
   public final Class c_RCConfig;
   public final Field f_RCConfig_honorStructureGenerationOption;
   public final Field f_RCConfig_minDistToSpawnForGeneration;
   public final Method m_RCConfig_isGenerationEnabled_biome;
   public final Method m_RCConfig_isGenerationEnabled_worldprovider;
   public final Class c_StructureRegistry;
   public final Field f_StructureRegistry_instance;
   public final Class c_SelectivePlacer;
   public final Class c_Environment;
   public final Field f_Environment_biome;
   public final Class c_NaturalGeneration;
   public final Method m_NaturalGeneration_selectors;
   public final Method m_NaturalGeneration_getLimitations;
   public final Field f_NaturalGeneration_placer;
   public final Method m_NaturalGeneration_getGenerationWeight_params;
   public final Class c_NaturalGeneration$SpawnLimitation;
   public final Method m_NaturalGeneration$SpawnLimitation_areResolved;
   public final Class c_CachedStructureSelectors;
   public final Method m_CachedStructureSelectors_get;
   public final Class c_MixingStructureSelector;
   public final Method m_MixingStructureSelector_generatedStructures;
   public final Class c_SimpleLeveledRegistry;
   public final Method m_SimpleLeveledRegistry_id;
   public final Class c_Structure;
   public final Class c_GenerationType;
   public final Class c_StructureSpawnContext$GenerateMaturity;
   public final Class c_StructureGenerator;
   public final Constructor m_StructureGenerator_init;
   public final Method m_StructureGenerator_structure_structure;
   public final Method m_StructureGenerator_world_world;
   public final Method m_StructureGenerator_generationInfo_generationtype;
   public final Method m_StructureGenerator_seed_long;
   public final Method m_StructureGenerator_maturity_enum;
   public final Method m_StructureGenerator_randomPosition;
   public final Method m_StructureGenerator_fromCenter;
   public final Method m_StructureGenerator_partially;
   public final Method m_StructureGenerator_environment_none;
   public final Method m_StructureGenerator_generate;
   public final Class c_StructureGenerationEventLite$Post;
   public final Class c_IvVecMathHelper;
   public final Method m_IvVecMathHelper_distanceSQ_d_d;

   public RecurrentReflect() throws Exception {
      this.m_WorldStructureGenerationData_get = ReflectUtil.findMethod(this.c_WorldStructureGenerationData, "get");
      this.m_WorldStructureGenerationData_structureEntriesIn = ReflectUtil.findMethod(this.c_WorldStructureGenerationData, "structureEntriesIn");
      this.m_WorldStructureGenerationData_checkChunk = ReflectUtil.findMethod(this.c_WorldStructureGenerationData, "checkChunk");
      this.f_WorldStructureGenerationData_entryMap = ReflectUtil.findField(this.c_WorldStructureGenerationData, "entryMap");
      this.m_WorldStructureGenerationData_removeEntry = ReflectUtil.findMethod(this.c_WorldStructureGenerationData, "removeEntry");
      this.c_WorldGenStructures = Class.forName("ivorius.reccomplex.world.gen.feature.WorldGenStructures");
      this.m_WorldGenStructures_complementStructuresInChunk = ReflectUtil.findMethod(this.c_WorldGenStructures, "complementStructuresInChunk");
      this.m_WorldGenStructures_planStaticStructuresInChunk = ReflectUtil.findMethod(this.c_WorldGenStructures, "planStaticStructuresInChunk");
      this.m_WorldGenStructures_distance = ReflectUtil.findMethod(this.c_WorldGenStructures, "distance");
      this.m_WorldGenStructures_randomSurfacePos = ReflectUtil.findMethod(this.c_WorldGenStructures, "randomSurfacePos");
      this.c_RCConfig = Class.forName("ivorius.reccomplex.RCConfig");
      this.f_RCConfig_honorStructureGenerationOption = ReflectUtil.findField(this.c_RCConfig, "honorStructureGenerationOption");
      this.f_RCConfig_minDistToSpawnForGeneration = ReflectUtil.findField(this.c_RCConfig, "minDistToSpawnForGeneration");
      this.m_RCConfig_isGenerationEnabled_biome = this.c_RCConfig.getDeclaredMethod("isGenerationEnabled", Biome.class);
      this.m_RCConfig_isGenerationEnabled_biome.setAccessible(true);
      this.m_RCConfig_isGenerationEnabled_worldprovider = this.c_RCConfig.getDeclaredMethod("isGenerationEnabled", WorldProvider.class);
      this.m_RCConfig_isGenerationEnabled_worldprovider.setAccessible(true);
      this.c_StructureRegistry = Class.forName("ivorius.reccomplex.world.gen.feature.structure.StructureRegistry");
      this.f_StructureRegistry_instance = ReflectUtil.findField(this.c_StructureRegistry, "INSTANCE");
      this.c_SelectivePlacer = Class.forName("ivorius.reccomplex.world.gen.feature.structure.generic.placement.SelectivePlacer");
      this.c_Environment = Class.forName("ivorius.reccomplex.world.gen.feature.structure.Environment");
      this.f_Environment_biome = ReflectUtil.findField(this.c_Environment, "biome");
      this.c_NaturalGeneration = Class.forName("ivorius.reccomplex.world.gen.feature.structure.generic.generation.NaturalGeneration");
      this.m_NaturalGeneration_selectors = ReflectUtil.findMethod(this.c_NaturalGeneration, "selectors");
      this.m_NaturalGeneration_getLimitations = ReflectUtil.findMethod(this.c_NaturalGeneration, "getLimitations");
      this.f_NaturalGeneration_placer = ReflectUtil.findField(this.c_NaturalGeneration, "placer");
      this.m_NaturalGeneration_getGenerationWeight_params = this.c_NaturalGeneration.getDeclaredMethod("getGenerationWeight", WorldProvider.class, Biome.class);
      this.c_NaturalGeneration$SpawnLimitation = Class.forName("ivorius.reccomplex.world.gen.feature.structure.generic.generation.NaturalGeneration$SpawnLimitation");
      this.m_NaturalGeneration$SpawnLimitation_areResolved = ReflectUtil.findMethod(this.c_NaturalGeneration$SpawnLimitation, "areResolved");
      this.c_CachedStructureSelectors = Class.forName("ivorius.reccomplex.world.gen.feature.selector.CachedStructureSelectors");
      this.m_CachedStructureSelectors_get = ReflectUtil.findMethod(this.c_CachedStructureSelectors, "get");
      this.c_MixingStructureSelector = Class.forName("ivorius.reccomplex.world.gen.feature.selector.MixingStructureSelector");
      this.m_MixingStructureSelector_generatedStructures = ReflectUtil.findMethod(this.c_MixingStructureSelector, "generatedStructures");
      this.c_SimpleLeveledRegistry = Class.forName("ivorius.reccomplex.files.SimpleLeveledRegistry");
      this.m_SimpleLeveledRegistry_id = ReflectUtil.findMethod(this.c_SimpleLeveledRegistry, "id");
      this.c_Structure = Class.forName("ivorius.reccomplex.world.gen.feature.structure.Structure");
      this.c_GenerationType = Class.forName("ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType");
      this.c_StructureSpawnContext$GenerateMaturity = Class.forName("ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext$GenerateMaturity");
      this.c_StructureGenerator = Class.forName("ivorius.reccomplex.world.gen.feature.StructureGenerator");
      this.m_StructureGenerator_init = this.c_StructureGenerator.getDeclaredConstructor();
      this.m_StructureGenerator_init.setAccessible(true);
      this.m_StructureGenerator_structure_structure = this.c_StructureGenerator.getDeclaredMethod("structure", this.c_Structure);
      this.m_StructureGenerator_structure_structure.setAccessible(true);
      this.m_StructureGenerator_world_world = this.c_StructureGenerator.getDeclaredMethod("world", WorldServer.class);
      this.m_StructureGenerator_world_world.setAccessible(true);
      this.m_StructureGenerator_generationInfo_generationtype = this.c_StructureGenerator.getDeclaredMethod("generationInfo", this.c_GenerationType);
      this.m_StructureGenerator_generationInfo_generationtype.setAccessible(true);
      this.m_StructureGenerator_seed_long = this.c_StructureGenerator.getDeclaredMethod("seed", Long.class);
      this.m_StructureGenerator_seed_long.setAccessible(true);
      this.m_StructureGenerator_maturity_enum = this.c_StructureGenerator.getDeclaredMethod("maturity", this.c_StructureSpawnContext$GenerateMaturity);
      this.m_StructureGenerator_maturity_enum.setAccessible(true);
      this.m_StructureGenerator_randomPosition = ReflectUtil.findMethod(this.c_StructureGenerator, "randomPosition");
      this.m_StructureGenerator_fromCenter = ReflectUtil.findMethod(this.c_StructureGenerator, "fromCenter");
      this.m_StructureGenerator_partially = ReflectUtil.findMethod(this.c_StructureGenerator, "partially");
      this.m_StructureGenerator_environment_none = this.c_StructureGenerator.getDeclaredMethod("environment");
      this.m_StructureGenerator_environment_none.setAccessible(true);
      this.m_StructureGenerator_generate = ReflectUtil.findMethod(this.c_StructureGenerator, "generate");
      this.c_StructureGenerationEventLite$Post = Class.forName("ivorius.reccomplex.events.StructureGenerationEventLite$Post");
      this.c_IvVecMathHelper = Class.forName("ivorius.ivtoolkit.math.IvVecMathHelper");
      this.m_IvVecMathHelper_distanceSQ_d_d = this.c_IvVecMathHelper.getDeclaredMethod("distanceSQ", double[].class, double[].class);
      this.m_IvVecMathHelper_distanceSQ_d_d.setAccessible(true);
   }

   public Enum generateMaturityFromString(String name) {
      return Enum.valueOf(this.c_StructureSpawnContext$GenerateMaturity, name);
   }

   public Object getWorldStructureGenerationData(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_WorldStructureGenerationData_get.invoke((Object)null, world);
   }

   public List getStructuresComplementList(Object i_WorldStructureGenerationData, ChunkPos chunkPos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return (List)((Stream)this.m_WorldStructureGenerationData_structureEntriesIn.invoke(i_WorldStructureGenerationData, chunkPos)).collect(Collectors.toList());
   }

   public boolean checkChunk(Object i_WorldStructureGenerationData, ChunkPos chunkPos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_WorldStructureGenerationData_checkChunk.invoke(i_WorldStructureGenerationData, chunkPos)).booleanValue();
   }

   public Object getEntryMap(Object i_WorldStructureGenerationData) throws IllegalArgumentException, IllegalAccessException {
      return this.f_WorldStructureGenerationData_entryMap.get(i_WorldStructureGenerationData);
   }

   public Object removeEntry(Object i_WorldStructureGenerationData, UUID uuid) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_WorldStructureGenerationData_removeEntry.invoke(i_WorldStructureGenerationData, uuid);
   }

   public void complementStructuresInChunk(ChunkPos chunkPos, WorldServer world, List structuresComplementList) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_WorldGenStructures_complementStructuresInChunk.invoke((Object)null, chunkPos, world, structuresComplementList);
   }

   public void planStaticStructuresInChunk(Random random, ChunkPos chunkPos, WorldServer world, BlockPos spawnPos, @Nullable Object structurePredicate) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      this.m_WorldGenStructures_planStaticStructuresInChunk.invoke((Object)null, random, chunkPos, world, spawnPos, structurePredicate);
   }

   public float distance(ChunkPos chunkA, ChunkPos chunkB) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Float)this.m_WorldGenStructures_distance.invoke((Object)null, chunkA, chunkB)).floatValue();
   }

   public Object getRandomSurfacePos(ChunkPos chunkPos, long seed) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_WorldGenStructures_randomSurfacePos.invoke((Object)null, chunkPos, seed);
   }

   public boolean honorStructureGenerationOption() throws IllegalArgumentException, IllegalAccessException {
      return this.f_RCConfig_honorStructureGenerationOption.getBoolean((Object)null);
   }

   public float minDistToSpawnForGeneration() throws IllegalArgumentException, IllegalAccessException {
      return this.f_RCConfig_minDistToSpawnForGeneration.getFloat((Object)null);
   }

   public boolean isGenerationEnabled(Biome biome) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_RCConfig_isGenerationEnabled_biome.invoke((Object)null, biome)).booleanValue();
   }

   public boolean isGenerationEnabled(WorldProvider provider) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_RCConfig_isGenerationEnabled_worldprovider.invoke((Object)null, provider)).booleanValue();
   }

   public Object getStructureRegistryInstance() throws IllegalArgumentException, IllegalAccessException {
      return this.f_StructureRegistry_instance.get((Object)null);
   }

   public Object getNaturalGenerationSelectors(Object structureRegistry) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_NaturalGeneration_selectors.invoke((Object)null, structureRegistry);
   }

   public Object getNaturalGenerationLimitations(Object naturalGeneration) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_NaturalGeneration_getLimitations.invoke(naturalGeneration);
   }

   public Object getNaturalGenerationPlacer(Object naturalGeneration) throws IllegalArgumentException, IllegalAccessException {
      return this.f_NaturalGeneration_placer.get(naturalGeneration);
   }

   public double getNaturalGenerationWeight(Object naturalGeneration, WorldProvider provider, Biome biome) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Double)this.m_NaturalGeneration_getGenerationWeight_params.invoke(naturalGeneration, provider, biome)).doubleValue();
   }

   public boolean isSpawnLimitationResolved(Object spawnLimitation, World world, String structureName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Boolean)this.m_NaturalGeneration$SpawnLimitation_areResolved.invoke(spawnLimitation, world, structureName)).booleanValue();
   }

   public Object getSelectorFromCachedSelectors(Object cached, Biome biome, WorldProvider provider) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_CachedStructureSelectors_get.invoke(cached, biome, provider);
   }

   public List<Pair<Object, Object>> getStructurePairsWithSelector(Object selector, Random random, Biome biome, WorldProvider provider, Float spawnDistance) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return (List)this.m_MixingStructureSelector_generatedStructures.invoke(selector, random, biome, provider, spawnDistance);
   }

   public String getStructureName(Object structure) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      Object structureRegistryInstance = this.getStructureRegistryInstance();
      return (String)this.m_SimpleLeveledRegistry_id.invoke(structureRegistryInstance, structure);
   }

   public Object newStructureGenerator() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_StructureGenerator_init.newInstance();
   }

   public Biome environmentToBiome(Object environment) throws IllegalArgumentException, IllegalAccessException {
      return (Biome)this.f_Environment_biome.get(environment);
   }

   public Object setStructure(Object generator, Object structure) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_StructureGenerator_structure_structure.invoke(generator, structure);
   }

   public Object setWorld(Object generator, WorldServer world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_StructureGenerator_world_world.invoke(generator, world);
   }

   public Object setGenerationInfo(Object generator, Object generationType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_StructureGenerator_generationInfo_generationtype.invoke(generator, generationType);
   }

   public Object setSeed(Object generator, long seed) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_StructureGenerator_seed_long.invoke(generator, seed);
   }

   public Object setMaturity(Object generator, Object maturityEnum) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_StructureGenerator_maturity_enum.invoke(generator, maturityEnum);
   }

   public Object setRandomPosition(Object generator, Object blockSurfacePos, @Nullable Object placer) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_StructureGenerator_randomPosition.invoke(generator, blockSurfacePos, placer);
   }

   public Object setFromCenter(Object generator, boolean value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_StructureGenerator_fromCenter.invoke(generator, value);
   }

   public Object partially(Object generator, boolean partially, ChunkPos chunkPos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_StructureGenerator_partially.invoke(generator, partially, chunkPos);
   }

   public Object getEnvironment(Object generator) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_StructureGenerator_environment_none.invoke(generator);
   }

   public Object generate(Object generator) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_StructureGenerator_generate.invoke(generator);
   }

   public double distanceSq(double[] pos1, double[] pos2) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return ((Double)this.m_IvVecMathHelper_distanceSQ_d_d.invoke((Object)null, pos1, pos2)).doubleValue();
   }
}
