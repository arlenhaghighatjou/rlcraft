package leviathan143.loottweaker.common.lib;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableFinder {
   public static final LootTableFinder DEFAULT = new LootTableFinder();
   private static final Logger LOGGER = LogManager.getLogger();
   private final Set<ResourceLocation> validIds = new HashSet(LootTableList.getAll());
   private boolean fullScanPerformed = false;

   public LootTableFinder() {
   }

   public boolean exists(ResourceLocation tableId) {
      if (this.validIds.contains(tableId)) {
         return true;
      } else {
         String assetLocation = "assets/" + tableId.getResourceDomain() + "/loot_tables/" + tableId.getResourcePath() + ".json";
         if (this.getClass().getClassLoader().getResource(assetLocation) != null) {
            this.add(tableId);
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean add(ResourceLocation tableId) {
      return this.validIds.add(tableId);
   }

   public boolean fullScanPerformed() {
      return this.fullScanPerformed;
   }

   public Iterable<ResourceLocation> findAll() {
      if (!this.fullScanPerformed) {
         LOGGER.info("Locating all existing loot tables");
         ClassLoader modClassLoader = Loader.instance().getModClassLoader();
         Set<Path> visitedSources = new HashSet();

         for(ModContainer mod : Loader.instance().getActiveModList()) {
            if (mod.getSource() == null) {
               LOGGER.info("Skipped {} ({}) as it reported a null source", mod.getModId(), mod.getName());
            } else if (mod.getSource().exists()) {
               Path sourcePath = mod.getSource().toPath();
               if (!visitedSources.contains(sourcePath)) {
                  LOGGER.debug("Visiting source of {} at {}", mod.getModId(), mod.getSource());
                  this.visitSource(modClassLoader, sourcePath, this::add);
                  visitedSources.add(sourcePath);
               }
            } else {
               Level level = !mod.getModId().equals("minecraft") && !mod.getModId().equals("mcp") ? Level.INFO : Level.DEBUG;
               LOGGER.log(level, "Skipped {} ({}) as it reported a nonexistent source {}", mod.getModId(), mod.getName(), mod.getSource().getAbsolutePath());
            }
         }

         LOGGER.info("All existing loot tables located");
         this.fullScanPerformed = true;
      }

      return this.validIds;
   }

   private void visitSource(ClassLoader modClassLoader, Path sourcePath, Consumer<ResourceLocation> idSubmitter) {
      try {
         if (Files.isDirectory(sourcePath, new LinkOption[0])) {
            FileSystem fs = FileSystems.getDefault();
            Path assetsDir = fs.getPath("assets");
            if (!Files.exists(assetsDir, new LinkOption[0])) {
               return;
            }

            this.walkAssetsDirectory(assetsDir, idSubmitter);
         }

         FileSystem fs = this.getFileSystem(modClassLoader, sourcePath);
         Throwable var21 = null;

         try {
            Path assetsDir = fs.getPath("assets");
            if (Files.exists(assetsDir, new LinkOption[0])) {
               this.walkAssetsDirectory(assetsDir, idSubmitter);
               return;
            }
         } catch (Throwable var17) {
            var21 = var17;
            throw var17;
         } finally {
            if (fs != null) {
               if (var21 != null) {
                  try {
                     fs.close();
                  } catch (Throwable var16) {
                     var21.addSuppressed(var16);
                  }
               } else {
                  fs.close();
               }
            }

         }

      } catch (IOException var19) {
         var19.printStackTrace();
      }
   }

   private FileSystem getFileSystem(ClassLoader modClassLoader, Path sourcePath) throws IOException {
      return !Files.isDirectory(sourcePath, new LinkOption[0]) ? FileSystems.newFileSystem(sourcePath, modClassLoader) : FileSystems.getDefault();
   }

   private void walkAssetsDirectory(Path assetsDir, Consumer<ResourceLocation> idSubmitter) throws IOException {
      Stream var10000 = Files.walk(assetsDir, 1, new FileVisitOption[0]);
      assetsDir.getClass();
      var10000 = var10000.filter(Predicates.not(assetsDir::equals));
      var10000.getClass();

      for(Path domain : var10000::iterator) {
         Path lootTablesDir = domain.resolve("loot_tables");
         if (Files.exists(lootTablesDir, new LinkOption[0])) {
            this.walkLootTablesDirectory(lootTablesDir, idSubmitter);
         }
      }

   }

   private void walkLootTablesDirectory(Path lootTablesDir, Consumer<ResourceLocation> idSubmitter) throws IOException {
      Stream var10000 = Files.walk(lootTablesDir);
      lootTablesDir.getClass();
      var10000 = var10000.filter(Predicates.not(lootTablesDir::equals));
      var10000.getClass();

      for(Path lootTable : var10000::iterator) {
         if (FilenameUtils.getExtension(lootTable.getFileName().toString()).equals("json")) {
            String namespace = lootTablesDir.getName(1).toString();
            String path = FilenameUtils.removeExtension(lootTablesDir.relativize(lootTable).toString());
            idSubmitter.accept(new ResourceLocation(namespace, path));
         }
      }

   }
}
