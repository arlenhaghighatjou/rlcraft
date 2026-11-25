package org.orecruncher.dsurround.client.fx.particle.mote;

import javax.annotation.Nonnull;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.chunk.ClientChunkCache;

@SideOnly(Side.CLIENT)
public abstract class MoteMotionBase extends MoteAgeable {
   protected double motionX;
   protected double motionY;
   protected double motionZ;
   protected double gravity;
   protected double prevX;
   protected double prevY;
   protected double prevZ;

   protected MoteMotionBase(@Nonnull World world, double x, double y, double z, double dX, double dY, double dZ) {
      super(world, x, y, z);
      this.prevX = this.posX;
      this.prevY = this.posY;
      this.prevZ = this.posZ;
      this.motionX = dX;
      this.motionY = dY;
      this.motionZ = dZ;
      this.gravity = 0.06D;
   }

   protected float renderX(float partialTicks) {
      return (float)(this.prevX + (this.posX - this.prevX) * (double)partialTicks - this.interpX());
   }

   protected float renderY(float partialTicks) {
      return (float)(this.prevY + (this.posY - this.prevY) * (double)partialTicks - this.interpY());
   }

   protected float renderZ(float partialTicks) {
      return (float)(this.prevZ + (this.posZ - this.prevZ) * (double)partialTicks - this.interpZ());
   }

   protected boolean hasCollided() {
      return ClientChunkCache.instance().getBlockState(this.position).getMaterial().isSolid();
   }

   protected void handleCollision() {
      this.kill();
   }

   protected void update() {
      this.prevX = this.posX;
      this.prevY = this.posY;
      this.prevZ = this.posZ;
      this.motionY -= this.gravity;
      this.posX += this.motionX;
      this.posY += this.motionY;
      this.posZ += this.motionZ;
      this.position.setPos(this.posX, this.posY, this.posZ);
      if (this.hasCollided()) {
         this.handleCollision();
      } else {
         this.motionX *= 0.9800000190734863D;
         this.motionY *= 0.9800000190734863D;
         this.motionZ *= 0.9800000190734863D;
      }

   }
}
