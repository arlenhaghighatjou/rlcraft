package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class MessageGUI extends GUIScreen {
   public final String title;

   public MessageGUI(String title, String message) {
      this(title, message, 1.0D);
   }

   public MessageGUI(String title, String message, double textScale) {
      super(textScale);
      this.title = title;
      if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) {
         GUIScreen.showStacked(this);
      } else {
         Minecraft.getMinecraft().displayGuiScreen(this);
      }

      this.drawStack = false;
      GUINavbar navbar = new GUINavbar(this);
      this.root.setSubElementAutoplaceMethod((byte)8);
      this.root.addAll(new GUIElement[]{new GUIDarkenedBackground(this), navbar, new GUITextSpacer(this), new GUIText(this, message, Color.PURPLE), new GUITextSpacer(this), (new GUITextButton(this, "OK", Color.WHITE)).addClickActions(new Runnable[]{this::close})});
   }

   public String title() {
      return this.title;
   }
}
