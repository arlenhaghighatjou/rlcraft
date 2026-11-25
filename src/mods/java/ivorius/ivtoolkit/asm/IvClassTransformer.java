package ivorius.ivtoolkit.asm;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class IvClassTransformer {
   public Logger logger;

   protected IvClassTransformer(Logger logger) {
      this.logger = logger;
   }

   public void printSubMethodError(String className, String methodID, String submethodID) {
      this.logger.error("Could not patch " + className + "." + methodID + " (" + submethodID + ")!");
   }

   public static String getMethodDescriptor(Object returnValue, Object... params) {
      Type[] types = new Type[params.length];

      for(int i = 0; i < params.length; ++i) {
         types[i] = getType(params[i]);
      }

      return Type.getMethodDescriptor(getType(returnValue), types);
   }

   private static Type getType(Object obj) {
      if (obj instanceof Type) {
         return (Type)obj;
      } else if (obj instanceof Class) {
         return Type.getType((Class)obj);
      } else if (obj instanceof String) {
         return Type.getObjectType((String)obj);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static String getSRGDescriptor(String signature) {
      return FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(signature);
   }

   public static String getSrgName(MethodInsnNode node) {
      return IvDevRemapper.getSRGName(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(node.owner, node.name, node.desc));
   }

   public static String getSrgName(String className, MethodNode node) {
      return IvDevRemapper.getSRGName(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, node.name, node.desc));
   }

   public static String getSrgName(FieldInsnNode node) {
      return IvDevRemapper.getSRGName(FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(node.owner, node.name, node.desc));
   }

   public static String getSrgClassName(String className) {
      return FMLDeobfuscatingRemapper.INSTANCE.map(className);
   }

   public byte[] transform(String actualClassName, String srgClassName, byte[] data, boolean obf) {
      ClassNode classNode = null;
      boolean didChange = false;

      try {
         classNode = new ClassNode();
         ClassReader classReader = new ClassReader(data);
         classReader.accept(classNode, 0);
      } catch (Exception var10) {
         this.logger.error("Error patching class PRE " + actualClassName + " (" + srgClassName + ")!", var10);
         return data;
      }

      try {
         didChange = this.transform(actualClassName.replaceAll("\\.", "/"), classNode, obf);
      } catch (Exception var9) {
         this.logger.error("Error patching class MAIN " + actualClassName + " (" + srgClassName + ")!", var9);
         return data;
      }

      if (didChange) {
         try {
            ClassWriter writer = new ClassWriter(1);
            classNode.accept(writer);
            return writer.toByteArray();
         } catch (Exception var8) {
            this.logger.error("Error patching class POST " + actualClassName + " (" + srgClassName + ")!", var8);
            return data;
         }
      } else {
         return data;
      }
   }

   public abstract boolean transform(String var1, ClassNode var2, boolean var3);
}
