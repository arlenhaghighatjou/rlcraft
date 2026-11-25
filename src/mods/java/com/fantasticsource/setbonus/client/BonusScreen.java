package com.fantasticsource.setbonus.client;

import com.fantasticsource.mctools.gui.GUIScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent.PostText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class BonusScreen extends GUIScreen {
   public static BonusScreen bonusScreen = new BonusScreen();
   private static long debounce = System.currentTimeMillis();
   public ItemStack stack = null;

   public BonusScreen() {
   }

   @SubscribeEvent
   public static void tooltip(PostText event) {
   }

   @SubscribeEvent
   public static void keyPress(KeyInputEvent event) {
   }

   public String title() {
      return "Bonus Screen";
   }

   protected void init() {
   }
}
