package meldexun.asmutil.transformer.node;

import java.util.function.Consumer;
import java.util.function.Predicate;
import org.objectweb.asm.tree.FieldNode;

public class FieldNodeTransformer extends AbstractTransformer {
   public FieldNodeTransformer(String obfClassName, String obfName, String obfDesc, String className, String name, String desc, Consumer<FieldNode> transformer) {
      super(obfClassName, className, (classNode) -> {
         for(FieldNode fieldNode : classNode.fields) {
            if (fieldNode.name.equals(obfName) && fieldNode.desc.equals(obfDesc) || fieldNode.name.equals(name) && fieldNode.desc.equals(desc)) {
               transformer.accept(fieldNode);
               return (void)true;
            }
         }

         return (void)false;
      });
   }
}
