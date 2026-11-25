package ivorius.reccomplex.world.gen.feature;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import ivorius.ivtoolkit.tools.NBTTagLists;
import ivorius.reccomplex.utils.RCAxisAlignedTransform;
import ivorius.reccomplex.utils.RCStructureBoundingBoxes;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.WorldSavedData;

public class WorldStructureGenerationData extends WorldSavedData {
   private static final String IDENTIFIER = "reccomplex-structuredata";
   protected final Set<ChunkPos> checkedChunks;
   protected final Set<ChunkPos> checkedChunksFinal;
   protected final Map<UUID, WorldStructureGenerationData.Entry> entryMap;
   protected final SetMultimap<ChunkPos, WorldStructureGenerationData.Entry> chunkMap;
   protected final SetMultimap<String, WorldStructureGenerationData.StructureEntry> instanceMap;

   public WorldStructureGenerationData(String id) {
      super(id);
      this.checkedChunks = new HashSet();
      this.checkedChunksFinal = new HashSet();
      this.entryMap = new HashMap();
      this.chunkMap = HashMultimap.create();
      this.instanceMap = HashMultimap.create();
   }

   public WorldStructureGenerationData() {
      this("reccomplex-structuredata");
   }

   public static WorldStructureGenerationData get(World world) {
      WorldStructureGenerationData data = (WorldStructureGenerationData)world.getPerWorldStorage().getOrLoadData(WorldStructureGenerationData.class, "reccomplex-structuredata");
      if (data == null) {
         data = new WorldStructureGenerationData();
         world.getPerWorldStorage().setData(data.mapName, data);
      }

      return data;
   }

   public Stream<WorldStructureGenerationData.StructureEntry> structureEntriesIn(ChunkPos coords) {
      Stream var10000 = this.entriesAt(coords);
      WorldStructureGenerationData.StructureEntry.class.getClass();
      var10000 = var10000.filter(WorldStructureGenerationData.StructureEntry.class::isInstance);
      WorldStructureGenerationData.StructureEntry.class.getClass();
      return var10000.map(WorldStructureGenerationData.StructureEntry.class::cast);
   }

   public Stream<WorldStructureGenerationData.Entry> entriesAt(ChunkPos coords) {
      return this.chunkMap.get(coords).stream();
   }

   public Stream<WorldStructureGenerationData.Entry> entriesAt(BlockPos coords) {
      return this.entriesAt(new ChunkPos(coords.getX() - 8 >> 4, coords.getZ() - 8 >> 4)).filter((input) -> {
         StructureBoundingBox bb = input.getBoundingBox();
         return bb != null && bb.isVecInside(coords);
      });
   }

   public Stream<WorldStructureGenerationData.Entry> entriesAt(StructureBoundingBox boundingBox) {
      return RCStructureBoundingBoxes.rasterize(boundingBox, true).stream().flatMap((chunkPos) -> {
         return this.entriesAt(chunkPos).filter((input) -> {
            StructureBoundingBox bb = input.getBoundingBox();
            return bb != null && bb.intersectsWith(boundingBox);
         });
      });
   }

   public Set<ChunkPos> addEntry(WorldStructureGenerationData.Entry entry) {
      this.entryMap.put(entry.getUuid(), entry);
      Set<ChunkPos> rasterized = entry.rasterize();

      for(ChunkPos coords : rasterized) {
         this.chunkMap.put(coords, entry);
      }

      if (entry instanceof WorldStructureGenerationData.StructureEntry) {
         this.instanceMap.put(((WorldStructureGenerationData.StructureEntry)entry).getStructureID(), (WorldStructureGenerationData.StructureEntry)entry);
      }

      this.markDirty();
      return Sets.intersection(this.checkedChunks, rasterized);
   }

   public WorldStructureGenerationData.Entry getEntry(UUID id) {
      return (WorldStructureGenerationData.Entry)this.entryMap.get(id);
   }

   public WorldStructureGenerationData.Entry removeEntry(UUID id) {
      WorldStructureGenerationData.Entry entry = (WorldStructureGenerationData.Entry)this.entryMap.remove(id);
      this.chunkMap.values().removeIf((e) -> {
         return e.uuid.equals(id);
      });
      this.instanceMap.values().removeIf((e) -> {
         return e.uuid.equals(id);
      });
      if (entry != null) {
         this.markDirty();
      }

      return entry;
   }

   public Set<WorldStructureGenerationData.StructureEntry> getEntriesByID(String id) {
      return this.instanceMap.get(id);
   }

   public Stream<ChunkPos> checkAllChunks(Stream<ChunkPos> chunks) {
      return chunks.filter(this::checkChunk);
   }

   public boolean checkChunk(ChunkPos coords) {
      boolean added = this.checkedChunks.add(coords);
      if (added) {
         this.markDirty();
      }

      return added;
   }

   public boolean checkChunkFinal(ChunkPos coords) {
      boolean added = this.checkedChunksFinal.add(coords);
      if (added) {
         this.markDirty();
      }

      return added;
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.entryMap.clear();
      this.chunkMap.clear();
      this.instanceMap.clear();
      NBTCompoundObjects.readListFrom(compound, "entries", WorldStructureGenerationData.StructureEntry::new).forEach(this::addEntry);
      NBTCompoundObjects.readListFrom(compound, "customEntries", WorldStructureGenerationData.CustomEntry::new).forEach(this::addEntry);
      this.checkedChunks.clear();
      NBTTagLists.intArraysFrom(compound, "checkedChunks").forEach((ints) -> {
         this.checkedChunks.add(new ChunkPos(ints[0], ints[1]));
      });
      this.checkedChunksFinal.clear();
      NBTTagLists.intArraysFrom(compound, "checkedChunksFinal").forEach((ints) -> {
         this.checkedChunksFinal.add(new ChunkPos(ints[0], ints[1]));
      });
   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      NBTCompoundObjects.writeListTo(compound, "entries", (Iterable)this.entryMap.values().stream().filter((e) -> {
         return e instanceof WorldStructureGenerationData.StructureEntry;
      }).collect(Collectors.toList()));
      NBTCompoundObjects.writeListTo(compound, "customEntries", (Iterable)this.entryMap.values().stream().filter((e) -> {
         return e instanceof WorldStructureGenerationData.CustomEntry;
      }).collect(Collectors.toList()));
      NBTTagLists.writeIntArraysTo(compound, "checkedChunks", (List)this.checkedChunks.stream().map((c) -> {
         return new int[]{c.x, c.z};
      }).collect(Collectors.toList()));
      NBTTagLists.writeIntArraysTo(compound, "checkedChunksFinal", (List)this.checkedChunksFinal.stream().map((c) -> {
         return new int[]{c.x, c.z};
      }).collect(Collectors.toList()));
      return compound;
   }

   public static class CustomEntry extends WorldStructureGenerationData.Entry {
      public String name;

      public CustomEntry() {
      }

      public CustomEntry(@Nonnull UUID uuid, StructureBoundingBox boundingBox, String name) {
         super(uuid, boundingBox);
         this.name = name;
      }

      @Nonnull
      public static WorldStructureGenerationData.CustomEntry from(String name, StructureBoundingBox boundingBox) {
         return new WorldStructureGenerationData.CustomEntry(UUID.randomUUID(), boundingBox, name);
      }

      public String description() {
         return this.name;
      }

      public void writeToNBT(NBTTagCompound compound) {
         super.writeToNBT(compound);
         compound.setString("name", this.name);
      }

      public void readFromNBT(NBTTagCompound compound) {
         super.readFromNBT(compound);
         this.name = compound.getString("name");
      }
   }

   public abstract static class Entry implements NBTCompoundObject {
      @Nonnull
      protected UUID uuid = UUID.randomUUID();
      protected StructureBoundingBox boundingBox = new StructureBoundingBox();
      protected boolean blocking = true;

      public Entry() {
      }

      public Entry(@Nonnull UUID uuid, StructureBoundingBox boundingBox) {
         this.uuid = uuid;
         this.boundingBox = boundingBox;
      }

      @Nonnull
      public UUID getUuid() {
         return this.uuid;
      }

      public void setUuid(@Nonnull UUID uuid) {
         this.uuid = uuid;
      }

      public StructureBoundingBox getBoundingBox() {
         return this.boundingBox;
      }

      public void setBoundingBox(StructureBoundingBox boundingBox) {
         this.boundingBox = boundingBox;
      }

      public boolean blocking() {
         return this.blocking;
      }

      public void setBlocking(boolean blocking) {
         this.blocking = blocking;
      }

      public Set<ChunkPos> rasterize() {
         return RCStructureBoundingBoxes.rasterize(this.getBoundingBox(), true);
      }

      public abstract String description();

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            WorldStructureGenerationData.StructureEntry entry = (WorldStructureGenerationData.StructureEntry)o;
            return this.uuid.equals(entry.uuid);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.uuid.hashCode();
      }

      public void readFromNBT(NBTTagCompound compound) {
         this.uuid = new UUID(compound.getLong("UUIDMS"), compound.getLong("UUIDLS"));
         this.boundingBox = new StructureBoundingBox(compound.getIntArray("boundingBox"));
         this.blocking = !compound.hasKey("blocking", 1) || compound.getBoolean("blocking");
      }

      public void writeToNBT(NBTTagCompound compound) {
         compound.setLong("UUIDMS", this.uuid.getMostSignificantBits());
         compound.setLong("UUIDLS", this.uuid.getLeastSignificantBits());
         compound.setTag("boundingBox", this.boundingBox.toNBTTagIntArray());
         compound.setBoolean("blocking", this.blocking);
      }
   }

   public static class StructureEntry extends WorldStructureGenerationData.Entry {
      protected String structureID;
      protected String generationInfoID;
      protected long seed;
      protected AxisAlignedTransform2D transform;
      protected NBTBase instanceData;
      protected boolean firstTime = true;
      protected boolean preventComplementation = false;

      public StructureEntry() {
      }

      public StructureEntry(@Nonnull UUID uuid, StructureBoundingBox boundingBox, String structureID, String generationInfoID, AxisAlignedTransform2D transform, boolean preventComplementation) {
         super(uuid, boundingBox);
         this.structureID = structureID;
         this.generationInfoID = generationInfoID;
         this.transform = transform;
         this.preventComplementation = preventComplementation;
      }

      @Nonnull
      public static WorldStructureGenerationData.StructureEntry complete(String structureID, String generationInfoID, StructureBoundingBox boundingBox, AxisAlignedTransform2D transform, boolean hasBeenGenerated) {
         return new WorldStructureGenerationData.StructureEntry(UUID.randomUUID(), boundingBox, structureID, generationInfoID, transform, hasBeenGenerated);
      }

      public String getStructureID() {
         return this.structureID;
      }

      public AxisAlignedTransform2D getTransform() {
         return this.transform;
      }

      public long getSeed() {
         return this.seed;
      }

      public boolean preventComplementation() {
         return this.preventComplementation;
      }

      public void setPreventComplementation(boolean preventComplementation) {
         this.preventComplementation = preventComplementation;
      }

      public NBTBase getInstanceData() {
         return this.instanceData;
      }

      public void readFromNBT(NBTTagCompound compound) {
         super.readFromNBT(compound);
         this.structureID = compound.getString("structureID");
         this.generationInfoID = compound.hasKey(this.generationInfoID, 8) ? compound.getString("generationInfoID") : null;
         this.seed = compound.hasKey("seed") ? compound.getLong("seed") : (new Random()).nextLong();
         this.transform = RCAxisAlignedTransform.read(compound, "rotation", "mirrorX");
         if (compound.hasKey("lowerCoord")) {
            BlockPos lowerCoord = BlockPositions.readFromNBT("lowerCoord", compound);
            Structure structure = (Structure)StructureRegistry.INSTANCE.get(this.structureID);
            this.boundingBox = structure != null ? Structures.boundingBox(lowerCoord, RCAxisAlignedTransform.applySize(this.transform, structure.size())) : new StructureBoundingBox();
         }

         if (compound.hasKey("instanceData", 10)) {
            this.instanceData = compound.getCompoundTag("instanceData");
         }

         this.firstTime = compound.getBoolean("firstTime");
         this.preventComplementation = compound.getBoolean("preventComplementation") || compound.getBoolean("hasBeenGenerated");
      }

      public void writeToNBT(NBTTagCompound compound) {
         super.writeToNBT(compound);
         compound.setString("structureID", this.structureID);
         if (this.generationInfoID != null) {
            compound.setString("generationInfoID", this.generationInfoID);
         }

         compound.setLong("seed", this.seed);
         RCAxisAlignedTransform.write(compound, this.transform, "rotation", "mirrorX");
         if (this.instanceData != null) {
            compound.setTag("instanceData", this.instanceData);
         }

         compound.setBoolean("firstTime", this.firstTime);
         compound.setBoolean("preventComplementation", this.preventComplementation);
      }

      public String description() {
         return this.structureID;
      }
   }
}
