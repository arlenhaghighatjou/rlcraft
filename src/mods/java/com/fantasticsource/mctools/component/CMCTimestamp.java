package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.MCTimestamp;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CLong;
import com.fantasticsource.tools.component.Component;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;

public class CMCTimestamp extends Component {
   public MCTimestamp value;

   public CMCTimestamp() {
   }

   public CMCTimestamp set(MCTimestamp value) {
      this.value = value;
      return this;
   }

   public CMCTimestamp write(ByteBuf buf) {
      buf.writeLong(this.value.getInstant().getEpochSecond());
      buf.writeInt(this.value.getInstant().getNano());
      buf.writeLong(this.value.serverTick);
      buf.writeLong(this.value.clientTick);
      buf.writeLong(this.value.worldTick);
      return this;
   }

   public CMCTimestamp read(ByteBuf buf) {
      this.set(new MCTimestamp(Instant.ofEpochSecond(buf.readLong(), (long)buf.readInt()), buf.readLong(), buf.readLong(), buf.readLong()));
      return this;
   }

   public CMCTimestamp save(OutputStream stream) {
      (new CLong()).set(this.value.getInstant().getEpochSecond()).save(stream);
      (new CInt()).set(this.value.getInstant().getNano()).save(stream);
      (new CLong()).set(this.value.serverTick).save(stream);
      (new CLong()).set(this.value.clientTick).save(stream);
      (new CLong()).set(this.value.worldTick).save(stream);
      return this;
   }

   public CMCTimestamp load(InputStream stream) {
      CLong l = new CLong();
      this.set(new MCTimestamp(Instant.ofEpochSecond(l.load(stream).value, (long)(new CInt()).load(stream).value), l.load(stream).value, l.load(stream).value, l.load(stream).value));
      return this;
   }
}
