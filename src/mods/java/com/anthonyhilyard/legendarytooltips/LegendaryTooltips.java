package com.anthonyhilyard.legendarytooltips;

import com.anthonyhilyard.legendarytooltips.render.TooltipDecor;
import com.anthonyhilyard.legendarytooltips.util.ItemColor;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent.PostText;
import net.minecraftforge.client.event.RenderTooltipEvent.Pre;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Mod(
   modid = "legendarytooltips",
   name = "Legendary Tooltips",
   version = "1.1.9",
   acceptedMinecraftVersions = "[1.12.2]",
   clientSideOnly = true
)
@EventBusSubscriber(
   modid = "legendarytooltips",
   value = {Side.CLIENT}
)
public class LegendaryTooltips {
   @Instance("legendarytooltips")
   public LegendaryTooltips instance;
   public static final int STANDARD = -1;
   public static final int NUM_FRAMES = 16;
   private static ItemStack lastTooltipItem = null;

   public LegendaryTooltips() {
   }

   @EventHandler
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

   @SubscribeEvent
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

      if (net.minecraftforge.fml.common.Loader.isModLoaded("equipmentcompare")) {
         try {
            if (((Boolean)Class.forName("com.anthonyhilyard.legendarytooltips.compat.EquipmentCompareHandler").getMethod("comparisonsJustActivated").invoke((Object)null)).booleanValue()) {
               for(int i = 1; i < 10; ++i) {
                  TooltipDecor.resetTimer(i);
               }
            }
         } catch (Exception var3) {
            Loader.LOGGER.error(ExceptionUtils.getStackTrace(var3));
         }
      }

   }

   @SubscribeEvent
   public static void onPreTooltipEvent(Pre event) {
      int index = 0;
      if (net.minecraftforge.fml.common.Loader.isModLoaded("equipmentcompare")) {
         try {
            index = ((Integer)Class.forName("com.anthonyhilyard.legendarytooltips.compat.EquipmentCompareHandler").getMethod("getEventIndex", Event.class).invoke((Object)null, event)).intValue();
         } catch (Exception var3) {
            Loader.LOGGER.error(ExceptionUtils.getStackTrace(var3));
         }
      }

      TooltipDecor.setCachedLines(event.getLines(), index);
   }

   @SubscribeEvent
   public static void onTooltipColorEvent(net.minecraftforge.client.event.RenderTooltipEvent.Color event) {
      Integer[] borderColors = itemFrameColors(event.getStack(), new Integer[]{event.getBorderStart(), event.getBorderEnd(), event.getBackground()});
      TooltipDecor.setCurrentTooltipBorderStart(borderColors[0].intValue());
      TooltipDecor.setCurrentTooltipBorderEnd(borderColors[1].intValue());
      boolean comparison = false;
      if (net.minecraftforge.fml.common.Loader.isModLoaded("equipmentcompare")) {
         try {
            comparison = ((Boolean)Class.forName("com.anthonyhilyard.legendarytooltips.compat.EquipmentCompareHandler").getMethod("isComparisonEvent", Event.class).invoke((Object)null, event)).booleanValue();
         } catch (Exception var4) {
            Loader.LOGGER.error(ExceptionUtils.getStackTrace(var4));
         }
      }

      if (comparison) {
         event.setBorderStart(0);
         event.setBorderEnd(0);
      } else {
         event.setBorderStart(borderColors[0].intValue());
         event.setBorderEnd(borderColors[1].intValue());
      }

      event.setBackground(borderColors[2].intValue());
   }

   @SubscribeEvent
   public static void onPostTooltipEvent(PostText event) {
      boolean comparison = false;
      int index = 0;
      if (net.minecraftforge.fml.common.Loader.isModLoaded("equipmentcompare")) {
         try {
            comparison = ((Boolean)Class.forName("com.anthonyhilyard.legendarytooltips.compat.EquipmentCompareHandler").getMethod("isComparisonEvent", Event.class).invoke((Object)null, event)).booleanValue();
            index = ((Integer)Class.forName("com.anthonyhilyard.legendarytooltips.compat.EquipmentCompareHandler").getMethod("getEventIndex", Event.class).invoke((Object)null, event)).intValue();
         } catch (Exception var4) {
            Loader.LOGGER.error(ExceptionUtils.getStackTrace(var4));
         }
      }

      if (LegendaryTooltipsConfig.INSTANCE.tooltipShadow) {
         if (comparison) {
            TooltipDecor.drawShadow(event.getX(), event.getY() - 11, event.getWidth(), event.getHeight() + 11);
         } else {
            TooltipDecor.drawShadow(event.getX(), event.getY(), event.getWidth(), event.getHeight());
         }
      }

      if (comparison) {
         TooltipDecor.drawBorder(event.getX(), event.getY() - 11, event.getWidth(), event.getHeight() + 11, event.getStack(), event.getLines(), event.getFontRenderer(), LegendaryTooltipsConfig.INSTANCE.getFrameLevelForItem(event.getStack()), comparison, index);
      } else {
         TooltipDecor.drawBorder(event.getX(), event.getY(), event.getWidth(), event.getHeight(), event.getStack(), event.getLines(), event.getFontRenderer(), LegendaryTooltipsConfig.INSTANCE.getFrameLevelForItem(event.getStack()), comparison, index);
      }

   }
}
