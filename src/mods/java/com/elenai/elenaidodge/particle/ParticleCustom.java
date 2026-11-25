package com.elenai.elenaidodge.particle;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleCustom extends Particle {
   private static final double GRAVITY = 0.04D;
   private static final double GROUND_DECCEL = 0.7D;
   private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);
   private ParticleCustom.TextureDefinition TEXTURE_DEF;
   private int currentAnimationFrame = 0;
   private float progress = 0.0F;
   private float rotSpeed = 0.0F;
   private double deccel = 0.98D;
   private float initialAlpha = 1.0F;
   private float finalAlpha = 1.0F;
   private Color initialTint = Color.WHITE;
   private Color finalTint = Color.WHITE;
   private float initialScale = 1.0F;
   private float finalScale = 1.0F;
   private boolean enableDepth = true;

   public ParticleCustom(ParticleCustom.TextureDefinition parTexDef, World parWorld, double parX, double parY, double parZ) {
      super(parWorld, parX, parY, parZ);
      this.TEXTURE_DEF = parTexDef;
   }

   public ParticleCustom(ParticleCustom.TextureDefinition parTexDef, World parWorld, double parX, double parY, double parZ, double parMotionX, double parMotionY, double parMotionZ) {
      super(parWorld, parX, parY, parZ);
      this.TEXTURE_DEF = parTexDef;
      this.motionX = parMotionX;
      this.motionY = parMotionY;
      this.motionZ = parMotionZ;
   }

   public void onUpdate() {
      this.updateTick();
      this.processGravityAndDeccel();
      this.move(this.motionX, this.motionY, this.motionZ);
      this.processAlphaTween();
      this.processTintTween();
      this.processScaleTween();
   }

   private void updateTick() {
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

      this.progress = (float)this.particleAge / (float)this.particleMaxAge;
      if (this.TEXTURE_DEF.isTweenAnimationMode()) {
         this.currentAnimationFrame = (int)(this.progress * (float)(this.TEXTURE_DEF.getAnimationFrameCount() + 1));
      } else if (this.currentAnimationFrame++ >= this.TEXTURE_DEF.getAnimationFrameCount()) {
         this.currentAnimationFrame = 0;
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.prevParticleAngle = this.particleAngle;
      this.particleAngle += 3.1415927F * this.rotSpeed * 2.0F;
   }

   private void processGravityAndDeccel() {
      this.motionY -= 0.04D * (double)this.particleGravity;
      this.motionX *= this.deccel;
      this.motionY *= this.deccel;
      this.motionZ *= this.deccel;
      this.rotSpeed = (float)((double)this.rotSpeed * this.deccel);
      if (this.onGround && this.canCollide) {
         this.motionX *= 0.7D * this.deccel;
         this.motionZ *= 0.7D * this.deccel;
      }

   }

   protected void processAlphaTween() {
      this.particleAlpha = this.initialAlpha + this.progress * (this.finalAlpha - this.initialAlpha);
   }

   protected void processTintTween() {
      this.particleRed = ((float)this.initialTint.getRed() + this.progress * (float)(this.finalTint.getRed() - this.initialTint.getRed())) / 256.0F;
      this.particleBlue = ((float)this.initialTint.getBlue() + this.progress * (float)(this.finalTint.getBlue() - this.initialTint.getBlue())) / 256.0F;
      this.particleGreen = ((float)this.initialTint.getGreen() + this.progress * (float)(this.finalTint.getGreen() - this.initialTint.getGreen())) / 256.0F;
   }

   protected void processScaleTween() {
      this.particleScale = this.initialScale + this.progress * (this.finalScale - this.initialScale);
   }

   public int getFXLayer() {
      return 3;
   }

   public boolean shouldDisableDepth() {
      return !this.enableDepth;
   }

   public void renderParticle(WorldRenderer bufferIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.alphaFunc(516, 0.003921569F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, this.particleAlpha);
      Minecraft.getMinecraft().getTextureManager().bindTexture(this.TEXTURE_DEF.getResourceLocation());
      float uMin = this.TEXTURE_DEF.getUMin();
      float uMax = this.TEXTURE_DEF.getUMax();
      float frameV = (float)this.currentAnimationFrame * (this.TEXTURE_DEF.getVMax() - this.TEXTURE_DEF.getVmin());
      float vMin = this.TEXTURE_DEF.getVmin() + frameV;
      float vMax = this.TEXTURE_DEF.getVMax() + frameV;
      float scale = 0.1F * this.particleScale;
      float xInterp = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float yInterp = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float zInterp = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      Vec3d[] avec3d = new Vec3d[]{new Vec3d((double)(-rotationX * scale - rotationXY * scale), (double)(-rotationZ * scale), (double)(-rotationYZ * scale - rotationXZ * scale)), new Vec3d((double)(-rotationX * scale + rotationXY * scale), (double)(rotationZ * scale), (double)(-rotationYZ * scale + rotationXZ * scale)), new Vec3d((double)(rotationX * scale + rotationXY * scale), (double)(rotationZ * scale), (double)(rotationYZ * scale + rotationXZ * scale)), new Vec3d((double)(rotationX * scale - rotationXY * scale), (double)(-rotationZ * scale), (double)(rotationYZ * scale - rotationXZ * scale))};
      if (this.particleAngle != 0.0F) {
         float angleInterp = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
         float f9 = MathHelper.cos(angleInterp * 0.5F);
         float xComponent = MathHelper.sin(angleInterp * 0.5F) * (float)cameraViewDir.x;
         float yComponent = MathHelper.sin(angleInterp * 0.5F) * (float)cameraViewDir.y;
         float zComponent = MathHelper.sin(angleInterp * 0.5F) * (float)cameraViewDir.z;
         Vec3d vec3d = new Vec3d((double)xComponent, (double)yComponent, (double)zComponent);

         for(int l = 0; l < 4; ++l) {
            avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double)(2.0F * f9)));
         }
      }

      int brightness = this.getBrightnessForRender(partialTicks);
      int j = brightness >> 16 & '\uffff';
      int k = brightness & '\uffff';
      bufferIn.begin(7, VERTEX_FORMAT);
      bufferIn.pos((double)xInterp + avec3d[0].x, (double)yInterp + avec3d[0].y, (double)zInterp + avec3d[0].z).tex((double)uMax, (double)vMax).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).normal(0.0F, 1.0F, 0.0F).endVertex();
      bufferIn.pos((double)xInterp + avec3d[1].x, (double)yInterp + avec3d[1].y, (double)zInterp + avec3d[1].z).tex((double)uMax, (double)vMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).normal(0.0F, 1.0F, 0.0F).endVertex();
      bufferIn.pos((double)xInterp + avec3d[2].x, (double)yInterp + avec3d[2].y, (double)zInterp + avec3d[2].z).tex((double)uMin, (double)vMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).normal(0.0F, 1.0F, 0.0F).endVertex();
      bufferIn.pos((double)xInterp + avec3d[3].x, (double)yInterp + avec3d[3].y, (double)zInterp + avec3d[3].z).tex((double)uMin, (double)vMax).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).normal(0.0F, 1.0F, 0.0F).endVertex();
      Tessellator.getInstance().draw();
      GlStateManager.popMatrix();
   }

   public ParticleCustom setLifeSpan(int lifeIn) {
      this.particleMaxAge = lifeIn;
      return this;
   }

   public ParticleCustom setCanCollide(boolean canCollideIn) {
      this.canCollide = canCollideIn;
      return this;
   }

   public ParticleCustom setAABB(AxisAlignedBB boundingBoxIn) {
      this.setBoundingBox(boundingBoxIn);
      return this;
   }

   public ParticleCustom setDeccel(double deccelIn) {
      this.deccel = deccelIn;
      return this;
   }

   public ParticleCustom setScale(float scaleIn) {
      this.particleScale = scaleIn;
      this.initialScale = scaleIn;
      this.finalScale = scaleIn;
      return this;
   }

   public ParticleCustom setTintColor(float redIn, float greenIn, float blueIn) {
      this.particleRed = redIn;
      this.particleGreen = greenIn;
      this.particleBlue = blueIn;
      this.initialTint = new Color(redIn / 256.0F, greenIn / 256.0F, blueIn / 256.0F);
      this.finalTint = this.initialTint;
      return this;
   }

   public ParticleCustom setTintColor(Color colorIn) {
      this.particleGreen = (float)colorIn.getGreen() / 256.0F;
      this.particleBlue = (float)colorIn.getBlue() / 256.0F;
      this.particleRed = (float)colorIn.getRed() / 256.0F;
      this.initialTint = colorIn;
      this.finalTint = this.initialTint;
      return this;
   }

   public ParticleCustom setTintColorAndAlpha(float redIn, float greenIn, float blueIn, float alphaIn) {
      this.setTintColor(redIn, greenIn, blueIn);
      this.setAlpha(alphaIn);
      return this;
   }

   public ParticleCustom setTintColorAndAlpha(Color colorIn) {
      this.setTintColor(colorIn);
      this.setAlpha(colorIn);
      return this;
   }

   public ParticleCustom setAlpha(float alphaIn) {
      this.particleAlpha = alphaIn;
      this.initialAlpha = alphaIn;
      this.finalAlpha = this.initialAlpha;
      return this;
   }

   public ParticleCustom setAlpha(Color colorIn) {
      this.particleAlpha = (float)colorIn.getAlpha() / 256.0F;
      this.initialAlpha = this.particleAlpha;
      this.finalAlpha = this.initialAlpha;
      return this;
   }

   public ParticleCustom setInitalTint(Color colorIn) {
      this.initialTint = colorIn;
      this.particleGreen = (float)colorIn.getGreen() / 256.0F;
      this.particleBlue = (float)colorIn.getBlue() / 256.0F;
      this.particleRed = (float)colorIn.getRed() / 256.0F;
      return this;
   }

   public ParticleCustom setFinalTint(Color colorIn) {
      this.finalTint = colorIn;
      return this;
   }

   public ParticleCustom setInitialAlpha(float alphaIn) {
      this.initialAlpha = alphaIn;
      this.particleAlpha = alphaIn;
      return this;
   }

   public ParticleCustom setInitialAlpha(Color colorIn) {
      this.initialAlpha = (float)colorIn.getAlpha() / 256.0F;
      this.particleAlpha = (float)colorIn.getAlpha() / 256.0F;
      return this;
   }

   public ParticleCustom setFinalAlpha(float alphaIn) {
      this.finalAlpha = alphaIn;
      return this;
   }

   public ParticleCustom setFinalAlpha(Color colorIn) {
      this.finalAlpha = (float)colorIn.getAlpha() / 256.0F;
      return this;
   }

   public ParticleCustom setInitialScale(float scaleIn) {
      this.particleScale = scaleIn;
      this.initialScale = scaleIn;
      return this;
   }

   public ParticleCustom setFinalScale(float scaleIn) {
      this.finalScale = scaleIn;
      return this;
   }

   public ParticleCustom setGravity(float gravityIn) {
      this.particleGravity = gravityIn;
      return this;
   }

   public ParticleCustom setRotSpeed(float rotIn) {
      this.rotSpeed = rotIn;
      return this;
   }

   public ParticleCustom setEnableDepth(boolean enableDepthIn) {
      this.enableDepth = enableDepthIn;
      return this;
   }

   public static class TextureDefinition {
      private String name;
      private ResourceLocation resourceLocation;
      private float uMin;
      private float vMin;
      private float uMax;
      private float vMax;
      private boolean tweenAnimationMode;
      private int animationFrameCount;

      public TextureDefinition(String parName) {
         this(parName, 0.0F, 0.0F, 1.0F, 1.0F);
      }

      private TextureDefinition(String parName, float parUMin, float parVMin, float parUMax, float parVMax) {
         this(parName, parUMin, parVMin, parUMax, parVMax, false, 1);
      }

      public TextureDefinition(String parName, boolean parAnimMode, int parAnimFrames) {
         this(parName, 0.0F, 0.0F, 1.0F, 1.0F / (float)parAnimFrames, parAnimMode, parAnimFrames);
      }

      private TextureDefinition(String parName, float parUMin, float parVMin, float parUMax, float parVMax, boolean parAnimMode, int parAnimFrames) {
         this.name = parName;
         this.resourceLocation = new ResourceLocation("elenaidodge", "textures/particles/" + this.name + ".png");
         this.uMin = parUMin;
         this.vMin = parVMin;
         this.uMax = parUMax;
         this.vMax = parVMax;
         this.tweenAnimationMode = parAnimMode;
         this.animationFrameCount = parAnimFrames;
      }

      public String getName() {
         return this.name;
      }

      public ResourceLocation getResourceLocation() {
         return this.resourceLocation;
      }

      public float getUMin() {
         return this.uMin;
      }

      public float getVmin() {
         return this.vMin;
      }

      public float getUMax() {
         return this.uMax;
      }

      public float getVMax() {
         return this.vMax;
      }

      public boolean isTweenAnimationMode() {
         return this.tweenAnimationMode;
      }

      public int getAnimationFrameCount() {
         return this.animationFrameCount;
      }
   }
}
