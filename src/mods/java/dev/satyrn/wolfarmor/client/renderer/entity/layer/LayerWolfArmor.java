package dev.satyrn.wolfarmor.client.renderer.entity.layer;

import com.google.common.collect.Maps;
import dev.satyrn.wolfarmor.WolfArmorMod;
import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import dev.satyrn.wolfarmor.client.model.ModelWolfArmor;
import dev.satyrn.wolfarmor.item.ItemWolfArmor;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.model.ModelBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerWolfArmor implements LayerRenderer<EntityWolf> {
   private final ModelBase[] armorLayers = new ModelBase[2];
   protected final RenderLiving<?> renderer;
   private static final Map<String, ResourceLocation> WOLF_ARMOR_TEXTURE_MAP = Maps.newHashMap();
   private float colorRed = 1.0F;
   private float colorGreen = 1.0F;
   private float colorBlue = 1.0F;
   private float alpha = 1.0F;
   private boolean shouldSkipArmorGlint;

   public LayerWolfArmor(@Nonnull RenderLiving renderer) {
      this.renderer = renderer;
      this.initArmor();
   }

   public void doRenderLayer(@Nonnull EntityWolf entityWolf, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (WolfArmorMod.getConfig().getArmorRendered()) {
         IArmoredWolf wolfArmor = (IArmoredWolf)entityWolf;
         ItemStack itemStack = wolfArmor.getArmorItemStack();
         if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemWolfArmor) {
            ItemWolfArmor armorItem = (ItemWolfArmor)itemStack.getItem();

            for(int layer = 0; layer < this.armorLayers.length; ++layer) {
               ModelBase model = this.armorLayers[layer];
               model.setModelAttributes(this.renderer.getMainModel());
               model.setLivingAnimations(entityWolf, limbSwing, limbSwingAmount, partialTicks);
               this.renderer.bindTexture(this.getArmorResource(itemStack, layer, false));
               int color = armorItem.getColor(itemStack);
               if (color != -1) {
                  float r = (float)(color >> 16 & 255) / 255.0F;
                  float g = (float)(color >> 8 & 255) / 255.0F;
                  float b = (float)(color & 255) / 255.0F;
                  GlStateManager.color(this.getColorRed() * r, this.getColorGreen() * g, this.getColorBlue() * b, this.getAlpha());
               } else {
                  GlStateManager.color(this.getColorRed(), this.getColorGreen(), this.getColorBlue(), this.getAlpha());
               }

               model.render(entityWolf, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
               GlStateManager.color(this.getColorRed(), this.getColorGreen(), this.getColorBlue(), this.getAlpha());
               if (armorItem.getHasOverlay(itemStack)) {
                  this.renderer.bindTexture(this.getArmorResource(itemStack, layer, true));
                  model.render(entityWolf, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
               }

               if (!this.getShouldSkipArmorGlint() && itemStack.hasEffect()) {
                  LayerArmorBase.renderEnchantedGlint(this.renderer, entityWolf, model, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
               }
            }
         }

      }
   }

   protected void initArmor() {
      ModelWolfArmor armorOuterLayer = new ModelWolfArmor(0.2F);
      ModelWolfArmor armorInnerLayer = new ModelWolfArmor(0.1F);
      this.setArmorOuterLayer(armorOuterLayer);
      this.setArmorInnerLayer(armorInnerLayer);
   }

   protected final void setArmorOuterLayer(ModelBase armorOuterLayer) {
      this.armorLayers[0] = armorOuterLayer;
   }

   protected final void setArmorInnerLayer(ModelBase armorInnerLayer) {
      this.armorLayers[1] = armorInnerLayer;
   }

   @Nullable
   public ModelBase getArmorOuterLayer() {
      return this.armorLayers.length >= 1 ? this.armorLayers[0] : null;
   }

   @Nullable
   public ModelBase getArmorInnerLayer() {
      return this.armorLayers.length >= 2 ? this.armorLayers[1] : null;
   }

   @Nonnull
   private ResourceLocation getArmorResource(@Nonnull ItemStack itemStack, int layer, boolean isOverlay) {
      ItemWolfArmor armor = (ItemWolfArmor)itemStack.getItem();
      String texture = armor.getMaterial().getName();
      String domain = "wolfarmor";
      int i = texture.indexOf(58);
      if (i > -1) {
         domain = texture.substring(0, i);
         texture = texture.substring(i);
      }

      String path = String.format("%s:textures/entity/wolf/armor/%s_layer_%d%s.png", domain, texture, layer + 1, isOverlay ? "_overlay" : "");
      ResourceLocation resource = (ResourceLocation)WOLF_ARMOR_TEXTURE_MAP.get(path);
      if (resource == null) {
         resource = new ResourceLocation(path);
         WOLF_ARMOR_TEXTURE_MAP.put(path, resource);
      }

      return resource;
   }

   private float getColorRed() {
      return this.colorRed;
   }

   private float getColorGreen() {
      return this.colorGreen;
   }

   private float getColorBlue() {
      return this.colorBlue;
   }

   private float getAlpha() {
      return this.alpha;
   }

   public boolean shouldCombineTextures() {
      return false;
   }

   protected boolean getShouldSkipArmorGlint() {
      return this.shouldSkipArmorGlint;
   }

   @Nonnull
   public LayerWolfArmor setColorRed(float colorRed) {
      this.colorRed = colorRed;
      return this;
   }

   @Nonnull
   public LayerWolfArmor setColorGreen(float colorGreen) {
      this.colorGreen = colorGreen;
      return this;
   }

   @Nonnull
   public LayerWolfArmor setColorBlue(float colorBlue) {
      this.colorBlue = colorBlue;
      return this;
   }

   @Nonnull
   public LayerWolfArmor setAlpha(float alpha) {
      this.alpha = alpha;
      return this;
   }

   @Nonnull
   public LayerWolfArmor setShouldSkipArmorGlint(boolean shouldSkipArmorGlint) {
      this.shouldSkipArmorGlint = shouldSkipArmorGlint;
      return this;
   }
}
