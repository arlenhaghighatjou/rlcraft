package ivorius.ivtoolkit.asm;

import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class IvClassTransformerGeneral extends IvClassTransformer {
   protected IvClassTransformerGeneral(Logger logger) {
      super(logger);
   }

   public boolean transform(String className, ClassNode classNode, boolean obf) {
      boolean didChange = false;

      for(MethodNode m : classNode.methods) {
         if (this.transformMethod(className, m, obf)) {
            didChange = true;
         }
      }

      return didChange;
   }

   public abstract boolean transformMethod(String var1, MethodNode var2, boolean var3);
}
