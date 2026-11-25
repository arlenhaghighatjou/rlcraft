package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;

public interface IObfuscatedComponent {
   static <T extends Component & IObfuscatedComponent> T writeMarkedObf(ByteBuf buf, T component) {
      (new CStringUTF8()).set(component.getClass().getName()).write(buf);
      ((IObfuscatedComponent)component).writeObf(buf);
      return component;
   }

   static Component readMarkedObf(ByteBuf buf) {
      try {
         return ((IObfuscatedComponent)((Component)((IObfuscatedComponent)((Component)Class.forName((new CStringUTF8()).read(buf).value).newInstance())))).readObf(buf);
      } catch (IllegalAccessException | ClassNotFoundException | InstantiationException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   Component writeObf(ByteBuf var1);

   Component readObf(ByteBuf var1);
}
