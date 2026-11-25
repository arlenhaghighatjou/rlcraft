package betterquesting.api2.client.gui.controls.io;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.controls.IValueIO;
import net.minecraft.util.math.MathHelper;

public class FloatSimpleIO implements IValueIO<Float> {
   private boolean lerp;
   private float lerpSpd;
   private boolean clamp;
   private final float min;
   private final float max;
   protected float v;
   protected float s;
   private long t;

   public FloatSimpleIO() {
      this(0.0F, 0.0F, 1.0F);
   }

   public FloatSimpleIO(float startVal, float min, float max) {
      this.lerp = false;
      this.lerpSpd = 0.02F;
      this.clamp = true;
      this.v = startVal;
      this.s = startVal;
      this.min = min;
      this.max = max;
      this.t = System.currentTimeMillis();
   }

   public FloatSimpleIO setClamp(boolean enable) {
      this.clamp = enable;
      return this;
   }

   public FloatSimpleIO setLerp(boolean enable, float speed) {
      this.lerp = enable;
      this.lerpSpd = speed;
      return this;
   }

   public Float readValue() {
      if (this.lerp && this.s != this.v) {
         if (Math.abs(this.s - this.v) < 0.001F) {
            this.s = this.v;
            return this.v;
         } else {
            long d = System.currentTimeMillis() - this.t;
            this.s = RenderUtils.lerpFloat(this.s, this.v, (float)MathHelper.clamp((double)d * (double)this.lerpSpd, 0.0D, 1.0D));
            if (d > 0L) {
               this.t = System.currentTimeMillis();
            }

            return this.s;
         }
      } else {
         return this.v;
      }
   }

   public void writeValue(Float value) {
      if (this.s == this.v) {
         this.t = System.currentTimeMillis();
      }

      this.v = this.clamp ? MathHelper.clamp(value.floatValue(), this.min, this.max) : value.floatValue();
   }

   public Float readValueRaw() {
      return this.v;
   }

   public void writeValueRaw(Float value) {
      this.v = this.clamp ? MathHelper.clamp(value.floatValue(), this.min, this.max) : value.floatValue();
      this.s = this.v;
   }
}
