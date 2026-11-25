package ivorius.reccomplex.world.gen.feature.villages;

import com.google.common.collect.Sets;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class TemporaryVillagerRegistry {
   protected static Field handlerField;
   protected static TemporaryVillagerRegistry INSTANCE = new TemporaryVillagerRegistry();
   protected Set<IVillageCreationHandler> registeredHandlers = new HashSet();

   public TemporaryVillagerRegistry() {
   }

   public static TemporaryVillagerRegistry instance() {
      return INSTANCE;
   }

   protected static Map<Class<?>, IVillageCreationHandler> getMap() {
      if (handlerField == null) {
         handlerField = ReflectionHelper.findField(VillagerRegistry.class, new String[]{"villageCreationHandlers"});
      }

      try {
         return (Map)handlerField.get(VillagerRegistry.instance());
      } catch (IllegalAccessException var1) {
         var1.printStackTrace();
         return null;
      }
   }

   public void register(IVillageCreationHandler handler) {
      this.addToRegistry(handler);
      this.registeredHandlers.add(handler);
   }

   private void addToRegistry(IVillageCreationHandler handler) {
      VillagerRegistry.instance().registerVillageCreationHandler(handler);
   }

   public void unregister(IVillageCreationHandler handler) {
      this.removeFromRegistry(handler);
      this.registeredHandlers.remove(handler);
   }

   private void removeFromRegistry(IVillageCreationHandler handler) {
      Map<Class<?>, IVillageCreationHandler> map = getMap();
      if (map != null) {
         map.remove(handler.getComponentClass());
      }

   }

   public void setHandlers(Set<IVillageCreationHandler> handlers) {
      Sets.difference(this.registeredHandlers, handlers).forEach(this::removeFromRegistry);
      Sets.difference(handlers, this.registeredHandlers).forEach(this::addToRegistry);
      this.registeredHandlers.clear();
      this.registeredHandlers.addAll(handlers);
   }
}
