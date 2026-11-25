package net.ilexiconn.llibrary.server.network;

import com.google.common.collect.SetMultimap;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.ilexiconn.llibrary.LLibrary;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public enum NetworkHandler {
   INSTANCE;

   private Map<SimpleNetworkWrapper, Integer> idMap = new HashMap();

   private NetworkHandler() {
   }

   public <T extends AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerMessage(SimpleNetworkWrapper networkWrapper, Class<T> clazz) {
      try {
         AbstractMessage<T> message = (AbstractMessage)clazz.getDeclaredConstructor().newInstance();
         if (message.registerOnSide(Side.CLIENT)) {
            this.registerMessage(networkWrapper, clazz, Side.CLIENT);
         }

         if (message.registerOnSide(Side.SERVER)) {
            this.registerMessage(networkWrapper, clazz, Side.SERVER);
         }
      } catch (ReflectiveOperationException var4) {
         var4.printStackTrace();
      }

   }

   /** @deprecated */
   @Deprecated
   public <T extends AbstractMessage<T> & IMessageHandler<T, IMessage>> void registerMessage(SimpleNetworkWrapper networkWrapper, Class<T> clazz, Side side) {
      int id = 0;
      if (this.idMap.containsKey(networkWrapper)) {
         id = ((Integer)this.idMap.get(networkWrapper)).intValue();
      }

      networkWrapper.registerMessage(clazz, clazz, id, side);
      this.idMap.put(networkWrapper, Integer.valueOf(id + 1));
   }

   public void injectNetworkWrapper(ModContainer mod, ASMDataTable data) {
      SetMultimap<String, ASMData> annotations = data.getAnnotationsFor(mod);
      if (annotations != null) {
         Set<ASMData> targetList = annotations.get(NetworkWrapper.class.getName());
         ClassLoader classLoader = Loader.instance().getModClassLoader();

         for(ASMData target : targetList) {
            try {
               Class<?> targetClass = Class.forName(target.getClassName(), true, classLoader);
               Field field = targetClass.getDeclaredField(target.getObjectName());
               field.setAccessible(true);
               NetworkWrapper annotation = (NetworkWrapper)field.getAnnotation(NetworkWrapper.class);
               SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(mod.getModId());
               field.set((Object)null, networkWrapper);

               for(Class messageClass : annotation.value()) {
                  this.registerMessage(networkWrapper, messageClass);
               }
            } catch (Exception var16) {
               LLibrary.LOGGER.fatal("Failed to inject network wrapper for mod container {}", mod, var16);
            }
         }
      }

   }
}
