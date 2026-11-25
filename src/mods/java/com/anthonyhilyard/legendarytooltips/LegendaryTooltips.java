package com.anthonyhilyard.legendarytooltips;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.anthonyhilyard.legendarytooltips.render.TooltipDecor;
import com.anthonyhilyard.legendarytooltips.util.ItemColor;
import com.arlenh7.eaglercraft.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent.PostText;
import net.minecraftforge.client.event.RenderTooltipEvent.Pre;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
   modid = "legendarytooltips",
   name = "Legendary Tooltips",
   version = "1.1.9",
   acceptedMinecraftVersions = "[1.12.2]",
   clientSideOnly = true
)
public class LegendaryTooltips {
   public LegendaryTooltips instance;
   public static final int STANDARD = -1;
   public static final int NUM_FRAMES = 16;
   private static ItemStack lastTooltipItem = null;

   public LegendaryTooltips() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            com.anthonyhilyard.legendarytooltips.LegendaryTooltips.register(MinecraftForge.EVENT_BUS);
         }
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            com.anthonyhilyard.legendarytooltips.LegendaryTooltipsConfig.register(MinecraftForge.EVENT_BUS);
         }
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      LegendaryTooltipsConfig.loadConfig(event.getSuggestedConfigurationFile());
   }

   private static Integer[] itemFrameColors(ItemStack item, Integer[] defaults) {
      int frameLevel = LegendaryTooltipsConfig.INSTANCE.getFrameLevelForItem(item);
      if (frameLevel != -1) {
         int startColor = LegendaryTooltipsConfig.INSTANCE.getCustomBorderStartColor(frameLevel).intValue();
         int endColor = LegendaryTooltipsConfig.INSTANCE.getCustomBorderEndColor(frameLevel).intValue();
         int bgColor = LegendaryTooltipsConfig.INSTANCE.getCustomBackgroundColor(frameLevel).intValue();
         if (startColor == -1) {
            startColor = defaults[0].intValue();
         }

         if (endColor == -1) {
            endColor = defaults[1].intValue();
         }

         if (bgColor == -1) {
            bgColor = defaults[2].intValue();
         }

         return new Integer[]{startColor, endColor, bgColor};
      } else if (LegendaryTooltipsConfig.INSTANCE.bordersMatchRarity) {
         int rarityColor = ItemColor.getColorForItem(item, 16777215).intValue();
         float[] hsbVals = new float[3];
         Color.RGBtoHSB(rarityColor >> 16 & 255, rarityColor >> 8 & 255, rarityColor >> 0 & 255, hsbVals);
         boolean addHue = false;
         if (hsbVals[0] * 360.0F < 62.0F) {
            addHue = false;
         } else if (hsbVals[0] * 360.0F <= 240.0F) {
            addHue = true;
         }

         int startColor = Color.getHSBColor(addHue ? hsbVals[0] - 0.006F : hsbVals[0] + 0.006F, hsbVals[1], hsbVals[2]).getRGB();
         int endColor = Color.getHSBColor(addHue ? hsbVals[0] + 0.04F : hsbVals[0] - 0.04F, hsbVals[1], hsbVals[2]).getRGB();
         int bgColor = Color.getHSBColor(addHue ? hsbVals[0] - 0.0045F : hsbVals[0] + 0.0045F, hsbVals[1] * 0.85F, 0.0627451F).getRGB();
         return new Integer[]{startColor & -570425345, endColor & -1426063361, bgColor & -251658241};
      } else {
         return defaults;
      }
   }

   public static void register(EventBus bus) {
      bus.register(RenderTickEvent.class, LegendaryTooltips::onRenderTick);
      bus.register(Pre.class, LegendaryTooltips::onPreTooltipEvent);
      bus.register(net.minecraftforge.client.event.RenderTooltipEvent.Color.class, LegendaryTooltips::onTooltipColorEvent);
      bus.register(PostText.class, LegendaryTooltips::onPostTooltipEvent);
   }

   public static void onRenderTick(RenderTickEvent event) {
      if (event.phase == Phase.END) {
         TooltipDecor.updateTimers();
      }

      Minecraft mc = Minecraft.getMinecraft();
      if (mc.currentScreen != null && mc.currentScreen instanceof GuiContainer) {
         if (((GuiContainer)mc.currentScreen).getSlotUnderMouse() != null && ((GuiContainer)mc.currentScreen).getSlotUnderMouse().getHasStack()) {
            if (lastTooltipItem != ((GuiContainer)mc.currentScreen).getSlotUnderMouse().getStack()) {
               TooltipDecor.resetTimers();
               lastTooltipItem = ((GuiContainer)mc.currentScreen).getSlotUnderMouse().getStack();
            }
         } else {
            TooltipDecor.resetTimers();
         }
      }

   }

   public static void onPreTooltipEvent(Pre event) {
      TooltipDecor.setCachedLines(event.getLines(), 0);
   }

   public static void onTooltipColorEvent(net.minecraftforge.client.event.RenderTooltipEvent.Color event) {
      Integer[] borderColors = itemFrameColors(event.getStack(), new Integer[]{event.getBorderStart(), event.getBorderEnd(), event.getBackground()});
      TooltipDecor.setCurrentTooltipBorderStart(borderColors[0].intValue());
      TooltipDecor.setCurrentTooltipBorderEnd(borderColors[1].intValue());
      event.setBorderStart(borderColors[0].intValue());
      event.setBorderEnd(borderColors[1].intValue());
      event.setBackground(borderColors[2].intValue());
   }

   public static void onPostTooltipEvent(PostText event) {
      if (LegendaryTooltipsConfig.INSTANCE.tooltipShadow) {
         TooltipDecor.drawShadow(event.getX(), event.getY(), event.getWidth(), event.getHeight());
      }

      TooltipDecor.drawBorder(event.getX(), event.getY(), event.getWidth(), event.getHeight(), event.getStack(), event.getLines(), event.getFontRenderer(), LegendaryTooltipsConfig.INSTANCE.getFrameLevelForItem(event.getStack()), false, 0);
   }
}
