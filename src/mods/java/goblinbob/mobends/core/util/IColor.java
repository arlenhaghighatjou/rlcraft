package goblinbob.mobends.core.util;

public interface IColor extends IColorRead {
   void setR(float var1);

   void setG(float var1);

   void setB(float var1);

   void setA(float var1);

   void set(float var1, float var2, float var3, float var4);

   void add(float var1, float var2, float var3, float var4);
}
