package org.spongepowered.asm.service.modlauncher;

import com.google.common.collect.ImmutableList;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.ITransformationService;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collection;
import org.spongepowered.asm.launch.IClassProcessor;
import org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncher;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IClassTracker;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.IMixinInternal;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.service.MixinServiceAbstract;
import org.spongepowered.asm.util.IConsumer;

public class MixinServiceModLauncher extends MixinServiceAbstract {
   private static final String MODLAUNCHER_4_SPECIFICATION_VERSION = "4.0";
   private static final String MODLAUNCHER_9_SPECIFICATION_VERSION = "8.0";
   private static final String CONTAINER_PACKAGE = "org.spongepowered.asm.launch.platform.container.";
   private static final String MODLAUNCHER_4_ROOT_CONTAINER_CLASS = "org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncher";
   private static final String MODLAUNCHER_9_ROOT_CONTAINER_CLASS = "org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncherEx";
   private IClassProvider classProvider;
   private IClassBytecodeProvider bytecodeProvider;
   private MixinTransformationHandler transformationHandler;
   private ModLauncherClassTracker classTracker;
   private ModLauncherAuditTrail auditTrail;
   private IConsumer<MixinEnvironment.Phase> phaseConsumer;
   private volatile boolean initialised;
   private ContainerHandleModLauncher rootContainer;
   private MixinEnvironment.CompatibilityLevel minCompatibilityLevel = MixinEnvironment.CompatibilityLevel.JAVA_8;

   public MixinServiceModLauncher() {
      Package pkg = ITransformationService.class.getPackage();
      if (pkg.isCompatibleWith("8.0")) {
         this.createRootContainer("org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncherEx");
         this.minCompatibilityLevel = MixinEnvironment.CompatibilityLevel.JAVA_16;
      } else {
         this.createRootContainer("org.spongepowered.asm.launch.platform.container.ContainerHandleModLauncher");
      }

   }

   public void onInit(IClassBytecodeProvider bytecodeProvider) {
      if (this.initialised) {
         throw new IllegalStateException("Already initialised");
      } else {
         this.initialised = true;
         this.bytecodeProvider = bytecodeProvider;
      }
   }

   private void createRootContainer(String rootContainerClassName) {
      try {
         Class<?> clRootContainer = this.getClassProvider().findClass(rootContainerClassName);
         Constructor<?> ctor = clRootContainer.getDeclaredConstructor(String.class);
         this.rootContainer = (ContainerHandleModLauncher)ctor.newInstance(this.getName());
      } catch (ReflectiveOperationException var4) {
         var4.printStackTrace();
      }

   }

   public void onStartup() {
      this.phaseConsumer.accept(MixinEnvironment.Phase.DEFAULT);
   }

   public void offer(IMixinInternal internal) {
      if (internal instanceof IMixinTransformerFactory) {
         this.getTransformationHandler().offer((IMixinTransformerFactory)internal);
      }

      super.offer(internal);
   }

   public void wire(MixinEnvironment.Phase phase, IConsumer<MixinEnvironment.Phase> phaseConsumer) {
      super.wire(phase, phaseConsumer);
      this.phaseConsumer = phaseConsumer;
   }

   public String getName() {
      return "ModLauncher";
   }

   public MixinEnvironment.CompatibilityLevel getMinCompatibilityLevel() {
      return this.minCompatibilityLevel;
   }

   protected ILogger createLogger(String name) {
      return new LoggerAdapterLog4j2(name);
   }

   public boolean isValid() {
      try {
         Launcher.INSTANCE.hashCode();
         Package pkg = ITransformationService.class.getPackage();
         return pkg.isCompatibleWith("4.0");
      } catch (Throwable var2) {
         return false;
      }
   }

   public IClassProvider getClassProvider() {
      if (this.classProvider == null) {
         this.classProvider = new ModLauncherClassProvider();
      }

      return this.classProvider;
   }

   public IClassBytecodeProvider getBytecodeProvider() {
      if (this.bytecodeProvider == null) {
         throw new IllegalStateException("Service initialisation incomplete");
      } else {
         return this.bytecodeProvider;
      }
   }

   public ITransformerProvider getTransformerProvider() {
      return null;
   }

   public IClassTracker getClassTracker() {
      if (this.classTracker == null) {
         this.classTracker = new ModLauncherClassTracker();
      }

      return this.classTracker;
   }

   public IMixinAuditTrail getAuditTrail() {
      if (this.auditTrail == null) {
         this.auditTrail = new ModLauncherAuditTrail();
      }

      return this.auditTrail;
   }

   private MixinTransformationHandler getTransformationHandler() {
      if (this.transformationHandler == null) {
         this.transformationHandler = new MixinTransformationHandler();
      }

      return this.transformationHandler;
   }

   public Collection<String> getPlatformAgents() {
      return ImmutableList.of("org.spongepowered.asm.launch.platform.MixinPlatformAgentMinecraftForge");
   }

   public ContainerHandleModLauncher getPrimaryContainer() {
      return this.rootContainer;
   }

   public InputStream getResourceAsStream(String name) {
      return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
   }

   public Collection<IClassProcessor> getProcessors() {
      return ImmutableList.of(this.getTransformationHandler(), (IClassProcessor)this.getClassTracker());
   }
}
