package team.creative.enhancedvisuals.api;

public enum VisualCategory {
   overlay {
      public boolean isAffectedByWater() {
         return false;
      }
   },
   particle {
      public boolean isAffectedByWater() {
         return true;
      }
   },
   shader {
      public boolean isAffectedByWater() {
         return false;
      }
   };

   private VisualCategory() {
   }

   public abstract boolean isAffectedByWater();
}
