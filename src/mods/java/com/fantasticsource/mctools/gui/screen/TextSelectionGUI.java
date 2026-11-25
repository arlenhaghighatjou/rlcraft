package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class TextSelectionGUI extends GUIScreen {
   public final String title;

   public TextSelectionGUI(GUIText clickedElement, String title, String... options) {
      this(clickedElement, title, 1.0D, options);
   }

   public TextSelectionGUI(final GUIText clickedElement, String title, double textScale, String... options) {
      super(textScale);
      this.title = title;
      if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) {
         GUIScreen.showStacked(this);
      } else {
         Minecraft.getMinecraft().displayGuiScreen(this);
      }

      this.drawStack = false;
      GUINavbar navbar = new GUINavbar(this);
      GUIList list = new GUIList(this, false, 0.98D, 1.0D - navbar.height, new GUIElement[0]) {
         public GUIElement[] newLineDefaultElements() {
            GUIText text = new GUIText(this.screen, clickedElement.getText());
            return new GUIElement[]{text.addClickActions(new Runnable[]{() -> {
               clickedElement.setText(text.getText());
               this.screen.close();
            }})};
         }
      };
      GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.02D, 1.0D, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, list);
      this.root.addAll(new GUIElement[]{new GUIDarkenedBackground(this), navbar.addRecalcActions(new Runnable[]{() -> {
         list.height = 1.0D - navbar.height;
         scrollbar.height = 1.0D - navbar.height;
      }}), list, scrollbar});

      for(String option : options) {
         GUIText text = (GUIText)list.addLine().getLineElement(0);
         text.setText(option);
         if (option.equals(clickedElement.getText())) {
            text.setColor(getIdleColor(Color.PURPLE), getHoverColor(Color.PURPLE), Color.PURPLE);
         } else {
            text.setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
         }
      }

   }

   public String title() {
      return this.title;
   }
}
