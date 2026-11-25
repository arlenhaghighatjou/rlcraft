package meldexun.entityculling.util;

public interface ICullable {
   ICullable.CullInfo getCullInfo();

   ICullable.CullInfo getShadowCullInfo();

   boolean isCulled();

   void setCulled(boolean var1);

   boolean isShadowCulled();

   void setShadowCulled(boolean var1);

   boolean canBeOcclusionCulled();

   void setCanBeOcclusionCulled(boolean var1);

   public static class CullInfo {
      private int lastTimeUpdated = Integer.MIN_VALUE;
      private int id;

      public CullInfo() {
      }

      public boolean wasLastTimeUpdated(int frame) {
         return this.lastTimeUpdated == frame - 1;
      }

      public int getLastTimeUpdated() {
         return this.lastTimeUpdated;
      }

      public void setLastTimeUpdated(int lastTimeUpdated) {
         this.lastTimeUpdated = lastTimeUpdated;
      }

      public int getId(int frame) {
         return this.lastTimeUpdated == frame - 1 ? this.id : -1;
      }

      public void setId(int id) {
         this.id = id;
      }
   }
}
