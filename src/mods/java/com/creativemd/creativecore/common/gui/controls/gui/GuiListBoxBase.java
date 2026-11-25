package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import java.util.ArrayList;
import java.util.List;

public class GuiListBoxBase<T extends GuiControl> extends GuiScrollBox {
   protected List<T> content;
   protected List<GuiListBoxBase<T>.GuiButtonRemove> removeButtons;
   public final boolean modifiable;
   public int space = 2;

   public GuiListBoxBase(String name, int x, int y, int width, int height, boolean modifiable, List<T> entries) {
      super(name, x, y, width, height);
      this.content = entries;
      this.modifiable = modifiable;
      if (modifiable) {
         this.removeButtons = new ArrayList();
      }

      for(int i = 0; i < entries.size(); ++i) {
         this.addControl((GuiControl)entries.get(i));
         if (modifiable) {
            GuiListBoxBase<T>.GuiButtonRemove button = new GuiListBoxBase.GuiButtonRemove(i);
            this.addControl(button);
            this.removeButtons.add(button);
         }
      }

      this.reloadPositions();
   }

   public void reloadPositions() {
      int y = this.space / 2;

      for(int i = 0; i < this.content.size(); ++i) {
         GuiControl control = (GuiControl)this.content.get(i);
         if (this.modifiable) {
            GuiListBoxBase<T>.GuiButtonRemove button = (GuiListBoxBase.GuiButtonRemove)this.removeButtons.get(i);
            button.index = i;
            button.posY = y + 3;
         }

         control.posY = y;
         y += control.height + this.space;
      }

   }

   public void remove(int index) {
      this.removeControl((GuiControl)this.content.get(index));
      this.content.remove(index);
      if (this.modifiable) {
         this.removeControl((GuiControl)this.removeButtons.get(index));
         this.removeButtons.remove(index);
      }

      this.reloadPositions();
      this.raiseEvent(new GuiControlChangedEvent(this));
   }

   public void clear() {
      for(int i = 0; i < this.content.size(); ++i) {
         this.removeControl((GuiControl)this.content.get(i));
         if (this.modifiable) {
            this.removeControl((GuiControl)this.removeButtons.get(i));
         }
      }

      this.content.clear();
      if (this.modifiable) {
         this.removeButtons.clear();
      }

   }

   public void addAll(List<T> entries) {
      for(T entry : entries) {
         this.content.add(entry);
         this.addControl(entry);
         if (this.modifiable) {
            GuiListBoxBase<T>.GuiButtonRemove button = new GuiListBoxBase.GuiButtonRemove(this.content.size() - 1);
            this.addControl(button);
            this.removeButtons.add(button);
         }
      }

      this.reloadPositions();
   }

   public void add(T entry) {
      this.content.add(entry);
      this.addControl(entry);
      if (this.modifiable) {
         GuiListBoxBase<T>.GuiButtonRemove button = new GuiListBoxBase.GuiButtonRemove(this.content.size() - 1);
         this.addControl(button);
         this.removeButtons.add(button);
      }

      if (this.content.size() == 1) {
         this.reloadPositions();
      } else {
         GuiControl before = (GuiControl)this.content.get(this.content.size() - 2);
         entry.posY = before.posY + before.height + this.space;
         if (this.modifiable) {
            ((GuiListBoxBase.GuiButtonRemove)this.removeButtons.get(this.removeButtons.size() - 1)).posY = before.posY + before.height + this.space + 3;
         }
      }

      this.raiseEvent(new GuiControlChangedEvent(this));
   }

   public boolean isEmpty() {
      return this.content.isEmpty();
   }

   public int size() {
      return this.content.size();
   }

   public T get(int index) {
      return (T)(this.content.get(index));
   }

   public class GuiButtonRemove extends GuiButton {
      public int index;

      public GuiButtonRemove(int index) {
         super("x", GuiListBoxBase.this.width - 25, 0, 8, 8);
         this.index = index;
      }

      public void onClicked(int x, int y, int button) {
         GuiListBoxBase.this.remove(this.index);
      }
   }
}
