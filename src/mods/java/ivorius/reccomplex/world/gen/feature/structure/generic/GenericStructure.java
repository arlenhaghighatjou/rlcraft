package ivorius.reccomplex.world.gen.feature.structure.generic;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.blocks.BlockAreas;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.blocks.IvMutableBlockPos;
import ivorius.ivtoolkit.tools.IvWorldData;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.ivtoolkit.transform.Mover;
import ivorius.ivtoolkit.transform.PosTransformer;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.json.NBTToJson;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.temp.RCMover;
import ivorius.reccomplex.temp.RCPosTransformer;
import ivorius.reccomplex.utils.ItemHandlers;
import ivorius.reccomplex.utils.RCStructureBoundingBoxes;
import ivorius.reccomplex.utils.accessor.RCAccessorEntity;
import ivorius.reccomplex.utils.accessor.RCAccessorWorldServer;
import ivorius.reccomplex.utils.expression.DependencyExpression;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import ivorius.reccomplex.world.gen.feature.structure.VariableDomain;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.MazeGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.NaturalGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.RunTransformer;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.Transformer;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerGenerationBehavior;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerMulti;
import ivorius.reccomplex.world.storage.loot.LootGenerationHandler;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.apache.commons.lang3.tuple.Pair;

public class GenericStructure implements Structure<GenericStructure.InstanceData>, Cloneable {
   public static final int LATEST_VERSION = 3;
   public static final int MAX_GENERATING_LAYERS = 30;
   public final List<GenerationType> generationTypes = new ArrayList();
   public TransformerMulti transformer = new TransformerMulti();
   public final DependencyExpression dependencies = new DependencyExpression();
   public NBTTagCompound worldDataCompound;
   public boolean rotatable;
   public boolean mirrorable;
   public boolean blocking;
   public GenericVariableDomain variableDomain = new GenericVariableDomain();
   public Metadata metadata = new Metadata();
   public JsonObject customData;

   public GenericStructure() {
   }

   public static GenericStructure createDefaultStructure() {
      GenericStructure genericStructureInfo = new GenericStructure();
      genericStructureInfo.rotatable = true;
      genericStructureInfo.mirrorable = true;
      genericStructureInfo.blocking = true;
      genericStructureInfo.transformer.getData().setPreset("structure");
      genericStructureInfo.generationTypes.add(new NaturalGeneration());
      return genericStructureInfo;
   }

   private static double[] getEntityPos(NBTTagCompound compound) {
      NBTTagList pos = compound.getTagList("Pos", 6);
      return new double[]{pos.getDoubleAt(0), pos.getDoubleAt(1), pos.getDoubleAt(2)};
   }

   public static void setBlock(@Nonnull StructureSpawnContext context, int[] areaSize, @Nonnull BlockPos worldPos, @Nonnull IBlockState state, @Nonnull Supplier<NBTTagCompound> tileEntity) {
      WorldServer world = context.environment.world;
      if (context.setBlock(worldPos, state, 2)) {
         NBTTagCompound tileEntityCompound = (NBTTagCompound)tileEntity.get();
         if (tileEntityCompound != null && world.getBlockState(worldPos).getBlock() == state.getBlock()) {
            TileEntity worldTileEntity = world.getTileEntity(worldPos);
            if (worldTileEntity != null) {
               tileEntityCompound = RCMover.setTileEntityPos(tileEntityCompound, worldPos);
               worldTileEntity.readFromNBT(tileEntityCompound);
               RCPosTransformer.transformAdditionalData(worldTileEntity, context.transform, areaSize);
               RCMover.setAdditionalDataPos(worldTileEntity, worldPos);
               generateTileEntityContents(context, worldTileEntity);
            }
         }
      }

   }

   public static void generateEntityContents(@Nonnull StructureSpawnContext context, Entity entity) {
      if (!context.generateAsSource && ItemHandlers.hasModifiable(entity, (EnumFacing)null)) {
         LootGenerationHandler.generateAllTags(context, ItemHandlers.getModifiable(entity, (EnumFacing)null));
      }

   }

   public static void generateTileEntityContents(@Nonnull StructureSpawnContext context, TileEntity tileEntity) {
      if (!context.generateAsSource && ItemHandlers.hasModifiable(tileEntity, (EnumFacing)null)) {
         LootGenerationHandler.generateAllTags(context, ItemHandlers.getModifiable(tileEntity, (EnumFacing)null));
      }

   }

   @Nonnull
   public int[] size() {
      return Structures.size(this.worldDataCompound, new int[]{0, 0, 0});
   }

   public boolean isRotatable() {
      return this.rotatable;
   }

   public boolean isMirrorable() {
      return this.mirrorable;
   }

   public boolean isBlocking() {
      return this.blocking;
   }

   public void generate(@Nonnull StructureSpawnContext context, @Nonnull GenericStructure.InstanceData instanceData, @Nonnull TransformerMulti foreignTransformer) {
      WorldServer world = context.environment.world;
      IvWorldData worldData = this.constructWorldData();
      boolean asSource = context.generateAsSource;
      RunTransformer transformer = this.getRunTransformer(instanceData, foreignTransformer, asSource);
      instanceData.variableDomain.fill(context.environment.variables);
      RCAccessorWorldServer.ensureBlockEventArray(world);
      IvBlockCollection blockCollection = worldData.blockCollection;
      int[] areaSize = new int[]{blockCollection.width, blockCollection.height, blockCollection.length};
      BlockPos origin = StructureBoundingBoxes.min(context.boundingBox);
      Map<BlockPos, NBTTagCompound> tileEntityCompounds = new HashMap();

      for(NBTTagCompound tileEntityCompound : worldData.tileEntities) {
         BlockPos src = RCMover.getTileEntityPos(tileEntityCompound);
         tileEntityCompounds.put(src, tileEntityCompound);
      }

      if (transformer != null) {
         transformer.transformer.transform(transformer.instanceData, Transformer.Phase.BEFORE, context, worldData, transformer);
      }

      StructureBoundingBox relevantSourceArea = context.sourceIntersection(BlockAreas.toBoundingBox(blockCollection.area()));
      if (relevantSourceArea != null) {
         context.freezeHeightMap(relevantSourceArea);
         MutableBlockPos worldPos = new MutableBlockPos();

         for(int pass = 0; pass < 2; ++pass) {
            for(BlockPos sourcePos : RCStructureBoundingBoxes.mutablePositions(relevantSourceArea)) {
               IvMutableBlockPos.add(context.transform.applyOn(sourcePos, worldPos, areaSize), origin);
               if (context.includesComplex(worldPos)) {
                  IBlockState state = PosTransformer.transformBlockState(blockCollection.getBlockState(sourcePos), context.transform);
                  if (pass == this.getPass(state) && (transformer == null || !transformer.transformer.skipGeneration(transformer.instanceData, context, worldPos, state, worldData, sourcePos))) {
                     setBlock(context, areaSize, worldPos, state, () -> {
                        return (NBTTagCompound)tileEntityCompounds.get(sourcePos);
                     });
                  }
               }
            }
         }

         context.meltHeightMap();
      }

      if (transformer != null) {
         transformer.transformer.transform(transformer.instanceData, Transformer.Phase.AFTER, context, worldData, transformer);
      }

      for(NBTTagCompound entityCompound : worldData.entities) {
         double[] transformedEntityPos = context.transform.applyOn(getEntityPos(entityCompound), areaSize);
         if (context.includes(new Vec3i(transformedEntityPos[0] + (double)origin.getX(), transformedEntityPos[1] + (double)origin.getY(), transformedEntityPos[2] + (double)origin.getZ()))) {
            Entity entity = EntityList.createEntityFromNBT(entityCompound, world);
            if (entity != null) {
               PosTransformer.transformEntityPos(entity, context.transform, areaSize);
               Mover.moveEntity(entity, origin);
               RCAccessorEntity.setEntityUniqueID(entity, EaglercraftUUID.randomUUID());
               generateEntityContents(context, entity);
               world.spawnEntity(entity);
            } else {
               RecurrentComplex.logger.error("Error loading entity with ID '" + entityCompound.getString("id") + "'");
            }
         }
      }

   }

   @Nullable
   public RunTransformer getRunTransformer(@Nonnull GenericStructure.InstanceData instanceData, @Nonnull TransformerMulti foreignTransformer, boolean asSource) {
      if (asSource) {
         return null;
      } else if (instanceData.transformerData != null && instanceData.foreignTransformerData != null) {
         TransformerGenerationBehavior transformerGenerationBehavior = new TransformerGenerationBehavior();
         TransformerMulti fused = TransformerMulti.fuse(Arrays.asList(transformerGenerationBehavior, this.transformer, foreignTransformer));
         fused.setID("FusedStructureTransformer");
         return new RunTransformer(fused, fused.fuseDatas(Arrays.asList(instanceData.transformerGenerationBehavior, instanceData.transformerData, instanceData.foreignTransformerData)));
      } else {
         throw new IllegalStateException();
      }
   }

   @Nullable
   public GenericStructure.InstanceData prepareInstanceData(@Nonnull StructurePrepareContext context, @Nonnull TransformerMulti foreignTransformer) {
      GenericStructure.InstanceData instanceData = new GenericStructure.InstanceData();
      if (!context.generateAsSource) {
         IvWorldData worldData = this.constructWorldData();
         context.environment.variables.fill(instanceData.variableDomain);
         this.variableDomain.fill(instanceData.variableDomain, context.environment, context.random);
         TransformerGenerationBehavior transformerGenerationBehavior = new TransformerGenerationBehavior();
         TransformerMulti fused = TransformerMulti.fuse(Arrays.asList(transformerGenerationBehavior, this.transformer, foreignTransformer));
         TransformerMulti.InstanceData fusedDatas = fused.prepareInstanceData(context, worldData);
         instanceData.transformerGenerationBehavior = (TransformerGenerationBehavior.InstanceData)fusedDatas.pairedTransformers.stream().filter((t) -> {
            return t.getLeft() instanceof TransformerGenerationBehavior;
         }).map(Pair::getRight).findFirst().get();
         instanceData.transformerData = (TransformerMulti.InstanceData)fusedDatas.pairedTransformers.stream().filter((t) -> {
            return t.getLeft() == this.transformer;
         }).map(Pair::getRight).findFirst().get();
         instanceData.foreignTransformerData = (TransformerMulti.InstanceData)fusedDatas.pairedTransformers.stream().filter((t) -> {
            return t.getLeft() == foreignTransformer;
         }).map(Pair::getRight).findFirst().get();
         if (context.generateMaturity.isSuggest() && !fused.mayGenerate(fusedDatas, context, worldData)) {
            return null;
         }

         RunTransformer runTransformer = new RunTransformer(fused, fusedDatas);
         fused.configureInstanceData(fusedDatas, context, worldData, runTransformer);
      }

      return instanceData;
   }

   @Nonnull
   public GenericStructure.InstanceData loadInstanceData(@Nonnull StructureLoadContext context, @Nonnull NBTBase nbt, @Nonnull TransformerMulti transformer) {
      GenericStructure.InstanceData instanceData = new GenericStructure.InstanceData();
      instanceData.readFromNBT(context, nbt, this.transformer, transformer, this.constructWorldData());
      return instanceData;
   }

   public IvWorldData constructWorldData() {
      return new IvWorldData(this.worldDataCompound, RecurrentComplex.specialRegistry.itemHidingMode());
   }

   @Nonnull
   public <I extends GenerationType> List<I> generationTypes(@Nonnull Class<? extends I> clazz) {
      return (List)this.generationTypes.stream().filter((info) -> {
         return clazz.isAssignableFrom(info.getClass());
      }).map((info) -> {
         return info;
      }).collect(Collectors.toList());
   }

   public GenerationType generationType(@Nonnull String id) {
      for(GenerationType info : this.generationTypes) {
         if (Objects.equals(info.id(), id)) {
            return info;
         }
      }

      return null;
   }

   private int getPass(IBlockState state) {
      return !state.isNormalCube() && state.getMaterial() != Material.AIR ? 1 : 0;
   }

   @Nonnull
   public GenericStructure copyAsGenericStructure() {
      return this.copy();
   }

   public boolean areDependenciesResolved() {
      return this.dependencies.test(RecurrentComplex.saver);
   }

   @Nullable
   public IvBlockCollection blockCollection() {
      return this.constructWorldData().blockCollection;
   }

   @Nonnull
   public GenericVariableDomain declaredVariables() {
      return this.variableDomain;
   }

   public String toString() {
      String s = StructureRegistry.INSTANCE.id(this);
      return s != null ? s : "Generic Structure";
   }

   public List<TextComponentBase> instanceDataInfo(GenericStructure.InstanceData instanceData) {
      return !instanceData.variableDomain.all().isEmpty() ? Collections.singletonList(new TextComponentString(instanceData.variableDomain.all().toString())) : Collections.emptyList();
   }

   public GenericStructure copy() {
      return StructureSaveHandler.INSTANCE.fromJSON(StructureSaveHandler.INSTANCE.toJSON(this), this.worldDataCompound.copy());
   }

   public static class InstanceData implements NBTStorable {
      public static final String KEY_TRANSFORMER = "transformer";
      public static final String KEY_FOREIGN_TRANSFORMER = "foreignTransformer";
      public final VariableDomain variableDomain = new VariableDomain();
      public TransformerMulti.InstanceData transformerData;
      public TransformerMulti.InstanceData foreignTransformerData;
      public TransformerGenerationBehavior.InstanceData transformerGenerationBehavior = new TransformerGenerationBehavior.InstanceData();

      public InstanceData() {
      }

      public void readFromNBT(StructureLoadContext context, NBTBase nbt, TransformerMulti transformer, @Nonnull TransformerMulti foreignTransformer, IvWorldData worldData) {
         NBTTagCompound compound = nbt instanceof NBTTagCompound ? (NBTTagCompound)nbt : new NBTTagCompound();
         this.variableDomain.readFromNBT(compound.getCompoundTag("variables"));
         if (compound.hasKey("transformer")) {
            this.transformerData = transformer.loadInstanceData(context, compound.getTag("transformer"));
         }

         if (compound.hasKey("foreignTransformer")) {
            this.foreignTransformerData = foreignTransformer.loadInstanceData(context, compound.getTag("foreignTransformer"));
         }

         this.transformerGenerationBehavior.readFromNBT(context, nbt, worldData);
      }

      public NBTBase writeToNBT() {
         NBTTagCompound compound = (NBTTagCompound)this.transformerGenerationBehavior.writeToNBT();
         NBTCompoundObjects.writeTo(compound, "variables", this.variableDomain);
         if (this.transformerData != null) {
            compound.setTag("transformer", this.transformerData.writeToNBT());
         }

         if (this.foreignTransformerData != null) {
            compound.setTag("foreignTransformer", this.foreignTransformerData.writeToNBT());
         }

         return compound;
      }
   }

   public static class Serializer implements JsonDeserializer<GenericStructure>, JsonSerializer<GenericStructure> {
      public Serializer() {
      }

      public GenericStructure deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "status");
         GenericStructure structureInfo = new GenericStructure();
         Integer version;
         if (jsonObject.has("version")) {
            version = JsonUtils.getInt(jsonObject, "version");
         } else {
            version = Integer.valueOf(3);
            RecurrentComplex.logger.warn("Structure JSON missing 'version', using latest (3)");
         }

         GenerationType.idRandomizers.push(new EaglercraftRandom(-559038737L));
         if (jsonObject.has("generationInfos")) {
            Collections.addAll(structureInfo.generationTypes, context.deserialize(jsonObject.get("generationInfos"), GenerationType[].class));
         }

         GenerationType.idRandomizers.pop();
         if (version.intValue() == 1) {
            structureInfo.generationTypes.add(NaturalGeneration.deserializeFromVersion1(jsonObject, context));
         }

         if (jsonObject.has("naturalGenerationInfo")) {
            structureInfo.generationTypes.add(NaturalGeneration.getGson().fromJson(jsonObject.get("naturalGenerationInfo"), NaturalGeneration.class));
         }

         if (jsonObject.has("mazeGenerationInfo")) {
            structureInfo.generationTypes.add(MazeGeneration.getGson().fromJson(jsonObject.get("mazeGenerationInfo"), MazeGeneration.class));
         }

         Transformer.idRandomizers.push(new EaglercraftRandom(-559038737L));
         if (jsonObject.has("transformer")) {
            structureInfo.transformer = (TransformerMulti)context.deserialize(jsonObject.get("transformer"), TransformerMulti.class);
         } else if (jsonObject.has("transformers")) {
            Collections.addAll(structureInfo.transformer.getTransformers(), context.deserialize(jsonObject.get("transformers"), Transformer[].class));
         } else if (jsonObject.has("blockTransformers")) {
            Collections.addAll(structureInfo.transformer.getTransformers(), context.deserialize(jsonObject.get("blockTransformers"), Transformer[].class));
         }

         Transformer.idRandomizers.pop();
         structureInfo.rotatable = JsonUtils.getBoolean(jsonObject, "rotatable", false);
         structureInfo.mirrorable = JsonUtils.getBoolean(jsonObject, "mirrorable", false);
         structureInfo.blocking = JsonUtils.getBoolean(jsonObject, "blocking", true);
         structureInfo.variableDomain = (GenericVariableDomain)context.deserialize(JsonUtils.getJsonObject(jsonObject, "variableDomain", new JsonObject()), GenericVariableDomain.class);
         if (jsonObject.has("dependencyExpression")) {
            structureInfo.dependencies.setExpression(JsonUtils.getString(jsonObject, "dependencyExpression"));
         } else if (jsonObject.has("dependencies")) {
            structureInfo.dependencies.setExpression(DependencyExpression.ofMods((String[])context.deserialize(jsonObject.get("dependencies"), String[].class)));
         }

         if (jsonObject.has("worldData")) {
            structureInfo.worldDataCompound = (NBTTagCompound)context.deserialize(jsonObject.get("worldData"), NBTTagCompound.class);
         } else if (jsonObject.has("worldDataBase64")) {
            structureInfo.worldDataCompound = NBTToJson.getNBTFromBase64(JsonUtils.getString(jsonObject, "worldDataBase64"));
         }

         if (jsonObject.has("metadata")) {
            structureInfo.metadata = (Metadata)context.deserialize(jsonObject.get("metadata"), Metadata.class);
         }

         structureInfo.customData = JsonUtils.getJsonObject(jsonObject, "customData", new JsonObject());
         return structureInfo;
      }

      public JsonElement serialize(GenericStructure structureInfo, Type par2Type, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("version", Integer.valueOf(3));
         jsonObject.add("generationInfos", context.serialize(structureInfo.generationTypes));
         jsonObject.add("transformer", context.serialize(structureInfo.transformer));
         jsonObject.addProperty("rotatable", Boolean.valueOf(structureInfo.rotatable));
         jsonObject.addProperty("mirrorable", Boolean.valueOf(structureInfo.mirrorable));
         jsonObject.addProperty("blocking", Boolean.valueOf(structureInfo.blocking));
         jsonObject.add("variableDomain", context.serialize(structureInfo.variableDomain));
         jsonObject.add("dependencyExpression", context.serialize(structureInfo.dependencies.getExpression()));
         jsonObject.add("metadata", context.serialize(structureInfo.metadata));
         jsonObject.add("customData", structureInfo.customData);
         return jsonObject;
      }
   }
}
