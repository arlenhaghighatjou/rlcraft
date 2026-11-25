package meldexun.renderlib.util;

import java.nio.ByteBuffer;
import meldexun.matrixutil.MemoryUtil;
import org.lwjgl.opengl.GL15;

public class GLBuffer implements MemoryAccess {
   private final int buffer;
   private final long size;
   private final boolean persistent;
   private boolean mapped;
   private ByteBuffer byteBuffer;
   private long address;

   public GLBuffer(long size, int flags, int usage) {
      this(size, flags, usage, false, 0);
   }

   public GLBuffer(long size, int flags, int usage, boolean persistent, int persistentAccess) {
      if (persistent && GLUtil.CAPS.OpenGL44) {
         this.buffer = GLUtil.createBuffer(size, flags | 64, usage);
         this.size = size;
         this.persistent = true;
         this.mapped = true;
         this.byteBuffer = GLUtil.map(this.buffer, size, persistentAccess | 64, 0, (ByteBuffer)null);
         this.address = MemoryUtil.getAddress(this.byteBuffer);
      } else {
         this.buffer = GLUtil.createBuffer(size, flags, usage);
         this.size = size;
         this.persistent = false;
      }

   }

   public int getBuffer() {
      return this.buffer;
   }

   public long getSize() {
      return this.size;
   }

   public void map(int rangeAccess, int access) {
      this.map(this.size, rangeAccess, access);
   }

   public void map(long length, int rangeAccess, int access) {
      if (!this.persistent && !this.mapped) {
         this.byteBuffer = GLUtil.map(this.buffer, length, rangeAccess, access, this.byteBuffer);
         this.address = MemoryUtil.getAddress(this.byteBuffer);
         this.mapped = true;
      }

   }

   public boolean isMapped() {
      return this.mapped;
   }

   public void unmap() {
      if (!this.persistent) {
         this.forceUnmap();
      }

   }

   private void forceUnmap() {
      if (this.mapped) {
         GLUtil.unmap(this.buffer);
         this.mapped = false;
         this.byteBuffer = null;
         this.address = 0L;
      }

   }

   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }

   public long getAddress() {
      return this.address;
   }

   public void dispose() {
      this.forceUnmap();
      GL15.glDeleteBuffers(this.buffer);
   }
}
