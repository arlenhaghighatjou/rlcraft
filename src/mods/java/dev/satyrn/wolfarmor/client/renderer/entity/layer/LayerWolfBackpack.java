package dev.satyrn.wolfarmor.client.renderer.entity.layer;

import net.lax1dude.eaglercraft.v1_8.HString;
import net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums;
import com.google.common.collect.Maps;
import dev.satyrn.wolfarmor.WolfArmorMod;
import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import dev.satyrn.wolfarmor.client.model.ModelWolfBackpack;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.model.ModelBase;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerWolfBackpack implements LayerRenderer<EntityWolf> {
   protected ModelBase modelWolfBackpack;
   private final RenderLiving<?> renderer;
   private static final Map<String, ResourceLocation> WOLF_PACK_TEXTURE_MAP = Maps.newHashMap();

   public LayerWolfBackpack(@Nonnull RenderLiving<?> renderer) {
      this.renderer = renderer;
      this.modelWolfBackpack = new ModelWolfBackpack(0.0F);
   }

   public ModelBase getModelWolfBackpack() {
      return this.modelWolfBackpack;
   }

   public void doRenderLayer(@Nonnull EntityWolf entityWolf, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (WolfArmorMod.getConfig().getChestRendered()) {
         IArmoredWolf wolfArmor = (IArmoredWolf)entityWolf;
         if (wolfArmor.getHasChest()) {
            this.modelWolfBackpack.setModelAttributes(this.renderer.getMainModel());
            this.modelWolfBackpack.setLivingAnimations(entityWolf, limbSwing, limbSwingAmount, partialTicks);
            this.renderer.bindTexture(this.getPackTextureForItem(wolfArmor.getChestType()));
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if (!entityWolf.isInvisible()) {
               this.modelWolfBackpack.render(entityWolf, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            } else {
               GlStateManager.pushMatrix();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
               GlStateManager.depthMask(false);
               GlStateManager.enableBlend();
               GlStateManager.blendFunc(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA);
               this.modelWolfBackpack.render(entityWolf, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
               GlStateManager.depthMask(true);
               GlStateManager.disableBlend();
               GlStateManager.popMatrix();
            }
         }

      }
   }

   @Nonnull
   protected ResourceLocation getPackTextureForItem(@Nullable Item chestItem) {
      String chestType = null;
      if (chestItem instanceof ItemBlock) {
         ItemBlock block = (ItemBlock)chestItem;
         if (block.getBlock().equals(Blocks.ENDER_CHEST)) {
            chestType = "ender";
         }
      }

      String path = HString.format("%s:textures/entity/wolf/chest/%schest.png", "wolfarmor", chestType == null ? "" : chestType + "_");
      ResourceLocation resource = (ResourceLocation)WOLF_PACK_TEXTURE_MAP.get(path);
      if (resource == null) {
         resource = new ResourceLocation(path);
         WOLF_PACK_TEXTURE_MAP.put(path, resource);
      }

      return resource;
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
