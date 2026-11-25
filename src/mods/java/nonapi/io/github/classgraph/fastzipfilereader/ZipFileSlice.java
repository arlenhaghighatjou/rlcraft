package nonapi.io.github.classgraph.fastzipfilereader;

import java.io.File;
import java.io.IOException;
import nonapi.io.github.classgraph.recycler.Recycler;
import nonapi.io.github.classgraph.scanspec.WhiteBlackList;

public class ZipFileSlice {
   private final ZipFileSlice parentZipFileSlice;
   public final PhysicalZipFile physicalZipFile;
   final long startOffsetWithinPhysicalZipFile;
   final long len;
   private final String pathWithinParentZipFileSlice;
   final Recycler<ZipFileSliceReader, RuntimeException> zipFileSliceReaderRecycler;

   private Recycler<ZipFileSliceReader, RuntimeException> newZipFileSliceReaderRecycler() {
      return new Recycler<ZipFileSliceReader, RuntimeException>() {
         public ZipFileSliceReader newInstance() throws RuntimeException {
            return new ZipFileSliceReader(ZipFileSlice.this);
         }
      };
   }

   ZipFileSlice(PhysicalZipFile physicalZipFile) {
      this.parentZipFileSlice = null;
      this.physicalZipFile = physicalZipFile;
      this.startOffsetWithinPhysicalZipFile = 0L;
      this.len = physicalZipFile.fileLen;
      this.pathWithinParentZipFileSlice = physicalZipFile.getPath();
      this.zipFileSliceReaderRecycler = this.newZipFileSliceReaderRecycler();
   }

   ZipFileSlice(PhysicalZipFile physicalZipFileInRam, FastZipEntry zipEntry) {
      this.parentZipFileSlice = zipEntry.parentLogicalZipFile;
      this.physicalZipFile = physicalZipFileInRam;
      this.startOffsetWithinPhysicalZipFile = 0L;
      this.len = this.physicalZipFile.fileLen;
      this.pathWithinParentZipFileSlice = zipEntry.entryName;
      this.zipFileSliceReaderRecycler = this.newZipFileSliceReaderRecycler();
   }

   ZipFileSlice(FastZipEntry zipEntry) throws IOException, InterruptedException {
      this.parentZipFileSlice = zipEntry.parentLogicalZipFile;
      this.physicalZipFile = zipEntry.parentLogicalZipFile.physicalZipFile;
      this.startOffsetWithinPhysicalZipFile = zipEntry.getEntryDataStartOffsetWithinPhysicalZipFile();
      this.len = zipEntry.compressedSize;
      this.pathWithinParentZipFileSlice = zipEntry.entryName;
      this.zipFileSliceReaderRecycler = this.newZipFileSliceReaderRecycler();
   }

   ZipFileSlice(ZipFileSlice other) {
      this.parentZipFileSlice = other.parentZipFileSlice;
      this.physicalZipFile = other.physicalZipFile;
      this.startOffsetWithinPhysicalZipFile = other.startOffsetWithinPhysicalZipFile;
      this.len = other.len;
      this.pathWithinParentZipFileSlice = other.pathWithinParentZipFileSlice;
      this.zipFileSliceReaderRecycler = other.zipFileSliceReaderRecycler;
   }

   public boolean isWhitelistedAndNotBlacklisted(WhiteBlackList.WhiteBlackListLeafname jarWhiteBlackList) {
      return jarWhiteBlackList.isWhitelistedAndNotBlacklisted(this.pathWithinParentZipFileSlice) && (this.parentZipFileSlice == null || this.parentZipFileSlice.isWhitelistedAndNotBlacklisted(jarWhiteBlackList));
   }

   public ZipFileSlice getParentZipFileSlice() {
      return this.parentZipFileSlice;
   }

   public String getPathWithinParentZipFileSlice() {
      return this.pathWithinParentZipFileSlice;
   }

   private void appendPath(StringBuilder buf) {
      if (this.parentZipFileSlice != null) {
         this.parentZipFileSlice.appendPath(buf);
         if (buf.length() > 0) {
            buf.append("!/");
         }
      }

      buf.append(this.pathWithinParentZipFileSlice);
   }

   public String getPath() {
      StringBuilder buf = new StringBuilder();
      this.appendPath(buf);
      return buf.toString();
   }

   public File getPhysicalFile() {
      return this.physicalZipFile.getFile();
   }

   public int hashCode() {
      return this.physicalZipFile.getPath().hashCode() ^ (int)this.startOffsetWithinPhysicalZipFile ^ (int)this.len;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ZipFileSlice)) {
         return false;
      } else {
         ZipFileSlice other = (ZipFileSlice)obj;
         return this.startOffsetWithinPhysicalZipFile == other.startOffsetWithinPhysicalZipFile && this.len == other.len && this.physicalZipFile.equals(other.physicalZipFile);
      }
   }

   public String toString() {
      return "[" + (this.physicalZipFile.isDeflatedToRam ? "ByteBuffer deflated to RAM from " + this.getPath() : (this.physicalZipFile.getFile() == null ? "ByteBuffer downloaded to RAM from " + this.getPath() : this.physicalZipFile.getFile())) + " ; byte range: " + this.startOffsetWithinPhysicalZipFile + ".." + (this.startOffsetWithinPhysicalZipFile + this.len) + " / " + this.physicalZipFile.fileLen + "]";
   }
}
