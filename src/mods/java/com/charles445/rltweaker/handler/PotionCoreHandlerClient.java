package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.reflect.PotionCoreClientReflect;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PotionCoreHandlerClient {
   boolean isOverpoweredArmorBarLoaded = Loader.isModLoaded("overpoweredarmorbar");
   boolean isToughnessBarLoaded = Loader.isModLoaded("toughnessbar");
   PotionCoreClientReflect reflector;
   Minecraft mc = Minecraft.getMinecraft();
   @Nullable
   public PotionCoreHandlerClient.OverloadedCompatibilityHandler overloadedHandler;

   public PotionCoreHandlerClient() {
      try {
         this.reflector = new PotionCoreClientReflect();
         CompatUtil.wrapSpecificHandler("PCRenderOverlaysPre", (x$0) -> {
            new PotionCoreHandlerClient.PCRenderOverlaysPre(x$0);
         }, "com.tmtravlr.potioncore.PotionCoreEventHandlerClient", "renderOverlaysPre");
         CompatUtil.wrapSpecificHandler("PCRenderOverlaysPost", (x$0) -> {
            new PotionCoreHandlerClient.PCRenderOverlaysPost(x$0);
         }, "com.tmtravlr.potioncore.PotionCoreEventHandlerClient", "renderOverlaysPost");
         if (this.isOverpoweredArmorBarLoaded) {
            this.overloadedHandler = new PotionCoreHandlerClient.OverloadedCompatibilityHandler();
         }
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup PotionCoreHandlerClient!", var2);
         ErrorUtil.logSilent("PotionCore Client Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public class OverloadedCompatibilityHandler {
      @Nullable
      public Field f_offset = null;

      public OverloadedCompatibilityHandler() {
         try {
            Class c_ModConfig = Class.forName("locusway.overpoweredarmorbar.ModConfig");
            this.f_offset = ReflectUtil.findField(c_ModConfig, "offset");
         } catch (Exception var3) {
            RLTweaker.logger.error("Failed to setup OverloadedCompatibilityHandler ModConfig", var3);
            ErrorUtil.logSilent("PotionCore Overloaded ModConfig");
         }

         MinecraftForge.EVENT_BUS.register(this);
      }

      private boolean getOverloadedOffset() throws IllegalArgumentException, IllegalAccessException {
         return this.f_offset != null ? this.f_offset.getBoolean((Object)null) : false;
      }

      @SubscribeEvent(
         receiveCanceled = true,
         priority = EventPriority.LOW
      )
      public void renderOverlaysAny(RenderGameOverlayEvent event) {
         if (event.getType() == ElementType.ARMOR) {
            if (ModConfig.client.potioncore.overloadedArmorBarCompatibility) {
               double magicShielding = PotionCoreHandlerClient.this.mc.player.getEntityAttribute(PotionCoreHandlerClient.this.reflector.MAGIC_SHIELDING).getAttributeValue();
               if (magicShielding > 0.0D) {
                  int width = Math.min(82, MathHelper.floor(magicShielding * 4.0D + 2.0D));
                  if (width == 82) {
                     width = 83;
                  }

                  if (width > 2) {
                     try {
                        ScaledResolution scale = event.getResolution();
                        int left = scale.getScaledWidth() / 2 - 92;
                        int top = scale.getScaledHeight() - 40;
                        boolean offset = this.getOverloadedOffset();
                        float health = (float)PotionCoreHandlerClient.this.mc.player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
                        if (!offset && health > 20.0F) {
                           health = 20.0F;
                        }

                        float absorption = (float)MathHelper.ceil(PotionCoreHandlerClient.this.mc.player.getAbsorptionAmount());
                        if (!offset && absorption > 20.0F) {
                           absorption = 20.0F;
                        }

                        int healthbars = (int)Math.ceil((double)(health / 20.0F)) + (int)Math.ceil((double)(absorption / 20.0F));
                        int bump = Math.max(12 - healthbars, 3);
                        top = top - (healthbars - 1) * bump - 10;
                        GlStateManager.enableBlend();
                        GlStateManager.pushMatrix();
                        PotionCoreHandlerClient.this.mc.getTextureManager().bindTexture(PotionCoreHandlerClient.this.reflector.ARMOR_MAGIC_SHIELD);
                        PotionCoreHandlerClient.this.reflector.drawEnchantmentGlint((double)left, (double)top, -120.0D, (double)width, 11.0D);
                        GlStateManager.popMatrix();
                        GlStateManager.disableBlend();
                        PotionCoreHandlerClient.this.mc.getTextureManager().bindTexture(Gui.ICONS);
                        GlStateManager.color(1.0F, 1.0F, 1.0F);
                     } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var13) {
                        ErrorUtil.logSilent("PotionCore Overloaded Glint Invocation");
                     }
                  }
               }
            }

         }
      }
   }

   @SideOnly(Side.CLIENT)
   public class PCRenderOverlaysPost {
      private IEventListener handler;

      public PCRenderOverlaysPost(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void renderOverlaysPost(Post event) {
         if (event.getType() != ElementType.ARMOR) {
            this.handler.invoke(event);
         } else {
            if (ModConfig.client.potioncore.renderArmorIcons) {
               this.handler.invoke(event);
               if (ModConfig.client.potioncore.magicShieldingHUDFix) {
                  GlStateManager.color(1.0F, 1.0F, 1.0F);
               }
            }

         }
      }
   }

   @SideOnly(Side.CLIENT)
   public class PCRenderOverlaysPre {
      private IEventListener handler;

      public PCRenderOverlaysPre(IEventListener handler) {
         this.handler = handler;
         MinecraftForge.EVENT_BUS.register(this);
      }

      @SubscribeEvent
      public void renderOverlaysPre(Pre event) {
         if (event.getType() != ElementType.ARMOR) {
            this.handler.invoke(event);
         } else {
            if (ModConfig.client.potioncore.renderArmorResistance) {
               this.handler.invoke(event);
            }

         }
      }
   }
}
