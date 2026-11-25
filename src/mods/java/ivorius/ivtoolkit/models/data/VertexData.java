package ivorius.ivtoolkit.models.data;

import java.nio.FloatBuffer;

public interface VertexData extends Disposable {
   int getNumVertices();

   int getNumMaxVertices();

   VertexAttributes getAttributes();

   void setVertices(float[] var1, int var2, int var3);

   void updateVertices(int var1, float[] var2, int var3, int var4);

   FloatBuffer getBuffer();
}
