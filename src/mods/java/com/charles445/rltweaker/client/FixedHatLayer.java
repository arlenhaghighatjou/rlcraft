package com.charles445.rltweaker.client;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FixedHatLayer implements LayerRenderer<EntityLivingBase> {
   protected final RenderLivingBase<?> livingEntityRenderer;
   public static Class c_CapabilityHatContainer;
   public static Field f_CapabilityHatContainer_Companion;
   public static Object o_CapabilityHatContainer_Companion;
   public static Class c_Companion;
   public static Method m_Companion_getCapability;
   public static Class c_IHatContainer;
   public static Method m_IHatContainer_getEquipped;
   public static Field f_RenderLivingBase_layerRenderers;
   static boolean loaded = false;

   public FixedHatLayer(RenderLivingBase<?> livingEntityRendererIn) {
      this.livingEntityRenderer = livingEntityRendererIn;
   }

   public static void init() {
      if (isLoaded()) {
         removeOldLayers();
      }

      if (isLoaded()) {
         for(RenderPlayer renderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            renderer.addLayer(new FixedHatLayer(renderer));
         }
      }

   }

   public static boolean isLoaded() {
      return loaded;
   }

   public static void removeOldLayers() {
      for(RenderPlayer renderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
         try {
            List<LayerRenderer<EntityLivingBase>> layerRenderers = (List)f_RenderLivingBase_layerRenderers.get(renderer);
            Iterator<LayerRenderer<EntityLivingBase>> it = layerRenderers.iterator();

            while(it.hasNext()) {
               LayerRenderer<EntityLivingBase> layer = (LayerRenderer)it.next();
               if (layer.toString().contains("wiresegal.classyhats.client.render.PlayerLayerRendererHat")) {
                  it.remove();
                  RLTweaker.logger.debug("Removed hat layer");
               }
            }
         } catch (Exception var5) {
            RLTweaker.logger.error("Failed to remove old layers in FixedHatLayer!", var5);
            loaded = false;
            return;
         }
      }

   }

   @Nullable
   private Object getCapability(EntityPlayer player) {
      try {
         return m_Companion_getCapability.invoke(o_CapabilityHatContainer_Companion, player);
      } catch (Exception var3) {
         return null;
      }
   }

   @Nullable
   private Object getEquipped(Object container) {
      try {
         return m_IHatContainer_getEquipped.invoke(container);
      } catch (Exception var3) {
         return null;
      }
   }

   public void doRenderLayer(EntityLivingBase player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (isLoaded()) {
         if (player instanceof EntityPlayer) {
            Object container = this.getCapability((EntityPlayer)player);
            if (container != null) {
               Object stackObj = this.getEquipped(container);
               if (stackObj instanceof ItemStack) {
                  ItemStack stack = (ItemStack)stackObj;
                  if (!stack.isEmpty()) {
                     GlStateManager.pushMatrix();
                     ModelBase modelBase = this.livingEntityRenderer.getMainModel();
                     if (modelBase instanceof ModelBiped) {
                        ModelRenderer bipedHead = ((ModelBiped)modelBase).bipedHead;
                        if (player.isSneaking()) {
                           GlStateManager.translate(0.0F, 0.2F, 0.0F);
                        }

                        bipedHead.postRender(0.0625F);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.translate(0.0F, -0.25F, 0.0F);
                        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                        GlStateManager.scale(0.625F, -0.625F, -0.625F);
                        Minecraft.getMinecraft().getItemRenderer().renderItem(player, stack, TransformType.HEAD);
                        GlStateManager.popMatrix();
                     }
                  }

               }
            }
         }
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }

   static {
      try {
         f_RenderLivingBase_layerRenderers = ReflectUtil.findFieldAny(RenderLivingBase.class, "layerRenderers", "layerRenderers");
         c_IHatContainer = Class.forName("wiresegal.classyhats.capability.data.IHatContainer");
         c_CapabilityHatContainer = Class.forName("wiresegal.classyhats.capability.CapabilityHatContainer");
         f_CapabilityHatContainer_Companion = ReflectUtil.findField(c_CapabilityHatContainer, "Companion");
         o_CapabilityHatContainer_Companion = f_CapabilityHatContainer_Companion.get((Object)null);
         c_Companion = Class.forName("wiresegal.classyhats.capability.CapabilityHatContainer$Companion");
         m_Companion_getCapability = ReflectUtil.findMethod(c_Companion, "getCapability");
         m_IHatContainer_getEquipped = ReflectUtil.findMethod(c_IHatContainer, "getEquipped");
         loaded = true;
      } catch (Exception var1) {
         RLTweaker.logger.error("Failed to reflect in FixedHatLayer!", var1);
         loaded = false;
      }

   }
}
