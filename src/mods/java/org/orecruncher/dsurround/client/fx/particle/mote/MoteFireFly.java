package org.orecruncher.dsurround.client.fx.particle.mote;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Color;

@SideOnly(Side.CLIENT)
public class MoteFireFly extends MoteAnimatedBase implements IIlluminatedMote {
   private static final int startColorRGB = Color.YELLOW.rgb();
   private static final int fadeColorRGB = Color.LGREEN.rgb();
   private static final float XZ_MOTION_DELTA = 0.2F;
   private static final float Y_MOTION_DELTA = 0.1F;
   private static final float ACCELERATION = 0.004F;

   public MoteFireFly(World world, double x, double y, double z) {
      super(160, 8, world, x, y, z, 0.0D, 0.0D, 0.0D);
      this.motionX = RANDOM.nextGaussian() * 0.20000000298023224D;
      this.motionZ = RANDOM.nextGaussian() * 0.20000000298023224D;
      this.motionY = RANDOM.nextGaussian() * 0.10000000149011612D;
      this.xAcceleration = RANDOM.nextGaussian() * 0.004000000189989805D;
      this.yAcceleration = RANDOM.nextGaussian() / 2.0D * 0.004000000189989805D;
      this.zAcceleration = RANDOM.nextGaussian() * 0.004000000189989805D;
      this.gravity = 0.0D;
      this.particleScale *= 0.01875F;
      this.maxAge = 120 + RANDOM.nextInt(12);
      this.setColor(startColorRGB);
      this.setColorFade(fadeColorRGB);
   }

   public void update() {
      super.update();
      this.doRender = this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0;
   }

   public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
      if (this.doRender) {
         super.renderParticle(buffer, entityIn, partialTicks, rotX, rotZ, rotYZ, rotXY, rotXZ);
      }

   }

   protected double lightedX(float partialTicks) {
      return (double)((float)(this.prevX + (this.posX - this.prevX) * (double)partialTicks));
   }

   protected double lightedY(float partialTicks) {
      return (double)((float)(this.prevY + (this.posY - this.prevY) * (double)partialTicks));
   }

   protected double lightedZ(float partialTicks) {
      return (double)((float)(this.prevZ + (this.posZ - this.prevZ) * (double)partialTicks));
   }

   public Vec3d getPosition() {
      float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
      double x = this.lightedX(partialTicks);
      double y = this.lightedY(partialTicks);
      double z = this.lightedZ(partialTicks);
      return new Vec3d(x, y, z);
   }

   public Color getColor() {
      return new Color(this.red, this.green, this.blue);
   }

   public float getAlpha() {
      return (float)this.alpha * 0.005F;
   }

   public float getRadius() {
      return 3.0F;
   }
}
