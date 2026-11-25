package meldexun.asmutil.transformer.node;

import java.util.function.Consumer;
import java.util.function.Predicate;
import org.objectweb.asm.tree.MethodNode;

public class MethodNodeTransformer extends AbstractTransformer {
   public MethodNodeTransformer(String obfClassName, String obfName, String obfDesc, String className, String name, String desc, Consumer<MethodNode> transformer) {
      super(obfClassName, className, (classNode) -> {
         for(MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(obfName) && methodNode.desc.equals(obfDesc) || methodNode.name.equals(name) && methodNode.desc.equals(desc)) {
               transformer.accept(methodNode);
               return (void)true;
            }
         }

         return (void)false;
      });
   }
}
