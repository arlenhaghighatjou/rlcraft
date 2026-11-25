package pl.asie.foamfix.coremod.injections;

public class CachingHashCodeInject {
   private int foamfix_hashCode;
   private boolean foamfix_hashCode_calced;

   public CachingHashCodeInject() {
   }

   public int hashCode() {
      if (!this.foamfix_hashCode_calced) {
         this.foamfix_hashCode = this.hashCode_foamfix_old();
         this.foamfix_hashCode_calced = true;
      }

      return this.foamfix_hashCode;
   }

   public int hashCode_foamfix_old() {
      return 0;
   }
}
