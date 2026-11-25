package pl.asie.patchy.handlers;

import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import pl.asie.patchy.Patchy;
import pl.asie.patchy.TransformerFunction;
import pl.asie.patchy.TransformerHandler;

public class TransformerHandlerClassNode extends TransformerHandler<ClassNode> {
   public static final int RECOMPUTE_FRAMES = 4194304;

   public TransformerHandlerClassNode(Patchy owner) {
      super(owner);
   }

   protected Class<ClassNode> getType() {
      return ClassNode.class;
   }

   protected byte[] process(byte[] data, List<TransformerFunction<ClassNode>> transformerFunctions) {
      ClassReader reader = new ClassReader(data);
      ClassNode node = new ClassNode();
      reader.accept(node, 0);

      for(TransformerFunction<ClassNode> func : transformerFunctions) {
         node = (ClassNode)func.apply(node);
      }

      ClassWriter writer = new ClassWriter((node.access & 4194304) != 0 ? 2 : 0);
      node.access &= -4194305;
      node.accept(writer);
      return writer.toByteArray();
   }
}
