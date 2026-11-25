package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CArrayList extends Component {
   public ArrayList<Component> value = new ArrayList();

   public CArrayList() {
   }

   public CArrayList set(ArrayList<Component> value) {
      this.value.clear();
      this.value.addAll(value);
      return this;
   }

   public CArrayList write(ByteBuf buf) {
      (new CInt()).set(this.value.size()).write(buf);

      for(Component component : this.value) {
         (new CStringUTF8()).set(component.getClass().getName()).write(buf);
         component.write(buf);
      }

      return this;
   }

   public CArrayList read(ByteBuf buf) {
      this.value.clear();

      for(int i = (new CInt()).read(buf).value; i > 0; --i) {
         String classname = (new CStringUTF8()).read(buf).value;

         try {
            this.value.add(((Component)Class.forName(classname).newInstance()).read(buf));
         } catch (IllegalAccessException | ClassNotFoundException | InstantiationException var5) {
            var5.printStackTrace();
         }
      }

      return this;
   }

   public CArrayList save(OutputStream stream) {
      (new CInt()).set(this.value.size()).save(stream);

      for(Component component : this.value) {
         (new CStringUTF8()).set(component.getClass().getName()).save(stream);
         component.save(stream);
      }

      return this;
   }

   public CArrayList load(InputStream stream) {
      this.value.clear();

      for(int i = (new CInt()).load(stream).value; i > 0; --i) {
         String classname = (new CStringUTF8()).load(stream).value;

         try {
            this.value.add(((Component)Class.forName(classname).newInstance()).load(stream));
         } catch (IllegalAccessException | ClassNotFoundException | InstantiationException var5) {
            var5.printStackTrace();
         }
      }

      return this;
   }
}
