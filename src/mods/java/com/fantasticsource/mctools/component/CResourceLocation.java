package com.fantasticsource.mctools.component;

import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import net.minecraft.util.ResourceLocation;

public class CResourceLocation extends Component {
   public ResourceLocation value;

   public CResourceLocation() {
   }

   public CResourceLocation(ResourceLocation value) {
      this.set(value);
   }

   public CResourceLocation set(ResourceLocation value) {
      this.value = value;
      return this;
   }

   public CResourceLocation write(ByteBuf buf) {
      (new CStringUTF8()).set(this.value.getResourceDomain()).write(buf).set(this.value.getResourcePath()).write(buf);
      return this;
   }

   public CResourceLocation read(ByteBuf buf) {
      return this.set(new ResourceLocation((new CStringUTF8()).read(buf).value, (new CStringUTF8()).read(buf).value));
   }

   public CResourceLocation save(OutputStream stream) {
      (new CStringUTF8()).set(this.value.getResourceDomain()).save(stream).set(this.value.getResourcePath()).save(stream);
      return this;
   }

   public CResourceLocation load(InputStream stream) {
      return this.set(new ResourceLocation((new CStringUTF8()).load(stream).value, (new CStringUTF8()).load(stream).value));
   }
}
