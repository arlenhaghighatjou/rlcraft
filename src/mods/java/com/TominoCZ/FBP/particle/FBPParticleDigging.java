package com.TominoCZ.FBP.particle;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.keys.FBPKeyBindings;
import com.TominoCZ.FBP.util.FBPMathUtil;
import com.TominoCZ.FBP.util.FBPRenderUtil;
import com.TominoCZ.FBP.vector.FBPVector3d;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPParticleDigging extends ParticleDigging {
   private final IBlockState sourceState;
   Minecraft mc;
   float prevGravity;
   double startY;
   double scaleAlpha;
   double prevParticleScale;
   double prevParticleAlpha;
   double prevMotionX;
   double prevMotionZ;
   double endMult = 0.75D;
   boolean modeDebounce;
   boolean wasFrozen;
   boolean destroyed;
   boolean killToggle;
   EnumFacing facing;
   FBPVector3d rot;
   FBPVector3d prevRot;
   FBPVector3d rotStep;
   Vec2f[] par;
   static Entity dummyEntity = new Entity((World)null) {
      protected void writeEntityToNBT(NBTTagCompound compound) {
      }

      protected void readEntityFromNBT(NBTTagCompound compound) {
      }

      protected void entityInit() {
      }
   };

   protected FBPParticleDigging(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, float scale, float R, float G, float B, IBlockState state, @Nullable EnumFacing facing, @Nullable TextureAtlasSprite texture) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, state);
      this.particleRed = R;
      this.particleGreen = G;
      this.particleBlue = B;
      this.mc = Minecraft.getMinecraft();
      this.rot = new FBPVector3d();
      this.prevRot = new FBPVector3d();
      this.facing = facing;
      this.createRotationMatrix();

      try {
         FBP.setSourcePos.invokeExact(this, new BlockPos(xCoordIn, yCoordIn, zCoordIn));
      } catch (Throwable var28) {
         var28.printStackTrace();
      }

      if (scale > -1.0F) {
         this.particleScale = scale;
      }

      if (scale < -1.0F && facing != null && facing == EnumFacing.UP && FBP.smartBreaking) {
         this.motionX *= 1.5D;
         this.motionY *= 0.1D;
         this.motionZ *= 1.5D;
         double particleSpeed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         double x = FBPMathUtil.add(cameraViewDir.x, 0.01D);
         double z = FBPMathUtil.add(cameraViewDir.z, 0.01D);
         this.motionX = x * particleSpeed;
         this.motionZ = z * particleSpeed;
      }

      if (this.modeDebounce = !FBP.randomRotation) {
         this.rot.zero();
         this.calculateYAngle();
      }

      this.sourceState = state;
      Block b = state.getBlock();
      this.particleGravity = (float)((double)b.blockParticleGravity * FBP.gravityMult);
      this.particleScale = (float)(FBP.scaleMult * (double)(FBP.randomizedScale ? this.particleScale : 1.0F));
      this.particleMaxAge = (int)FBP.random.nextDouble((double)FBP.minAge, (double)FBP.maxAge + 0.5D);
      this.scaleAlpha = (double)this.particleScale * 0.82D;
      this.destroyed = facing == null;
      if (texture == null) {
         BlockModelShapes blockModelShapes = this.mc.getBlockRendererDispatcher().getBlockModelShapes();
         if (!this.destroyed) {
            try {
               List<BakedQuad> quads = blockModelShapes.getModelForState(state).getQuads(state, facing, 0L);
               if (quads != null && !quads.isEmpty()) {
                  this.particleTexture = ((BakedQuad)quads.get(0)).getSprite();
               }
            } catch (Exception var27) {
               ;
            }
         }

         if (this.particleTexture == null || this.particleTexture.getIconName().equals("missingno")) {
            this.setParticleTexture(blockModelShapes.getTexture(state));
         }
      } else {
         this.particleTexture = texture;
      }

      if (FBP.randomFadingSpeed) {
         this.endMult = MathHelper.clamp(FBP.random.nextDouble(0.5D, 0.9D), 0.55D, 0.8D);
      }

      this.prevGravity = this.particleGravity;
      this.startY = this.posY;
      this.multipleParticleScaleBy(1.0F);
   }

   public Particle multipleParticleScaleBy(float scale) {
      Particle p = super.multipleParticleScaleBy(scale);
      float f = this.particleScale / 10.0F;
      if (FBP.restOnFloor && this.destroyed) {
         this.posY = this.prevPosY = this.startY - (double)f;
      }

      this.setBoundingBox(new AxisAlignedBB(this.posX - (double)f, this.posY, this.posZ - (double)f, this.posX + (double)f, this.posY + (double)(2.0F * f), this.posZ + (double)f));
      return p;
   }

   public Particle MultiplyVelocity(float multiplier) {
      this.motionX *= (double)multiplier;
      this.motionY = (this.motionY - 0.10000000149011612D) * (double)(multiplier / 2.0F) + 0.10000000149011612D;
      this.motionZ *= (double)multiplier;
      return this;
   }

   protected void multiplyColor(@Nullable BlockPos p_187154_1_) {
      if (this.sourceState.getBlock() != Blocks.GRASS || this.facing == EnumFacing.UP) {
         int i = this.mc.getBlockColors().colorMultiplier(this.sourceState, this.world, p_187154_1_, 0);
         this.particleRed *= (float)(i >> 16 & 255) / 255.0F;
         this.particleGreen *= (float)(i >> 8 & 255) / 255.0F;
         this.particleBlue *= (float)(i & 255) / 255.0F;
      }
   }

   public FBPParticleDigging init() {
      this.multiplyColor(new BlockPos(this.posX, this.posY, this.posZ));
      return this;
   }

   public FBPParticleDigging setBlockPos(BlockPos pos) {
      this.multiplyColor(pos);
      return this;
   }

   public int getFXLayer() {
      return 1;
   }

   public void onUpdate() {
      boolean allowedToMove = (double)MathHelper.abs((float)this.motionX) > 1.0E-4D || (double)MathHelper.abs((float)this.motionZ) > 1.0E-4D;
      if (!FBP.frozen && FBP.bounceOffWalls && !this.mc.isGamePaused() && this.particleAge > 0) {
         if (!this.wasFrozen && allowedToMove) {
            boolean xCollided = this.prevPosX == this.posX;
            boolean zCollided = this.prevPosZ == this.posZ;
            if (xCollided) {
               this.motionX = -this.prevMotionX * 0.625D;
            }

            if (zCollided) {
               this.motionZ = -this.prevMotionZ * 0.625D;
            }

            if (!FBP.randomRotation && (xCollided || zCollided)) {
               this.calculateYAngle();
            }
         } else {
            this.wasFrozen = false;
         }
      }

      if (FBP.frozen && FBP.bounceOffWalls && !this.wasFrozen) {
         this.wasFrozen = true;
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.prevRot.copyFrom(this.rot);
      this.prevParticleAlpha = (double)this.particleAlpha;
      this.prevParticleScale = (double)this.particleScale;
      if (!this.mc.isGamePaused() && (!FBP.frozen || this.killToggle)) {
         if (!this.killToggle) {
            if (!FBP.randomRotation) {
               if (!this.modeDebounce) {
                  this.modeDebounce = true;
                  this.rot.z = 0.0D;
                  this.calculateYAngle();
               }

               if (allowedToMove) {
                  double x = (double)MathHelper.abs((float)(this.rotStep.x * this.getMult()));
                  if (this.motionX > 0.0D) {
                     if (this.motionZ > 0.0D) {
                        this.rot.x -= x;
                     } else if (this.motionZ < 0.0D) {
                        this.rot.x += x;
                     }
                  } else if (this.motionX < 0.0D) {
                     if (this.motionZ < 0.0D) {
                        this.rot.x += x;
                     } else if (this.motionZ > 0.0D) {
                        this.rot.x -= x;
                     }
                  }
               }
            } else {
               if (this.modeDebounce) {
                  this.modeDebounce = false;
                  this.rot.z = FBP.random.nextDouble(30.0D, 400.0D);
               }

               if (allowedToMove) {
                  this.rot.add(this.rotStep.multiply(this.getMult()));
               }
            }
         }

         if (!FBP.infiniteDuration) {
            ++this.particleAge;
         }

         if (this.particleAge >= this.particleMaxAge || this.killToggle) {
            this.particleScale = (float)((double)this.particleScale * 0.8876543045043945D * this.endMult);
            if ((double)this.particleAlpha > 0.01D && (double)this.particleScale <= this.scaleAlpha) {
               this.particleAlpha = (float)((double)this.particleAlpha * 0.6875200271606445D * this.endMult);
            }

            if ((double)this.particleAlpha <= 0.01D) {
               this.setExpired();
            }
         }

         if (!this.killToggle) {
            if (!this.onGround) {
               this.motionY -= 0.04D * (double)this.particleGravity;
            }

            this.move(this.motionX, this.motionY, this.motionZ);
            if (this.onGround && FBP.restOnFloor) {
               this.rot.x = (double)((float)Math.round(this.rot.x / 90.0D) * 90.0F);
               this.rot.z = (double)((float)Math.round(this.rot.z / 90.0D) * 90.0F);
            }

            if ((double)MathHelper.abs((float)this.motionX) > 1.0E-5D) {
               this.prevMotionX = this.motionX;
            }

            if ((double)MathHelper.abs((float)this.motionZ) > 1.0E-5D) {
               this.prevMotionZ = this.motionZ;
            }

            if (allowedToMove) {
               this.motionX *= 0.9800000190734863D;
               this.motionZ *= 0.9800000190734863D;
            }

            this.motionY *= 0.9800000190734863D;
            if (FBP.entityCollision) {
               for(Entity entityIn : this.world.getEntitiesWithinAABB(Entity.class, this.getBoundingBox())) {
                  if (!entityIn.noClip) {
                     double d0 = this.posX - entityIn.posX;
                     double d1 = this.posZ - entityIn.posZ;
                     double d2 = MathHelper.absMax(d0, d1);
                     if (d2 >= 0.009999999776482582D) {
                        d2 = Math.sqrt(d2);
                        d0 = d0 / d2;
                        d1 = d1 / d2;
                        double d3 = 1.0D / d2;
                        if (d3 > 1.0D) {
                           d3 = 1.0D;
                        }

                        this.motionX += d0 * d3 / 20.0D;
                        this.motionZ += d1 * d3 / 20.0D;
                        if (!FBP.randomRotation) {
                           this.calculateYAngle();
                        }

                        if (!FBP.frozen) {
                           this.onGround = false;
                        }
                     }
                  }
               }
            }

            if (FBP.waterPhysics) {
               if (this.isInWater()) {
                  this.handleWaterMovement();
                  if (FBP.INSTANCE.doesMaterialFloat(this.sourceState.getMaterial())) {
                     this.motionY = (double)(0.11F + this.particleScale / 1.25F * 0.02F);
                  } else {
                     this.motionX *= 0.932515086137662D;
                     this.motionZ *= 0.932515086137662D;
                     this.particleGravity = 0.35F;
                     this.motionY *= 0.8500000238418579D;
                  }

                  if (!FBP.randomRotation) {
                     this.calculateYAngle();
                  }

                  if (this.onGround) {
                     this.onGround = false;
                  }
               } else {
                  this.particleGravity = this.prevGravity;
               }
            }

            if (this.onGround) {
               if (FBP.lowTraction) {
                  this.motionX *= 0.932515086137662D;
                  this.motionZ *= 0.932515086137662D;
               } else {
                  this.motionX *= 0.6654999988079071D;
                  this.motionZ *= 0.6654999988079071D;
               }
            }
         }
      }

   }

   public boolean isInWater() {
      double scale = (double)(this.particleScale / 20.0F);
      int minX = MathHelper.floor(this.posX - scale);
      int maxX = MathHelper.ceil(this.posX + scale);
      int minY = MathHelper.floor(this.posY - scale);
      int maxY = MathHelper.ceil(this.posY + scale);
      int minZ = MathHelper.floor(this.posZ - scale);
      int maxZ = MathHelper.ceil(this.posZ + scale);
      if (this.world.isAreaLoaded(new StructureBoundingBox(minX, minY, minZ, maxX, maxY, maxZ), true)) {
         for(int x = minX; x < maxX; ++x) {
            for(int y = minY; y < maxY; ++y) {
               for(int z = minZ; z < maxZ; ++z) {
                  IBlockState block = this.world.getBlockState(new BlockPos(x, y, z));
                  if (block.getMaterial() == Material.WATER) {
                     double d0 = (double)((float)(y + 1) - BlockLiquid.getLiquidHeightPercent(((Integer)block.getValue(BlockLiquid.LEVEL)).intValue()));
                     if (this.posY <= d0) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private void handleWaterMovement() {
      dummyEntity.motionX = this.motionX;
      dummyEntity.motionY = this.motionY;
      dummyEntity.motionZ = this.motionZ;
      double scale = (double)(this.particleScale / 20.0F);
      if (this.world.handleMaterialAcceleration(this.getBoundingBox().expand(0.0D, -0.4000000059604645D, 0.0D).contract(0.001D, 0.001D, 0.001D), Material.WATER, dummyEntity)) {
         this.motionX = dummyEntity.motionX;
         this.motionY = dummyEntity.motionY;
         this.motionZ = dummyEntity.motionZ;
      }

   }

   public void move(double x, double y, double z) {
      double X = x;
      double Y = y;
      double Z = z;
      List<AxisAlignedBB> list = this.world.getCollisionBoxes((Entity)null, this.getBoundingBox().expand(x, y, z));

      for(AxisAlignedBB axisalignedbb : list) {
         y = axisalignedbb.calculateYOffset(this.getBoundingBox(), y);
      }

      this.setBoundingBox(this.getBoundingBox().offset(0.0D, y, 0.0D));

      for(AxisAlignedBB axisalignedbb : list) {
         x = axisalignedbb.calculateXOffset(this.getBoundingBox(), x);
      }

      this.setBoundingBox(this.getBoundingBox().offset(x, 0.0D, 0.0D));

      for(AxisAlignedBB axisalignedbb : list) {
         z = axisalignedbb.calculateZOffset(this.getBoundingBox(), z);
      }

      this.setBoundingBox(this.getBoundingBox().offset(0.0D, 0.0D, z));
      this.resetPositionToBB();
      this.onGround = y != Y && Y < 0.0D;
      if (!FBP.lowTraction && !FBP.bounceOffWalls) {
         if (x != X) {
            this.motionX *= 0.699999988079071D;
         }

         if (z != Z) {
            this.motionZ *= 0.699999988079071D;
         }
      }

   }

   public void renderParticle(BufferBuilder buf, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (!FBP.isEnabled() && this.particleMaxAge != 0) {
         this.particleMaxAge = 0;
      }

      if (FBPKeyBindings.FBPSweep.isKeyDown() && !this.killToggle) {
         this.killToggle = true;
      }

      float f = 0.0F;
      float f1 = 0.0F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      float f4 = (float)(this.prevParticleScale + ((double)this.particleScale - this.prevParticleScale) * (double)partialTicks);
      if (this.particleTexture != null) {
         if (!FBP.cartoonMode) {
            f = this.particleTexture.getInterpolatedU((double)(this.particleTextureJitterX / 4.0F * 16.0F));
            f2 = this.particleTexture.getInterpolatedV((double)(this.particleTextureJitterY / 4.0F * 16.0F));
         }

         f1 = this.particleTexture.getInterpolatedU((double)((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
         f3 = this.particleTexture.getInterpolatedV((double)((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
      } else {
         f = ((float)this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
         f1 = f + 0.015609375F;
         f2 = ((float)this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
         f3 = f2 + 0.015609375F;
      }

      float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
      float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
      float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      this.par = new Vec2f[]{new Vec2f(f1, f3), new Vec2f(f1, f2), new Vec2f(f, f2), new Vec2f(f, f3)};
      float alpha = (float)(this.prevParticleAlpha + ((double)this.particleAlpha - this.prevParticleAlpha) * (double)partialTicks);
      if (FBP.restOnFloor) {
         f6 += f4 / 10.0F;
      }

      FBPVector3d smoothRot = new FBPVector3d(0.0D, 0.0D, 0.0D);
      if (FBP.rotationMult > 0.0D) {
         smoothRot.y = this.rot.y;
         smoothRot.z = this.rot.z;
         if (!FBP.randomRotation) {
            smoothRot.x = this.rot.x;
         }

         if (!FBP.frozen) {
            FBPVector3d vec = this.rot.partialVec(this.prevRot, partialTicks);
            if (FBP.randomRotation) {
               smoothRot.y = vec.y;
               smoothRot.z = vec.z;
            } else {
               smoothRot.x = vec.x;
            }
         }
      }

      int j = i >> 16 & '\uffff';
      int k = i & '\uffff';
      float[] var10000 = new float[]{f1, f3, f1, f2, f, f2, f, f3};
      FBPRenderUtil.renderCubeShaded_S(buf, this.par, f5, f6, f7, (double)(f4 / 10.0F), smoothRot, i >> 16 & '\uffff', i & '\uffff', this.particleRed, this.particleGreen, this.particleBlue, alpha, FBP.cartoonMode);
   }

   private void createRotationMatrix() {
      double rx0 = FBP.random.nextDouble();
      double ry0 = FBP.random.nextDouble();
      double rz0 = FBP.random.nextDouble();
      this.rotStep = new FBPVector3d(rx0 > 0.5D ? 1.0D : -1.0D, ry0 > 0.5D ? 1.0D : -1.0D, rz0 > 0.5D ? 1.0D : -1.0D);
      this.rot.copyFrom(this.rotStep);
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender(float partialTicks) {
      AxisAlignedBB box = this.getBoundingBox();
      if (this.world.isBlockLoaded(new BlockPos(this.posX, 0.0D, this.posZ))) {
         double d0 = (box.maxY - box.minY) * 0.66D;
         double k = this.posY + d0 + 0.01D - (double)(FBP.restOnFloor ? this.particleScale / 10.0F : 0.0F);
         return this.world.getCombinedLight(new BlockPos(this.posX, k, this.posZ), 0);
      } else {
         return 0;
      }
   }

   private void calculateYAngle() {
      double angleSin = Math.toDegrees(Math.asin(this.motionX / Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ)));
      if (this.motionX > 0.0D) {
         if (this.motionZ > 0.0D) {
            this.rot.y = -angleSin;
         } else {
            this.rot.y = angleSin;
         }
      } else if (this.motionZ > 0.0D) {
         this.rot.y = -angleSin;
      } else {
         this.rot.y = angleSin;
      }

   }

   double getMult() {
      return Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) * (double)(FBP.randomRotation ? 200 : 500) * FBP.rotationMult;
   }

   @SideOnly(Side.CLIENT)
   public static class Factory implements IParticleFactory {
      public Factory() {
      }

      public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
         return (new FBPParticleDigging(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, -1.0F, 1.0F, 1.0F, 1.0F, Block.getStateById(p_178902_15_[0]), (EnumFacing)null, (TextureAtlasSprite)null)).init();
      }
   }
}
