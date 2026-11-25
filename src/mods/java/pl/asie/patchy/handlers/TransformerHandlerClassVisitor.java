package pl.asie.patchy.handlers;

import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import pl.asie.patchy.Patchy;
import pl.asie.patchy.TransformerFunction;
import pl.asie.patchy.TransformerHandler;

public class TransformerHandlerClassVisitor extends TransformerHandler<ClassVisitor> {
   public TransformerHandlerClassVisitor(Patchy owner) {
      super(owner);
   }

   protected Class<ClassVisitor> getType() {
      return ClassVisitor.class;
   }

   protected byte[] process(byte[] data, List<TransformerFunction<ClassVisitor>> transformerFunctions) {
      ClassWriter writer = new ClassWriter(0);
      ClassVisitor visitor = writer;

      for(int i = transformerFunctions.size() - 1; i >= 0; --i) {
         visitor = (ClassVisitor)((TransformerFunction)transformerFunctions.get(i)).apply(visitor);
      }

      ClassReader reader = new ClassReader(data);
      reader.accept(visitor, 0);
      return writer.toByteArray();
   }
}
