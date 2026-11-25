package ivorius.ivtoolkit.models;

import ivorius.ivtoolkit.models.data.BufferBuilderObject;
import ivorius.ivtoolkit.models.data.Disposable;
import ivorius.ivtoolkit.models.data.IndexBufferObject;
import ivorius.ivtoolkit.models.data.IndexData;
import ivorius.ivtoolkit.models.data.VertexAttributes;
import ivorius.ivtoolkit.models.data.VertexData;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Mesh implements Disposable {
   private final VertexData vertices;
   private final IndexData indices;

   public Mesh(boolean isStatic, int maxVertices, int maxIndices, VertexAttributes attributes) {
      this.vertices = new BufferBuilderObject(isStatic, maxVertices, attributes);
      this.indices = new IndexBufferObject(isStatic, maxIndices);
   }

   public void dispose() {
      this.vertices.dispose();
      this.indices.dispose();
   }

   public FloatBuffer getVerticesBuffer() {
      return this.vertices.getBuffer();
   }

   public ShortBuffer getIndicesBuffer() {
      return this.indices.getBuffer();
   }

   public VertexData getVertices() {
      return this.vertices;
   }

   public IndexData getIndices() {
      return this.indices;
   }
}
