package ivorius.reccomplex.files.loading;

import io.netty.buffer.ByteBuf;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.files.RCFiles;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import javax.annotation.Nonnull;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public enum ResourceDirectory {
   ACTIVE(true, LeveledRegistry.Level.CUSTOM),
   INACTIVE(false, LeveledRegistry.Level.CUSTOM),
   SERVER_ACTIVE(true, LeveledRegistry.Level.SERVER),
   SERVER_INACTIVE(false, LeveledRegistry.Level.SERVER);

   public static final String ACTIVE_DIR_NAME = "active";
   public static final String INACTIVE_DIR_NAME = "inactive";
   public static final String RESOURCES_FILE_NAME = "structures";
   private boolean active;
   private LeveledRegistry.Level level;

   private ResourceDirectory(boolean active, LeveledRegistry.Level level) {
      this.active = active;
      this.level = level;
   }

   public static ResourceDirectory custom(boolean active) {
      return active ? ACTIVE : INACTIVE;
   }

   public static ResourceDirectory server(boolean active) {
      return active ? SERVER_ACTIVE : SERVER_INACTIVE;
   }

   public static ResourceDirectory read(ByteBuf buf) {
      return valueOf(ByteBufUtils.readUTF8String(buf));
   }

   public static File getCustomDirectory() {
      return RecurrentComplex.proxy.getDataDirectory();
   }

   @Nonnull
   public static File getServerDirectory() {
      return getServer().getEntityWorld().getSaveHandler().getWorldDirectory();
   }

   public static void tryReload(@Nonnull FileLoader loader, @Nonnull LeveledRegistry.Level level) throws IllegalArgumentException, ResourceDirectory.NoServerException {
      try {
         reload(loader, level);
      } catch (RCFiles.ResourceLocationLoadException var3) {
         RecurrentComplex.logger.error("Error reloading from resource location '" + var3.getLocation() + "'", var3);
      }

   }

   public static void reload(@Nonnull FileLoader loader, @Nonnull LeveledRegistry.Level level) throws IllegalArgumentException, ResourceDirectory.NoServerException, RCFiles.ResourceLocationLoadException {
      switch(level) {
      case CUSTOM:
         loader.clearFiles(level);
         tryLoadResources(loader, level, getCustomDirectory().toPath(), "", true);
         break;
      case MODDED:
         loader.clearFiles(level);

         for(ModContainer mod : Loader.instance().getModList()) {
            String domain = mod.getModId();
            Path path = null;

            try {
               path = RCFiles.pathFromResourceLocation(new ResourceLocation(domain.toLowerCase(), ""));
               if (path != null) {
                  tryLoadResources(loader, level, path, domain, false);
               }
            } finally {
               RCFiles.closeQuietly(path);
            }
         }

         return;
      case SERVER:
         loader.clearFiles(level);
         tryLoadResources(loader, level, getServerDirectory().toPath(), "", true);
         break;
      default:
         throw new IllegalArgumentException();
      }

   }

   public static void tryLoadResources(FileLoader loader, LeveledRegistry.Level level, Path path, String domain, boolean create) {
      tryLoadResources(loader, path.resolve("structures"), level, loader.keySet(), domain, create);
   }

   public static void tryLoadResources(FileLoader loader, Path path, LeveledRegistry.Level level, Collection<String> suffices, String domain, boolean create) {
      tryLoadAll(loader, path.resolve("inactive"), new FileLoadContext(domain, false, level), create, suffices);
      tryLoadAll(loader, path.resolve("active"), new FileLoadContext(domain, true, level), create, suffices);
      tryLoadAll(loader, path.resolve("silentStructures"), new FileLoadContext(domain, false, level), false, suffices);
      tryLoadAll(loader, path.resolve("genericStructures"), new FileLoadContext(domain, true, level), false, suffices);
      tryLoadAll(loader, path.resolve("inventoryGenerators"), new FileLoadContext(domain, true, level), false, suffices);
   }

   protected static void tryLoadAll(FileLoader loader, Path path, FileLoadContext context, boolean create, Collection<String> suffices) {
      if (create) {
         try {
            Files.createDirectories(path);
         } catch (Exception var6) {
            RecurrentComplex.logger.error("Error creating directory", var6);
            return;
         }
      }

      loader.tryLoadAll(path, context, suffices);
   }

   @Nonnull
   protected static ResourceLocation resourceLocation(String domain, String directoryName) {
      return new ResourceLocation(domain, String.format("%s/%s", "structures", directoryName));
   }

   protected static MinecraftServer getServer() throws ResourceDirectory.NoServerException {
      if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
         throw new ResourceDirectory.NoServerException();
      } else {
         MinecraftServer instance = FMLCommonHandler.instance().getMinecraftServerInstance();
         if (instance == null) {
            throw new ResourceDirectory.NoServerException();
         } else {
            return instance;
         }
      }
   }

   public boolean isServer() {
      return this == SERVER_ACTIVE || this == SERVER_INACTIVE;
   }

   public boolean isCustom() {
      return this == ACTIVE || this == INACTIVE;
   }

   public LeveledRegistry.Level getLevel() {
      return this.level;
   }

   @Nonnull
   public Path toPath() {
      return this.toFile().toPath();
   }

   public File toFile() {
      return RCFiles.getValidatedFolder(new File(this.getParent(), this.subDirectoryName()), true);
   }

   public File getParent() {
      return this.isServer() ? new File(getServerDirectory(), "structures") : new File(getCustomDirectory(), "structures");
   }

   public String subDirectoryName() {
      return this.isActive() ? "active" : "inactive";
   }

   public boolean isActive() {
      return this.active;
   }

   public ResourceDirectory opposite() {
      switch(this) {
      case ACTIVE:
         return INACTIVE;
      case INACTIVE:
         return ACTIVE;
      case SERVER_ACTIVE:
         return SERVER_INACTIVE;
      case SERVER_INACTIVE:
         return SERVER_ACTIVE;
      default:
         throw new IllegalStateException();
      }
   }

   public void write(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.name());
   }

   public String readableName() {
      switch(this) {
      case ACTIVE:
         return "active";
      case INACTIVE:
         return "inactive";
      case SERVER_ACTIVE:
         return "world/active";
      case SERVER_INACTIVE:
         return "world/inactive";
      default:
         throw new IllegalStateException();
      }
   }

   public static class NoServerException extends RuntimeException {
      public NoServerException() {
      }

      public NoServerException(String message) {
         super(message);
      }

      public NoServerException(String message, Throwable cause) {
         super(message, cause);
      }

      public NoServerException(Throwable cause) {
         super(cause);
      }

      public NoServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
         super(message, cause, enableSuppression, writableStackTrace);
      }
   }
}
