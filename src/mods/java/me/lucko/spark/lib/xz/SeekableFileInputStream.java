package me.lucko.spark.lib.xz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SeekableFileInputStream extends SeekableInputStream {
   protected RandomAccessFile randomAccessFile;

   public SeekableFileInputStream(File var1) throws FileNotFoundException {
      this.randomAccessFile = new RandomAccessFile(var1, "r");
   }

   public SeekableFileInputStream(String var1) throws FileNotFoundException {
      this.randomAccessFile = new RandomAccessFile(var1, "r");
   }

   public SeekableFileInputStream(RandomAccessFile var1) {
      this.randomAccessFile = var1;
   }

   public int read() throws IOException {
      return this.randomAccessFile.read();
   }

   public int read(byte[] var1) throws IOException {
      return this.randomAccessFile.read(var1);
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      return this.randomAccessFile.read(var1, var2, var3);
   }

   public void close() throws IOException {
      this.randomAccessFile.close();
   }

   public long length() throws IOException {
      return this.randomAccessFile.length();
   }

   public long position() throws IOException {
      return this.randomAccessFile.getFilePointer();
   }

   public void seek(long var1) throws IOException {
      this.randomAccessFile.seek(var1);
   }
}
