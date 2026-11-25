package ivorius.reccomplex.world.gen.feature.structure.generic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import ivorius.ivtoolkit.tools.IvFileHelper;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.files.loading.FileLoaderRegistry;
import ivorius.reccomplex.files.saving.FileSaverAdapter;
import ivorius.reccomplex.json.NBTToJson;
import ivorius.reccomplex.utils.ByteArrays;
import ivorius.reccomplex.utils.zip.IvZips;
import ivorius.reccomplex.utils.zip.ZipFinder;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class StructureSaveHandler {
   public static final StructureSaveHandler INSTANCE = new StructureSaveHandler("rcst", StructureRegistry.INSTANCE);
   public static final String STRUCTURE_INFO_JSON_FILENAME = "structure.json";
   public static final String WORLD_DATA_NBT_FILENAME = "worldData.nbt";
   public final Gson gson = this.createGson();
   public String suffix;
   public StructureRegistry registry;

   public StructureSaveHandler(String suffix, StructureRegistry registry) {
      this.suffix = suffix;
      this.registry = registry;
   }

   public Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(GenericStructure.class, new GenericStructure.Serializer());
      StructureRegistry.TRANSFORMERS.constructGson(builder);
      StructureRegistry.GENERATION_TYPES.constructGson(builder);
      builder.registerTypeAdapter(GenericVariableDomain.Variable.class, new GenericVariableDomain.Variable.Serializer());
      NBTToJson.registerSafeNBTSerializer(builder);
      return builder.create();
   }

   public GenericStructure fromJSON(String jsonData, NBTTagCompound worldData) throws JsonSyntaxException {
      GenericStructure structure = (GenericStructure)this.gson.fromJson(jsonData, GenericStructure.class);
      structure.worldDataCompound = worldData;
      return structure;
   }

   public String toJSON(GenericStructure structureInfo) {
      return this.gson.toJson(structureInfo, GenericStructure.class);
   }

   public GenericStructure fromResource(ResourceLocation resourceLocation) {
      try {
         ZipInputStream zipInputStream = new ZipInputStream(IvFileHelper.inputStreamFromResourceLocation(resourceLocation));
         Throwable var3 = null;

         GenericStructure var4;
         try {
            var4 = this.fromZip(zipInputStream);
         } catch (Throwable var14) {
            var3 = var14;
            throw var14;
         } finally {
            if (zipInputStream != null) {
               if (var3 != null) {
                  try {
                     zipInputStream.close();
                  } catch (Throwable var13) {
                     var3.addSuppressed(var13);
                  }
               } else {
                  zipInputStream.close();
               }
            }

         }

         return var4;
      } catch (Exception var16) {
         RecurrentComplex.logger.error("Could not read generic structure " + resourceLocation.toString(), var16);
         return null;
      }
   }

   public GenericStructure fromZip(ZipInputStream zipInputStream) throws IOException {
      ZipFinder finder = new ZipFinder();
      ZipFinder.Result<String> json = finder.<String>bytes("structure.json", String::new);
      ZipFinder.Result<NBTTagCompound> worldData = finder.<NBTTagCompound>bytes("worldData.nbt", (bytes) -> {
         return CompressedStreamTools.readCompressed(new ByteArrayInputStream(bytes));
      });

      try {
         finder.read(zipInputStream);
         return this.fromJSON(json.get(), worldData.get());
      } catch (ZipFinder.MissingEntryException | IOException var6) {
         throw new IOException("Error loading structure", var6);
      }
   }

   public void toZip(Structure<?> structure, ZipOutputStream zipOutputStream) throws IOException {
      GenericStructure copy = structure.copyAsGenericStructure();
      Objects.requireNonNull(copy);
      IvZips.addZipEntry(zipOutputStream, "structure.json", this.toJSON(copy).getBytes());
      IvZips.addZipEntry(zipOutputStream, "worldData.nbt", ByteArrays.toByteArray((s) -> {
         CompressedStreamTools.writeCompressed(copy.worldDataCompound, s);
      }));
      zipOutputStream.close();
   }

   public class Loader extends FileLoaderRegistry<GenericStructure> {
      public Loader() {
         super(StructureSaveHandler.this.suffix, StructureSaveHandler.this.registry);
      }

      public GenericStructure read(Path path, String name) throws Exception {
         ZipInputStream zip = new ZipInputStream(Files.newInputStream(path));
         Throwable var4 = null;

         GenericStructure var5;
         try {
            var5 = StructureSaveHandler.this.fromZip(zip);
         } catch (Throwable var14) {
            var4 = var14;
            throw var14;
         } finally {
            if (zip != null) {
               if (var4 != null) {
                  try {
                     zip.close();
                  } catch (Throwable var13) {
                     var4.addSuppressed(var13);
                  }
               } else {
                  zip.close();
               }
            }

         }

         return var5;
      }
   }

   public class Saver extends FileSaverAdapter<Structure<?>> {
      public Saver(String id) {
         super(id, StructureSaveHandler.this.suffix, StructureSaveHandler.this.registry);
      }

      public void saveFile(Path path, Structure<?> structure) throws Exception {
         ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(path));
         Throwable var4 = null;

         try {
            StructureSaveHandler.this.toZip(structure, zipOutputStream);
         } catch (Throwable var13) {
            var4 = var13;
            throw var13;
         } finally {
            if (zipOutputStream != null) {
               if (var4 != null) {
                  try {
                     zipOutputStream.close();
                  } catch (Throwable var12) {
                     var4.addSuppressed(var12);
                  }
               } else {
                  zipOutputStream.close();
               }
            }

         }

      }
   }
}
