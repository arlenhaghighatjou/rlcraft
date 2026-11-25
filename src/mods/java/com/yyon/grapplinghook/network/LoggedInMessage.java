package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleConfig;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class LoggedInMessage implements IMessage {
   GrappleConfig.Config conf = null;

   public LoggedInMessage() {
   }

   public LoggedInMessage(GrappleConfig.Config serverconf) {
      this.conf = serverconf;
   }

   public void fromBytes(ByteBuf buf) {
      Class<GrappleConfig.Config> confclass = GrappleConfig.Config.class;
      Field[] fields = confclass.getDeclaredFields();
      Arrays.sort(fields, new Comparator<Field>() {
         public int compare(Field o1, Field o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });
      this.conf = new GrappleConfig.Config();

      for(Field field : fields) {
         Type fieldtype = field.getGenericType();

         try {
            if (fieldtype.getTypeName().equals("int")) {
               field.setInt(this.conf, buf.readInt());
            } else if (fieldtype.getTypeName().equals("double")) {
               field.setDouble(this.conf, buf.readDouble());
            } else if (fieldtype.getTypeName().equals("boolean")) {
               field.setBoolean(this.conf, buf.readBoolean());
            } else if (fieldtype.getTypeName().equals("java.lang.String")) {
               int len = buf.readInt();
               CharSequence charseq = buf.readCharSequence(len, Charset.defaultCharset());
               field.set(this.conf, charseq.toString());
            } else {
               System.out.println("Unknown Type");
               System.out.println(fieldtype.getTypeName());
            }
         } catch (IllegalAccessException var11) {
            System.out.println(var11);
         }
      }

   }

   public void toBytes(ByteBuf buf) {
      Class<GrappleConfig.Config> confclass = GrappleConfig.Config.class;
      Field[] fields = confclass.getDeclaredFields();
      Arrays.sort(fields, new Comparator<Field>() {
         public int compare(Field o1, Field o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });

      for(Field field : fields) {
         Type fieldtype = field.getGenericType();

         try {
            if (fieldtype.getTypeName().equals("int")) {
               buf.writeInt(field.getInt(this.conf));
            } else if (fieldtype.getTypeName().equals("double")) {
               buf.writeDouble(field.getDouble(this.conf));
            } else if (fieldtype.getTypeName().equals("boolean")) {
               buf.writeBoolean(field.getBoolean(this.conf));
            } else if (fieldtype.getTypeName().equals("java.lang.String")) {
               String str = (String)field.get(this.conf);
               buf.writeInt(str.length());
               buf.writeCharSequence(str.subSequence(0, str.length()), Charset.defaultCharset());
            } else {
               System.out.println("Unknown Type");
               System.out.println(fieldtype.getTypeName());
            }
         } catch (IllegalAccessException var10) {
            System.out.println(var10);
         }
      }

   }

   public static class Handler implements IMessageHandler<LoggedInMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(LoggedInMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new LoggedInMessage.Handler.runner(message, ctx));
         return null;
      }

      public class runner implements Runnable {
         LoggedInMessage message;
         MessageContext ctx;

         public runner(LoggedInMessage message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
         }

         public void run() {
            GrappleConfig.setserveroptions(this.message.conf);
         }
      }
   }
}
