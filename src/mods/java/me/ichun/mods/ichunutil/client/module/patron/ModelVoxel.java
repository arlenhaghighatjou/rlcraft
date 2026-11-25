package me.ichun.mods.ichunutil.client.module.patron;

import java.util.ArrayList;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import me.ichun.mods.ichunutil.client.entity.EntityLatchedRenderer;
import me.ichun.mods.ichunutil.common.core.tracker.EntityTrackerRegistry;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class ModelVoxel extends ModelBase {
   public EaglercraftRandom rand = new EaglercraftRandom();
   public ModelRenderer[] modelHead = new ModelRenderer[512];
   public ModelRenderer[] modelBody = new ModelRenderer[384];
   public ModelRenderer[] modelLimb = new ModelRenderer[192];
   public ModelRenderer[] modelLimbMirrored = new ModelRenderer[192];

   public ModelVoxel() {
      int sizeX = 8;
      int sizeY = 8;
      int sizeZ = 8;
      this.textureWidth = 48;
      this.textureHeight = 24;

      for(int i = 0; i < sizeX; ++i) {
         for(int j = 0; j < sizeY; ++j) {
            for(int k = 0; k < sizeZ; ++k) {
               if (i == 0 || i == sizeX - 1 || j == 0 || j == sizeY - 1 || k == 0 || k == sizeZ - 1) {
                  int x = j == sizeY - 1 && i != 0 && i != sizeX - 1 && k != 0 && k != sizeZ - 1 ? sizeX + sizeZ + i : (k == 0 ? sizeX + sizeZ + sizeX + sizeZ + i - 3 : (k == sizeZ - 1 ? sizeX + sizeZ + i - 1 : (k > 0 && i < sizeX / 2 ? sizeX + sizeZ + sizeX + sizeZ - 3 - k : sizeX - 1 + k)));
                  if (j == sizeY - 1 && i != 0 && i != sizeX - 1 && k != 0 && k != sizeZ - 1) {
                     int var31 = sizeZ - k;
                  } else {
                     int var10000 = sizeZ + sizeY - j - 2;
                  }

                  ModelRenderer pixel = new ModelRenderer(this, x, sizeZ + sizeY - j - 2);
                  this.modelHead[i + j * sizeX + k * sizeY * sizeX] = pixel;
                  pixel.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
               }
            }
         }
      }

      sizeX = 8;
      sizeY = 12;
      sizeZ = 4;

      for(int i = 0; i < sizeX; ++i) {
         for(int j = 0; j < sizeY; ++j) {
            for(int k = 0; k < sizeZ; ++k) {
               if (i == 0 || i == sizeX - 1 || j == 0 || j == sizeY - 1 || k == 0 || k == sizeZ - 1) {
                  int x = j == sizeY - 1 && i != 0 && i != sizeX - 1 && k != 0 && k != sizeZ - 1 ? sizeX + sizeZ + i : (k == 0 ? sizeX + sizeZ + sizeX + sizeZ + i - 3 : (k == sizeZ - 1 ? sizeX + sizeZ + i - 1 : (k > 0 && i < sizeX / 2 ? sizeX + sizeZ + sizeX + sizeZ - 3 - k : sizeX - 1 + k)));
                  if (j == sizeY - 1 && i != 0 && i != sizeX - 1 && k != 0 && k != sizeZ - 1) {
                     int var33 = sizeZ - k;
                  } else {
                     int var32 = sizeZ + sizeY - j - 2;
                  }

                  ModelRenderer pixel = new ModelRenderer(this, x, sizeZ + sizeY - j - 2);
                  this.modelBody[i + j * sizeX + k * sizeY * sizeX] = pixel;
                  pixel.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
               }
            }
         }
      }

      sizeX = 4;
      sizeY = 12;
      sizeZ = 4;

      for(int i = 0; i < sizeX; ++i) {
         for(int j = 0; j < sizeY; ++j) {
            for(int k = 0; k < sizeZ; ++k) {
               if (i == 0 || i == sizeX - 1 || j == 0 || j == sizeY - 1 || k == 0 || k == sizeZ - 1) {
                  int x = j == sizeY - 1 && i != 0 && i != sizeX - 1 && k != 0 && k != sizeZ - 1 ? sizeX + sizeZ + i : (k == 0 ? sizeX + sizeZ + sizeX + sizeZ + i - 3 : (k == sizeZ - 1 ? sizeX + sizeZ + i - 1 : (k > 0 && i < sizeX / 2 ? sizeX + sizeZ + sizeX + sizeZ - 3 - k : sizeX - 1 + k)));
                  if (j == sizeY - 1 && i != 0 && i != sizeX - 1 && k != 0 && k != sizeZ - 1) {
                     int var35 = sizeZ - k;
                  } else {
                     int var34 = sizeZ + sizeY - j - 2;
                  }

                  ModelRenderer pixel = new ModelRenderer(this, x, sizeZ + sizeY - j - 2);
                  this.modelLimb[i + j * sizeX + k * sizeY * sizeX] = pixel;
                  pixel.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
               }
            }
         }
      }

      for(int i = 0; i < sizeX; ++i) {
         for(int j = 0; j < sizeY; ++j) {
            for(int k = 0; k < sizeZ; ++k) {
               if (i == 0 || i == sizeX - 1 || j == 0 || j == sizeY - 1 || k == 0 || k == sizeZ - 1) {
                  int x = j == sizeY - 1 && i != 0 && i != sizeX - 1 && k != 0 && k != sizeZ - 1 ? sizeX + sizeZ + i : (k == 0 ? sizeX + sizeZ + sizeX + sizeZ + i - 3 : (k == sizeZ - 1 ? sizeX + sizeZ + i - 1 : (k > 0 && i < sizeX / 2 ? sizeX + sizeZ + sizeX + sizeZ - 3 - k : sizeX - 1 + k)));
                  if (j == sizeY - 1 && i != 0 && i != sizeX - 1 && k != 0 && k != sizeZ - 1) {
                     int var37 = sizeZ - k;
                  } else {
                     int var36 = sizeZ + sizeY - j - 2;
                  }

                  ModelRenderer pixel = new ModelRenderer(this, x, sizeZ + sizeY - j - 2);
                  pixel.mirror = true;
                  this.modelLimbMirrored[i + j * sizeX + k * sizeY * sizeX] = pixel;
                  pixel.addBox(-0.5F, -0.5F, -0.5F, 1, 1, 1);
               }
            }
         }
      }

   }

   public void renderPlayer(EntityLatchedRenderer trail, long time, int seedBase, ArrayList<EntityTrackerRegistry.EntityInfo> loc, double pX, double pY, double pZ, float f5, float renderTick, int[] skins) {
      GlStateManager.pushMatrix();
      GlStateManager.alphaFunc(516, 0.003921569F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.disableCull();
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);

      for(int j = 0; j < Math.min(loc.size(), 100); ++j) {
         EntityTrackerRegistry.EntityInfo info = (EntityTrackerRegistry.EntityInfo)loc.get(j);
         if (info.lastTick % 2L != 0L) {
            boolean canRender = false;
            PatronTracker info1 = (PatronTracker)info.getTracker(PatronTracker.class);
            if (info1 != null) {
               canRender = info1.canRender;
            }

            if (canRender) {
               float prog2 = MathHelper.clamp(((float)j + renderTick) / 100.0F, 0.0F, 1.0F);
               GlStateManager.pushMatrix();
               double tX = trail.prevPosX + (trail.posX - trail.prevPosX) * (double)renderTick;
               double tY = trail.prevPosY + (trail.posY - trail.prevPosY) * (double)renderTick;
               double tZ = trail.prevPosZ + (trail.posZ - trail.prevPosZ) * (double)renderTick;
               GlStateManager.translate(info.posX - tX + pX, info.posY - tY + pY, info.posZ - tZ + pZ);
               GlStateManager.scale(-1.0F, -1.0F, 1.0F);
               float scale = 0.9375F;
               GlStateManager.scale(scale, scale, scale);
               GlStateManager.translate(0.0F, -0.6825F, 0.0F);
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               this.rand.setSeed(info.lastTick + (long)seedBase);
               int i = this.rand.nextInt(6);
               if (skins == null) {
                  GlStateManager.disableTexture2D();
                  GlStateManager.color(0.7F, 0.7F, 0.7F, 0.8F);
               } else {
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F - 0.9F * MathHelper.clamp(((float)j + renderTick) / 100.0F, 0.0F, 1.0F));
               }

               if (skins != null) {
                  if (i < 2) {
                     GlStateManager.bindTexture(skins[0]);
                  } else if (i < 4) {
                     GlStateManager.bindTexture(skins[1]);
                  } else {
                     GlStateManager.bindTexture(skins[i - 2]);
                  }
               }

               this.renderLimb(j, i, info.sneaking, info.renderYawOffset, info.rotationYawHead, info.rotationPitch, info.limbSwing, info.limbSwingAmount, info1.yawChange * (float)j * (1.0F - 0.4F * prog2), info1.pitchChange * (float)j * (1.0F - 0.4F * prog2), f5, renderTick);
               GlStateManager.popMatrix();
            }
         }
      }

      GlStateManager.disableBlend();
      if (skins == null) {
         GlStateManager.enableTexture2D();
      }

      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.popMatrix();
   }

   public void renderLimb(int progInt, int limb, boolean sneaking, float renderYaw, float rotationYaw, float rotationPitch, float limbSwing, float limbSwingAmount, float yaw, float pitch, float f5, float renderTick) {
      GlStateManager.pushMatrix();
      float prog = MathHelper.clamp(((float)progInt + renderTick) / 100.0F, 0.0F, 1.0F);
      float shatterProg = 1.0F + 0.7F * prog;
      float properShatterProg = 1.0F - MathHelper.clamp((float)Math.pow((double)(1.0F - MathHelper.clamp((prog - 0.025F) / 0.2F, 0.0F, 1.0F)), 2.0D), 0.0F, 1.0F);
      rotationPitch = rotationPitch * shatterProg;
      if (limb != 5) {
         GlStateManager.rotate(renderYaw, 0.0F, 1.0F, 0.0F);
      } else {
         GlStateManager.rotate(rotationYaw, 0.0F, 1.0F, 0.0F);
      }

      float f8 = limbSwing - limbSwingAmount;
      int sizeX = 4;
      int sizeY = 12;
      int sizeZ = 4;
      float offsetX = 0.0F;
      float offsetY = 0.0F;
      float offsetZ = 0.0F;
      float spread = 0.65F;
      if (limb == 4) {
         sizeX = 8;
      } else if (limb == 5) {
         sizeX = 8;
         sizeY = 8;
         sizeZ = 8;
      }

      int i = 0;
      int j = 0;

      int k;
      for(k = 0; i != -(sizeX / 2) && i != sizeX / 2 - 1 && j != -sizeY && j != -1 && k != -(sizeZ / 2) && k != sizeZ / 2 - 1; k = this.rand.nextInt(sizeZ) - sizeZ / 2) {
         i = this.rand.nextInt(sizeX) - sizeX / 2;
         j = this.rand.nextInt(sizeY) - sizeY;
      }

      GlStateManager.pushMatrix();
      ModelRenderer[] list;
      if (limb < 4) {
         list = limb % 2 == 0 ? this.modelLimbMirrored : this.modelLimb;
      } else if (limb == 4) {
         list = this.modelBody;
      } else {
         list = this.modelHead;
      }

      ModelRenderer pixel = list[sizeX / 2 + i + (sizeY + j) * sizeX + (sizeZ / 2 + k) * sizeX * sizeY];
      pixel.rotationPointX = 0.0F;
      pixel.rotationPointY = 0.0F;
      pixel.rotationPointZ = 0.0F;
      if (limb == 0) {
         GlStateManager.translate(0.125F + (this.rand.nextFloat() - 0.5F) * spread * properShatterProg, 0.0F, 0.0F);
      } else if (limb == 1) {
         GlStateManager.translate(-0.125F + (this.rand.nextFloat() - 0.5F) * spread * properShatterProg, 0.0F, 0.0F);
      } else if (limb == 2) {
         GlStateManager.translate(0.25F + (this.rand.nextFloat() - 0.5F) * spread * properShatterProg, -0.625F * shatterProg, 0.0F);
         pixel.rotationPointX = 2.0F;
         pixel.rotationPointY = -2.0F * shatterProg;
      } else if (limb == 3) {
         GlStateManager.translate(-0.25F + (this.rand.nextFloat() - 0.5F) * spread * properShatterProg, -0.625F * shatterProg, 0.0F);
         pixel.rotationPointX = -2.0F;
         pixel.rotationPointY = -2.0F * shatterProg;
      } else if (limb == 4) {
         GlStateManager.translate(0.0F, -0.75F * shatterProg, 0.0F);
      } else if (limb == 5) {
         GlStateManager.translate(0.0F, sneaking ? -0.5625F : -0.75F * shatterProg, 0.0F);
         GlStateManager.rotate(rotationPitch, 1.0F, 0.0F, 0.0F);
         pixel.rotationPointY = -8.0F * shatterProg;
      }

      if (limb < 4) {
         if (limb == 0 || limb == 3) {
            GlStateManager.rotate((float)Math.toDegrees(Math.cos((double)(f8 * 0.6662F + 3.1415927F)) * 1.399999976158142D * (double)limbSwingAmount) * shatterProg, 1.0F, 0.0F, 0.0F);
         }

         if (limb == 1 || limb == 2) {
            GlStateManager.rotate((float)Math.toDegrees(Math.cos((double)(f8 * 0.6662F)) * 1.399999976158142D * (double)limbSwingAmount) * shatterProg, 1.0F, 0.0F, 0.0F);
         }
      }

      GlStateManager.translate(0.0F, 0.6875F * (1.0F - shatterProg) + this.rand.nextFloat() * 0.01F, 0.0F);
      GlStateManager.translate(-(offsetX + (float)i + 0.5F) / 16.0F + (this.rand.nextFloat() - 0.5F) * spread * properShatterProg, -(offsetY + (float)j + 0.5F) / 16.0F * shatterProg, -(offsetZ + (float)k + 0.5F) / 16.0F + (this.rand.nextFloat() - 0.5F) * spread * properShatterProg);
      pixel.rotateAngleX = pitch / 57.295776F;
      pixel.rotateAngleY = yaw / 57.295776F;
      double vScale = Math.pow(1.0D - (double)prog, 0.75D);
      GlStateManager.scale(vScale, vScale, vScale);
      pixel.render(f5);
      GlStateManager.popMatrix();
      GlStateManager.popMatrix();
   }
}
