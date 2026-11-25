package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.screen.YesNoGUI;
import com.fantasticsource.tools.datastructures.Color;
import java.util.ArrayList;

public abstract class GUIList extends GUIScrollView {
   public static final Color AL_WHITE = Color.WHITE.copy().setAF(0.3F);
   public static final Color AL_BLACK = Color.BLACK.copy().setAF(0.3F);
   public final boolean editable;
   protected boolean confirmLineDeletion = false;

   public GUIList(GUIScreen screen, boolean editable, double width, double height, GUIElement... subElements) {
      super(screen, width, height, subElements);
      this.editable = editable;
      if (editable) {
         this.addAddLineLine();
      }

   }

   public GUIList(GUIScreen screen, boolean editable, double x, double y, double width, double height, GUIElement... subElements) {
      super(screen, x, y, width, height, subElements);
      this.editable = editable;
      if (editable) {
         this.addAddLineLine();
      }

   }

   public GUIList setConfirmLineDeletion(boolean confirmLineDeletion) {
      this.confirmLineDeletion = confirmLineDeletion;
      return this;
   }

   private void addAddLineLine() {
      GUIList.Line line = new GUIList.Line(this.screen, this.editable);
      line.add(new GUIElement(this.screen, 1.0D, 0.0D));
      line.add(GUIButton.newAddButton(this.screen)).addClickActions(this::addLine);
      this.add(line);
   }

   public GUIList.Line addLine() {
      return this.addLine(this.lineCount());
   }

   public GUIList.Line addLine(int index) {
      return this.addLine(index, this.newLineDefaultElements());
   }

   public GUIList.Line addLine(GUIElement... lineElements) {
      return this.addLine(this.lineCount(), lineElements);
   }

   public GUIList.Line addLine(int index, GUIElement... lineElements) {
      if (index > this.lineCount()) {
         throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Size: " + this.lineCount());
      } else {
         GUIList.Line line = new GUIList.Line(this.screen, this.editable, this.newLineBackgroundElement());
         line.add(new GUIElement(this.screen, 1.0D, 0.0D));
         if (this.editable) {
            GUIButton button = GUIButton.newAddButton(this.screen);
            line.add(button).addClickActions(() -> {
               for(int i = 0; i < this.size(); ++i) {
                  if (this.get(i).indexOf(button) != -1) {
                     this.addLine(i);
                     break;
                  }
               }

            });
            line.add(GUIButton.newRemoveButton(this.screen).addClickActions(new Runnable[]{() -> {
               if (!this.confirmLineDeletion) {
                  this.remove(line);
               } else {
                  YesNoGUI yesNoGUI = new YesNoGUI("Confirm Removal", "Are you sure you want to remove this line?");
                  yesNoGUI.addOnClosedActions(new Runnable[]{() -> {
                     if (yesNoGUI.pressedYes) {
                        this.remove(line);
                     }

                  }});
               }

            }}));
         }

         if (lineElements != null) {
            line.addAll(lineElements);
         }

         this.add(index, line);
         return line;
      }
   }

   public GUIList.Line getLineContaining(GUIElement containedElement) {
      for(GUIList.Line line : this.getLines()) {
         if (line.children.contains(containedElement)) {
            return line;
         }
      }

      return null;
   }

   public int getLineIndexContaining(GUIElement containedElement) {
      int i = 0;

      for(GUIList.Line line : this.getLines()) {
         if (line.children.contains(containedElement)) {
            return i;
         }

         ++i;
      }

      return -1;
   }

   public GUIList addAllLines(GUIElement[]... lines) {
      for(GUIElement[] line : lines) {
         this.addLine(line);
      }

      return this;
   }

   public GUIList.Line[] getLines() {
      GUIList.Line[] result = new GUIList.Line[this.lineCount()];
      System.arraycopy(this.children.toArray(new GUIList.Line[0]), 0, result, 0, result.length);
      return result;
   }

   public GUIList.Line getLine(int index) {
      return this.get(index);
   }

   public GUIList.Line getLastFilledLine() {
      return this.getLine(this.lineCount() - 1);
   }

   public final double buttonWidth() {
      GUIElement button = ((GUIElement)this.children.get(this.children.size() - 1)).get(1);
      return button.width;
   }

   public abstract GUIElement[] newLineDefaultElements();

   public GUIElement newLineBackgroundElement() {
      return new GUIGradient(this.screen, 1.0D, 1.0D, AL_BLACK, AL_BLACK, AL_WHITE, AL_WHITE);
   }

   public int lineCount() {
      return this.editable ? this.size() - 1 : this.size();
   }

   public GUIList.Line get(int index) {
      return (GUIList.Line)super.get(index);
   }

   public static class Line extends GUIAutocroppedView {
      public final boolean editable;

      public Line(GUIScreen screen, boolean editable) {
         this(screen, editable, 0.0D);
      }

      public Line(GUIScreen screen, boolean editable, double padding) {
         this(screen, editable, padding, (GUIElement)null);
      }

      public Line(GUIScreen screen, boolean editable, GUIElement background) {
         this(screen, editable, 0.0D, background);
      }

      public Line(GUIScreen screen, boolean editable, double padding, GUIElement background) {
         super(screen, padding, background);
         this.editable = editable;
      }

      public Line(GUIScreen screen, boolean editable, double x, double y) {
         this(screen, editable, x, y, 0.0D);
      }

      public Line(GUIScreen screen, boolean editable, double x, double y, double padding) {
         this(screen, editable, x, y, padding, (GUIElement)null);
      }

      public Line(GUIScreen screen, boolean editable, double x, double y, GUIElement background) {
         this(screen, editable, x, y, 0.0D, background);
      }

      public Line(GUIScreen screen, boolean editable, double x, double y, double padding, GUIElement background) {
         super(screen, x, y, padding, background);
         this.editable = editable;
      }

      public ArrayList<GUIElement> getLineElements() {
         int offset = this.editable ? 3 : 1;
         if (this.size() > 0 && this.get(0) == this.background) {
            ++offset;
         }

         if (this.size() <= offset) {
            return new ArrayList();
         } else {
            ArrayList<GUIElement> list = new ArrayList();

            for(int i = offset; i < this.children.size(); ++i) {
               list.add(this.get(i));
            }

            return list;
         }
      }

      public GUIElement getLineElement(int index) {
         return (GUIElement)this.getLineElements().get(index);
      }
   }
}
