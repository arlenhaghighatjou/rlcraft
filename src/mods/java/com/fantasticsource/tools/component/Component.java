package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Component {
   public Component() {
   }

   public static <T extends Component> T writeMarked(ByteBuf buf, T component) {
      (new CStringUTF8()).set(component.getClass().getName()).write(buf);
      component.write(buf);
      return component;
   }

   public static <T extends Component> T writeMarkedOrNull(ByteBuf buf, T component) {
      if (component == null) {
         buf.writeBoolean(false);
      } else {
         buf.writeBoolean(true);
         writeMarked(buf, component);
      }

      return component;
   }

   public static Component readMarked(ByteBuf buf) {
      try {
         return ((Component)Class.forName((new CStringUTF8()).read(buf).value).newInstance()).read(buf);
      } catch (IllegalAccessException | ClassNotFoundException | InstantiationException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static Component readMarkedOrNull(ByteBuf buf) {
      return buf.readBoolean() ? readMarked(buf) : null;
   }

   public static <T extends Component> T saveMarked(OutputStream stream, T component) {
      (new CStringUTF8()).set(component.getClass().getName()).save(stream);
      component.save(stream);
      return component;
   }

   public static <T extends Component> T saveMarkedOrNull(OutputStream stream, T component) {
      if (component == null) {
         (new CBoolean()).set(false).save(stream);
      } else {
         (new CBoolean()).set(true).save(stream);
         saveMarked(stream, component);
      }

      return component;
   }

   public static Component loadMarked(InputStream stream) {
      try {
         return ((Component)Class.forName((new CStringUTF8()).load(stream).value).newInstance()).load(stream);
      } catch (IllegalAccessException | ClassNotFoundException | InstantiationException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static Component loadMarkedOrNull(InputStream stream) {
      return (new CBoolean()).load(stream).value ? loadMarked(stream) : null;
   }

   public static <T extends Component> T writeTextMarked(BufferedWriter writer, T component) {
      try {
         writer.write(component.getClass().getName() + "\r\n");
         component.writeText(writer);
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      return component;
   }

   public static Component readTextMarked(BufferedReader reader) {
      try {
         return ((Component)Class.forName(reader.readLine()).newInstance()).readText(reader);
      } catch (IllegalAccessException | ClassNotFoundException | IOException | InstantiationException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public abstract Component write(ByteBuf var1);

   public abstract Component read(ByteBuf var1);

   public abstract Component save(OutputStream var1);

   public abstract Component load(InputStream var1);

   public Component writeText(BufferedWriter writer) {
      return this;
   }

   public Component readText(BufferedReader reader) {
      return this;
   }

   public Component copy() {
      ByteArrayOutputStream os = new ByteArrayOutputStream(10240);
      saveMarked(os, this);
      return loadMarked(new ByteArrayInputStream(os.toByteArray()));
   }

   public void onClientSync() {
   }
}
