package com.oblivioussp.spartanhudbaubles.client.gui;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.oblivioussp.spartanhudbaubles.client.event.RenderBaubleOnHUDEvent;
import com.oblivioussp.spartanhudbaubles.util.Config;
import com.oblivioussp.spartanhudbaubles.util.SkinType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(
   modid = "spartanhudbaubles",
   value = {Side.CLIENT}
)
public class HudElementBaubles extends HudElement {
   private static HudElementBaubles ACTIVE = null;
   private static final ResourceLocation HUD = new ResourceLocation("spartanhudbaubles", "textures/gui/hud.png");
   private static final ResourceLocation BAUBLES_EXPANDED_INVENTORY = new ResourceLocation("baubles", "textures/gui/expanded_inventory.png");
   private static final int DEFAULT_WIDTH = 130;
   private static final int DEFAULT_HEIGHT = 22;
   private boolean isPreview;

   public HudElementBaubles() {
      super(130, 22);
      this.isPreview = false;
   }

   public HudElementBaubles(boolean enablePreview) {
      this();
      this.isPreview = enablePreview;
   }

   public void render() {
      List<ItemStack> stacksToRender = new ArrayList();
      if (!this.isPreview) {
         IBaublesItemHandler baublesHandler = BaublesApi.getBaublesHandler(Minecraft.getMinecraft().player);

         for(int i = 0; i < baublesHandler.getSlots(); ++i) {
            ItemStack checkStack = baublesHandler.getStackInSlot(i);
            if (!checkStack.isEmpty()) {
               stacksToRender.add(checkStack);
            }
         }
      }

      int slotsToRender = this.isPreview ? 7 : stacksToRender.size();
      Minecraft mc = Minecraft.getMinecraft();
      ScaledResolution res = new ScaledResolution(mc);
      if (this.isPreview || !stacksToRender.isEmpty() && !mc.player.isSpectator()) {
         GlStateManager.enableBlend();
         int x = this.getAlignedX(Config.hudAlignment, Config.hudOffsetX, res);
         int y = this.getAlignedY(Config.hudAlignment, Config.hudOffsetY, res);
         AlignmentHelper.HorizontalAlignment horizontal = Config.hudAlignment.getHorizontal();
         AlignmentHelper.VerticalAlignment vertical = Config.hudAlignment.getVertical();
         GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
         int startX = 3;
         int startY = 3;
         int spacing = 2;
         int length = (Config.hudRotateVertical ? startY : startX) * 2 + 16 * slotsToRender + spacing * (slotsToRender - 1);
         if (!Config.hudRotateVertical && length != this.width) {
            this.width = length;
            this.height = 22;
         }

         if (Config.hudRotateVertical && length != this.height) {
            this.height = length;
            this.width = 22;
         }

         if (Config.showBaublesIcon && Config.skinType != SkinType.NONE) {
            mc.getTextureManager().bindTexture(BAUBLES_EXPANDED_INVENTORY);
            int iconX = Config.hudRotateVertical ? x + 6 : (horizontal == AlignmentHelper.HorizontalAlignment.LEFT ? x + this.width + 1 : x - 11);
            int iconY = Config.hudRotateVertical ? (vertical == AlignmentHelper.VerticalAlignment.BOTTOM ? y - 11 : y + this.height + 1) : y + 6;
            gui.drawTexturedModalRect(iconX, iconY, 210, 48, 10, 10);
         }

         label83:
         switch(Config.skinType) {
         case HOTBAR_STYLE:
            mc.getTextureManager().bindTexture(HUD);
            switch(slotsToRender) {
            case 1:
               gui.drawTexturedModalRect(x, y, 0, 0, 22, 22);
               break label83;
            case 2:
               this.drawTextureRectFromRotation(x, y, 0, 24, 0, 21, 22, Config.hudRotateVertical);
               this.drawTextureRectFromRotation(x, y, 21, 135, 0, 19, 22, Config.hudRotateVertical);
               break label83;
            case 3:
               this.drawTextureRectFromRotation(x, y, 0, 24, 0, 39, 22, Config.hudRotateVertical);
               this.drawTextureRectFromRotation(x, y, 39, 135, 0, 19, 22, Config.hudRotateVertical);
               break label83;
            case 4:
               this.drawTextureRectFromRotation(x, y, 0, 24, 0, 39, 22, Config.hudRotateVertical);
               this.drawTextureRectFromRotation(x, y, 39, 117, 0, 37, 22, Config.hudRotateVertical);
               break label83;
            case 5:
               this.drawTextureRectFromRotation(x, y, 0, 24, 0, 57, 22, Config.hudRotateVertical);
               this.drawTextureRectFromRotation(x, y, 57, 117, 0, 37, 22, Config.hudRotateVertical);
               break label83;
            case 6:
               this.drawTextureRectFromRotation(x, y, 0, 24, 0, 57, 22, Config.hudRotateVertical);
               this.drawTextureRectFromRotation(x, y, 57, 99, 0, 55, 22, Config.hudRotateVertical);
               break label83;
            case 7:
               this.drawTextureRectFromRotation(x, y, 0, 24, 0, 130, 22, Config.hudRotateVertical);
            default:
               break label83;
            }
         case TOOLTIP_STYLE:
            gui.drawGradientRect(x + 1, y, x + this.width - 1, y + 1, Config.tooltipBackgroundColour, Config.tooltipBackgroundColour);
            gui.drawGradientRect(x + 1, y + this.height - 1, x + this.width - 1, y + this.height, Config.tooltipBackgroundColour, Config.tooltipBackgroundColour);
            gui.drawGradientRect(x, y + 1, x + 1, y + this.height - 1, Config.tooltipBackgroundColour, Config.tooltipBackgroundColour);
            gui.drawGradientRect(x + this.width - 1, y + 1, x + this.width, y + this.height - 1, Config.tooltipBackgroundColour, Config.tooltipBackgroundColour);
            gui.drawGradientRect(x + 1, y + 1, x + this.width - 1, y + this.height - 1, Config.tooltipBackgroundColour, Config.tooltipBackgroundColour);
            gui.drawGradientRect(x + 1, y + 1, x + this.width - 1, y + 2, Config.tooltipFrameTopColour, Config.tooltipFrameTopColour);
            gui.drawGradientRect(x + 1, y + this.height - 2, x + this.width - 1, y + this.height - 1, Config.tooltipFrameBottomColour, Config.tooltipFrameBottomColour);
            gui.drawGradientRect(x + 1, y + 2, x + 2, y + this.height - 2, Config.tooltipFrameTopColour, Config.tooltipFrameBottomColour);
            gui.drawGradientRect(x + this.width - 2, y + 2, x + this.width - 1, y + this.height - 2, Config.tooltipFrameTopColour, Config.tooltipFrameBottomColour);
            break;
         case MINIMAL:
            gui.drawGradientRect(x, y, x + this.width, y + this.height, Config.minimalTopColour, Config.minimalBottomColour);
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         RenderHelper.enableGUIStandardItemLighting();
         if (!this.isPreview) {
            for(int i = 0; i < stacksToRender.size(); ++i) {
               ItemStack stack = (ItemStack)stacksToRender.get(i);
               int offsetX = Config.hudRotateVertical ? x + startX : x + startX + (16 + spacing) * i;
               int offsetY = Config.hudRotateVertical ? y + startY + (16 + spacing) * i : y + startY;
               MinecraftForge.EVENT_BUS.post(new RenderBaubleOnHUDEvent.Pre(stack, offsetX, offsetY, Config.hudAlignment, Config.hudRotateVertical, res, gui, mc.fontRenderer));
               mc.getRenderItem().renderItemAndEffectIntoGUI(stack, offsetX, offsetY);
               mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, offsetX, offsetY, (String)null);
               MinecraftForge.EVENT_BUS.post(new RenderBaubleOnHUDEvent.Post(stack, offsetX, offsetY, Config.hudAlignment, Config.hudRotateVertical, res, gui, mc.fontRenderer));
            }
         } else {
            mc.getTextureManager().bindTexture(BAUBLES_EXPANDED_INVENTORY);
            new ItemStack(Blocks.BARRIER);

            for(int i = 0; i < slotsToRender; ++i) {
               int offsetX = Config.hudRotateVertical ? x + startX : x + startX + (16 + spacing) * i;
               int offsetY = Config.hudRotateVertical ? y + startY + (16 + spacing) * i : y + startY;
               gui.drawTexturedModalRect(offsetX + 3, offsetY + 3, 200, 48, 10, 10);
            }
         }

         RenderHelper.disableStandardItemLighting();
      }
   }

   private void drawTextureRectFromRotation(int x, int y, int offset, int textureU, int textureV, int width, int height, boolean rotate) {
      int texX = rotate ? x : x + offset;
      int texY = rotate ? y + offset : y;
      int texU = rotate ? textureV : textureU;
      int texV = rotate ? textureU : textureV;
      int texWidth = rotate ? height : width;
      int texHeight = rotate ? width : height;
      Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(texX, texY, texU, texV, texWidth, texHeight);
   }

   @SubscribeEvent
   public static void onHUDOverlay(Post ev) {
      Minecraft mc = Minecraft.getMinecraft();
      if (!Config.disableHUD && ev.getType() == ElementType.EXPERIENCE) {
         if (mc == null || mc.currentScreen != null && mc.currentScreen.getClass() == GuiPreviewHUD.class) {
            return;
         }

         if (ACTIVE == null) {
            ACTIVE = new HudElementBaubles();
         }

         ACTIVE.render();
      }

   }
}
