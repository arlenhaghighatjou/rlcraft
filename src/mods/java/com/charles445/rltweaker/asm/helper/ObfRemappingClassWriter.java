package com.charles445.rltweaker.asm.helper;

public class ObfRemappingClassWriter extends ComputeClassWriter {
   public ObfRemappingClassWriter(int flags) {
      super(flags);
   }

   protected String getCommonSuperClass(String type1, String type2) {
      return ObfHelper.toObfClassName(super.getCommonSuperClass(ObfHelper.toDeobfClassName(type1.replace('/', '.')), type2.replace('/', '.'))).replace('.', '/');
   }
}
