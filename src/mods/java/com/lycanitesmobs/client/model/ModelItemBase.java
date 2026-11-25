package com.lycanitesmobs.client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.animation.ModelPartAnimation;
import com.lycanitesmobs.client.obj.ObjObject;
import com.lycanitesmobs.client.obj.TessellatorModel;
import com.lycanitesmobs.client.obj.VBOModel;
import com.lycanitesmobs.client.renderer.IItemModelRenderer;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public abstract class ModelItemBase implements IAnimationModel {
   public static float modelXRotOffset = 180.0F;
   public static float modelYPosOffset = -1.5F;
   public TessellatorModel wavefrontObject;
   public List<ObjObject> wavefrontParts;
   public Map<String, ModelObjPart> animationParts = new HashMap();
   protected Animator animator;
   protected ModelAnimation animation;
   protected ModelObjPart currentAnimationPart;
   protected Map<ItemStack, ModelObjState> modelStates = new HashMap();
   protected ModelObjState currentModelState;

   public ModelItemBase() {
   }

   public ModelItemBase initModel(String name, ModInfo groupInfo, String path) {
      this.wavefrontObject = new VBOModel(new ResourceLocation(groupInfo.modid, "models/" + path + ".obj"));
      this.wavefrontParts = this.wavefrontObject.objObjects;
      if (this.wavefrontParts.isEmpty()) {
         LycanitesMobs.logWarning("", "Unable to load any parts for the " + name + " model!");
      }

      this.animator = new Animator();
      ResourceLocation animPartsLoc = new ResourceLocation(groupInfo.modid, "models/" + path + "_parts.json");

      try {
         Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
         InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(animPartsLoc).getInputStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));

         try {
            JsonArray jsonArray = (JsonArray)JsonUtils.fromJson(gson, reader, JsonArray.class);
            Iterator<JsonElement> jsonIterator = jsonArray.iterator();

            while(jsonIterator.hasNext()) {
               JsonObject partJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
               ModelObjPart animationPart = new ModelObjPart();
               animationPart.loadFromJson(partJson);
               this.addAnimationPart(animationPart);
            }
         } finally {
            IOUtils.closeQuietly(reader);
         }
      } catch (Exception var25) {
         LycanitesMobs.logWarning("", "There was a problem loading animation parts for " + name + ":");
         var25.printStackTrace();
      }

      for(ModelObjPart part : this.animationParts.values()) {
         part.addChildren((ModelObjPart[])this.animationParts.values().toArray(new ModelObjPart[this.animationParts.size()]));
      }

      ResourceLocation animationLocation = new ResourceLocation(groupInfo.modid, "models/" + path + "_animation.json");

      try {
         Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
         InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(animationLocation).getInputStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));

         try {
            JsonObject json = (JsonObject)JsonUtils.fromJson(gson, reader, JsonObject.class);
            this.animation = new ModelAnimation();
            this.animation.loadFromJson(json);
         } finally {
            IOUtils.closeQuietly(reader);
         }
      } catch (Exception var23) {
         LycanitesMobs.logWarning("Model", "Unable to load animation json for " + name + ".");
      }

      return this;
   }

   public void addAnimationPart(ModelObjPart animationPart) {
      if (this.animationParts.containsKey(animationPart.name)) {
         LycanitesMobs.logWarning("", "Tried to add an animation part that already exists: " + animationPart.name + ".");
      } else {
         if (animationPart.parentName != null && animationPart.parentName.equals(animationPart.name)) {
            animationPart.parentName = null;
         }

         this.animationParts.put(animationPart.name, animationPart);
      }
   }

   public void addCustomLayers(IItemModelRenderer renderer) {
      if (this.animation != null) {
         this.animation.addItemLayers(renderer);
      }

   }

   public void render(ItemStack itemStack, EnumHand hand, IItemModelRenderer renderer, ModelObjPart offsetObjPart, LayerItem layer, float loop, boolean animate) {
      if (itemStack != null) {
         if (layer == null && this.animation != null) {
            layer = this.animation.getBaseLayer(renderer);
         }

         renderer.bindItemTexture(this.getTexture(itemStack, layer));
         if (animate) {
            this.generateAnimationFrames(itemStack, layer, loop, offsetObjPart);
         }

         for(ObjObject part : this.wavefrontParts) {
            String partName = part.getName().toLowerCase();
            if (this.canRenderPart(partName, itemStack, layer)) {
               this.currentAnimationPart = (ModelObjPart)this.animationParts.get(partName);
               GlStateManager.pushMatrix();
               this.doAngle(modelXRotOffset, 1.0F, 0.0F, 0.0F);
               this.doTranslate(0.0F, modelYPosOffset, 0.0F);
               this.currentAnimationPart.applyAnimationFrames(this.animator);
               this.onRenderStart(layer, itemStack);
               this.wavefrontObject.renderGroup(part, this.getPartColor(partName, itemStack, layer, loop), this.getPartTextureOffset(partName, itemStack, layer, loop), (VertexFormat)null);
               this.onRenderFinish(layer, itemStack);
               GlStateManager.popMatrix();
            }
         }

         if (animate) {
            this.clearAnimationFrames();
         }

      }
   }

   public void onRenderStart(LayerItem layer, ItemStack itemStack) {
      if (!CreatureManager.getInstance().config.disableModelAlpha) {
         GlStateManager.enableBlend();
      }

      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      if (layer != null) {
         layer.onRenderStart(itemStack);
      }

   }

   public void onRenderFinish(LayerItem layer, ItemStack itemStack) {
      if (!CreatureManager.getInstance().config.disableModelAlpha) {
         GlStateManager.disableBlend();
      }

      if (layer != null) {
         layer.onRenderFinish(itemStack);
      }

   }

   public void generateAnimationFrames(ItemStack itemStack, LayerItem layer, float loop, ModelObjPart offsetObjPart) {
      for(ObjObject part : this.wavefrontParts) {
         String partName = part.getName().toLowerCase();
         if (this.canRenderPart(partName, itemStack, layer)) {
            this.currentAnimationPart = (ModelObjPart)this.animationParts.get(partName);
            this.animatePart(partName, itemStack, loop);
         }
      }

   }

   public void clearAnimationFrames() {
      for(ModelObjPart animationPart : this.animationParts.values()) {
         animationPart.animationFrames.clear();
      }

   }

   public boolean canRenderPart(String partName, ItemStack itemStack, LayerItem layer) {
      if (partName == null) {
         return false;
      } else {
         partName = partName.toLowerCase();
         return this.animationParts.containsKey(partName);
      }
   }

   public void animatePart(String partName, ItemStack itemStack, float loop) {
      if (this.animation != null) {
         for(ModelPartAnimation partAnimation : this.animation.partAnimations) {
            partAnimation.animatePart(this, partName, loop);
         }
      }

   }

   public ResourceLocation getTexture(ItemStack itemStack, LayerItem layer) {
      return null;
   }

   public Vector4f getPartColor(String partName, ItemStack itemStack, LayerItem layer, float loop) {
      return layer != null ? layer.getPartColor(partName, itemStack, loop) : new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public Vector2f getPartTextureOffset(String partName, ItemStack itemStack, LayerItem layer, float loop) {
      return layer != null ? layer.getTextureOffset(partName, itemStack, loop) : new Vector2f(0.0F, 0.0F);
   }

   public void doAngle(float rotation, float angleX, float angleY, float angleZ) {
      GL11.glRotatef(rotation, angleX, angleY, angleZ);
   }

   public void doRotate(float rotX, float rotY, float rotZ) {
      GL11.glRotatef(rotX, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(rotY, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(rotZ, 0.0F, 0.0F, 1.0F);
   }

   public void doTranslate(float posX, float posY, float posZ) {
      GL11.glTranslatef(posX, posY, posZ);
   }

   public void doScale(float scaleX, float scaleY, float scaleZ) {
      GL11.glScalef(scaleX, scaleY, scaleZ);
   }

   public void angle(float rotation, float angleX, float angleY, float angleZ) {
      this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame("angle", rotation, angleX, angleY, angleZ));
   }

   public void rotate(float rotX, float rotY, float rotZ) {
      this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame("rotate", 1.0F, rotX, rotY, rotZ));
   }

   public void translate(float posX, float posY, float posZ) {
      this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame("translate", 1.0F, posX, posY, posZ));
   }

   public void scale(float scaleX, float scaleY, float scaleZ) {
      this.currentAnimationPart.addAnimationFrame(new ModelObjAnimationFrame("scale", 1.0F, scaleX, scaleY, scaleZ));
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
