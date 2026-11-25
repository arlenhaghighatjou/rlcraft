package org.orecruncher.dsurround.client.fx.particle;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleBlockDust;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.WorldUtils;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class ParticleDust extends ParticleBlockDust {
   private final float f;
   private final float f1;
   private final float f2;
   private final float f3;
   private final float f4;
   private final MutableBlockPos pos = new MutableBlockPos();
   private int slX16;
   private int blX16;

   public ParticleDust(World world, double x, double y, double z, IBlockState block) {
      super(world, x, y, z, 0.0D, 0.0D, 0.0D, block);
      this.canCollide = false;
      this.rand = XorShiftRandom.current();
      this.multipleParticleScaleBy((float)(0.30000001192092896D + this.rand.nextGaussian() / 30.0D));
      this.setPosition(this.posX, this.posY, this.posZ);
      this.f = this.particleTexture.getInterpolatedU((double)(this.particleTextureJitterX / 4.0F * 16.0F));
      this.f1 = this.particleTexture.getInterpolatedU((double)((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
      this.f2 = this.particleTexture.getInterpolatedV((double)(this.particleTextureJitterY / 4.0F * 16.0F));
      this.f3 = this.particleTexture.getInterpolatedV((double)((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
      this.f4 = 0.1F * this.particleScale;
   }

   public void move(double dX, double dY, double dZ) {
      this.posX += dX;
      this.posY += dY;
      this.posZ += dZ;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= 0.04D * (double)this.particleGravity;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9800000190734863D;
      this.motionY *= 0.9800000190734863D;
      this.motionZ *= 0.9800000190734863D;
      this.pos.setPos(this.posX, this.posY, this.posZ);
      if (this.particleMaxAge-- <= 0) {
         this.setExpired();
      } else if (WorldUtils.isSolidBlock(this.world, this.pos)) {
         this.setExpired();
      }

      int combinedLight = this.getBrightnessForRender(0.0F);
      this.slX16 = combinedLight >> 16 & '\uffff';
      this.blX16 = combinedLight & '\uffff';
   }

   public void renderParticle(BufferBuilder worldRendererIn, @Nonnull Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      worldRendererIn.pos((double)(f5 - rotationX * this.f4 - rotationXY * this.f4), (double)(f6 - rotationZ * this.f4), (double)(f7 - rotationYZ * this.f4 - rotationXZ * this.f4)).tex((double)this.f, (double)this.f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(this.slX16, this.blX16).endVertex();
      worldRendererIn.pos((double)(f5 - rotationX * this.f4 + rotationXY * this.f4), (double)(f6 + rotationZ * this.f4), (double)(f7 - rotationYZ * this.f4 + rotationXZ * this.f4)).tex((double)this.f, (double)this.f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(this.slX16, this.blX16).endVertex();
      worldRendererIn.pos((double)(f5 + rotationX * this.f4 + rotationXY * this.f4), (double)(f6 + rotationZ * this.f4), (double)(f7 + rotationYZ * this.f4 + rotationXZ * this.f4)).tex((double)this.f1, (double)this.f2).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(this.slX16, this.blX16).endVertex();
      worldRendererIn.pos((double)(f5 + rotationX * this.f4 - rotationXY * this.f4), (double)(f6 - rotationZ * this.f4), (double)(f7 + rotationYZ * this.f4 - rotationXZ * this.f4)).tex((double)this.f1, (double)this.f3).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(this.slX16, this.blX16).endVertex();
   }

   public int getBrightnessForRender(float partialTicks) {
      return this.world.getCombinedLight(this.pos, 0);
   }
}
