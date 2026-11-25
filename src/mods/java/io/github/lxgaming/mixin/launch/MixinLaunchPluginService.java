package io.github.lxgaming.mixin.launch;

import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService.ITransformerLoader;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService.Phase;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public class MixinLaunchPluginService implements ILaunchPluginService {
   private static final List<String> SKIP_PACKAGES = Arrays.asList("org.objectweb.asm.", "org.spongepowered.asm.launch.", "org.spongepowered.asm.lib.", "org.spongepowered.asm.mixin.", "org.spongepowered.asm.service.", "org.spongepowered.asm.util.");

   public MixinLaunchPluginService() {
   }

   public String name() {
      return "mixinbootstrap";
   }

   public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
      throw new UnsupportedOperationException("Outdated ModLauncher");
   }

   public boolean processClass(Phase phase, ClassNode classNode, Type classType) {
      throw new UnsupportedOperationException("Outdated ModLauncher");
   }

   public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty, String reason) {
      return EnumSet.noneOf(Phase.class);
   }

   public boolean processClass(Phase phase, ClassNode classNode, Type classType, String reason) {
      return false;
   }

   public void initializeLaunch(ITransformerLoader transformerLoader, Path[] specialPaths) {
      TransformingClassLoader classLoader = (TransformingClassLoader)Thread.currentThread().getContextClassLoader();
      classLoader.addTargetPackageFilter((name) -> {
         Stream var10000 = SKIP_PACKAGES.stream();
         name.getClass();
         return var10000.noneMatch(name::startsWith);
      });
   }
}
