package ivorius.reccomplex.utils.accessor;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RCAccessorWorldServer {
   private static Class<?> worldServerServerBlockEventListClass;
   private static Field blockEventQueueField;
   private static Constructor<?> blockEventListConstructor;

   public RCAccessorWorldServer() {
   }

   public static void ensureBlockEventArray(WorldServer worldServer) {
      if (worldServerServerBlockEventListClass == null) {
         try {
            worldServerServerBlockEventListClass = Class.forName("net.minecraft.world.WorldServer$ServerBlockEventList");
         } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
         }
      }

      if (blockEventQueueField == null) {
         blockEventQueueField = ReflectionHelper.findField(WorldServer.class, "blockEventQueue", "blockEventQueue");
      }

      if (blockEventListConstructor == null) {
         blockEventListConstructor = worldServerServerBlockEventListClass.getDeclaredConstructors()[0];
         blockEventListConstructor.setAccessible(true);
      }

      try {
         if (blockEventQueueField.get(worldServer) == null) {
            Object instance = Array.newInstance(worldServerServerBlockEventListClass, 2);
            Array.set(instance, 0, blockEventListConstructor.newInstance());
            Array.set(instance, 1, blockEventListConstructor.newInstance());
            blockEventQueueField.set(worldServer, instance);
         }
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException var2) {
         var2.printStackTrace();
      }

   }
}
