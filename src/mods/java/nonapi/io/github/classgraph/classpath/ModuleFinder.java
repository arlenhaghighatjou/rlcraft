package nonapi.io.github.classgraph.classpath;

import io.github.classgraph.ModuleRef;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.ReflectionUtils;

public class ModuleFinder {
   private List<ModuleRef> systemModuleRefs;
   private List<ModuleRef> nonSystemModuleRefs;

   public List<ModuleRef> getSystemModuleRefs() {
      return this.systemModuleRefs;
   }

   public List<ModuleRef> getNonSystemModuleRefs() {
      return this.nonSystemModuleRefs;
   }

   private static void findLayerOrder(Object layer, Set<Object> layerVisited, Set<Object> parentLayers, Deque<Object> layerOrderOut) {
      if (layerVisited.add(layer)) {
         List<Object> parents = (List)ReflectionUtils.invokeMethod(layer, "parents", true);
         if (parents != null) {
            parentLayers.addAll(parents);

            for(Object parent : parents) {
               findLayerOrder(parent, layerVisited, parentLayers, layerOrderOut);
            }
         }

         layerOrderOut.push(layer);
      }

   }

   private static List<ModuleRef> findModuleRefs(LinkedHashSet<Object> layers, ScanSpec scanSpec, LogNode log) {
      if (layers.isEmpty()) {
         return Collections.emptyList();
      } else {
         Deque<Object> layerOrder = new ArrayDeque();
         Set<Object> parentLayers = new HashSet();

         for(Object layer : layers) {
            findLayerOrder(layer, new HashSet(), parentLayers, layerOrder);
         }

         if (scanSpec.addedModuleLayers != null) {
            for(Object layer : scanSpec.addedModuleLayers) {
               findLayerOrder(layer, new HashSet(), parentLayers, layerOrder);
            }
         }

         List<Object> layerOrderFinal;
         if (scanSpec.ignoreParentModuleLayers) {
            layerOrderFinal = new ArrayList();

            for(Object layer : layerOrder) {
               if (!parentLayers.contains(layer)) {
                  layerOrderFinal.add(layer);
               }
            }
         } else {
            layerOrderFinal = new ArrayList(layerOrder);
         }

         Set<Object> addedModules = new HashSet();
         LinkedHashSet<ModuleRef> moduleRefOrder = new LinkedHashSet();

         for(Object layer : layerOrderFinal) {
            Object configuration = ReflectionUtils.invokeMethod(layer, "configuration", true);
            if (configuration != null) {
               Set<Object> modules = (Set)ReflectionUtils.invokeMethod(configuration, "modules", true);
               if (modules != null) {
                  List<ModuleRef> modulesInLayer = new ArrayList();

                  for(Object module : modules) {
                     Object moduleReference = ReflectionUtils.invokeMethod(module, "reference", true);
                     if (moduleReference != null && addedModules.add(moduleReference)) {
                        try {
                           modulesInLayer.add(new ModuleRef(moduleReference, layer));
                        } catch (IllegalArgumentException var17) {
                           if (log != null) {
                              log.log("Exception while creating ModuleRef for module " + moduleReference, var17);
                           }
                        }
                     }
                  }

                  CollectionUtils.sortIfNotEmpty(modulesInLayer);
                  moduleRefOrder.addAll(modulesInLayer);
               }
            }
         }

         return new ArrayList(moduleRefOrder);
      }
   }

   private static List<ModuleRef> findModuleRefsFromCallstack(Class<?>[] callStack, ScanSpec scanSpec, LogNode log) {
      LinkedHashSet<Object> layers = new LinkedHashSet();
      if (callStack != null) {
         for(Class<?> stackFrameClass : callStack) {
            Object module = ReflectionUtils.invokeMethod(stackFrameClass, "getModule", false);
            if (module != null) {
               Object layer = ReflectionUtils.invokeMethod(module, "getLayer", true);
               if (layer != null) {
                  layers.add(layer);
               }
            }
         }
      }

      Class<?> moduleLayerClass = null;

      try {
         moduleLayerClass = Class.forName("java.lang.ModuleLayer");
      } catch (LinkageError | ClassNotFoundException var10) {
         ;
      }

      if (moduleLayerClass != null) {
         Object bootLayer = ReflectionUtils.invokeStaticMethod(moduleLayerClass, "boot", false);
         if (bootLayer != null) {
            layers.add(bootLayer);
         }
      }

      return findModuleRefs(layers, scanSpec, log);
   }

   public ModuleFinder(Class<?>[] callStack, ScanSpec scanSpec, LogNode log) {
      boolean disableModules = scanSpec.overrideClassLoaders != null || scanSpec.overrideClasspath != null;
      if (!disableModules) {
         List<ModuleRef> allModuleRefsList = null;
         if (scanSpec.overrideModuleLayers == null) {
            if (callStack != null && callStack.length > 0) {
               allModuleRefsList = findModuleRefsFromCallstack(callStack, scanSpec, log);
            }
         } else {
            if (log != null) {
               LogNode subLog = log.log("Overriding module layers");

               for(Object moduleLayer : scanSpec.overrideModuleLayers) {
                  subLog.log(moduleLayer.toString());
               }
            }

            allModuleRefsList = findModuleRefs(new LinkedHashSet(scanSpec.overrideModuleLayers), scanSpec, log);
         }

         if (allModuleRefsList != null) {
            this.systemModuleRefs = new ArrayList();
            this.nonSystemModuleRefs = new ArrayList();

            for(ModuleRef moduleRef : allModuleRefsList) {
               if (moduleRef.isSystemModule()) {
                  this.systemModuleRefs.add(moduleRef);
               } else {
                  this.nonSystemModuleRefs.add(moduleRef);
               }
            }
         }

         if (log != null) {
            LogNode sysSubLog = log.log("Found system modules:");
            if (this.systemModuleRefs != null && !this.systemModuleRefs.isEmpty()) {
               for(ModuleRef moduleRef : this.systemModuleRefs) {
                  sysSubLog.log(moduleRef.toString());
               }
            } else {
               sysSubLog.log("[None]");
            }

            LogNode nonSysSubLog = log.log("Found non-system modules:");
            if (this.nonSystemModuleRefs != null && !this.nonSystemModuleRefs.isEmpty()) {
               for(ModuleRef moduleRef : this.nonSystemModuleRefs) {
                  nonSysSubLog.log(moduleRef.toString());
               }
            } else {
               nonSysSubLog.log("[None]");
            }
         }
      } else if (log != null) {
         log.log("Module scanning is disabled, because classloaders or classpath was overridden");
      }

   }
}
