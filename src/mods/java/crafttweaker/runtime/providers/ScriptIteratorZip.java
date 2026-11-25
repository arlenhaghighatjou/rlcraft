package crafttweaker.runtime.providers;

import crafttweaker.runtime.IScriptIterator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ScriptIteratorZip implements IScriptIterator {
   private final File file;
   private final ZipFile zipFile;
   private final Iterator<ZipEntry> entries;
   private final File directory;
   private ZipEntry current;

   private ScriptIteratorZip(File file, ZipFile zipFile, Iterator<ZipEntry> entries, File directory, ZipEntry current) {
      this.file = file;
      this.zipFile = zipFile;
      this.entries = entries;
      this.directory = directory;
      this.current = current;
   }

   public ScriptIteratorZip(File file) throws IOException {
      this.file = file;
      this.directory = null;
      this.zipFile = new ZipFile(file);
      List<ZipEntry> entriesList = new ArrayList();
      Enumeration<? extends ZipEntry> original = this.zipFile.entries();

      while(original.hasMoreElements()) {
         ZipEntry entry = (ZipEntry)original.nextElement();
         if (entry.getName().toLowerCase().endsWith(".zs")) {
            entriesList.add(entry);
         }
      }

      this.entries = entriesList.iterator();
   }

   public ScriptIteratorZip(File file, File directory) throws IOException {
      this.file = file;
      this.directory = directory;
      this.zipFile = new ZipFile(file);
      List<ZipEntry> entriesList = new ArrayList();
      Enumeration<? extends ZipEntry> original = this.zipFile.entries();

      while(original.hasMoreElements()) {
         ZipEntry entry = (ZipEntry)original.nextElement();
         if (entry.getName().toLowerCase().endsWith(".zs")) {
            entriesList.add(entry);
         }
      }

      this.entries = entriesList.iterator();
   }

   public String getCurrentName() {
      return this.current.getName();
   }

   public String getGroupName() {
      if (this.file != null && this.directory != null) {
         return this.file.getAbsolutePath().substring(this.directory.getAbsolutePath().length() + 1);
      } else {
         return this.file != null ? this.file.getName() : "invalid_group_name";
      }
   }

   public boolean next() {
      if (this.entries.hasNext()) {
         this.current = (ZipEntry)this.entries.next();
         return true;
      } else {
         return false;
      }
   }

   public String getName() {
      return this.current != null && this.zipFile != null ? this.current.getName().split("/")[this.current.getName().split("/").length - 1] : "zzNullzz";
   }

   public InputStream open() throws IOException {
      return this.zipFile.getInputStream(this.current);
   }

   public IScriptIterator copyCurrent() {
      return new ScriptIteratorZip(this.file, this.zipFile, this.entries, this.directory, this.current);
   }
}
