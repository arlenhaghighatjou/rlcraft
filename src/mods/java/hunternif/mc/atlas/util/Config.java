package hunternif.mc.atlas.util;

interface Config<T extends SaveData> {
   void load(T var1);

   void save(T var1);
}
