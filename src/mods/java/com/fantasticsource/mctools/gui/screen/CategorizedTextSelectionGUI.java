package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;

public class CategorizedTextSelectionGUI extends GUIScreen {
   protected final Color LIGHT_ORANGE;
   protected final Color[] LIGHT_ORANGES;
   protected final HashMap<GUIList.Line, GUIList.Line> categoryToFirstSubElement;
   public final String title;

   public CategorizedTextSelectionGUI(GUIText clickedElement, String title, LinkedHashMap<String, String[]> categorizedOptions) {
      this(clickedElement, title, 1.0D, categorizedOptions);
   }

   public CategorizedTextSelectionGUI(final GUIText clickedElement, String title, double textScale, LinkedHashMap<String, String[]> categorizedOptions) {
      super(textScale);
      this.LIGHT_ORANGE = Color.ORANGE.copy().setVF(0.8F);
      this.LIGHT_ORANGES = new Color[]{this.LIGHT_ORANGE, this.LIGHT_ORANGE.copy().setSF(0.5F), Color.WHITE};
      this.categoryToFirstSubElement = new HashMap();
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
            return new GUIElement[]{new GUIText(this.screen, clickedElement.getText())};
         }
      };
      GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.02D, 1.0D, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, list);
      this.root.addAll(new GUIElement[]{new GUIDarkenedBackground(this), navbar.addRecalcActions(new Runnable[]{() -> {
         list.height = 1.0D - navbar.height;
         scrollbar.height = 1.0D - navbar.height;
      }}), list, scrollbar});
      GUIList.Line focusLine = null;

      for(Entry<String, String[]> entry : categorizedOptions.entrySet()) {
         list.addLine();
         GUIList.Line line = list.addLine();
         GUIText text = (GUIText)line.getLineElement(0);
         text.setText((String)entry.getKey()).setColor(this.LIGHT_ORANGES[0], this.LIGHT_ORANGES[1], this.LIGHT_ORANGES[2]);
         text.addClickActions(new Runnable[]{() -> {
            if (((String[])entry.getValue()).length > 0) {
               int index = list.indexOf(line);
               if (list.indexOf((GUIElement)this.categoryToFirstSubElement.get(line)) == -1) {
                  List<String> options = Arrays.asList(entry.getValue());
                  Collections.reverse(options);

                  for(String option : options) {
                     GUIList.Line line2 = list.addLine(index + 1);
                     GUIText text2 = (GUIText)line2.getLineElement(0);
                     text2.setText(option);
                     text2.addClickActions(new Runnable[]{() -> {
                        clickedElement.setText(text2.getText());
                        this.close();
                     }});
                     if (option.equals(clickedElement.getText())) {
                        text2.setColor(getIdleColor(Color.PURPLE), getHoverColor(Color.PURPLE), Color.PURPLE);
                     } else {
                        text2.setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
                     }

                     line2.add(line2.indexOf(text2), new GUIText(this, "   "));
                  }

                  this.categoryToFirstSubElement.put(line, list.get(index + 1));
               } else {
                  for(int i = ((String[])entry.getValue()).length; i > 0; --i) {
                     list.remove(index + 1);
                  }
               }
            }

            this.recalc();
         }});
         line.add(line.indexOf(text), new GUIText(this, " "));
         if (((String[])entry.getValue()).length > 0 && Tools.contains(entry.getValue(), clickedElement.getText())) {
            focusLine = line;

            for(String option : (String[])entry.getValue()) {
               GUIList.Line line2 = list.addLine();
               GUIText text2 = (GUIText)line2.getLineElement(0);
               text2.setText(option);
               text2.addClickActions(new Runnable[]{() -> {
                  clickedElement.setText(text2.getText());
                  this.close();
               }});
               if (option.equals(clickedElement.getText())) {
                  text2.setColor(getIdleColor(Color.PURPLE), getHoverColor(Color.PURPLE), Color.PURPLE);
               } else {
                  text2.setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
               }

               line2.add(line2.indexOf(text2), new GUIText(this, "   "));
            }

            this.categoryToFirstSubElement.put(line, list.get(list.indexOf(line) + 1));
         }
      }

      list.addLine();
      if (focusLine != null) {
         list.focus(focusLine);
      }

   }

   public String title() {
      return this.title;
   }
}
