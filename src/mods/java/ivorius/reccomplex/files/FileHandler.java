package ivorius.reccomplex.files;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.files.loading.FileSuffixFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;

public class FileHandler {
   public FileHandler() {
   }

   public List<Path> tryFind(Path path, String name, String suffix) {
      Path filename = RCFiles.filenamePath(name, suffix);
      return RCFiles.listFilesRecursively(path, (entry) -> {
         return entry.getFileName().equals(filename);
      }, true);
   }

   public Set<String> tryFindIDs(Path path, String suffix) {
      return (Set)RCFiles.listFilesRecursively(path, new FileSuffixFilter(new String[]{suffix}), true).stream().map(Path::getFileName).map(Path::toString).map(FilenameUtils::removeExtension).collect(Collectors.toSet());
   }

   public Pair<Set<Path>, Set<Path>> tryDelete(Path path, String name, String suffix) {
      Set<Path> success = new HashSet();
      Set<Path> failure = new HashSet();
      this.tryFind(path, name, suffix).forEach((p) -> {
         try {
            Files.delete(p);
            success.add(p);
         } catch (IOException var4) {
            RecurrentComplex.logger.error("Error deleting resource: " + p, var4);
            failure.add(p);
         }

      });
      return Pair.of(success, failure);
   }
}
