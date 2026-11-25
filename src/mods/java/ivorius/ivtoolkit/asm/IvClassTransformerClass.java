package ivorius.ivtoolkit.asm;

import java.util.ArrayList;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class IvClassTransformerClass extends IvClassTransformer {
   public ArrayList<String[]> registeredMethods = new ArrayList();

   public IvClassTransformerClass(Logger logger) {
      super(logger);
   }

   public void registerExpectedMethod(String methodID, String obfName, String signature) {
      this.registeredMethods.add(new String[]{obfName, signature, methodID});
   }

   public boolean transform(String className, ClassNode classNode, boolean obf) {
      boolean[] sigs = new boolean[this.registeredMethods.size()];

      for(MethodNode m : classNode.methods) {
         for(int methodIndex = 0; methodIndex < this.registeredMethods.size(); ++methodIndex) {
            String[] methodInfo = (String[])this.registeredMethods.get(methodIndex);
            String srgName = getSrgName(className, m);
            String srgSignature = getSRGDescriptor(m.desc);
            if (srgName.equals(methodInfo[0]) && srgSignature.equals(methodInfo[1]) && this.transformMethod(className, methodInfo[2], m, obf)) {
               sigs[methodIndex] = true;
            }
         }
      }

      boolean didChange = false;

      for(int methodIndex = 0; methodIndex < this.registeredMethods.size(); ++methodIndex) {
         if (!sigs[methodIndex]) {
            String[] methodInfo = (String[])this.registeredMethods.get(methodIndex);
            this.logger.error("Could not transform expected method in class \"" + className + "\" (Obf: " + obf + "): " + methodInfo[0] + " - " + methodInfo[1] + " - " + methodInfo[2]);
         } else {
            didChange = true;
         }
      }

      return didChange;
   }

   public abstract boolean transformMethod(String var1, String var2, MethodNode var3, boolean var4);
}
