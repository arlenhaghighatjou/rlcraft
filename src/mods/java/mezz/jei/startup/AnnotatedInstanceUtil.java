package mezz.jei.startup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JEIPlugin;
import mezz.jei.util.Log;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;

public final class AnnotatedInstanceUtil {
   private AnnotatedInstanceUtil() {
   }

   public static List<IModPlugin> getModPlugins(ASMDataTable asmDataTable) {
      return getInstances(asmDataTable, JEIPlugin.class, IModPlugin.class);
   }

   private static <T> List<T> getInstances(ASMDataTable asmDataTable, Class annotationClass, Class<T> instanceClass) {
      String annotationClassName = annotationClass.getCanonicalName();
      Set<ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
      List<T> instances = new ArrayList();

      for(ASMData asmData : asmDatas) {
         try {
            Class<?> asmClass = Class.forName(asmData.getClassName());
            Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
            T instance = (T)asmInstanceClass.newInstance();
            instances.add(instance);
         } catch (InstantiationException | IllegalAccessException | LinkageError | ClassNotFoundException var11) {
            Log.get().error("Failed to load: {}", asmData.getClassName(), var11);
         }
      }

      return instances;
   }
}
