package com.lycanitesmobs.client.model;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.obj.ObjObject;
import com.lycanitesmobs.client.obj.TessellatorModel;
import com.lycanitesmobs.client.obj.VBOModel;
import com.lycanitesmobs.client.renderer.layer.LayerCreatureBase;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.client.model.obj.OBJModel.MaterialLibrary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ModelObjOld extends ModelCustom {
   public static float modelXRotOffset = 180.0F;
   public static float modelYPosOffset = -1.5F;
   public TessellatorModel wavefrontObject;
   public List<ObjObject> wavefrontParts;
   public Map<String, float[]> partCenters;
   public Map<String, float[]> partSubCenters;
   public Map<String, float[]> offsets;
   public boolean lockHeadX;
   public boolean lockHeadY;
   public boolean bodyIsTrophy;
   public float trophyScale;
   public float[] trophyOffset;
   public float[] trophyMouthOffset;
   public boolean dontColor;

   public ModelObjOld() {
      this(1.0F);
   }

   public ModelObjOld(float shadowSize) {
      this.partCenters = new HashMap();
      this.partSubCenters = new HashMap();
      this.offsets = new HashMap();
      this.lockHeadX = false;
      this.lockHeadY = false;
      this.bodyIsTrophy = true;
      this.trophyScale = 1.0F;
      this.trophyOffset = new float[0];
      this.trophyMouthOffset = new float[0];
      this.dontColor = false;
   }

   public static IModel loadModel(ResourceLocation resourceLocation) {
      return new OBJModel(new MaterialLibrary(), resourceLocation);
   }

   public ModelObjOld initModel(String name, ModInfo groupInfo, String path) {
      CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(name);
      if (creatureInfo != null && !creatureInfo.enabled) {
         return this;
      } else {
         this.wavefrontObject = new VBOModel(new ResourceLocation(groupInfo.modid, "models/" + path + ".obj"));
         this.wavefrontParts = this.wavefrontObject.objObjects;
         if (this.wavefrontParts.isEmpty()) {
            LycanitesMobs.logWarning("", "Unable to load any parts for the " + name + " model!");
         }

         return this;
      }
   }

   public void render(Entity entity, float time, float distance, float loop, float lookY, float lookX, float scale, LayerCreatureBase layer, boolean animate) {
      boolean trophyModel = false;
      if (scale < 0.0F) {
         trophyModel = true;
         scale = -scale;
      } else {
         scale = scale * 16.0F;
         if (entity instanceof BaseCreatureEntity) {
            scale = (float)((double)scale * ((BaseCreatureEntity)entity).getRenderScale());
         }
      }

      if (entity instanceof BaseCreatureEntity) {
         BaseCreatureEntity creature = (BaseCreatureEntity)entity;
         if (creature.onlyRenderTicks >= 0.0F) {
            loop = creature.onlyRenderTicks;
         }
      }

      for(ObjObject part : this.wavefrontParts) {
         if (part.getName() != null) {
            String partName = part.getName().toLowerCase();
            boolean isTrophyPart = this.isTrophyPart(partName);
            if (this.bodyIsTrophy && partName.contains("body")) {
               isTrophyPart = true;
            }

            if (this.canRenderPart(partName, entity, layer, trophyModel) && (!trophyModel || isTrophyPart)) {
               GlStateManager.pushMatrix();
               GlStateManager.enableAlpha();
               this.rotate(modelXRotOffset, 1.0F, 0.0F, 0.0F);
               this.translate(0.0F, modelYPosOffset, 0.0F);
               if (this.isChild && !trophyModel) {
                  this.childScale(partName);
               }

               this.scale(scale, scale, scale);
               if (trophyModel) {
                  this.scale(this.trophyScale, this.trophyScale, this.trophyScale);
               }

               this.centerPart(partName);
               if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).hasPerchTarget()) {
                  distance = 0.0F;
               }

               this.animatePart(partName, (EntityLiving)entity, time, distance, loop, -lookY, lookX, scale);
               if (trophyModel) {
                  if (!partName.contains("head") && !partName.contains("body")) {
                     float[] mouthOffset = this.comparePartCenters(this.bodyIsTrophy ? "body" : "head", partName);
                     this.translate(mouthOffset[0], mouthOffset[1], mouthOffset[2]);
                     if (this.trophyMouthOffset.length >= 3) {
                        this.translate(this.trophyMouthOffset[0], this.trophyMouthOffset[1], this.trophyMouthOffset[2]);
                     }
                  }

                  if (partName.contains("head")) {
                     if (!partName.contains("left")) {
                        this.translate(-0.3F, 0.0F, 0.0F);
                        this.rotate(5.0F, 0.0F, 1.0F, 0.0F);
                     }

                     if (!partName.contains("right")) {
                        this.translate(0.3F, 0.0F, 0.0F);
                        this.rotate(-5.0F, 0.0F, 1.0F, 0.0F);
                     }
                  }

                  this.uncenterPart(partName);
                  if (this.trophyOffset.length >= 3) {
                     this.translate(this.trophyOffset[0], this.trophyOffset[1], this.trophyOffset[2]);
                  }
               }

               this.uncenterPart(partName);
               this.onRenderStart(layer, entity, trophyModel);
               this.wavefrontObject.renderGroup(part, this.getPartColor(partName, entity, layer, trophyModel, loop), this.getPartTextureOffset(partName, entity, layer, trophyModel, loop), (VertexFormat)null);
               this.onRenderFinish(layer, entity, trophyModel);
               GlStateManager.popMatrix();
            }
         }
      }

   }

   public void onRenderStart(LayerCreatureBase layer, Entity entity, boolean renderAsTrophy) {
      if (!CreatureManager.getInstance().config.disableModelAlpha) {
         GlStateManager.enableBlend();
      }

      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      if (layer != null) {
         layer.onRenderStart(entity, renderAsTrophy);
      }

   }

   public void onRenderFinish(LayerCreatureBase layer, Entity entity, boolean renderAsTrophy) {
      if (!CreatureManager.getInstance().config.disableModelAlpha) {
         GlStateManager.disableBlend();
      }

      if (layer != null) {
         layer.onRenderFinish(entity, renderAsTrophy);
      }

   }

   public boolean isTrophyPart(String partName) {
      if (partName == null) {
         return false;
      } else {
         partName = partName.toLowerCase();
         return partName.contains("head") || partName.contains("mouth") || partName.contains("eye");
      }
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      float pi = 3.1415927F;
      float posX = 0.0F;
      float posY = 0.0F;
      float posZ = 0.0F;
      float angleX = 0.0F;
      float angleY = 0.0F;
      float angleZ = 0.0F;
      float rotation = 0.0F;
      float rotX = 0.0F;
      float rotY = 0.0F;
      float rotZ = 0.0F;
      if (partName.toLowerCase().contains("head")) {
         if (!this.lockHeadX) {
            rotX = (float)((double)rotX + Math.toDegrees((double)(lookX / 57.295776F)));
         }

         if (!this.lockHeadY) {
            rotY = (float)((double)rotY + Math.toDegrees((double)(lookY / 57.295776F)));
         }
      }

      this.rotate(rotation, angleX, angleY, angleZ);
      this.rotate(rotX, rotY, rotZ);
      this.translate(posX, posY, posZ);
   }

   public void childScale(String partName) {
      this.scale(0.5F, 0.5F, 0.5F);
   }

   public void rotate(float rotX, float rotY, float rotZ) {
      GL11.glRotatef(rotX, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(rotY, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(rotZ, 0.0F, 0.0F, 1.0F);
   }

   public void rotate(float rotation, float angleX, float angleY, float angleZ) {
      GL11.glRotatef(rotation, angleX, angleY, angleZ);
   }

   public void translate(float posX, float posY, float posZ) {
      GL11.glTranslatef(posX, posY, posZ);
   }

   public void scale(float scaleX, float scaleY, float scaleZ) {
      GL11.glScalef(scaleX, scaleY, scaleZ);
   }

   public void setPartCenter(String partName, float centerX, float centerY, float centerZ) {
      if (this.isTrophyPart(partName)) {
         this.bodyIsTrophy = false;
      }

      this.partCenters.put(partName, new float[]{centerX, centerY, centerZ});
   }

   public void setPartCenters(float centerX, float centerY, float centerZ, String... partNames) {
      for(String partName : partNames) {
         this.setPartCenter(partName, centerX, centerY, centerZ);
      }

   }

   public float[] getPartCenter(String partName) {
      return !this.partCenters.containsKey(partName) ? new float[]{0.0F, 0.0F, 0.0F} : (float[])this.partCenters.get(partName);
   }

   public void centerPart(String partName) {
      if (this.partCenters.containsKey(partName)) {
         float[] partCenter = (float[])this.partCenters.get(partName);
         this.translate(partCenter[0], partCenter[1], partCenter[2]);
      }
   }

   public void uncenterPart(String partName) {
      if (this.partCenters.containsKey(partName)) {
         float[] partCenter = (float[])this.partCenters.get(partName);
         this.translate(-partCenter[0], -partCenter[1], -partCenter[2]);
      }
   }

   public void centerPartToPart(String part, String targetPart) {
      this.uncenterPart(part);
      float[] partCenter = (float[])this.partCenters.get(targetPart);
      if (partCenter != null) {
         this.translate(partCenter[0], partCenter[1], partCenter[2]);
      }

   }

   public void uncenterPartToPart(String part, String targetPart) {
      float[] partCenter = (float[])this.partCenters.get(targetPart);
      if (partCenter != null) {
         this.translate(-partCenter[0], -partCenter[1], -partCenter[2]);
      }

      this.centerPart(part);
   }

   public float[] comparePartCenters(String centerPartName, String targetPartName) {
      float[] centerPart = this.getPartCenter(centerPartName);
      float[] targetPart = this.getPartCenter(targetPartName);
      float[] partDifference = new float[3];
      if (targetPart == null) {
         return partDifference;
      } else {
         for(int i = 0; i < 3; ++i) {
            partDifference[i] = targetPart[i] - centerPart[i];
         }

         return partDifference;
      }
   }

   public void setPartSubCenter(String partName, float centerX, float centerY, float centerZ) {
      this.partSubCenters.put(partName, new float[]{centerX, centerY, centerZ});
   }

   public void setPartSubCenters(float centerX, float centerY, float centerZ, String... partNames) {
      for(String partName : partNames) {
         this.setPartSubCenter(partName, centerX, centerY, centerZ);
      }

   }

   public void subCenterPart(String partName) {
      float[] offset = this.getSubCenterOffset(partName);
      if (offset != null) {
         this.translate(offset[0], offset[1], offset[2]);
      }
   }

   public void unsubCenterPart(String partName) {
      float[] offset = this.getSubCenterOffset(partName);
      if (offset != null) {
         this.translate(-offset[0], -offset[1], -offset[2]);
      }
   }

   public float[] getSubCenterOffset(String partName) {
      if (!this.partCenters.containsKey(partName)) {
         return null;
      } else if (!this.partSubCenters.containsKey(partName)) {
         return null;
      } else {
         float[] partCenter = (float[])this.partCenters.get(partName);
         float[] partSubCenter = (float[])this.partSubCenters.get(partName);
         float[] offset = new float[3];

         for(int coord = 0; coord < 3; ++coord) {
            offset[coord] = partSubCenter[coord] - partCenter[coord];
         }

         return offset;
      }
   }

   public void setOffset(String offsetName, float[] offset) {
      this.offsets.put(offsetName, offset);
   }

   public float[] getOffset(String offsetName) {
      return !this.offsets.containsKey(offsetName) ? new float[]{0.0F, 0.0F, 0.0F} : (float[])this.offsets.get(offsetName);
   }

   public double rotateToPoint(double aTarget, double bTarget) {
      return this.rotateToPoint(0.0D, 0.0D, aTarget, bTarget);
   }

   public double rotateToPoint(double aCenter, double bCenter, double aTarget, double bTarget) {
      if (aTarget - aCenter == 0.0D) {
         if (aTarget > aCenter) {
            return 0.0D;
         }

         if (aTarget < aCenter) {
            return 180.0D;
         }
      }

      if (bTarget - bCenter == 0.0D) {
         if (bTarget > bCenter) {
            return 90.0D;
         }

         if (bTarget < bCenter) {
            return -90.0D;
         }
      }

      return aTarget - aCenter == 0.0D && bTarget - bCenter == 0.0D ? 0.0D : Math.toDegrees(Math.atan2(aCenter - aTarget, bCenter - bTarget) - 1.5707963267948966D);
   }

   public double[] rotateToPoint(double xCenter, double yCenter, double zCenter, double xTarget, double yTarget, double zTarget) {
      double[] rotations = new double[3];
      rotations[0] = this.rotateToPoint(yCenter, -zCenter, yTarget, -zTarget);
      rotations[1] = this.rotateToPoint(-zCenter, xCenter, -zTarget, xTarget);
      rotations[2] = this.rotateToPoint(yCenter, xCenter, yTarget, xTarget);
      return rotations;
   }
}
