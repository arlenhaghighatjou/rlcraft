package com.fantasticsource.mctools.component;

import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

public class CItemStack extends Component {
   public ItemStack value = ItemStack.EMPTY;

   public CItemStack() {
   }

   public CItemStack(ItemStack value) {
      this.set(value);
   }

   public CItemStack set(ItemStack value) {
      this.value = value;
      return this;
   }

   public CItemStack write(ByteBuf buf) {
      (new CStringUTF8()).set(this.value.serializeNBT().toString()).write(buf);
      return this;
   }

   public CItemStack read(ByteBuf buf) {
      try {
         return this.set(new ItemStack(JsonToNBT.getTagFromJson((new CStringUTF8()).read(buf).value)));
      } catch (NBTException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public CItemStack save(OutputStream stream) {
      (new CStringUTF8()).set(this.value.serializeNBT().toString()).save(stream);
      return this;
   }

   public CItemStack load(InputStream stream) {
      try {
         return this.set(new ItemStack(JsonToNBT.getTagFromJson((new CStringUTF8()).load(stream).value)));
      } catch (NBTException var3) {
         var3.printStackTrace();
         return null;
      }
   }
}
