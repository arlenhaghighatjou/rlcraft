package ivorius.ivtoolkit.rendering;

public interface Iv2DScreenEffect {
   boolean shouldApply(float var1);

   void apply(int var1, int var2, float var3, IvOpenGLTexturePingPong var4);

   void destruct();
}
