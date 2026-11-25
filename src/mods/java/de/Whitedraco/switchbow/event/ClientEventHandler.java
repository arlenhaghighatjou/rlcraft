package de.Whitedraco.switchbow.event;

import de.Whitedraco.switchbow.EnchantInit;
import de.Whitedraco.switchbow.PotionInit;
import de.Whitedraco.switchbow.Config.SwitchBowConfig;
import de.Whitedraco.switchbow.Config.WriteLoadConfig;
import de.Whitedraco.switchbow.helper.ArrowItemStackEqual;
import de.Whitedraco.switchbow.helper.SwitchBowHelper;
import de.Whitedraco.switchbow.item.ItemSwitchBow;
import de.Whitedraco.switchbow.item.ItemSwitchCrossbow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientEventHandler {
   private static final ResourceLocation Love_Texture = new ResourceLocation("switchbow".toLowerCase(), "textures/gui/overlay/love.png");
   private static final ResourceLocation Scope_Texture = new ResourceLocation("switchbow".toLowerCase(), "textures/gui/overlay/scope.png");
   private int Cooldown = 120;
   ItemStack lastarrow = ItemStack.EMPTY;
   private double min = 35.0D;
   private float zoomFOV = Float.MAX_VALUE;
   private boolean doZoom = false;

   public ClientEventHandler() {
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onUpdateFOV(FOVUpdateEvent event) {
      float f = event.getFov();
      boolean flag = false;
      if (event.getEntity().getItemInUseCount() > 0 && EnchantmentHelper.getEnchantmentLevel(EnchantInit.ActivScope, event.getEntity().getActiveItemStack()) > 0) {
         f -= SwitchBowConfig.scopeZoom.getFloa();
         this.zoomFOV = f;
         this.doZoom = true;
         flag = true;
      } else {
         this.doZoom = false;
      }

      if (this.zoomFOV != Float.MAX_VALUE && !this.doZoom) {
         float actFOV = event.getFov();
         int factor = 5;
         if (this.zoomFOV + SwitchBowConfig.scopeZoom.getFloa() / (float)factor < actFOV) {
            f = this.zoomFOV + SwitchBowConfig.scopeZoom.getFloa() / (float)factor;
            this.zoomFOV = f;
            flag = true;
         } else {
            this.zoomFOV = Float.MAX_VALUE;
         }
      }

      if (flag) {
         event.setNewfov(f);
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void RenderSpecificHand(RenderSpecificHandEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      Item activeItem = null;
      if (mc.player.isHandActive()) {
         activeItem = mc.player.getActiveItemStack().getItem();
      }

      if (mc.player.getActiveHand() != EnumHand.MAIN_HAND || mc.player.getItemInUseCount() <= 0 || !(activeItem instanceof ItemSwitchCrossbow) && !(activeItem instanceof ItemSwitchBow)) {
         if (mc.player.getActiveHand() == EnumHand.OFF_HAND && mc.player.getItemInUseCount() > 0 && (activeItem instanceof ItemSwitchCrossbow || activeItem instanceof ItemSwitchBow) && event.getHand() == EnumHand.MAIN_HAND) {
            event.setCanceled(true);
         }
      } else if (event.getHand() == EnumHand.OFF_HAND) {
         event.setCanceled(true);
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void onRenderGameOverlay(RenderGameOverlayEvent event) {
      if (!event.isCancelable() && event.getType() == ElementType.EXPERIENCE) {
         Minecraft mc = Minecraft.getMinecraft();
         if (mc.gameSettings.thirdPersonView == 0 && !mc.player.isSpectator()) {
            if (mc.player.getItemInUseCount() > 0 && mc.player.getActiveItemStack().getItem() instanceof ItemBow && SwitchBowConfig.showScope.getBool() && EnchantmentHelper.getEnchantmentLevel(EnchantInit.ActivScope, mc.player.getActiveItemStack()) > 0) {
               ScaledResolution scaledRes = new ScaledResolution(mc);
               double x = (double)scaledRes.getScaledWidth() + 1.0D;
               double y = (double)scaledRes.getScaledHeight();
               double a;
               double b;
               double c;
               double d;
               if (x <= y && x != y) {
                  c = this.min;
                  b = x - c * 2.0D;
                  a = b;
                  d = (y - b) / 2.0D;
               } else {
                  d = this.min;
                  a = y - d * 2.0D;
                  b = a;
                  c = (x - a) / 2.0D;
               }

               GlStateManager.disableDepth();
               GlStateManager.depthMask(false);
               GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.disableAlpha();
               mc.getTextureManager().bindTexture(Scope_Texture);
               Tessellator tessellator = Tessellator.getInstance();
               BufferBuilder vertexbuffer = tessellator.getBuffer();
               vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
               vertexbuffer.pos(c, d + a, -90.0D).tex(0.0D, 1.0D).endVertex();
               vertexbuffer.pos(c + b, d + a, -90.0D).tex(1.0D, 1.0D).endVertex();
               vertexbuffer.pos(c + b, d, -90.0D).tex(1.0D, 0.0D).endVertex();
               vertexbuffer.pos(c, d, -90.0D).tex(0.0D, 0.0D).endVertex();
               tessellator.draw();
               GlStateManager.depthMask(true);
               GlStateManager.enableDepth();
               GlStateManager.enableAlpha();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            if (mc.player.isPotionActive(PotionInit.PotionLove)) {
               GlStateManager.disableDepth();
               GlStateManager.alphaFunc(519, 1.0F);
               GlStateManager.depthMask(false);
               GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.disableAlpha();
               mc.getTextureManager().bindTexture(Love_Texture);
               Tessellator tessellator = Tessellator.getInstance();
               BufferBuilder vertexbuffer = tessellator.getBuffer();
               vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
               ScaledResolution scaledRes = new ScaledResolution(mc);
               vertexbuffer.pos(0.0D, (double)scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
               vertexbuffer.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
               vertexbuffer.pos((double)scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
               vertexbuffer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
               tessellator.draw();
               GlStateManager.depthMask(true);
               GlStateManager.enableDepth();
               GlStateManager.enableAlpha();
               GlStateManager.alphaFunc(516, 0.1F);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            if (mc.player.getHeldItemMainhand() != ItemStack.EMPTY) {
               if (mc.player.getHeldItemMainhand().getItem() instanceof ItemSwitchBow || mc.player.getHeldItemMainhand().getItem() instanceof ItemSwitchCrossbow) {
                  ItemStack arrow = SwitchBowHelper.getSelectionArrow(mc.player.getHeldItemMainhand());
                  this.showArrowAndString(arrow, mc, event);
                  return;
               }

               if (mc.player.getHeldItemOffhand() != ItemStack.EMPTY && (mc.player.getHeldItemOffhand().getItem() instanceof ItemSwitchBow || mc.player.getHeldItemOffhand().getItem() instanceof ItemSwitchCrossbow)) {
                  ItemStack arrow = SwitchBowHelper.getSelectionArrow(mc.player.getHeldItemOffhand());
                  this.showArrowAndString(arrow, mc, event);
                  return;
               }
            }

            if (mc.player.isPotionActive(PotionInit.PotionLove)) {
               GlStateManager.disableDepth();
               GlStateManager.alphaFunc(519, 1.0F);
               GlStateManager.depthMask(false);
               GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.disableAlpha();
               mc.getTextureManager().bindTexture(Love_Texture);
               Tessellator tessellator = Tessellator.getInstance();
               BufferBuilder vertexbuffer = tessellator.getBuffer();
               vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
               ScaledResolution scaledRes = new ScaledResolution(mc);
               vertexbuffer.pos(0.0D, (double)scaledRes.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
               vertexbuffer.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
               vertexbuffer.pos((double)scaledRes.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
               vertexbuffer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
               tessellator.draw();
               GlStateManager.depthMask(true);
               GlStateManager.enableDepth();
               GlStateManager.enableAlpha();
               GlStateManager.alphaFunc(516, 0.1F);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            return;
         }
      }

   }

   private void showArrowAndString(ItemStack arrow, Minecraft mc, RenderGameOverlayEvent event) {
      int x = event.getResolution().getScaledWidth();
      int y = event.getResolution().getScaledHeight();
      if (arrow != ItemStack.EMPTY && arrow.getItem() != Item.getItemFromBlock(Blocks.AIR)) {
         if (SwitchBowConfig.arrowPicPosX.getInteger() == -99) {
            SwitchBowConfig.arrowPicPosX.setInteger(50000);
            WriteLoadConfig.rewriteConfigSwitchBow();
         }

         if (SwitchBowConfig.arrowPicPosY.getInteger() == -99) {
            SwitchBowConfig.arrowPicPosY.setInteger(50000);
            WriteLoadConfig.rewriteConfigSwitchBow();
         }

         mc.getRenderItem().renderItemIntoGUI(arrow, this.calculatePos(SwitchBowConfig.arrowPicPosX.getInteger(), x), this.calculatePos(SwitchBowConfig.arrowPicPosY.getInteger(), y));
         if (this.lastarrow != ItemStack.EMPTY && !ArrowItemStackEqual.containsArrow(arrow, this.lastarrow)) {
            this.Cooldown = 120;
         }

         if (this.Cooldown > 0) {
            if (SwitchBowConfig.arrowStringPosX.getInteger() == -99) {
               SwitchBowConfig.arrowStringPosX.setInteger((this.calculatePos(SwitchBowConfig.arrowPicPosX.getInteger(), x) + 18) * 100000 / x);
               WriteLoadConfig.rewriteConfigSwitchBow();
            }

            if (SwitchBowConfig.arrowStringPosY.getInteger() == -99) {
               SwitchBowConfig.arrowStringPosY.setInteger((this.calculatePos(SwitchBowConfig.arrowPicPosY.getInteger(), y) + 4) * 100000 / y);
               WriteLoadConfig.rewriteConfigSwitchBow();
            }

            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(arrow.getDisplayName(), (float)this.calculatePos(SwitchBowConfig.arrowStringPosX.getInteger(), x), (float)this.calculatePos(SwitchBowConfig.arrowStringPosY.getInteger(), y), 16777215);
         }

         --this.Cooldown;
         this.lastarrow = arrow;
      }
   }

   private int calculatePos(int factor, int format) {
      return format * factor / 100000;
   }
}
