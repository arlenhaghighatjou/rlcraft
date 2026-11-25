package org.orecruncher.dsurround.client.fx.particle.mote;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MoteRainSplash extends MoteWaterSpray {
   public MoteRainSplash(World world, double x, double y, double z) {
      super(world, x, y, z, 0.0D, 0.0D, 0.0D);
      this.motionX = (RANDOM.nextDouble() * 2.0D - 1.0D) * 0.4000000059604645D;
      this.motionY = (RANDOM.nextDouble() * 2.0D - 1.0D) * 0.4000000059604645D;
      this.motionZ = (RANDOM.nextDouble() * 2.0D - 1.0D) * 0.4000000059604645D;
      float f = (float)(RANDOM.nextDouble() + RANDOM.nextDouble() + 1.0D) * 0.15F;
      float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
      this.motionX = this.motionX / (double)f1 * (double)f * 0.4000000059604645D;
      this.motionY = this.motionY / (double)f1 * (double)f * 0.4000000059604645D + 0.10000000149011612D;
      this.motionZ = this.motionZ / (double)f1 * (double)f * 0.4000000059604645D;
      this.motionX *= 0.30000001192092896D;
      this.motionY = RANDOM.nextDouble() * 0.20000000298023224D + 0.10000000149011612D;
      this.motionZ *= 0.30000001192092896D;
   }

   public void configureColor() {
      this.red = this.green = this.blue = this.alpha = 255;
   }
}
