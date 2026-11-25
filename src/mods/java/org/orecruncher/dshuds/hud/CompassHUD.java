package org.orecruncher.dshuds.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dshuds.Environment;
import org.orecruncher.dshuds.ModBase;
import org.orecruncher.dshuds.ModOptions;
import org.orecruncher.lib.ForgeUtils;
import org.orecruncher.lib.ItemStackUtil;
import org.orecruncher.lib.Localization;
import org.orecruncher.lib.MinecraftClock;
import org.orecruncher.lib.PlayerUtils;
import org.orecruncher.lib.gui.TextPanel;
import org.orecruncher.lib.gui.Panel.Reference;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public class CompassHUD extends GuiOverlay {
   private static final int BAND_WIDTH = 65;
   private static final int BAND_HEIGHT = 12;
   private static final int ROSE_DIM = 256;
   private static final float TEXT_LINE_START = 1.5F;
   private static final Item COMPASS = ForgeUtils.getItem("minecraft:compass");
   private static final Item CLOCK = ForgeUtils.getItem("minecraft:clock");
   private static final Item SEASON_CLOCK = ForgeUtils.getItem("sereneseasons:season_clock");
   private final TextPanel textPanel = new TextPanel();
   private boolean showCompass = false;

   public CompassHUD() {
   }

   @Nonnull
   protected String getLocationString() {
      BlockPos pos = Environment.getPlayerPosition();
      return TextFormatting.AQUA + String.format(Locale.getDefault(), ModOptions.compassHUD.coordFormat, pos.getX(), pos.getY(), pos.getZ());
   }

   @Nonnull
   protected String getBiomeName() {
      return TextFormatting.GOLD + Environment.getBiomeName();
   }

   protected boolean showCompass() {
      return ModOptions.compassHUD.enable && PlayerUtils.isHolding(Environment.getPlayer(), COMPASS);
   }

   protected static boolean holdingOrItemFrameItem(Item item) {
      if (item == null) {
         return false;
      } else if (PlayerUtils.isHolding(Environment.getPlayer(), item)) {
         return true;
      } else {
         Entity e = PlayerUtils.entityImLookingAt(Environment.getPlayer());
         if (!(e instanceof EntityItemFrame)) {
            return false;
         } else {
            ItemStack stack = ((EntityItemFrame)e).getDisplayedItem();
            return ItemStackUtil.isValidItemStack(stack) && stack.getItem() == item;
         }
      }
   }

   protected boolean showClock() {
      return ModOptions.clockHUD.enable ? holdingOrItemFrameItem(CLOCK) : false;
   }

   protected boolean showSeason() {
      return ModOptions.clockHUD.enable ? holdingOrItemFrameItem(SEASON_CLOCK) : false;
   }

   public void doTick(int tickRef) {
      if (tickRef != 0 && tickRef % 4 == 0) {
         this.textPanel.resetText();
         List<String> text = new ArrayList();
         if (this.showCompass = this.showCompass()) {
            text.add(this.getLocationString());
            text.add(this.getBiomeName());
         }

         if (this.showSeason()) {
            text.add(Environment.getSeasonString());
         }

         if (this.showClock()) {
            if (text.size() > 0) {
               text.add("");
            }

            MinecraftClock clock = Environment.getClock();
            text.add(clock.getFormattedTime());
            text.add(clock.getTimeOfDay());
            long time = ModBase.proxy().currentSessionDuration();
            int elapsedHours = (int)(time / 3600000L);
            time = time - (long)(elapsedHours * 3600000);
            int elapsedMinutes = (int)(time / 60000L);
            time = time - (long)(elapsedMinutes * '\uea60');
            int elapsedSeconds = (int)(time / 1000L);
            text.add(Localization.format("dshuds.format.SessionTime", new Object[]{elapsedHours, elapsedMinutes, elapsedSeconds}));
         }

         if (text.size() > 0) {
            this.textPanel.setText(text);
         }
      }

   }

   public void doRender(@Nonnull Pre event) {
      if (event.getType() == ElementType.CROSSHAIRS && this.textPanel.hasText()) {
         Minecraft mc = Minecraft.getMinecraft();
         FontRenderer font = mc.fontRenderer;
         ScaledResolution resolution = event.getResolution();
         int centerX = (resolution.getScaledWidth() + 1) / 2;
         int centerY = (resolution.getScaledHeight() + 1) / 2;
         this.textPanel.setAlpha(ModOptions.compassHUD.transparency);
         this.textPanel.render(centerX, centerY + (int)((float)font.FONT_HEIGHT * 1.5F), Reference.TOP_CENTER);
         CompassHUD.Style style = CompassHUD.Style.getStyle(ModOptions.compassHUD.style);
         mc.getTextureManager().bindTexture(style.getTextureResource());
         if (this.showCompass) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, ModOptions.compassHUD.transparency);
            if (!style.isRose()) {
               int direction = MathStuff.floor((double)(mc.player.rotationYaw * 256.0F / 360.0F) + 0.5D) & 255;
               int x = (resolution.getScaledWidth() - style.getWidth() + 1) / 2;
               int y = (resolution.getScaledHeight() - style.getHeight() + 1) / 2 - style.getHeight();
               if (direction < 128) {
                  this.drawTexturedModalRect(x, y, direction, ModOptions.compassHUD.style * style.getHeight() * 2, style.getWidth(), style.getHeight());
               } else {
                  this.drawTexturedModalRect(x, y, direction - 128, ModOptions.compassHUD.style * style.getHeight() * 2 + style.getHeight(), style.getWidth(), style.getHeight());
               }
            } else {
               GlStateManager.pushMatrix();
               GlStateManager.translate((float)centerX, (float)centerY - 30.0F, 0.0F);
               GlStateManager.rotate(70.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotate(-MathStuff.wrapDegrees(mc.player.rotationYaw + 180.0F), 0.0F, 0.0F, 1.0F);
               int x = -(style.getWidth() + 1) / 2;
               int y = -(style.getHeight() + 1) / 2;
               this.drawTexturedModalRect(x, y, 0, 0, style.getWidth(), style.getHeight());
               GlStateManager.popMatrix();
            }
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   private static enum Style {
      BAND_0(false, "textures/compass.png", 65, 12),
      BAND_1(false, "textures/compass.png", 65, 12),
      BAND_2(false, "textures/compass.png", 65, 12),
      BAND_3(false, "textures/compass.png", 65, 12),
      ROSE_1(true, "textures/compassrose1.png", 256, 256),
      ROSE_2(true, "textures/compassrose2.png", 256, 256),
      ROSE_3(true, "textures/compassrose3.png", 256, 256);

      private final boolean isRose;
      private final ResourceLocation texture;
      private final int width;
      private final int height;

      private Style(boolean isRose, @Nonnull String texture, int w, int h) {
         this.isRose = isRose;
         this.texture = new ResourceLocation("dshuds", texture);
         this.width = w;
         this.height = h;
      }

      public boolean isRose() {
         return this.isRose;
      }

      public ResourceLocation getTextureResource() {
         return this.texture;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public static CompassHUD.Style getStyle(int index) {
         return index >= 0 && index < values().length ? values()[index] : BAND_0;
      }
   }
}
