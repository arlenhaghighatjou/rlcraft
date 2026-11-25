package meldexun.entityculling.asm;

import meldexun.asmutil.transformer.clazz.AbstractClassTransformer;
import net.minecraft.launchwrapper.IClassTransformer;

public class EntityCullingClassTransformer extends AbstractClassTransformer implements IClassTransformer {
   public static final boolean OPTIFINE_DETECTED;

   public EntityCullingClassTransformer() {
   }

   protected void registerTransformers() {
   }

   static {
      boolean flag = false;

      try {
         Class.forName("optifine.OptiFineClassTransformer", false, EntityCullingPlugin.class.getClassLoader());
         flag = true;
      } catch (ClassNotFoundException var2) {
         ;
      }

      OPTIFINE_DETECTED = flag;
   }
}
