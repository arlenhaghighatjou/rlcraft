package betterquesting.api2.client.gui.panels.lists;

import betterquesting.api2.client.gui.misc.IGuiRect;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class CanvasFileDirectory extends CanvasSearch<File, File> {
   private static final CanvasFileDirectory.FileSort sorter = new CanvasFileDirectory.FileSort();
   private final FileFilter filter;
   private File curDir;

   public CanvasFileDirectory(IGuiRect rect, File dirStart, FileFilter filter) {
      super(rect);
      this.filter = filter;
      this.curDir = dirStart;
      if (this.curDir != null && !this.curDir.isDirectory()) {
         this.curDir = this.curDir.getParentFile();
      }

   }

   public void setCurDirectory(File file) {
      this.curDir = file;
      this.refreshSearch();
      this.scrollY.writeValue(Float.valueOf(0.0F));
   }

   protected Iterator<File> getIterator() {
      File[] files;
      if (this.curDir == null) {
         files = File.listRoots();
      } else {
         files = !this.curDir.isDirectory() ? new File[0] : this.curDir.listFiles(this.filter);
      }

      if (files == null) {
         files = new File[0];
      }

      List<File> fList = Arrays.asList(files);
      fList.sort(sorter);
      return fList.iterator();
   }

   protected void queryMatches(File value, String query, ArrayDeque<File> results) {
      if (value.getName().toLowerCase().contains(query.toLowerCase())) {
         results.add(value);
      }

   }

   private static class FileSort implements Comparator<File> {
      private FileSort() {
      }

      public int compare(File f1, File f2) {
         if (f1 != null && f2 != null) {
            if (f1.isDirectory() == f2.isDirectory()) {
               return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
            } else {
               return f1.isDirectory() ? -1 : 1;
            }
         } else {
            return 0;
         }
      }

      public boolean equals(Object obj) {
         return obj == this;
      }
   }
}
