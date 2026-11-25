package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.datastructures.Color;
import java.util.ArrayList;

public class GUITextSpoiler extends GUIText {
   private boolean hidden;
   private ArrayList<GUIElement> hideableElements;

   public GUITextSpoiler(GUIScreen screen, String text, GUIElement... hideableElements) {
      this(screen, text, Color.WHITE, 1.0D, hideableElements);
   }

   public GUITextSpoiler(GUIScreen screen, String text, double scale, GUIElement... hideableElements) {
      this(screen, text, Color.WHITE, scale, hideableElements);
   }

   public GUITextSpoiler(GUIScreen screen, String text, Color color, GUIElement... hideableElements) {
      this(screen, text, color, color, color, 1.0D, hideableElements);
   }

   public GUITextSpoiler(GUIScreen screen, String text, Color color, double scale, GUIElement... hideableElements) {
      this(screen, text, color, color, color, scale, hideableElements);
   }

   public GUITextSpoiler(GUIScreen screen, String text, Color color, Color hoverColor, Color activeColor, GUIElement... hideableElements) {
      this(screen, text, color, hoverColor, activeColor, 1.0D, hideableElements);
   }

   public GUITextSpoiler(GUIScreen screen, String text, Color color, Color hoverColor, Color activeColor, double scale, GUIElement... hideableElements) {
      super(screen, text.charAt(text.length() - 1) == '\n' ? text : text + '\n', color, hoverColor, activeColor, scale);
      this.hidden = true;
      this.hideableElements = new ArrayList();

      for(GUIElement element : hideableElements) {
         this.add(element);
      }

   }

   public GUITextSpoiler(GUIScreen screen, double x, double y, String text, GUIElement... hideableElements) {
      this(screen, x, y, text, Color.WHITE, 1.0D, hideableElements);
   }

   public GUITextSpoiler(GUIScreen screen, double x, double y, String text, double scale, GUIElement... hideableElements) {
      this(screen, x, y, text, Color.WHITE, scale, hideableElements);
   }

   public GUITextSpoiler(GUIScreen screen, double x, double y, String text, Color color, GUIElement... hideableElements) {
      this(screen, x, y, text, color, color, color, 1.0D, hideableElements);
   }

   public GUITextSpoiler(GUIScreen screen, double x, double y, String text, Color color, double scale, GUIElement... hideableElements) {
      this(screen, x, y, text, color, color, color, scale, hideableElements);
   }

   public GUITextSpoiler(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor, GUIElement... hideableElements) {
      this(screen, x, y, text, color, hoverColor, activeColor, 1.0D, hideableElements);
   }

   public GUITextSpoiler(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor, double scale, GUIElement... hideableElements) {
      super(screen, x, y, text.charAt(text.length() - 1) == '\n' ? text : text + '\n', color, hoverColor, activeColor, scale);
      this.hidden = true;
      this.hideableElements = new ArrayList();

      for(GUIElement element : hideableElements) {
         this.add(element);
      }

   }

   public GUITextSpoiler add(GUIElement hideableElement) {
      if (this.hideableElements == null) {
         return null;
      } else {
         boolean reshow = !this.hidden;
         if (reshow) {
            this.hide();
         }

         this.hideableElements.add(hideableElement);
         if (reshow) {
            this.show();
         }

         return this;
      }
   }

   public GUIElement add(int index, GUIElement hideableElement) {
      if (this.hideableElements == null) {
         return null;
      } else {
         boolean reshow = !this.hidden;
         if (reshow) {
            this.hide();
         }

         this.hideableElements.add(index, hideableElement);
         if (reshow) {
            this.show();
         }

         return this;
      }
   }

   public void remove(GUIElement element) {
      if (this.hideableElements != null) {
         this.hideableElements.remove(element);
      }
   }

   public void remove(int index) {
      if (this.hideableElements != null) {
         this.hideableElements.remove(index);
      }
   }

   public int indexOf(GUIElement hideableElement) {
      return this.hideableElements == null ? -1 : this.hideableElements.indexOf(hideableElement);
   }

   public void clear() {
      if (this.hideableElements != null) {
         this.hideableElements.clear();
      }
   }

   public int size() {
      return this.hideableElements == null ? 0 : this.hideableElements.size();
   }

   public GUIElement get(int index) {
      return this.hideableElements == null ? null : (GUIElement)this.hideableElements.get(index);
   }

   public void show() {
      if (this.hidden) {
         this.hidden = false;
         int index = this.parent.indexOf(this);

         for(GUIElement element : this.hideableElements) {
            ++index;
            this.parent.add(index, element);
         }

      }
   }

   public void hide() {
      if (!this.hidden) {
         this.hidden = true;

         for(GUIElement element : this.hideableElements) {
            this.parent.remove(element);
         }

      }
   }

   public void toggle() {
      if (this.hidden) {
         this.show();
      } else {
         this.hide();
      }

   }

   public void click() {
      this.toggle();
      super.click();
   }
}
