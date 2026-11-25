package ivorius.ivtoolkit.tools;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.ResourceLocation;

public class IvPathHelper {
   public IvPathHelper() {
   }

   public static Path resourceToPath(URL resource) throws IOException, URISyntaxException {
      Objects.requireNonNull(resource, "Resource URL cannot be null");
      URI uri = resource.toURI();
      String scheme = uri.getScheme();
      if (scheme.equals("file")) {
         return Paths.get(uri);
      } else if (!scheme.equals("jar")) {
         throw new IllegalArgumentException("Cannot convert to Path: " + uri);
      } else {
         String s = uri.toString();
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

   public static Path pathFromResourceLocation(ResourceLocation resourceLocation) throws URISyntaxException, IOException {
      URL resource = IvPathHelper.class.getResource("/assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath());
      return resource != null ? resourceToPath(resource.toURI().toURL()) : null;
   }

   public static List<Path> listFilesRecursively(Path dir, final Filter<Path> filter, final boolean recursive) throws IOException {
      final List<Path> files = new ArrayList();
      Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
         public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (filter.accept(file)) {
               files.add(file);
            }

            return recursive ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
         }
      });
      return files;
   }
}
