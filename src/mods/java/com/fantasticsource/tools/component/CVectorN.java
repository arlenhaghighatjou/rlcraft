package com.fantasticsource.tools.component;

import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;

public class CVectorN extends Component {
   public VectorN value;

   public CVectorN() {
   }

   public CVectorN set(VectorN value) {
      this.value = value;
      return this;
   }

   public CVectorN set(double... values) {
      this.value = new VectorN(values);
      return this;
   }

   public CVectorN write(ByteBuf buf) {
      buf.writeInt(this.value.values.length);

      for(double d : this.value.values) {
         buf.writeDouble(d);
      }

      return this;
   }

   public CVectorN read(ByteBuf buf) {
      double[] values = new double[buf.readInt()];

      for(int i = 0; i < values.length; ++i) {
         values[i] = buf.readDouble();
      }

      this.set(values);
      return this;
   }

   public CVectorN save(OutputStream stream) {
      (new CInt()).set(this.value.values.length).save(stream);
      CDouble cd = new CDouble();

      for(double d : this.value.values) {
         cd.set(d).save(stream);
      }

      return this;
   }

   public CVectorN load(InputStream stream) {
      double[] values = new double[(new CInt()).load(stream).value];
      CDouble cd = new CDouble();

      for(int i = 0; i < values.length; ++i) {
         values[i] = cd.load(stream).value;
      }

      this.set(values);
      return this;
   }
}
