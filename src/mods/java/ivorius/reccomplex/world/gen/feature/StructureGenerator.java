package ivorius.reccomplex.world.gen.feature;

import com.google.common.collect.Sets;
import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.events.RCEventBus;
import ivorius.reccomplex.events.StructureGenerationEvent;
import ivorius.reccomplex.events.StructureGenerationEventLite;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.RCAxisAlignedTransform;
import ivorius.reccomplex.utils.RCStructureBoundingBoxes;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.Placer;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.StructurePlaceContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.RunTransformer;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerMulti;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.MinecraftForge;

public class StructureGenerator<S extends NBTStorable> {
   public static final int MIN_DIST_TO_LIMIT = 1;
   public static final long PLACE_SEED = 1923419028309182309L;
   public static final long PREPARE_SEED = 2482039482903842269L;
   public static final long GENERATE_SEED = 2309842093742837432L;
   public static final long TRANSFORM_SEED = 1283901823092812394L;
   @Nullable
   private WorldServer world;
   @Nullable
   private Structure<S> structure;
   @Nullable
   private String structureID;
   @Nullable
   private BlockPos lowerCoord;
   @Nullable
   private BlockSurfacePos surfacePos;
   @Nullable
   private Placer placer;
   private boolean fromCenter;
   @Nullable
   private Environment environment;
   @Nullable
   private Long seed;
   @Nullable
   private AxisAlignedTransform2D transform;
   @Nullable
   private StructureBoundingBox boundingBox;
   @Nullable
   private StructureBoundingBox generationBB;
   @Nullable
   private Predicate<Vec3i> generationPredicate;
   private RunTransformer transformer;
   private String generationInfoID;
   private GenerationType generationType;
   private int generationLayer = 0;
   private boolean generateAsSource = false;
   private StructureSpawnContext.GenerateMaturity generateMaturity = StructureSpawnContext.GenerateMaturity.FIRST;
   @Nullable
   private S instanceData;
   @Nullable
   private NBTBase instanceDataNBT;
   private boolean allowOverlaps = false;
   private boolean memorize = true;
   private boolean partially;

   public StructureGenerator(Structure<S> structure) {
      this.structure(structure);
   }

   public StructureGenerator() {
   }

   @Nonnull
   public static Placer worldHeightPlacer() {
      return (context, blockCollection) -> {
         return context.environment.world.getHeight(RCStructureBoundingBoxes.getCenter(context.boundingBox)).getY();
      };
   }

   public static String name(String structureName) {
      return structureName != null ? structureName : "Unknown";
   }

   @Nonnull
   public StructureGenerator.GenerationResult generate() {
      Optional<S> optionalInstanceData = this.instanceData();
      if (!optionalInstanceData.isPresent()) {
         return this.failGenerate(StructureGenerator.GenerationResult.Failure.placement);
      } else {
         S instanceData = optionalInstanceData.get();
         StructureSpawnContext spawn = (StructureSpawnContext)this.spawn().get();
         Structure<S> structure = this.structure();
         String structureID = this.structureID();
         boolean firstTime = spawn.generateMaturity.isFirstTime();
         WorldServer world = spawn.environment.world;
         StructureBoundingBox boundingBox = spawn.boundingBox;
         if (this.maturity().isSuggest()) {
            if (boundingBox.minY < 1 || boundingBox.maxY > world.getHeight() - 1 - 1) {
               return this.failGenerate(StructureGenerator.GenerationResult.Failure.outOfBounds);
            }

            if (RCConfig.avoidOverlappingGeneration && !this.allowOverlaps && !WorldStructureGenerationData.get(world).entriesAt(boundingBox).noneMatch(WorldStructureGenerationData.Entry::blocking)) {
               return this.failGenerate(StructureGenerator.GenerationResult.Failure.structureOverlap);
            }

            if (RCEventBus.INSTANCE.post(new StructureGenerationEvent.Suggest(structure, spawn)) || structureID != null && MinecraftForge.EVENT_BUS.post(new StructureGenerationEventLite.Suggest(world, structureID, boundingBox, spawn.generationLayer, firstTime))) {
               return this.failGenerate(StructureGenerator.GenerationResult.Failure.cancel);
            }
         }

         if (firstTime) {
            RCEventBus.INSTANCE.post(new StructureGenerationEvent.Pre(structure, spawn));
            if (structureID != null) {
               MinecraftForge.EVENT_BUS.post(new StructureGenerationEventLite.Pre(world, structureID, boundingBox, spawn.generationLayer, firstTime));
            }
         }

         RCWorldgenMonitor.start("generating " + this.structureID());

         label197: {
            StructureGenerator.GenerationResult.Failure structureEntry;
            try {
               structure.generate(spawn, instanceData, this.foreignTransformer());
               break label197;
            } catch (java.lang.Exception var19) {
               structureEntry = this.failGenerate(new StructureGenerator.GenerationResult.Failure.Exception(var19));
            } finally {
               RCWorldgenMonitor.stop();
            }

            return structureEntry;
         }

         if (!firstTime) {
            return StructureGenerator.GenerationResult.Success.complement;
         } else {
            RecurrentComplex.logger.trace(String.format("Generated structure '%s' in %s (%d)", name(structureID), boundingBox, world.provider.getDimension()));
            RCEventBus.INSTANCE.post(new StructureGenerationEvent.Post(structure, spawn));
            if (structureID != null) {
               MinecraftForge.EVENT_BUS.post(new StructureGenerationEventLite.Post(world, structureID, boundingBox, spawn.generationLayer, firstTime));
            }

            if (structureID != null && this.memorize) {
               String generationInfoID = this.generationType != null ? this.generationType.id() : null;
               WorldStructureGenerationData.StructureEntry structureEntry = WorldStructureGenerationData.StructureEntry.complete(structureID, generationInfoID, boundingBox, spawn.transform, !this.partially);
               structureEntry.blocking = structure.isBlocking();
               structureEntry.firstTime = false;
               structureEntry.seed = this.seed();

               try {
                  structureEntry.instanceData = instanceData.writeToNBT();
               } catch (java.lang.Exception var18) {
                  RecurrentComplex.logger.error(String.format("Error saving instance data for structure %s in %s", structure, boundingBox), var18);
               }

               Collection<ChunkPos> existingChunks = Sets.newHashSet(WorldStructureGenerationData.get(world).addEntry(structureEntry));
               if (this.partially) {
                  this.maturity(StructureSpawnContext.GenerateMaturity.COMPLEMENT);
                  StructureBoundingBox oldBB = this.generationBB;

                  for(ChunkPos existingChunk : existingChunks) {
                     this.generationBB(Structures.chunkBoundingBox(existingChunk, true));
                     if (!oldBB.intersectsWith(this.generationBB)) {
                        RCWorldgenMonitor.start("pre-complementing " + this.structureID());
                        structure.generate((StructureSpawnContext)this.spawn().get(), instanceData, RCConfig.getUniversalTransformer());
                        RCWorldgenMonitor.stop();
                     }
                  }

                  this.generationBB(oldBB);
               }

               return new StructureGenerator.GenerationResult.Success.New(structureEntry);
            } else {
               return StructureGenerator.GenerationResult.Success.contemporary;
            }
         }
      }
   }

   @Nonnull
   public TransformerMulti foreignTransformer() {
      return this.transformer != null ? this.transformer.transformer : RCConfig.getUniversalTransformer();
   }

   @Nonnull
   protected StructureGenerator.GenerationResult.Failure failGenerate(@Nonnull StructureGenerator.GenerationResult.Failure failure) {
      if (RCConfig.logFailingStructure(this.structure)) {
         Optional<Integer> dim = Optional.ofNullable(this.world).map((w) -> {
            return w.provider.getDimension();
         });
         RecurrentComplex.logger.trace(String.format("%s canceled generation at %s (%s): %s", this.structure, this.lowerCoord().orElse((Object)null), dim.map(String::valueOf).orElse("Unknown"), failure));
         if (failure instanceof StructureGenerator.GenerationResult.Failure.Exception) {
            RecurrentComplex.logger.error("Error on structure generation", ((StructureGenerator.GenerationResult.Failure.Exception)failure).exception);
         }
      }

      return failure;
   }

   public StructureGenerator<S> asChild(StructureSpawnContext context) {
      return this.environment(context.environment.child()).seed(Long.valueOf(context.random.nextLong())).transform(context.transform).generationBB(context.generationBB).generationPredicate(context.generationPredicate).generationLayer(context.generationLayer + 1).asSource(context.generateAsSource).maturity(context.generateMaturity.isFirstTime() ? StructureSpawnContext.GenerateMaturity.FIRST : StructureSpawnContext.GenerateMaturity.COMPLEMENT);
   }

   public StructureGenerator<S> world(@Nonnull WorldServer world) {
      this.world = world;
      return this;
   }

   @Nonnull
   public WorldServer world() {
      WorldServer world = this.world != null ? this.world : (this.environment != null ? this.environment.world : null);
      if (world == null) {
         throw new IllegalStateException("No world!");
      } else {
         return world;
      }
   }

   public long seed() {
      return (this.seed != null ? this.seed : (this.seed = this.world().rand.nextLong())).longValue();
   }

   public StructureGenerator<S> structure(@Nonnull Structure<S> structure) {
      this.structure = structure;
      return this;
   }

   public StructureGenerator<S> structureID(@Nonnull String structureID) {
      this.structureID = structureID;
      return this;
   }

   @Nonnull
   public Structure<S> structure() {
      Structure<S> structure = this.structure != null ? this.structure : (this.structureID != null ? (Structure)StructureRegistry.INSTANCE.get(this.structureID) : null);
      if (structure == null) {
         throw new IllegalStateException();
      } else {
         return structure;
      }
   }

   @Nullable
   public String structureID() {
      return this.structureID != null ? this.structureID : (this.structure != null ? StructureRegistry.INSTANCE.id(this.structure) : null);
   }

   public StructureGenerator<S> lowerCoord(@Nonnull BlockPos lowerCoord) {
      this.lowerCoord = lowerCoord;
      return this;
   }

   @Nonnull
   public Optional<BlockPos> lowerCoord() {
      return this.lowerCoord != null ? Optional.of(this.lowerCoord) : this.boundingBox().map(StructureBoundingBoxes::min);
   }

   public StructureGenerator<S> randomPosition(@Nonnull BlockSurfacePos surfacePos, @Nullable Placer placer) {
      this.surfacePos = surfacePos;
      this.placer = placer != null ? placer : worldHeightPlacer();
      return this;
   }

   public StructureGenerator<S> fromCenter(boolean fromCenter) {
      this.fromCenter = fromCenter;
      return this;
   }

   public StructureGenerator<S> environment(@Nonnull Environment environment) {
      this.environment = environment;
      return this;
   }

   @Nonnull
   public Environment environment() {
      return this.environment != null ? this.environment : Environment.inNature(this.world(), this.surfaceBoundingBox(), this.generationType != null ? this.generationType : (this.generationInfoID != null ? this.structure().generationType(this.generationInfoID) : null));
   }

   public StructureGenerator<S> seed(Long seed) {
      this.seed = seed;
      return this;
   }

   public StructureGenerator<S> transform(AxisAlignedTransform2D transform) {
      this.transform = transform;
      return this;
   }

   @Nonnull
   public AxisAlignedTransform2D transform() {
      if (this.transform != null) {
         return this.transform;
      } else {
         Structure<S> structure = this.structure();
         Random random = new Random(this.seed() ^ 1283901823092812394L);
         AxisAlignedTransform2D transform = AxisAlignedTransform2D.from(structure.isRotatable() ? random.nextInt(4) : 0, structure.isMirrorable() && random.nextBoolean());
         return this.transform = transform;
      }
   }

   public StructureGenerator<S> boundingBox(@Nonnull StructureBoundingBox boundingBox) {
      this.boundingBox = boundingBox;
      return this;
   }

   public Optional<StructureBoundingBox> boundingBox() {
      return this.boundingBox(true);
   }

   @Nonnull
   public StructureBoundingBox surfaceBoundingBox() {
      return (StructureBoundingBox)this.boundingBox(false).get();
   }

   @Nonnull
   protected Optional<StructureBoundingBox> boundingBox(boolean placed) {
      StructureBoundingBox boundingBox = this.boundingBox != null ? this.boundingBox : null;
      if (boundingBox == null) {
         int[] size = this.structureSize();
         if (this.lowerCoord != null) {
            boundingBox = Structures.boundingBox(this.fromCenter ? this.lowerCoord.subtract(new Vec3i(size[0] / 2, 0, size[2] / 2)) : this.lowerCoord, size);
         } else {
            if (this.surfacePos == null || this.placer == null) {
               throw new IllegalStateException("No place!");
            }

            boundingBox = Structures.boundingBox((this.fromCenter ? this.surfacePos.subtract(size[0] / 2, size[2] / 2) : this.surfacePos).blockPos(0), size);
            if (placed) {
               RCWorldgenMonitor.start("placing " + this.structureID());
               int y = this.placer.place(this.place(), this.structure().blockCollection());
               RCWorldgenMonitor.stop();
               if (y < 0) {
                  return Optional.empty();
               }

               boundingBox.minY += y;
               boundingBox.maxY += y;
            }
         }
      }

      if (placed) {
         this.boundingBox = boundingBox;
      }

      return Optional.of(boundingBox);
   }

   public int[] structureSize() {
      return RCAxisAlignedTransform.applySize(this.transform(), this.structure().size());
   }

   @Nullable
   public StructureBoundingBox generationBB() {
      return this.generationBB;
   }

   public StructureGenerator<S> generationBB(@Nullable StructureBoundingBox generationBB) {
      this.generationBB = generationBB;
      return this;
   }

   @Nullable
   public Predicate<Vec3i> generationPredicate() {
      return this.generationPredicate;
   }

   public StructureGenerator<S> generationPredicate(@Nullable Predicate<Vec3i> generationPredicate) {
      this.generationPredicate = generationPredicate;
      return this;
   }

   public RunTransformer transformer() {
      return this.transformer;
   }

   public StructureGenerator<S> transformer(RunTransformer transformer) {
      this.transformer = transformer;
      return this;
   }

   public StructureGenerator<S> generationInfo(@Nullable GenerationType generationType) {
      this.generationType = generationType;
      return this;
   }

   public StructureGenerator<S> generationInfo(@Nullable String generationInfo) {
      this.generationInfoID = generationInfo;
      return this;
   }

   public StructureGenerator<S> generationLayer(int generationLayer) {
      this.generationLayer = generationLayer;
      return this;
   }

   public StructureGenerator<S> asSource(boolean generateAsSource) {
      this.generateAsSource = generateAsSource;
      return this;
   }

   public StructureGenerator<S> maturity(StructureSpawnContext.GenerateMaturity generateMaturity) {
      this.generateMaturity = generateMaturity;
      return this;
   }

   public StructureSpawnContext.GenerateMaturity maturity() {
      return this.generateMaturity;
   }

   public StructureGenerator<S> instanceData(S s) {
      this.instanceData = s;
      return this;
   }

   public StructureGenerator<S> instanceData(NBTBase nbt) {
      this.instanceDataNBT = nbt;
      return this;
   }

   @Nonnull
   public Optional<S> instanceData() {
      return this.instanceData != null ? Optional.of(this.instanceData) : (this.instanceDataNBT != null ? this.load().map((load) -> {
         return this.structure().loadInstanceData(load, this.instanceDataNBT, this.foreignTransformer());
      }) : this.prepare().flatMap((prepare) -> {
         try {
            RCWorldgenMonitor.start("preparing " + this.structureID());
            Optional<S> prepared = Optional.ofNullable(this.structure().prepareInstanceData(prepare, this.foreignTransformer()));
            RCWorldgenMonitor.stop();
            return prepared;
         } catch (java.lang.Exception var3) {
            if (var3 instanceof StructureGenerator.ExpectedException && ((StructureGenerator.ExpectedException)var3).isExpected()) {
               RecurrentComplex.logger.error(String.format("Error preparing structure: %s, Cause: %s", this.structure(), var3.getMessage()));
            } else {
               RecurrentComplex.logger.error("Error preparing structure: " + this.structure(), var3);
            }

            return Optional.empty();
         }
      }));
   }

   public StructureGenerator<S> memorize(boolean memorize) {
      this.memorize = memorize;
      return this;
   }

   public StructureGenerator<S> partially(boolean partially, ChunkPos pos) {
      this.partially = partially;
      this.generationBB(partially ? Structures.chunkBoundingBox(pos, true) : null);
      return this;
   }

   public StructureGenerator<S> allowOverlaps(boolean allowOverlaps) {
      this.allowOverlaps = allowOverlaps;
      return this;
   }

   @Nonnull
   public StructurePlaceContext place() {
      return new StructurePlaceContext(new Random(this.seed() ^ 1923419028309182309L), this.environment(), this.transform(), this.surfaceBoundingBox());
   }

   @Nonnull
   public Optional<StructurePrepareContext> prepare() {
      return this.boundingBox().map((bb) -> {
         return new StructurePrepareContext(this.transform(), bb, this.generateAsSource, this.environment(), new Random(this.seed() ^ 2482039482903842269L), this.generateMaturity);
      });
   }

   @Nonnull
   public Optional<StructureLoadContext> load() {
      return this.boundingBox().map((bb) -> {
         return new StructureLoadContext(this.transform(), bb, this.generateAsSource);
      });
   }

   @Nonnull
   public Optional<StructureSpawnContext> spawn() {
      return this.boundingBox().map((bb) -> {
         return new StructureSpawnContext(this.environment(), new Random(this.seed() ^ 2309842093742837432L), this.transform(), bb, this.generationBB, this.generationPredicate, this.generationLayer, this.generateAsSource, this.generateMaturity);
      });
   }

   public interface ExpectedException {
      boolean isExpected();
   }

   public static class GenerationResult {
      public GenerationResult() {
      }

      public boolean succeeded() {
         return false;
      }

      public static class Failure extends StructureGenerator.GenerationResult {
         public static StructureGenerator.GenerationResult.Failure placement = new StructureGenerator.GenerationResult.Failure("No suitable place");
         public static StructureGenerator.GenerationResult.Failure structureOverlap = new StructureGenerator.GenerationResult.Failure("Overlapping Structure");
         public static StructureGenerator.GenerationResult.Failure outOfBounds = new StructureGenerator.GenerationResult.Failure("Out of Bounds");
         public static StructureGenerator.GenerationResult.Failure cancel = new StructureGenerator.GenerationResult.Failure("Cancelled for Other Reasons");
         public final String description;

         public Failure(String description) {
            this.description = description;
         }

         public static class Exception extends StructureGenerator.GenerationResult.Failure {
            public final java.lang.Exception exception;

            public Exception(java.lang.Exception exception) {
               super("Error during generation");
               this.exception = exception;
            }
         }
      }

      public static class Success extends StructureGenerator.GenerationResult {
         public static StructureGenerator.GenerationResult.Success complement = new StructureGenerator.GenerationResult.Success();
         public static StructureGenerator.GenerationResult.Success contemporary = new StructureGenerator.GenerationResult.Success();

         public Success() {
         }

         public boolean succeeded() {
            return true;
         }

         public static class New extends StructureGenerator.GenerationResult.Success {
            public final WorldStructureGenerationData.StructureEntry sight;

            public New(WorldStructureGenerationData.StructureEntry sight) {
               this.sight = sight;
            }
         }
      }
   }
}
