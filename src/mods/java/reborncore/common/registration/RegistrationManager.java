package reborncore.common.registration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation.EnumHolder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.Distribution;
import reborncore.RebornCore;

public class RegistrationManager {
   static List<IRegistryFactory> factoryList = new ArrayList();
   static List<Class<?>> registryClasses = new ArrayList();

   public RegistrationManager() {
   }

   public static void init(FMLPreInitializationEvent event) {
      long start = System.currentTimeMillis();
      ASMDataTable asmDataTable = event.getAsmData();
      loadFactorys(asmDataTable);
      Set<ASMData> asmDataSet = asmDataTable.getAll(RebornRegistry.class.getName());
      List<ASMData> asmDataList = new ArrayList(asmDataSet);
      asmDataList.sort(Comparator.comparingInt(RegistrationManager::getPriority));

      for(ASMData data : asmDataList) {
         try {
            if (isModPresent(data, asmDataTable) && isValidOnSide(data, asmDataTable)) {
               Class<?> clazz = Class.forName(data.getClassName());
               if (isEarlyReg(data)) {
                  handleClass(clazz, (ModContainer)null, factoryList);
               } else {
                  registryClasses.add(clazz);
               }
            }
         } catch (ClassNotFoundException var9) {
            throw new RuntimeException("Failed to load class", var9);
         }
      }

      registryClasses.sort(Comparator.comparing(Class::getCanonicalName));
      RebornCore.logHelper.info("Pre loaded registries in " + (System.currentTimeMillis() - start) + "ms");
   }

   private static int getPriority(ASMData asmData) {
      return asmData.getAnnotationInfo().containsKey("priority") ? -((Integer)asmData.getAnnotationInfo().get("priority")).intValue() : 0;
   }

   private static boolean isEarlyReg(ASMData asmData) {
      return asmData.getAnnotationInfo().containsKey("earlyReg") ? ((Boolean)asmData.getAnnotationInfo().get("earlyReg")).booleanValue() : false;
   }

   private static boolean isModPresent(ASMData asmData, ASMDataTable dataTable) {
      if (!asmData.getAnnotationInfo().containsKey("modOnly")) {
         return true;
      } else {
         String modOnly = (String)asmData.getAnnotationInfo().get("modOnly");
         if (modOnly != null && !modOnly.isEmpty()) {
            for(String modid : modOnly.split(",")) {
               if (modid.startsWith("@")) {
                  if (modid.equals("@client") && FMLCommonHandler.instance().getSide() != Side.CLIENT) {
                     return false;
                  }
               } else if (modid.startsWith("!")) {
                  if (Loader.isModLoaded(modid.replaceAll("!", ""))) {
                     return false;
                  }
               } else if (!Loader.isModLoaded(modid)) {
                  return false;
               }
            }

            return true;
         } else {
            return true;
         }
      }
   }

   private static boolean isValidOnSide(ASMData asmData, ASMDataTable dataTable) {
      String side = FMLLaunchHandler.side().toString().toUpperCase();
      if (asmData.getAnnotationInfo().containsKey("side")) {
         EnumHolder sideEnum = (EnumHolder)asmData.getAnnotationInfo().get("side");
         String classSide = sideEnum.getValue();
         if (!classSide.equals(Distribution.UNIVERSAL.toString()) && !side.equals(classSide)) {
            return false;
         }
      }

      for(ASMData sideData : dataTable.getAll(SideOnly.class.getName())) {
         if (sideData.getClassName().equals(asmData.getClassName()) && sideData.getAnnotationInfo().containsKey("value")) {
            EnumHolder sideEnum = (EnumHolder)sideData.getAnnotationInfo().get("value");
            String classSide = sideEnum.getValue();
            if (!side.equals(classSide)) {
               return false;
            }
         }
      }

      for(ASMData sideData : dataTable.getAll(Mod.class.getName())) {
         if (sideData.getClassName().equals(asmData.getClassName())) {
            if (FMLLaunchHandler.side() == Side.CLIENT) {
               if (sideData.getAnnotationInfo().containsKey("serverSideOnly")) {
                  boolean value = ((Boolean)sideData.getAnnotationInfo().get("serverSideOnly")).booleanValue();
                  if (value) {
                     return false;
                  }
               }
            } else if (sideData.getAnnotationInfo().containsKey("clientSideOnly")) {
               boolean value = ((Boolean)sideData.getAnnotationInfo().get("clientSideOnly")).booleanValue();
               if (value) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public static void load(FMLStateEvent event) {
      long start = System.currentTimeMillis();
      ModContainer activeMod = Loader.instance().activeModContainer();
      List<IRegistryFactory> factoryList = getFactorysForSate(event.getClass());
      if (!factoryList.isEmpty()) {
         for(Class<?> clazz : registryClasses) {
            handleClass(clazz, activeMod, factoryList);
         }

         factoryList.forEach(IRegistryFactory::factoryComplete);
         setActiveModContainer(activeMod);
      }

      RebornCore.logHelper.info("Loaded registrys for " + event.getClass().getName() + " in " + (System.currentTimeMillis() - start) + "ms");
   }

   private static void handleClass(Class<?> clazz, ModContainer activeMod, List<IRegistryFactory> factories) {
      RebornRegistry annotation = (RebornRegistry)getAnnoation(clazz.getAnnotations(), RebornRegistry.class);
      if (annotation != null && activeMod != null && !activeMod.getModId().equals(annotation.modID())) {
         setActiveMod(annotation.modID());
      }

      for(IRegistryFactory regFactory : factories) {
         for(Field field : clazz.getDeclaredFields()) {
            if (regFactory.getTargets().contains(RegistryTarget.FIELD) && field.isAnnotationPresent(regFactory.getAnnotation())) {
               regFactory.handleField(field);
            }
         }

         for(Method method : clazz.getDeclaredMethods()) {
            if (regFactory.getTargets().contains(RegistryTarget.MEHTOD) && method.isAnnotationPresent(regFactory.getAnnotation())) {
               regFactory.handleMethod(method);
            }
         }

         if (regFactory.getTargets().contains(RegistryTarget.CLASS) && clazz.isAnnotationPresent(regFactory.getAnnotation())) {
            regFactory.handleClass(clazz);
         }
      }

   }

   private static List<IRegistryFactory> getFactorysForSate(Class<? extends FMLStateEvent> event) {
      return (List)factoryList.stream().filter((iRegistryFactory) -> {
         return iRegistryFactory.getProcessSate() == event;
      }).collect(Collectors.toList());
   }

   public static Annotation getAnnoationFromArray(Annotation[] annotations, IRegistryFactory factory) {
      for(Annotation annotation : annotations) {
         if (annotation.annotationType() == factory.getAnnotation()) {
            return annotation;
         }
      }

      return null;
   }

   public static Annotation getAnnoation(Annotation[] annotations, Class<?> annoation) {
      for(Annotation annotation : annotations) {
         if (annotation.annotationType() == annoation) {
            return annotation;
         }
      }

      return null;
   }

   private static void loadFactorys(ASMDataTable dataTable) {
      for(ASMData data : dataTable.getAll(IRegistryFactory.RegistryFactory.class.getName())) {
         try {
            Class<?> clazz = Class.forName(data.getClassName());
            IRegistryFactory.RegistryFactory registryFactory = (IRegistryFactory.RegistryFactory)getAnnoation(clazz.getAnnotations(), IRegistryFactory.RegistryFactory.class);
            if (registryFactory.side().canExcetue() && Loader.isModLoaded(registryFactory.modID())) {
               Object object = clazz.newInstance();
               if (object instanceof IRegistryFactory) {
                  IRegistryFactory factory = (IRegistryFactory)object;
                  factoryList.add(factory);
               }
            }
         } catch (IllegalAccessException | InstantiationException | ClassNotFoundException var8) {
            var8.printStackTrace();
         }
      }

   }

   private static void setActiveMod(String modID) {
      for(ModContainer modContainer : Loader.instance().getActiveModList()) {
         if (modContainer.getModId().equals(modID)) {
            setActiveModContainer(modContainer);
            break;
         }
      }

   }

   private static void setActiveModContainer(ModContainer container) {
      Loader.instance().setActiveModContainer(container);
   }
}
