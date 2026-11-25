package org.spongepowered.tools.agent;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.IHotSwap;
import org.spongepowered.asm.mixin.transformer.throwables.MixinReloadException;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.transformers.MixinClassReader;
import org.spongepowered.asm.util.asm.ASM;

public class MixinAgent implements IHotSwap {
   public static final byte[] ERROR_BYTECODE = new byte[]{1};
   static final MixinAgentClassLoader classLoader = new MixinAgentClassLoader();
   static final ILogger logger = MixinService.getService().getLogger("mixin.agent");
   static Instrumentation instrumentation = null;
   private static List<MixinAgent> agents = new ArrayList();
   final IMixinTransformer classTransformer;

   public MixinAgent(IMixinTransformer classTransformer) {
      this.classTransformer = classTransformer;
      agents.add(this);
      if (instrumentation != null) {
         this.initTransformer();
      }

   }

   private void initTransformer() {
      instrumentation.addTransformer(new MixinAgent.Transformer(), true);
   }

   public void registerMixinClass(String name) {
      classLoader.addMixinClass(name);
   }

   public void registerTargetClass(String name, ClassNode classNode) {
      classLoader.addTargetClass(name, classNode);
   }

   public static void init(Instrumentation instrumentation) {
      instrumentation = instrumentation;
      if (!instrumentation.isRedefineClassesSupported()) {
         logger.error("The instrumentation doesn't support re-definition of classes");
      }

      for(MixinAgent agent : agents) {
         agent.initTransformer();
      }

   }

   public static void premain(String arg, Instrumentation instrumentation) {
      System.setProperty("mixin.hotSwap", "true");
      init(instrumentation);
   }

   public static void agentmain(String arg, Instrumentation instrumentation) {
      init(instrumentation);
   }

   class Transformer implements ClassFileTransformer {
      Transformer() {
      }

      public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain domain, byte[] classfileBuffer) throws IllegalClassFormatException {
         if (classBeingRedefined == null) {
            return null;
         } else {
            byte[] mixinBytecode = MixinAgent.classLoader.getFakeMixinBytecode(classBeingRedefined);
            if (mixinBytecode != null) {
               ClassNode classNode = new ClassNode(ASM.API_VERSION);
               ClassReader cr = new MixinClassReader(classfileBuffer, className);
               cr.accept(classNode, 8);
               List<String> targets = this.reloadMixin(className, classNode);
               return targets != null && this.reApplyMixins(targets) ? mixinBytecode : MixinAgent.ERROR_BYTECODE;
            } else {
               try {
                  MixinAgent.logger.info("Redefining class {}", className);
                  return MixinAgent.this.classTransformer.transformClassBytes((String)null, className, classfileBuffer);
               } catch (Throwable var10) {
                  MixinAgent.logger.error("Error while re-transforming class {}", className, var10);
                  return MixinAgent.ERROR_BYTECODE;
               }
            }
         }
      }

      private List<String> reloadMixin(String className, ClassNode classNode) {
         MixinAgent.logger.info("Redefining mixin {}", className);

         try {
            return MixinAgent.this.classTransformer.reload(className.replace('/', '.'), classNode);
         } catch (MixinReloadException var4) {
            MixinAgent.logger.error("Mixin {} cannot be reloaded, needs a restart to be applied: {} ", var4.getMixinInfo(), var4.getMessage());
         } catch (Throwable var5) {
            MixinAgent.logger.error("Error while finding targets for mixin {}", className, var5);
         }

         return null;
      }

      private boolean reApplyMixins(List<String> targets) {
         IMixinService service = MixinService.getService();

         for(String target : targets) {
            String targetName = target.replace('/', '.');
            MixinAgent.logger.debug("Re-transforming target class {}", target);

            try {
               Class<?> targetClass = service.getClassProvider().findClass(targetName);
               byte[] targetBytecode = MixinAgent.classLoader.getOriginalTargetBytecode(targetName);
               if (targetBytecode == null) {
                  MixinAgent.logger.error("Target class {} bytecode is not registered", targetName);
                  return false;
               }

               targetBytecode = MixinAgent.this.classTransformer.transformClassBytes((String)null, targetName, targetBytecode);
               MixinAgent.instrumentation.redefineClasses(new ClassDefinition[]{new ClassDefinition(targetClass, targetBytecode)});
            } catch (Throwable var8) {
               MixinAgent.logger.error("Error while re-transforming target class {}", target, var8);
               return false;
            }
         }

         return true;
      }
   }
}
