package org.spongepowered.asm.mixin.transformer;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ext.IClassGenerator;
import org.spongepowered.asm.mixin.transformer.throwables.InvalidMixinException;
import org.spongepowered.asm.service.ISyntheticClassInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.IConsumer;
import org.spongepowered.asm.util.asm.ASM;

final class InnerClassGenerator implements IClassGenerator {
   private static final ILogger logger = MixinService.getService().getLogger("mixin");
   private final IConsumer<ISyntheticClassInfo> registry;
   private final Map<String, String> innerClassNames = new HashMap();
   private final Map<String, InnerClassGenerator.InnerClassInfo> innerClasses = new HashMap();
   private final MixinCoprocessorNestHost nestHostCoprocessor;

   public InnerClassGenerator(IConsumer<ISyntheticClassInfo> registry, MixinCoprocessorNestHost nestHostCoprocessor) {
      this.registry = registry;
      this.nestHostCoprocessor = nestHostCoprocessor;
   }

   public String getName() {
      return "inner";
   }

   void registerInnerClass(MixinInfo owner, ClassInfo targetClass, String innerClassName) {
      String coordinate = String.format("%s:%s:%s", owner, innerClassName, targetClass.getName());
      String uniqueName = (String)this.innerClassNames.get(coordinate);
      if (uniqueName == null) {
         uniqueName = getUniqueReference(innerClassName, targetClass);
         ClassInfo nestHost = targetClass.resolveNestHost();
         InnerClassGenerator.InnerClassInfo info = new InnerClassGenerator.InnerClassInfo(owner, targetClass, nestHost, innerClassName, uniqueName, owner);
         this.innerClassNames.put(coordinate, uniqueName);
         this.innerClasses.put(uniqueName, info);
         this.registry.accept(info);
         logger.debug("Inner class {} in {} on {} gets unique name {}", innerClassName, owner.getClassRef(), targetClass, uniqueName);
         this.nestHostCoprocessor.registerNestMember(nestHost.getClassName(), uniqueName);
      }
   }

   BiMap<String, String> getInnerClasses(MixinInfo owner, String targetName) {
      BiMap<String, String> innerClasses = HashBiMap.create();

      for(InnerClassGenerator.InnerClassInfo innerClass : this.innerClasses.values()) {
         if (innerClass.getMixin() == owner && targetName.equals(innerClass.getTargetName())) {
            innerClasses.put(innerClass.getOriginalName(), innerClass.getName());
         }
      }

      return innerClasses;
   }

   public boolean generate(String name, ClassNode classNode) {
      String ref = name.replace('.', '/');
      InnerClassGenerator.InnerClassInfo info = (InnerClassGenerator.InnerClassInfo)this.innerClasses.get(ref);
      return info == null ? false : this.generate(info, classNode);
   }

   private boolean generate(InnerClassGenerator.InnerClassInfo info, ClassNode classNode) {
      try {
         logger.debug("Generating mapped inner class {} (originally {})", info.getName(), info.getOriginalName());
         info.accept(new InnerClassGenerator.InnerClassAdapter(classNode, info));
         return true;
      } catch (InvalidMixinException var4) {
         throw var4;
      } catch (Exception var5) {
         logger.catching(var5);
         return false;
      }
   }

   private static String getUniqueReference(String originalName, ClassInfo targetClass) {
      String name = originalName.substring(originalName.lastIndexOf(36) + 1);
      if (name.matches("^[0-9]+$")) {
         name = "Anonymous";
      }

      return String.format("%s$%s$%s", targetClass, name, EaglercraftUUID.randomUUID().toString().replace("-", ""));
   }

   static class InnerClassAdapter extends ClassRemapper {
      private final InnerClassGenerator.InnerClassInfo info;

      InnerClassAdapter(ClassVisitor cv, InnerClassGenerator.InnerClassInfo info) {
         super(ASM.API_VERSION, cv, info);
         this.info = info;
      }

      public void visitNestHost(String nestHost) {
         this.cv.visitNestHost(this.info.getNestHostName());
      }

      public void visitSource(String source, String debug) {
         super.visitSource(source, debug);
         AnnotationVisitor av = this.cv.visitAnnotation("Lorg/spongepowered/asm/mixin/transformer/meta/MixinInner;", false);
         av.visit("mixin", this.info.getOwner().toString());
         av.visit("name", this.info.getOriginalName().substring(this.info.getOriginalName().lastIndexOf(47) + 1));
         av.visitEnd();
      }

      public void visitInnerClass(String name, String outerName, String innerName, int access) {
         if (name.startsWith(this.info.getOriginalName() + "$")) {
            throw new InvalidMixinException(this.info.getOwner(), "Found unsupported nested inner class " + name + " in " + this.info.getOriginalName());
         } else {
            super.visitInnerClass(name, outerName, innerName, access);
         }
      }
   }

   static class InnerClassInfo extends Remapper implements ISyntheticClassInfo {
      private final IMixinInfo mixin;
      private final ClassInfo targetClassInfo;
      private final String originalName;
      private final String name;
      private final MixinInfo owner;
      private final String ownerName;
      private final String nestHostName;
      private int loadCounter;

      InnerClassInfo(IMixinInfo mixin, ClassInfo targetClass, ClassInfo nestHost, String originalName, String name, MixinInfo owner) {
         this.mixin = mixin;
         this.targetClassInfo = targetClass;
         this.originalName = originalName;
         this.name = name;
         this.owner = owner;
         this.ownerName = owner.getClassRef();
         this.nestHostName = nestHost.getName();
      }

      public IMixinInfo getMixin() {
         return this.mixin;
      }

      public boolean isLoaded() {
         return this.loadCounter > 0;
      }

      public String getName() {
         return this.name;
      }

      public String getClassName() {
         return this.name.replace('/', '.');
      }

      String getOriginalName() {
         return this.originalName;
      }

      MixinInfo getOwner() {
         return this.owner;
      }

      String getOwnerName() {
         return this.ownerName;
      }

      String getTargetName() {
         return this.targetClassInfo.getName();
      }

      ClassInfo getTargetClass() {
         return this.targetClassInfo;
      }

      String getNestHostName() {
         return this.nestHostName;
      }

      void accept(ClassVisitor classVisitor) throws ClassNotFoundException, IOException {
         ClassNode classNode = MixinService.getService().getBytecodeProvider().getClassNode(this.originalName);
         classNode.accept(classVisitor);
         ++this.loadCounter;
      }

      public String mapMethodName(String owner, String name, String desc) {
         if (this.ownerName.equalsIgnoreCase(owner)) {
            ClassInfo.Method method = this.owner.getClassInfo().findMethod(name, desc, 10);
            if (method != null) {
               return method.getName();
            }
         }

         return super.mapMethodName(owner, name, desc);
      }

      public String map(String key) {
         if (this.originalName.equals(key)) {
            return this.name;
         } else {
            return this.ownerName.equals(key) ? this.targetClassInfo.getName() : key;
         }
      }

      public String toString() {
         return this.name;
      }
   }
}
