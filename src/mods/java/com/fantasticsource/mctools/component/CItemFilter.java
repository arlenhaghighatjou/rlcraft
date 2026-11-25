package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map.Entry;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class CItemFilter extends Component {
   public ItemFilter value = null;

   public CItemFilter() {
   }

   public CItemFilter set(ItemFilter value) {
      this.value = value;
      return this;
   }

   public CItemFilter write(ByteBuf buf) {
      buf.writeBoolean(this.value.itemStack != null);
      if (this.value.itemStack != null) {
         (new CItemStack(this.value.itemStack)).write(buf);
      }

      buf.writeInt(this.value.tagsRequired.size());

      for(Entry<String, String> entry : this.value.tagsRequired.entrySet()) {
         ByteBufUtils.writeUTF8String(buf, (String)entry.getKey());
         ByteBufUtils.writeUTF8String(buf, (String)entry.getValue());
      }

      buf.writeInt(this.value.tagsDisallowed.size());

      for(Entry<String, String> entry : this.value.tagsDisallowed.entrySet()) {
         ByteBufUtils.writeUTF8String(buf, (String)entry.getKey());
         ByteBufUtils.writeUTF8String(buf, (String)entry.getValue());
      }

      return this;
   }

   public CItemFilter read(ByteBuf buf) {
      this.value = new ItemFilter();
      if (buf.readBoolean()) {
         this.value.itemStack = (new CItemStack()).read(buf).value;
      }

      for(int i = buf.readInt(); i > 0; --i) {
         this.value.tagsRequired.put(ByteBufUtils.readUTF8String(buf), ByteBufUtils.readUTF8String(buf));
      }

      for(int i = buf.readInt(); i > 0; --i) {
         this.value.tagsDisallowed.put(ByteBufUtils.readUTF8String(buf), ByteBufUtils.readUTF8String(buf));
      }

      return this;
   }

   public CItemFilter save(OutputStream stream) {
      CInt ci = new CInt();
      CStringUTF8 cs = new CStringUTF8();
      (new CBoolean()).set(this.value.itemStack != null).save(stream);
      if (this.value.itemStack != null) {
         (new CItemStack()).set(this.value.itemStack).save(stream);
      }

      ci.set(this.value.tagsRequired.size()).save(stream);

      for(Entry<String, String> entry : this.value.tagsRequired.entrySet()) {
         cs.set((String)entry.getKey()).save(stream).set((String)entry.getValue()).save(stream);
      }

      ci.set(this.value.tagsDisallowed.size()).save(stream);

      for(Entry<String, String> entry : this.value.tagsDisallowed.entrySet()) {
         cs.set((String)entry.getKey()).save(stream).set((String)entry.getValue()).save(stream);
      }

      return this;
   }

   public CItemFilter load(InputStream stream) {
      CInt ci = new CInt();
      CStringUTF8 cs = new CStringUTF8();
      this.value = new ItemFilter();
      if ((new CBoolean()).load(stream).value) {
         this.value.itemStack = (new CItemStack()).load(stream).value;
      }

      for(int i = ci.load(stream).value; i > 0; --i) {
         this.value.tagsRequired.put(cs.load(stream).value, cs.load(stream).value);
      }

      for(int i = ci.load(stream).value; i > 0; --i) {
         this.value.tagsDisallowed.put(cs.load(stream).value, cs.load(stream).value);
      }

      return this;
   }
}
