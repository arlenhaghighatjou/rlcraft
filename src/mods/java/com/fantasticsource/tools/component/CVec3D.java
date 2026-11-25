package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;
import java.io.InputStream;
import java.io.OutputStream;
import net.minecraft.util.math.Vec3d;

public class CVec3D extends Component {
   public CDouble x = new CDouble();
   public CDouble y = new CDouble();
   public CDouble z = new CDouble();

   public CVec3D() {
   }

   public double getX() {
      return this.x.value;
   }

   public double getY() {
      return this.y.value;
   }

   public double getZ() {
      return this.z.value;
   }

   public Vec3d get() {
      return new Vec3d(this.x.value, this.y.value, this.z.value);
   }

   public CVec3D set(double x, double y, double z) {
      this.x.value = x;
      this.y.value = y;
      this.z.value = z;
      return this;
   }

   public CVec3D write(ByteBuf buf) {
      this.x.write(buf);
      this.y.write(buf);
      this.z.write(buf);
      return this;
   }

   public CVec3D read(ByteBuf buf) {
      this.x.read(buf);
      this.y.read(buf);
      this.z.read(buf);
      return this;
   }

   public CVec3D save(OutputStream stream) {
      this.x.save(stream);
      this.y.save(stream);
      this.z.save(stream);
      return this;
   }

   public CVec3D load(InputStream stream) {
      this.x.load(stream);
      this.y.load(stream);
      this.z.load(stream);
      return this;
   }
}
