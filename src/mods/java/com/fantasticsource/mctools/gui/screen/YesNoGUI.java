package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.tools.datastructures.Color;

public class YesNoGUI extends GUIScreen {
   public final String title;
   public boolean pressedYes;

   public YesNoGUI(String title, String message) {
      this(title, message, 1.0D);
   }

   public YesNoGUI(String title, String message, double textScale) {
      super(textScale);
      this.pressedYes = false;
      this.title = title;
      this.show();
      this.drawStack = false;
      this.root.addAll(new GUIElement[]{new GUIDarkenedBackground(this), new GUINavbar(this)});
      this.root.setSubElementAutoplaceMethod((byte)8);
      this.root.addAll(new GUIElement[]{new GUITextSpacer(this), new GUIText(this, message, Color.PURPLE), new GUITextSpacer(this), (new GUITextButton(this, "YES", Color.GREEN)).addClickActions(new Runnable[]{() -> {
         this.pressedYes = true;
         this.close();
      }}), new GUITextSpacer(this), (new GUITextButton(this, "NO", Color.RED)).addClickActions(new Runnable[]{this::close})});
   }

   public String title() {
      return this.title;
   }
}
