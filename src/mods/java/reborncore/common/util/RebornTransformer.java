package reborncore.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class RebornTransformer implements IClassTransformer {
   List<RebornTransformer.ClassTransformer> classTransformers = new ArrayList();

   public RebornTransformer() {
   }

   public RebornTransformer.ClassTransformer transformClass(String className) {
      RebornTransformer.ClassTransformer classTransformer = new RebornTransformer.ClassTransformer(className);
      this.classTransformers.add(classTransformer);
      return classTransformer;
   }

   public List<RebornTransformer.ClassTransformer> getTransformers(String className) {
      return (List)this.classTransformers.stream().filter((classTransformer) -> {
         return classTransformer.name.equals(className);
      }).collect(Collectors.toList());
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      List<RebornTransformer.ClassTransformer> classTransformers = this.getTransformers(name);
      if (!classTransformers.isEmpty()) {
         ClassNode classNode = readClassFromBytes(basicClass);
         classTransformers.forEach((classTransformer) -> {
            classTransformer.handle(classNode);
         });
         return writeClassToBytes(classNode);
      } else {
         return basicClass;
      }
   }

   public static ClassNode readClassFromBytes(byte[] bytes) {
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(bytes);
      classReader.accept(classNode, 0);
      return classNode;
   }

   public static byte[] writeClassToBytes(ClassNode classNode) {
      ClassWriter writer = new ClassWriter(3);
      classNode.accept(writer);
      return writer.toByteArray();
   }

   public static class ClassTransformer {
      String name;
      List<RebornTransformer.MethodTransformer> methodTransformers = new ArrayList();

      public ClassTransformer(String name) {
         this.name = name;
      }

      public RebornTransformer.MethodTransformer findMethod(String name, String desc) {
         RebornTransformer.MethodTransformer methodTransformer = new RebornTransformer.MethodTransformer(name, desc);
         this.methodTransformers.add(methodTransformer);
         return methodTransformer;
      }

      private List<RebornTransformer.MethodTransformer> getMethodTransformers(MethodNode methodNode) {
         return (List)this.methodTransformers.stream().filter((methodTransformer) -> {
            return methodTransformer.name.equals(methodNode.name) && methodTransformer.desc.equals(methodNode.desc);
         }).collect(Collectors.toList());
      }

      private void handle(ClassNode classNode) {
         classNode.methods.forEach((methodNode) -> {
            this.getMethodTransformers(methodNode).forEach((methodTransformer) -> {
               methodTransformer.handle(methodNode);
            });
         });
      }
   }

   public static class MethodTransformer {
      String name;
      String desc;
      RebornTransformer.ClassTransformer classTransformer;
      List<Consumer<MethodNode>> methodTransformers = new ArrayList();

      public MethodTransformer(String name, String desc) {
         this.name = name;
         this.desc = desc;
      }

      public MethodTransformer(RebornTransformer.ClassTransformer classTransformer) {
         this.classTransformer = classTransformer;
      }

      public RebornTransformer.ClassTransformer getClassTransformer() {
         return this.classTransformer;
      }

      public RebornTransformer.MethodTransformer transform(Consumer<MethodNode> methodNodeConsumer) {
         this.methodTransformers.add(methodNodeConsumer);
         return this;
      }

      private void handle(MethodNode methodNode) {
         this.methodTransformers.forEach((methodNodeConsumer) -> {
            methodNodeConsumer.accept(methodNode);
         });
      }
   }
}
