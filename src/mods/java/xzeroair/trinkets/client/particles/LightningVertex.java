package xzeroair.trinkets.client.particles;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.Vec3d;
import xzeroair.trinkets.util.Reference;

public class LightningVertex {
   private double z;
   private double y;
   private double x;
   private EaglercraftRandom rand = Reference.random;
   private int rx;
   private int ry;
   private int rz;

   public LightningVertex(Vec3d vec) {
      this.x = vec.x;
      this.y = vec.y;
      this.z = vec.z;
      this.rx = this.rand.nextInt(4) * (this.rand.nextBoolean() ? -1 : 1);
      this.ry = this.rand.nextInt(4) * (this.rand.nextBoolean() ? -1 : 1);
      this.rz = this.rand.nextInt(4) * (this.rand.nextBoolean() ? -1 : 1);
   }

   public Vec3d getVec() {
      return new Vec3d(this.x + (double)((float)this.rx * 0.1F), this.y + (double)((float)this.ry * 0.1F), this.z + (double)((float)this.rz * 0.1F));
   }
}
