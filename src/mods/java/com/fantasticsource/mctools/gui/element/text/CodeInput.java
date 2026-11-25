package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import java.util.ArrayList;
import net.minecraft.nbt.NBTBase;

public class CodeInput extends GUIScrollView {
   public final double scale;
   public Color color;
   public Color hoverColor;
   public Color activeColor;
   public Color highlightColor;
   protected int cursorX;
   protected int selectionStartY;

   public CodeInput(GUIScreen screen, double width, double height, String... lines) {
      this(screen, width, height, 1.0D, lines);
   }

   public CodeInput(GUIScreen screen, double width, double height, double scale, String... lines) {
      this(screen, width, height, GUIScreen.getIdleColor(Color.WHITE), GUIScreen.getHoverColor(Color.WHITE), Color.WHITE, Color.WHITE.copy().setAF(0.4F), scale, lines);
   }

   public CodeInput(GUIScreen screen, double width, double height, Color color, Color hoverColor, Color activeColor, Color hightlightColor, String... lines) {
      this(screen, width, height, color, hoverColor, activeColor, hightlightColor, 1.0D, lines);
   }

   public CodeInput(GUIScreen screen, double width, double height, Color color, Color hoverColor, Color activeColor, Color hightlightColor, double scale, String... lines) {
      super(screen, width, height);
      this.selectionStartY = -1;
      this.scale = scale;
      this.color = color;
      this.hoverColor = hoverColor;
      this.activeColor = activeColor;
      this.highlightColor = hightlightColor;
      if (lines.length == 0) {
         this.add("");
      } else {
         for(String line : lines) {
            this.add(line);
         }
      }

      this.cursorX = ((GUITextInput)this.children.get(0)).text.length();
   }

   public CodeInput(GUIScreen screen, double x, double y, double width, double height, String... lines) {
      this(screen, x, y, width, height, 1.0D, lines);
   }

   public CodeInput(GUIScreen screen, double x, double y, double width, double height, double scale, String... lines) {
      this(screen, x, y, width, height, GUIScreen.getIdleColor(Color.WHITE), GUIScreen.getHoverColor(Color.WHITE), Color.WHITE, Color.WHITE.copy().setAF(0.4F), scale, lines);
   }

   public CodeInput(GUIScreen screen, double x, double y, double width, double height, Color color, Color hoverColor, Color activeColor, Color hightlightColor, String... lines) {
      this(screen, x, y, width, height, color, hoverColor, activeColor, hightlightColor, 1.0D, lines);
   }

   public CodeInput(GUIScreen screen, double x, double y, double width, double height, Color color, Color hoverColor, Color activeColor, Color hightlightColor, double scale, String... lines) {
      super(screen, x, y, width, height);
      this.selectionStartY = -1;
      this.scale = scale;
      this.color = color;
      this.hoverColor = hoverColor;
      this.activeColor = activeColor;
      this.highlightColor = hightlightColor;
      if (lines.length == 0) {
         this.add("");
      } else {
         for(String line : lines) {
            this.add(line);
         }
      }

      this.cursorX = ((GUITextInput)this.children.get(0)).text.length();
   }

   public ArrayList<String> getCodeLines() {
      ArrayList<String> result = new ArrayList();

      for(GUIElement element : this.children) {
         result.add(((GUITextInput)element).getText());
      }

      return result;
   }

   public ArrayList<String> getTrimmedCodeLines() {
      ArrayList<String> result = new ArrayList();

      for(GUIElement element : this.children) {
         result.add(((GUITextInput)element).getText().trim());
      }

      return result;
   }

   public String getCodeAsCompressedString() {
      StringBuilder builder = new StringBuilder();

      for(GUIElement element : this.children) {
         builder.append(((GUITextInput)element).getText().trim());
      }

      return builder.toString();
   }

   public void setCode(ArrayList<String> code) {
      this.children.clear();

      for(String line : code) {
         this.add(line);
      }

   }

   public void setCode(String code) {
      this.children.clear();

      for(String line : Tools.fixedSplit(code, "\n")) {
         this.add(line.replaceAll("\r", ""));
      }

   }

   public void setCode(NBTBase compound) {
      this.setCode(MCTools.legibleNBT(compound));
   }

   public GUIElement recalc(int subIndexChanged) {
      this.internalHeight = 0.0D;
      GUIElement prev = null;

      for(GUIElement element : this.children) {
         element.recalc(0);
         if (prev == null) {
            element.y = 0.0D;
         } else {
            element.y = prev.y + prev.height;
         }

         prev = element;
         this.internalHeight = Tools.max(this.internalHeight, element.y + element.height);
      }

      this.recalc2();
      this.postRecalc();
      return this;
   }

   public GUIElement add(GUIElement element) {
      if (!(element instanceof GUITextInput)) {
         throw new IllegalArgumentException("Multiline text inputs can only have text inputs added to them!");
      } else {
         return this.add(this.children.size(), element);
      }
   }

   public GUIElement add(int index, GUIElement element) {
      if (!(element instanceof GUITextInput)) {
         throw new IllegalArgumentException("Multiline text inputs can only have text inputs added to them!");
      } else {
         return super.add(index, element);
      }
   }

   public GUIElement add(String s) {
      return this.add(this.children.size(), s);
   }

   public GUIElement add(int index, String s) {
      GUIElement newElement;
      if (index == 0) {
         newElement = new GUITextInput(this.screen, 0.0D, 0.0D, s, FilterNone.INSTANCE, this.activeColor, this.scale);
      } else {
         GUIElement element = (GUIElement)this.children.get(index - 1);
         newElement = new GUITextInput(this.screen, 0.0D, element.y + (element.height + 1.0D / (double)this.screen.height) / this.height, s, FilterNone.INSTANCE, this.activeColor, this.scale);
      }

      for(int i = index; i < this.children.size(); ++i) {
         GUIElement element = (GUIElement)this.children.get(i);
         element.y += (1.0D / (double)this.screen.height + newElement.height) / this.height;
      }

      return super.add(index, newElement);
   }

   public void keyTyped(char typedChar, int keyCode) {
      for(GUIElement element : this.children) {
         if (element.isActive()) {
            element.keyTyped(typedChar, keyCode);
            break;
         }
      }

   }
}
