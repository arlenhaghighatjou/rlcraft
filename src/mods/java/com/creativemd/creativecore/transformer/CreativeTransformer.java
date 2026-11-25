package com.creativemd.creativecore.transformer;

import java.util.ArrayList;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public abstract class CreativeTransformer implements IClassTransformer {
   public final String modid;
   private final ArrayList<Transformer> transformers = new ArrayList();

   public CreativeTransformer(String modid) {
      this.modid = modid;
      this.initTransformers();
   }

   protected abstract void initTransformers();

   protected void addTransformer(Transformer transformer) {
      this.transformers.add(transformer);
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      return name.contains("com.creativemd") ? basicClass : this.transform(transformedName, basicClass);
   }

   public byte[] transform(String name, byte[] basicClass) {
      int i = 0;

      while(i < this.transformers.size()) {
         if (((Transformer)this.transformers.get(i)).is(name)) {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(classNode, 0);
            ((Transformer)this.transformers.get(i)).transform(classNode);
            ClassWriter writer = new ClassWriter(3);
            classNode.accept(writer);
            basicClass = writer.toByteArray();
            System.out.println("[" + this.modid + "] Patched " + ((Transformer)this.transformers.get(i)).className + " ...");
            ((Transformer)this.transformers.get(i)).done();
            ++i;
         } else {
            ++i;
         }
      }

      return basicClass;
   }
}
