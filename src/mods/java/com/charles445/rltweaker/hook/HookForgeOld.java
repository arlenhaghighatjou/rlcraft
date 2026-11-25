package com.charles445.rltweaker.hook;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class HookForgeOld {
   private static final Map<String, BiConsumer<IMessage, MessageContext>> serverExecutors = new ConcurrentHashMap();
   private static final Map<Class, Field[]> serverFieldCaches = new ConcurrentHashMap();

   public HookForgeOld() {
   }

   public static void receiveMessage(IMessage message, MessageContext ctx) {
      if (ctx.side == Side.SERVER) {
         BiConsumer<IMessage, MessageContext> consumer = (BiConsumer)serverExecutors.get(message.getClass().getName());
         if (consumer != null) {
            consumer.accept(message, ctx);
         }
      }

   }

   private static void addServer(String name, BiConsumer<IMessage, MessageContext> consumer) {
      serverExecutors.put(name, consumer);
   }

   @Nullable
   private static Field[] getFieldsForMessage(IMessage message, String... names) {
      Field[] fields = (Field[])serverFieldCaches.get(message.getClass());
      if (fields == null) {
         try {
            fields = ReflectUtil.findFields(message.getClass(), names);
            serverFieldCaches.put(message.getClass(), fields);
         } catch (Exception var4) {
            ;
         }
      }

      return fields;
   }

   @Nullable
   private static <T> T getParam(@Nullable Object message, Field f) throws Exception {
      return (T)f.get(message);
   }

   private static void setParam(@Nullable Object message, Field f, Object value) throws Exception {
      f.set(message, value);
   }
}
