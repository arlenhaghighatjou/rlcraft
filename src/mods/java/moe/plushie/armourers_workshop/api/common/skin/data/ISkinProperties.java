package moe.plushie.armourers_workshop.api.common.skin.data;

public interface ISkinProperties {
   void removeProperty(String var1);

   void setProperty(String var1, Object var2);

   String getPropertyString(String var1, String var2);

   int getPropertyInt(String var1, int var2);

   double getPropertyDouble(String var1, double var2);

   Boolean getPropertyBoolean(String var1, Boolean var2);

   Object getProperty(String var1, Object var2);

   boolean haveProperty(String var1);
}
