package com.fantasticsource.tools;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PNG {
   private static int totalBuffers = 0;
   private static long totalBufferMemory = 0L;
   private int width;
   private int height;
   private ByteBuffer directBuffer = null;
   private boolean loaded = false;

   private PNG() {
   }

   public static PNG load(String filename) {
      InputStream input = null;

      try {
         input = new FileInputStream(filename);
      } catch (FileNotFoundException var3) {
         var3.printStackTrace();
      }

      return load(input);
   }

   public static PNG load(InputStream input) {
      PNG png = new PNG();

      try {
         byte[] buffer = new byte[4096];
         read(input, buffer, 8);
         if (Tools.bytesToInt(buffer, 0) != -1991225785 || Tools.bytesToInt(buffer, 4) != 218765834) {
            throw new IOException("Not a PNG file (file header is not PNG file header)");
         }

         read(input, buffer, 8);
         if (Tools.bytesToInt(buffer, 0) != 13) {
            throw new IOException("PNG has wrong image header length");
         }

         if (!Tools.bytesToASCII(buffer, 4, 4).equals("IHDR")) {
            throw new IOException("PNG file's first chunk was not image header");
         }

         read(input, buffer, 13);
         png.width = Tools.bytesToInt(buffer, 0);
         png.height = Tools.bytesToInt(buffer, 4);
         if (buffer[8] != 8) {
            throw new IllegalArgumentException("PNG does not have 8 bits of alpha");
         }

         if (buffer[9] != 6) {
            throw new IllegalArgumentException("PNG is not 32 bit (true color + alpha) color format");
         }

         if (buffer[12] != 0) {
            throw new IOException("PNG does not use standard interlacing");
         }

         skip(input, 4);
         read(input, buffer, 8);

         while(!Tools.bytesToASCII(buffer, 4, 4).equals("IDAT")) {
            skip(input, Tools.bytesToInt(buffer, 0) + 4);
            read(input, buffer, 8);
         }

         int chunkBytesRemaining = Tools.bytesToInt(buffer, 0);
         int lineSize = png.width * 4;
         png.directBuffer = Tools.allocateNative(png.height * lineSize);
         ++totalBuffers;
         totalBufferMemory += (long)(png.height * lineSize);
         byte[] line = new byte[lineSize + 1];
         byte[] lastLine = new byte[lineSize + 1];
         Inflater inflater = new Inflater();

         try {
            for(int y = 0; y < png.height; ++y) {
               int bytesRead;
               for(int position = 0; position < line.length; position += bytesRead) {
                  bytesRead = inflater.inflate(line, position, line.length - position);
                  if (bytesRead == 0) {
                     if (inflater.finished()) {
                        throw new EOFException("PNG had EOF before all image data could be read");
                     }

                     if (chunkBytesRemaining == 0) {
                        skip(input, 4);
                        read(input, line, 8);
                        if (!Tools.bytesToASCII(line, 4, 4).equals("IDAT")) {
                           throw new IOException("PNG has less image data than header indicates");
                        }

                        chunkBytesRemaining = Tools.bytesToInt(line, 0);
                     }

                     int read = readChunkOrMax(buffer, input, chunkBytesRemaining);
                     chunkBytesRemaining -= read;
                     inflater.setInput(buffer, 0, read);
                  }
               }

               png.unfilter(line, lastLine);
               png.directBuffer.position(y * lineSize);
               png.directBuffer.put(line, 1, lineSize);
               System.arraycopy(line, 0, lastLine, 0, line.length);
            }
         } catch (DataFormatException var16) {
            var16.printStackTrace();
         } finally {
            inflater.end();
         }

         input.close();
         png.loaded = true;
         png.directBuffer.flip();
      } catch (IOException var18) {
         var18.printStackTrace();
      }

      return png;
   }

   public static int totalBuffersUsed() {
      return totalBuffers;
   }

   public static long totalBufferMemoryUsed() {
      return totalBufferMemory;
   }

   private static int readChunkOrMax(byte[] buffer, InputStream input, int chunkBytesRemaining) throws IOException {
      int length = buffer.length;
      if (length > chunkBytesRemaining) {
         length = chunkBytesRemaining;
      }

      read(input, buffer, length);
      return length;
   }

   private static void read(InputStream input, byte[] buffer, int length) throws IOException {
      int bytesRead;
      for(int offset = 0; length > 0; length -= bytesRead) {
         bytesRead = input.read(buffer, offset, length);
         if (bytesRead < 0) {
            throw new EOFException();
         }
      }

   }

   private static void skip(InputStream input, int length) throws IOException {
      while(length > 0) {
         length = (int)((long)length - input.skip((long)length));
      }

   }

   public boolean isLoaded() {
      return this.loaded;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public ByteBuffer getDirectBuffer() {
      return this.directBuffer;
   }

   public void free() {
      if (this.loaded) {
         try {
            Tools.freeDirectByteBuffer(this.directBuffer);
            this.directBuffer = null;
            --totalBuffers;
            totalBufferMemory -= (long)(this.height * this.width * 4);
         } catch (Exception var2) {
            var2.printStackTrace();
         }

         this.loaded = false;
      }

   }

   public void finalize() throws Throwable {
      super.finalize();
      if (this.loaded) {
         System.err.println("WARNING: PNG object was not freed manually!\r\nThis can cause some massive memory usage due to delayed freeing by garbage collector!");
      }

      this.free();
   }

   private void unfilter(byte[] line, byte[] lastLine) throws IOException {
      switch(line[0]) {
      case 0:
         break;
      case 1:
         this.sub(line);
         break;
      case 2:
         this.up(line, lastLine);
         break;
      case 3:
         this.average(line, lastLine);
         break;
      case 4:
         this.paeth(line, lastLine);
         break;
      default:
         throw new IOException("Bad filter type: " + line[0]);
      }

   }

   private void sub(byte[] line) {
      for(int i = 5; i < line.length; ++i) {
         line[i] += line[i - 4];
      }

   }

   private void up(byte[] line, byte[] lastLine) {
      for(int i = 1; i < line.length; ++i) {
         line[i] += lastLine[i];
      }

   }

   private void average(byte[] line, byte[] lastLine) {
      int i;
      for(i = 1; i <= 4; ++i) {
         line[i] += (byte)((lastLine[i] & 255) >>> 1);
      }

      while(i < line.length) {
         line[i] += (byte)((lastLine[i] & 255) + (line[i - 4] & 255) >>> 1);
         ++i;
      }

   }

   private void paeth(byte[] line, byte[] lastLine) {
      int i;
      for(i = 1; i <= 4; ++i) {
         line[i] += lastLine[i];
      }

      while(i < line.length) {
         int a = line[i - 4] & 255;
         int b = lastLine[i] & 255;
         int c = lastLine[i - 4] & 255;
         int p = a + b - c;
         int pa = Math.abs(p - a);
         int pb = Math.abs(p - b);
         int pc = Math.abs(p - c);
         if (pa <= pb && pa <= pc) {
            c = a;
         } else if (pb <= pc) {
            c = b;
         }

         line[i] += (byte)c;
         ++i;
      }

   }
}
