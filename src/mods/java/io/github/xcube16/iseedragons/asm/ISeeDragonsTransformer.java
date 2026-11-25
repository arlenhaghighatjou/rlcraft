package io.github.xcube16.iseedragons.asm;

import io.github.xcube16.iseedragons.ISD;
import io.github.xcube16.iseedragons.asm.helper.ASMHelper;
import io.github.xcube16.iseedragons.asm.patch.HookGorgonHead;
import io.github.xcube16.iseedragons.asm.patch.HookStructureGenerator;
import io.github.xcube16.iseedragons.asm.patch.PatchDragonDismount;
import io.github.xcube16.iseedragons.asm.patch.PatchDragonItemLag;
import io.github.xcube16.iseedragons.asm.patch.PatchIceAndFireAxes;
import io.github.xcube16.iseedragons.asm.patch.PatchLogSpam;
import io.github.xcube16.iseedragons.asm.patch.PatchMmmMmm;
import io.github.xcube16.iseedragons.asm.patch.PatchMyrmexEggDupe;
import io.github.xcube16.iseedragons.asm.patch.PatchNukeAdvancements;
import io.github.xcube16.iseedragons.asm.patch.PatchSerpentSpawning;
import io.github.xcube16.iseedragons.asm.patch.PatchStatueDupe;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.ClassNode;

public class ISeeDragonsTransformer implements IClassTransformer {
   private final Map<String, List<ISeeDragonsTransformer.PatchMeta>> transformMap = new HashMap();
   private static final ASMConfig config = new ASMConfig("iseedragons");

   public ISeeDragonsTransformer() {
      ISD.logger.info("Adding Patchers");
      this.addPatcher(PatchDragonItemLag.class);
      this.addPatcher(PatchIceAndFireAxes.class);
      this.addPatcher(PatchLogSpam.class);
      this.addPatcher(PatchMyrmexEggDupe.class);
      this.addPatcher(PatchNukeAdvancements.class);
      this.addPatcher(PatchSerpentSpawning.class);
      this.addPatcher(PatchDragonDismount.class);
      this.addPatcher(HookStructureGenerator.class);
      this.addPatcher(HookGorgonHead.class);
      this.addPatcher(PatchMmmMmm.class);
      this.addPatcher(PatchStatueDupe.class);
   }

   public byte[] transform(String name, String transformedName, byte[] basicClass) {
      if (!this.transformMap.containsKey(transformedName)) {
         return basicClass;
      } else {
         ISD.logger.info("ATTEMPTING TO PATCH  " + transformedName + "!");
         PatchResult result = PatchResult.NO_MUTATION;
         ClassNode clazzNode = ASMHelper.readClassFromBytes(basicClass);

         for(ISeeDragonsTransformer.PatchMeta manager : (List)this.transformMap.get(transformedName)) {
            try {
               ISD.logger.info("Applying patch [" + manager.name + "] " + manager.desc);
               result = result.add((PatchResult)manager.patch.apply(this, clazzNode));
            } catch (Exception var9) {
               ISD.logger.error("Failed at patch " + manager.name);
               ISD.logger.error("Failed to write " + transformedName);
               var9.printStackTrace();
               return basicClass;
            }
         }

         if (result.isMutated()) {
            ISD.logger.info("Writing class " + transformedName + " with flags " + result);
            return ASMHelper.writeClassToBytes(clazzNode, result.getFlags());
         } else {
            ISD.logger.info("All patches for class " + transformedName + " were cancelled, skipping...");
            return basicClass;
         }
      }
   }

   public void addPatcher(Class<?> clazz) {
      Patcher patcher = (Patcher)clazz.getAnnotation(Patcher.class);
      if (patcher == null) {
         throw new IllegalArgumentException(clazz.getName() + " does not have an @Patcher annotation");
      } else if (!patcher.config().isEmpty() && !config.getBoolean("general.asm." + patcher.config(), patcher.defaultEnable())) {
         ISD.logger.info("ASM patcher: [" + patcher.name() + "] disabled");
      } else {
         for(Method m : clazz.getDeclaredMethods()) {
            Patch patch = (Patch)m.getAnnotation(Patch.class);
            if (patch != null) {
               if (!Modifier.isPublic(m.getModifiers()) || !Modifier.isStatic(m.getModifiers()) || !Arrays.equals(m.getParameterTypes(), new Class[]{ISeeDragonsTransformer.class, ClassNode.class}) || !m.getReturnType().equals(PatchResult.class)) {
                  throw new IllegalArgumentException(clazz.getName() + "#" + m.getName() + " is not declared correctly to be a @Patch");
               }

               this.addPatch(patch.target(), patcher.name().equals("") ? clazz.getSimpleName() : patcher.name(), patch.desc(), (tweaker, clazzNode) -> {
                  try {
                     return (void)((PatchResult)m.invoke((Object)null, tweaker, clazzNode));
                  } catch (ReflectiveOperationException var4) {
                     throw new RuntimeException("This shouldn't have happened (blame xcube)", var4);
                  }
               });
            }
         }

         ISD.logger.info("ASM patcher: [" + patcher.name() + "] enabled");
      }
   }

   public void addPatch(String target, String name, String desc, BiFunction<ISeeDragonsTransformer, ClassNode, PatchResult> patch) {
      ((List)this.transformMap.computeIfAbsent(target, (t) -> {
         return (void)(new ArrayList());
      })).add(new ISeeDragonsTransformer.PatchMeta(name, desc, patch));
   }

   public void addPatch(String target, BiFunction<ISeeDragonsTransformer, ClassNode, PatchResult> patch) {
      this.addPatch(target, "<anonymous>", "", patch);
   }

   private static final class PatchMeta {
      final String name;
      final String desc;
      final BiFunction<ISeeDragonsTransformer, ClassNode, PatchResult> patch;

      private PatchMeta(String name, String desc, BiFunction<ISeeDragonsTransformer, ClassNode, PatchResult> patch) {
         this.name = name;
         this.desc = desc;
         this.patch = patch;
      }
   }
}
