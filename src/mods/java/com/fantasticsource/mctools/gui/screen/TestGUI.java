package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIEllipse;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUILine;
import com.fantasticsource.mctools.gui.element.text.GUIFadingText;
import com.fantasticsource.mctools.gui.element.textured.GUIImage;
import com.fantasticsource.mctools.gui.element.view.GUIPanZoomView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickEmpty;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TestGUI extends GUIScreen {
   public static int test;

   public TestGUI() {
   }

   @SubscribeEvent
   public static void interactAir(RightClickEmpty event) {
      if (event.getSide() == Side.CLIENT && event.getHand() == EnumHand.MAIN_HAND) {
         Minecraft.getMinecraft().displayGuiScreen(new TestGUI());
      }

   }

   public String title() {
      return "Test";
   }

   protected void init() {
      switch(test) {
      default:
         test = 0;
      case 0:
         this.root.add(new GUIGradient(this, 0.0D, 0.0D, 1.0D, 1.0D, Color.BLACK));
         GUIPanZoomView view = new GUIPanZoomView(this, 1.0D, 1.0D, new GUIElement[0]);
         this.root.add(view);
         view.add(new GUIEllipse(this, 0.0D, 0.0D, 1.0D, 1.0D, 64, Color.GRAY));
         view.add(new GUILine(this, 0.0D, 0.0D, 1.0D, 1.0D, Color.WHITE));
         view.add(new GUILine(this, 0.0D, 1.0D, 1.0D, 0.0D, Color.RED));
         view.add(new GUILine(this, 0.0D, 0.0D, 1.0D, 0.0D, Color.GREEN));
         view.add(new GUILine(this, 0.0D, 0.0D, 0.0D, 1.0D, Color.BLUE));
         view.add(new GUIFadingText(this, 0.0D, 0.0D, "Test", 180, 240, Color.WHITE.copy().setAF(0.3F)));
         view.add((new GUIImage(this, 0.0D, 0.0D, 64.0D, 64.0D, new ResourceLocation("fantasticlib", "image/gui.png"))).ignoreMCGUIScale(true).setTooltip("Test"));
         view.add((new GUIImage(this, 0.5D, 0.0D, 64.0D, 64.0D, new ResourceLocation("fantasticlib", "image/gui.png"))).setTooltip("Test"));
         ++test;
      }
   }
}
