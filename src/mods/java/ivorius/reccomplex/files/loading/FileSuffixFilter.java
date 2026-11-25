package ivorius.reccomplex.files.loading;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.Arrays;
import org.apache.commons.io.FilenameUtils;

public class FileSuffixFilter implements Filter<Path>, FilenameFilter {
   private Iterable<String> acceptedSuffixes;

   public FileSuffixFilter(Iterable<String> acceptedSuffixes) {
      this.acceptedSuffixes = acceptedSuffixes;
   }

   public FileSuffixFilter(String... acceptedSuffixes) {
      this(Arrays.asList(acceptedSuffixes));
   }

   public boolean accept(Path entry) throws IOException {
      return this.accept(entry.getFileName().toString());
   }

   public boolean accept(File dir, String name) {
      return this.accept(name);
   }

   protected boolean accept(String name) {
      for(String s : this.acceptedSuffixes) {
         if (FilenameUtils.isExtension(name, s)) {
            return true;
         }
      }

      return false;
   }
}
