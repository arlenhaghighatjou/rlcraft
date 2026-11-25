package ivorius.ivtoolkit.models.data;

import ivorius.ivtoolkit.models.utils.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GLAllocation;

public class VertexArray implements VertexData {
   final VertexAttributes attributes;
   final FloatBuffer buffer;
   final ByteBuffer byteBuffer;
   boolean isBound;

   public VertexArray(int numVertices, VertexAttribute... attributes) {
      this(numVertices, new VertexAttributes(attributes));
   }

   public VertexArray(int numVertices, VertexAttributes attributes) {
      this.isBound = false;
      this.attributes = attributes;
      this.byteBuffer = GLAllocation.createDirectByteBuffer(this.attributes.vertexSize * numVertices);
      this.buffer = this.byteBuffer.asFloatBuffer();
      this.buffer.flip();
      this.byteBuffer.flip();
   }

   public void dispose() {
   }

   public FloatBuffer getBuffer() {
      return this.buffer;
   }

   public int getNumVertices() {
      return this.buffer.limit() * 4 / this.attributes.vertexSize;
   }

   public int getNumMaxVertices() {
      return this.byteBuffer.capacity() / this.attributes.vertexSize;
   }

   public void setVertices(float[] vertices, int offset, int count) {
      BufferUtils.copy(vertices, this.byteBuffer, count, offset);
      this.buffer.position(0);
      this.buffer.limit(count);
   }

   public void updateVertices(int targetOffset, float[] vertices, int sourceOffset, int count) {
      int pos = this.byteBuffer.position();
      this.byteBuffer.position(targetOffset * 4);
      BufferUtils.copy(vertices, sourceOffset, count, this.byteBuffer);
      this.byteBuffer.position(pos);
   }

   public VertexAttributes getAttributes() {
      return this.attributes;
   }
}
