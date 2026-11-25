package familiarfauna.particle;

import familiarfauna.core.ClientProxy;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;

public class EntityBluePixieTrailFX extends Particle {
   private float defaultParticleScale;

   public EntityBluePixieTrailFX(World world, double xCoordIn, double yCoordIn, double zCoordIn, double motionXIn, double motionYIn, double motionZIn) {
      this(world, xCoordIn, yCoordIn, zCoordIn, motionXIn, motionYIn, motionZIn, 1.0F);
   }

   public EntityBluePixieTrailFX(World world, double xCoordIn, double yCoordIn, double zCoordIn, double motionXIn, double motionYIn, double motionZIn, float par14) {
      super(world, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
      this.particleTextureIndexX = 7;
      this.particleTextureIndexY = 1;
      this.motionX *= 0.10000000149011612D;
      this.motionY *= 0.10000000149011612D;
      this.motionZ *= 0.10000000149011612D;
      this.motionX += motionXIn;
      this.motionY += motionYIn;
      this.motionZ += motionZIn;
      this.particleScale *= 0.75F;
      this.particleScale *= par14;
      this.defaultParticleScale = this.particleScale;
      this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D) * 8.0D);
      this.particleMaxAge = (int)((float)this.particleMaxAge * par14);
      this.particleAge = world.rand.nextInt(3);
      this.particleAlpha = 1.0F;
      this.particleGravity = 0.02F;
      this.canCollide = false;
   }

   public int getFXLayer() {
      return 2;
   }

   public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rotX, float rotXZ, float rotZ, float rotYZ, float rotXY) {
      FMLClientHandler.instance().getClient().renderEngine.bindTexture(ClientProxy.particleTexturesLocation);
      float scaleMultiplier = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge * 32.0F;
      scaleMultiplier = MathHelper.clamp(scaleMultiplier, 0.0F, 1.0F);
      this.particleScale = this.defaultParticleScale * scaleMultiplier;
      GlStateManager.depthMask(false);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 1);
      super.renderParticle(buffer, entity, partialTicks, rotX, rotXZ, rotZ, rotYZ, rotXY);
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
   }

   public int getBrightnessForRender(float p_189214_1_) {
      float f = (float)this.particleMaxAge - ((float)this.particleAge + p_189214_1_) / (float)this.particleMaxAge;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      int i = super.getBrightnessForRender(p_189214_1_);
      int j = i & 255;
      int k = i >> 16 & 255;
      j = j + (int)(f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

      this.particleTextureIndexX = 7 - this.particleAge * 8 / this.particleMaxAge;
      this.move(this.motionX, this.motionY, this.motionZ);
      if (this.posY == this.prevPosY) {
         this.motionX *= 1.1D;
         this.motionZ *= 1.1D;
      }

      this.motionX *= 0.9599999785423279D;
      this.motionY *= 0.9599999785423279D;
      this.motionZ *= 0.9599999785423279D;
      if (this.onGround) {
         this.motionX *= 0.699999988079071D;
         this.motionZ *= 0.699999988079071D;
      }

   }
}
