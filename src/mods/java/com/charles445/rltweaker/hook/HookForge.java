package com.charles445.rltweaker.hook;

import com.charles445.rltweaker.util.ReflectUtil;
import com.charles445.rltweaker.util.TriConsumer;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class HookForge {
   private static final Map<String, HookForge.MessageExecutor> serverExecutors = new ConcurrentHashMap();
   private static final Map<Class, Field[]> serverFieldCaches = new ConcurrentHashMap();

   public HookForge() {
   }

   public static IMessage onMessage(final IMessageHandler handler, final IMessage message, final MessageContext ctx) {
      if (ctx.side == Side.SERVER) {
         final HookForge.MessageExecutor executor = (HookForge.MessageExecutor)serverExecutors.get(message.getClass().getName());
         if (executor != null && ctx.getServerHandler().player != null) {
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(new Runnable() {
               public void run() {
                  executor.consumer.accept(handler, message, ctx);
               }
            });
            if (executor.override) {
               return executor.reply();
            }
         }
      }

      return handler.onMessage(message, ctx);
   }

   public static void addServer(String name, TriConsumer<IMessageHandler, IMessage, MessageContext> consumer) {
      addServer(name, consumer, false);
   }

   public static void addServer(String name, TriConsumer<IMessageHandler, IMessage, MessageContext> consumer, boolean override) {
      addServer(name, new HookForge.MessageExecutor(override, consumer));
   }

   public static void addServer(String name, HookForge.MessageExecutor executor) {
      serverExecutors.put(name, executor);
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

   public static class MessageExecutor {
      boolean override;
      TriConsumer<IMessageHandler, IMessage, MessageContext> consumer;

      MessageExecutor(boolean override, TriConsumer<IMessageHandler, IMessage, MessageContext> consumer) {
         this.override = override;
         this.consumer = consumer;
      }

      IMessage reply() {
         return null;
      }
   }
}
