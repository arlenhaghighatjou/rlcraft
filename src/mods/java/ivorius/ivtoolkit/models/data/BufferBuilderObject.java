package ivorius.ivtoolkit.models.data;

import ivorius.ivtoolkit.models.utils.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.GL15;

public class BufferBuilderObject implements VertexData {
   final VertexAttributes attributes;
   final FloatBuffer buffer;
   final ByteBuffer byteBuffer;
   int bufferHandle;
   final boolean isStatic;
   final int usage;
   boolean isDirty;
   boolean isBound;

   public BufferBuilderObject(boolean isStatic, int numVertices, VertexAttribute... attributes) {
      this(isStatic, numVertices, new VertexAttributes(attributes));
   }

   public BufferBuilderObject(boolean isStatic, int numVertices, VertexAttributes attributes) {
      this.isDirty = false;
      this.isBound = false;
      this.isStatic = isStatic;
      this.attributes = attributes;
      this.byteBuffer = GLAllocation.createDirectByteBuffer(this.attributes.vertexSize * numVertices);
      this.buffer = this.byteBuffer.asFloatBuffer();
      this.buffer.flip();
      this.byteBuffer.flip();
      this.bufferHandle = this.createBufferObject();
      this.usage = isStatic ? '裤' : '裨';
   }

   private int createBufferObject() {
      return GL15.glGenBuffers();
   }

   public VertexAttributes getAttributes() {
      return this.attributes;
   }

   public int getNumVertices() {
      return this.buffer.limit() * 4 / this.attributes.vertexSize;
   }

   public int getNumMaxVertices() {
      return this.byteBuffer.capacity() / this.attributes.vertexSize;
   }

   public FloatBuffer getBuffer() {
      this.isDirty = true;
      return this.buffer;
   }

   private void bufferChanged() {
      if (this.isBound) {
         GL15.glBufferData(34962, this.byteBuffer, this.usage);
         this.isDirty = false;
      }

   }

   public void setVertices(float[] vertices, int offset, int count) {
      this.isDirty = true;
      BufferUtils.copy(vertices, this.byteBuffer, count, offset);
      this.buffer.position(0);
      this.buffer.limit(count);
      this.bufferChanged();
   }

   public void updateVertices(int targetOffset, float[] vertices, int sourceOffset, int count) {
      this.isDirty = true;
      int pos = this.byteBuffer.position();
      this.byteBuffer.position(targetOffset * 4);
      BufferUtils.copy(vertices, sourceOffset, count, this.byteBuffer);
      this.byteBuffer.position(pos);
      this.buffer.position(0);
      this.bufferChanged();
   }

   public void invalidate() {
      this.bufferHandle = this.createBufferObject();
      this.isDirty = true;
   }

   public void dispose() {
      GL15.glBindBuffer(34962, 0);
      if (this.bufferHandle > 0) {
         GL15.glDeleteBuffers(this.bufferHandle);
      }

      this.bufferHandle = 0;
   }
}
