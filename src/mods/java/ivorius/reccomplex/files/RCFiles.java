package ivorius.reccomplex.files;

import ivorius.reccomplex.RecurrentComplex;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

public class RCFiles {
   public RCFiles() {
   }

   public static String encodePath(String path) {
      return path.replaceAll(" ", "%20");
   }

   public static void closeQuietly(FileSystem fileSystem) {
      if (fileSystem != FileSystems.getDefault()) {
         try {
            fileSystem.close();
         } catch (ClosedFileSystemException | IOException var2) {
            ;
         }
      }

   }

   public static void closeQuietly(Path path) {
      if (path != null) {
         closeQuietly(path.getFileSystem());
      }

   }

   static Path resourceToPath(URL resource) throws IOException, URISyntaxException {
      Objects.requireNonNull(resource, "Resource URL cannot be null");
      URI uri = resource.toURI();
      String scheme = uri.getScheme();
      if (scheme.equals("file")) {
         return Paths.get(uri);
      } else if (!scheme.equals("jar")) {
         throw new IllegalArgumentException("Cannot convert to Path: " + uri);
      } else {
         String s = encodePath(uri.toString());
         int separator = s.indexOf("!/");
         String entryName = s.substring(separator + 2);
         URI fileURI = URI.create(s.substring(0, separator));

         try {
            FileSystem fs = FileSystems.getFileSystem(fileURI);
            if (fs.isOpen()) {
               return fs.getPath(entryName);
            }
         } catch (FileSystemNotFoundException var8) {
            ;
         }

         FileSystem fs = FileSystems.newFileSystem(fileURI, Collections.emptyMap());
         return fs.getPath(entryName);
      }
   }

   @Nullable
   public static Path pathFromResourceLocation(ResourceLocation resourceLocation) throws RCFiles.ResourceLocationLoadException {
      try {
         URL resource = RCFiles.class.getResource(String.format("/assets/%s%s", resourceLocation.getResourceDomain(), resourceLocation.getResourcePath().isEmpty() ? "" : "/" + resourceLocation.getResourcePath()));
         return resource != null ? resourceToPath(resource.toURI().toURL()) : null;
      } catch (Exception var2) {
         throw new RCFiles.ResourceLocationLoadException(var2, resourceLocation);
      }
   }

   @Nullable
   public static Path tryPathFromResourceLocation(ResourceLocation resourceLocation) {
      try {
         return pathFromResourceLocation(resourceLocation);
      } catch (Throwable var2) {
         RecurrentComplex.logger.error("Error reading from resource location '" + resourceLocation + "'", var2);
         return null;
      }
   }

   public static void walkFilesRecursively(Path dir, final Filter<Path> filter, final boolean recursive, final Consumer<Path> consumer) {
      if (Files.exists(dir, new LinkOption[0])) {
         try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
               public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                  if (filter.accept(file)) {
                     consumer.accept(file);
                  }

                  return recursive ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
               }
            });
         } catch (IOException var5) {
            RecurrentComplex.logger.error(var5);
         }

      }
   }

   public static List<Path> listFilesRecursively(Path dir, Filter<Path> filter, boolean recursive) {
      List<Path> list = new ArrayList();
      walkFilesRecursively(dir, filter, recursive, list::add);
      return list;
   }

   public static boolean ensure(File file) {
      if (!file.exists() && !file.mkdir()) {
         System.out.println("Could not create " + file.getName() + " folder");
         return false;
      } else {
         return true;
      }
   }

   public static File getValidatedFolder(File file, boolean create) {
      File var10000;
      label23: {
         if (create) {
            if (ensure(file)) {
               break label23;
            }
         } else if (file.exists()) {
            break label23;
         }

         var10000 = null;
         return var10000;
      }

      var10000 = file;
      return var10000;
   }

   @Nullable
   public static String extension(Path path) {
      return FilenameUtils.getExtension(path.getFileName().toString());
   }

   public static Path filenamePath(String name, String extension) {
      return Paths.get(name + "." + extension);
   }

   public static class ResourceLocationLoadException extends Exception {
      public final ResourceLocation location;

      public ResourceLocationLoadException(ResourceLocation location) {
         this.location = location;
      }

      public ResourceLocationLoadException(String message, ResourceLocation location) {
         super(message);
         this.location = location;
      }

      public ResourceLocationLoadException(String message, Throwable cause, ResourceLocation location) {
         super(message, cause);
         this.location = location;
      }

      public ResourceLocationLoadException(Throwable cause, ResourceLocation location) {
         super(cause);
         this.location = location;
      }

      public ResourceLocationLoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ResourceLocation location) {
         super(message, cause, enableSuppression, writableStackTrace);
         this.location = location;
      }

      public ResourceLocation getLocation() {
         return this.location;
      }
   }
}
