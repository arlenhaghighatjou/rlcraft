package svenhjol.meson.asm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import svenhjol.meson.Meson;
import svenhjol.meson.helper.ConfigHelper;

public abstract class MesonClassTransformer implements IClassTransformer {
   protected static final Map<String, MesonClassTransformer.Transformer> transformers = new HashMap();
   protected static ClassNameMap CLASS_MAPPINGS = null;

   public MesonClassTransformer() {
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      return transformers.containsKey(transformedName) ? (byte[])((MesonClassTransformer.Transformer)transformers.get(transformedName)).apply(basicClass) : basicClass;
   }

   @SafeVarargs
   public static byte[] transform(byte[] basicClass, Pair<MesonClassTransformer.MethodSignature, MesonClassTransformer.MethodAction>... methods) {
      ClassReader reader = new ClassReader(basicClass);
      ClassNode node = new ClassNode();
      reader.accept(node, 0);
      boolean didAnything = false;

      for(Pair<MesonClassTransformer.MethodSignature, MesonClassTransformer.MethodAction> pair : methods) {
         log("Applying Transformation to method (" + pair.getLeft() + ")");
         didAnything |= findMethodAndTransform(node, (MesonClassTransformer.MethodSignature)pair.getLeft(), (MesonClassTransformer.MethodAction)pair.getRight());
      }

      if (didAnything) {
         ClassWriter writer = new ClassWriter(3);
         node.accept(writer);
         return writer.toByteArray();
      } else {
         return basicClass;
      }
   }

   public static MesonClassTransformer.MethodAction combine(MesonClassTransformer.NodeFilter filter, MesonClassTransformer.NodeAction action) {
      return (mnode) -> {
         return applyOnNode(mnode, filter, action);
      };
   }

   public static boolean applyOnNode(MethodNode method, MesonClassTransformer.NodeFilter filter, MesonClassTransformer.NodeAction action) {
      Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
      boolean applied = false;

      while(iterator.hasNext()) {
         AbstractInsnNode anode = (AbstractInsnNode)iterator.next();
         if (filter.test(anode)) {
            log("Located target " + getNodeString(anode));
            applied = true;
            if (action.test(method, anode)) {
               break;
            }
         }
      }

      return applied;
   }

   public static boolean findMethodAndTransform(ClassNode node, MesonClassTransformer.MethodSignature sig, MesonClassTransformer.MethodAction pred) {
      String funcName = sig.funcName;
      if (MesonLoadingPlugin.obf) {
         funcName = sig.srgName;
      }

      for(MethodNode method : node.methods) {
         if ((method.name.equals(funcName) || method.name.equals(sig.obfName) || method.name.equals(sig.srgName)) && (method.desc.equals(sig.funcDesc) || method.desc.equals(sig.obfDesc))) {
            log("Located method, patching...");
            boolean finish = pred.test(method);
            log("Patch result: " + finish);
            return finish;
         }
      }

      log("Failed to locate the method!");
      return false;
   }

   public static boolean checkDesc(String desc, String expected) {
      return desc.equals(expected) || desc.equals(MesonClassTransformer.MethodSignature.obfuscate(expected));
   }

   public static String getNodeString(AbstractInsnNode node) {
      Printer printer = new Textifier();
      TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
      node.accept(visitor);
      StringWriter sw = new StringWriter();
      printer.print(new PrintWriter(sw));
      printer.getText().clear();
      return sw.toString().replaceAll("\n", "").trim();
   }

   public static void log(String string) {
      LogManager.getLogger("Meson ASM").info(string);
   }

   public static boolean checkTransformers(Configuration config, String... transformers) {
      boolean loaded = true;

      for(String transformer : transformers) {
         loaded = loaded && ConfigHelper.propBoolean(config, transformer, "Transformers", "", true);
      }

      config.setCategoryComment("Transformers", "This section contains core class patches.\nYou may disable these patches in case of compatibility problems.\nAny features that depend on a manually disabled patch will not work properly.");
      if (config.hasChanged()) {
         config.save();
      }

      return loaded;
   }

   public static boolean obf() {
      return MesonLoadingPlugin.obf;
   }

   public interface MethodAction extends Predicate<MethodNode> {
   }

   public static class MethodSignature {
      public String funcName;
      public String srgName;
      public String obfName;
      public String funcDesc;
      public String obfDesc;

      public MethodSignature(String funcName, String srgName, String obfName, String funcDesc) {
         this.funcName = funcName;
         this.srgName = srgName;
         this.obfName = obfName;
         this.funcDesc = funcDesc;
         this.obfDesc = obfuscate(funcDesc);
      }

      public String toString() {
         return "Names [" + this.funcName + ", " + this.srgName + ", " + this.obfName + "] Descriptor " + this.funcDesc + " / " + this.obfDesc;
      }

      public static String obfuscate(String desc) {
         for(String s : MesonClassTransformer.CLASS_MAPPINGS.keySet()) {
            int i = 0;

            while(desc.contains(s)) {
               desc = desc.replace(s, (CharSequence)MesonClassTransformer.CLASS_MAPPINGS.get(s));
               if (i++ > 10) {
                  Meson.fatal("Nope");
               }
            }
         }

         return desc;
      }
   }

   public interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> {
   }

   public interface NodeFilter extends Predicate<AbstractInsnNode> {
   }

   public interface Transformer extends Function<byte[], byte[]> {
   }
}
