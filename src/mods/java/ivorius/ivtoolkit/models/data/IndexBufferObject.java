package ivorius.ivtoolkit.models.data;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.GL15;

public class IndexBufferObject implements IndexData {
   ShortBuffer buffer;
   ByteBuffer byteBuffer;
   int bufferHandle;
   final boolean isDirect;
   boolean isDirty = true;
   boolean isBound = false;
   final int usage;

   public IndexBufferObject(boolean isStatic, int maxIndices) {
      this.byteBuffer = GLAllocation.createDirectByteBuffer(maxIndices * 2);
      this.isDirect = true;
      this.buffer = this.byteBuffer.asShortBuffer();
      this.buffer.flip();
      this.byteBuffer.flip();
      this.bufferHandle = this.createBufferObject();
      this.usage = isStatic ? '\u88e4' : '\u88e8';
   }

   public IndexBufferObject(int maxIndices) {
      this.byteBuffer = GLAllocation.createDirectByteBuffer(maxIndices * 2);
      this.isDirect = true;
      this.buffer = this.byteBuffer.asShortBuffer();
      this.buffer.flip();
      this.byteBuffer.flip();
      this.bufferHandle = this.createBufferObject();
      this.usage = 35044;
   }

   private int createBufferObject() {
      return GL15.glGenBuffers();
   }

   public int getNumIndices() {
      return this.buffer.limit();
   }

   public int getNumMaxIndices() {
      return this.buffer.capacity();
   }

   public void setIndices(short[] indices, int offset, int count) {
      this.isDirty = true;
      this.buffer.clear();
      this.buffer.put(indices, offset, count);
      this.buffer.flip();
      this.byteBuffer.position(0);
      this.byteBuffer.limit(count << 1);
      if (this.isBound) {
         GL15.glBufferData(34963, this.byteBuffer, this.usage);
         this.isDirty = false;
      }

   }

   public ShortBuffer getBuffer() {
      this.isDirty = true;
      return this.buffer;
   }

   public void bind() {
      if (this.bufferHandle == 0) {
         throw new RuntimeException("No buffer allocated!");
      } else {
         GL15.glBindBuffer(34963, this.bufferHandle);
         if (this.isDirty) {
            this.byteBuffer.limit(this.buffer.limit() * 2);
            GL15.glBufferData(34963, this.byteBuffer, this.usage);
            this.isDirty = false;
         }

         this.isBound = true;
      }
   }

   public void unbind() {
      GL15.glBindBuffer(34963, 0);
      this.isBound = false;
   }

   public void invalidate() {
      this.bufferHandle = this.createBufferObject();
      this.isDirty = true;
   }

   public void dispose() {
      GL15.glBindBuffer(34963, 0);
      if (this.bufferHandle > 0) {
         GL15.glDeleteBuffers(this.bufferHandle);
      }

      this.bufferHandle = 0;
   }
}
