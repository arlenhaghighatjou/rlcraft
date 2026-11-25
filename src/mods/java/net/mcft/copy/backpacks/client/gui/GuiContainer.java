package net.mcft.copy.backpacks.client.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiContainer extends GuiElementBase {
   protected List<GuiElementBase> children = new ArrayList();
   private boolean _expandHorizontal = true;
   private boolean _expandVertical = true;
   private int _padLeft;
   private int _padTop;
   private int _padRight;
   private int _padBottom;
   private boolean _hadChild = false;

   public GuiContainer() {
   }

   public GuiContainer(GuiContext context) {
      this.setContext(context);
   }

   public boolean doesExpand(Direction direction) {
      boolean var10000;
      label25: {
         if (this.getAlign(direction).canExpand()) {
            if (direction == Direction.HORIZONTAL) {
               if (this._expandHorizontal) {
                  break label25;
               }
            } else if (this._expandVertical) {
               break label25;
            }
         }

         var10000 = false;
         return var10000;
      }

      var10000 = true;
      return var10000;
   }

   public void setExpand(Direction direction, boolean value) {
      if (direction == Direction.HORIZONTAL) {
         this._expandHorizontal = value;
      } else {
         this._expandVertical = value;
      }

   }

   public int getPaddingMin(Direction direction) {
      return direction == Direction.HORIZONTAL ? this._padLeft : this._padTop;
   }

   public int getPaddingMax(Direction direction) {
      return direction == Direction.HORIZONTAL ? this._padRight : this._padBottom;
   }

   public int getPadding(Direction direction) {
      return this.getPaddingMin(direction) + this.getPaddingMax(direction);
   }

   public final int getPaddingLeft() {
      return this.getPaddingMin(Direction.HORIZONTAL);
   }

   public final int getPaddingTop() {
      return this.getPaddingMin(Direction.VERTICAL);
   }

   public final int getPaddingRight() {
      return this.getPaddingMax(Direction.HORIZONTAL);
   }

   public final int getPaddingBottom() {
      return this.getPaddingMax(Direction.VERTICAL);
   }

   public void setPadding(Direction direction, int min, int max) {
      if (direction == Direction.HORIZONTAL) {
         this._padLeft = min;
         this._padRight = max;
      } else {
         this._padTop = min;
         this._padBottom = max;
      }

      this.expandToFitChildren(direction);
   }

   public final void setPaddingHorizontal(int left, int right) {
      this.setPadding(Direction.HORIZONTAL, left, right);
   }

   public final void setPaddingHorizontal(int value) {
      this.setPaddingHorizontal(value, value);
   }

   public final void setPaddingVertical(int top, int bottom) {
      this.setPadding(Direction.VERTICAL, top, bottom);
   }

   public final void setPaddingVertical(int value) {
      this.setPaddingVertical(value, value);
   }

   public final void setPadding(int left, int top, int right, int bottom) {
      this.setPaddingHorizontal(left, right);
      this.setPaddingVertical(top, bottom);
   }

   public final void setPadding(int horizontal, int vertical) {
      this.setPadding(horizontal, vertical, horizontal, vertical);
   }

   public final void setPadding(int value) {
      this.setPadding(value, value);
   }

   public final void setFillHorizontal(int padding) {
      this.setFillHorizontal();
      this.setPaddingHorizontal(padding);
   }

   public final void setFillVertical(int padding) {
      this.setFillVertical();
      this.setPaddingVertical(padding);
   }

   public final void setFill(int padding) {
      this.setFillHorizontal(padding);
      this.setFillVertical(padding);
   }

   public void onChildSizeChanged(GuiElementBase element, Direction direction) {
      this.expandToFitChildren(direction);
   }

   public void onChildAlignChanged(GuiElementBase element, Direction direction) {
      this.expandToFitChildren(direction);
   }

   public int getChildPos(GuiElementBase element, Direction direction) {
      Alignment align = element.getAlign(direction);
      if (align instanceof Alignment.Min) {
         return this.getPaddingMin(direction) + ((Alignment.Min)align).min;
      } else if (align instanceof Alignment.Max) {
         return this.getSize(direction) - element.getSize(direction) - this.getPaddingMax(direction) - ((Alignment.Max)align).max;
      } else if (align instanceof Alignment.Both) {
         return this.getPaddingMin(direction) + ((Alignment.Both)align).min;
      } else if (align instanceof Alignment.Center) {
         return (this.getSize(direction) - element.getSize(direction)) / 2;
      } else {
         throw new UnsupportedOperationException("Unsupported Alignment '" + align.getClass() + "'");
      }
   }

   public final int getChildX(GuiElementBase element) {
      return this.getChildPos(element, Direction.HORIZONTAL);
   }

   public final int getChildY(GuiElementBase element) {
      return this.getChildPos(element, Direction.VERTICAL);
   }

   public void onChildAdded(GuiElementBase element) {
      for(Direction direction : Direction.values()) {
         this.updateChildSizes(direction);
         this.expandToFitChildren(direction);
      }

   }

   public void onChildMoved(GuiElementBase element) {
      for(Direction direction : Direction.values()) {
         this.updateChildSizes(direction);
         this.expandToFitChildren(direction);
      }

   }

   public void onChildRemoved(GuiElementBase element) {
      for(Direction direction : Direction.values()) {
         this.updateChildSizes(direction);
         this.expandToFitChildren(direction);
      }

   }

   public void addAll(GuiElementBase... elements) {
      for(GuiElementBase element : elements) {
         this.add(element);
      }

   }

   public void add(GuiElementBase element) {
      this.insert(this.children.size(), element);
   }

   public void insert(int index, GuiElementBase element) {
      if (element.getContext() != null) {
         throw new UnsupportedOperationException("The specified element already has a context set");
      } else {
         if (this.getContext() != null) {
            element.setContext(this.getContext());
         }

         element.setParent(this);
         this.children.add(index, element);
         this._hadChild = true;
         this.onChildAdded(element);
      }
   }

   public void move(int index, int targetIndex) {
      if (index >= 0 && index < this.children.size()) {
         if (targetIndex >= 0 && targetIndex < this.children.size()) {
            GuiElementBase element = (GuiElementBase)this.children.remove(index);
            this.children.add(targetIndex, element);
            this.onChildMoved(element);
         } else {
            throw new IndexOutOfBoundsException("Argument targetIndex is out of bounds");
         }
      } else {
         throw new IndexOutOfBoundsException("Argument index is out of bounds");
      }
   }

   public void remove(GuiElementBase element) {
      if (!this.children.remove(element)) {
         throw new UnsupportedOperationException("The specified element is not a child of this container");
      } else {
         if (element.isPressed()) {
            element.getContext().setPressed((GuiElementBase)null);
         }

         element.setContext((GuiContext)null);
         element.setParent((GuiContainer)null);
         this.onChildRemoved(element);
      }
   }

   public void clear() {
      while(!this.children.isEmpty()) {
         this.remove((GuiElementBase)this.children.get(0));
      }

   }

   public List<GuiElementBase> getChildren() {
      return Collections.unmodifiableList(this.children);
   }

   void setContext(GuiContext element) {
      super.setContext(element);
      this.children.forEach((child) -> {
         child.setContext(element);
      });
   }

   public void onSizeChanged(Direction direction) {
      if (!this._hadChild) {
         this.setExpand(direction, false);
      }

      this.updateChildSizes(direction);
      this.expandToFitChildren(direction);
   }

   protected void updateChildSizes(Direction direction) {
      for(GuiElementBase child : this.children) {
         Alignment align = child.getAlign(direction);
         if (align instanceof Alignment.Both) {
            Alignment.Both both = (Alignment.Both)align;
            child.setSize(direction, this.getSize(direction) - this.getPadding(direction) - both.min - both.max);
         }
      }

   }

   protected void expandToFitChildren(Direction direction) {
      if (!this.children.isEmpty() && this.doesExpand(direction)) {
         this.setSize(direction, this.getPadding(direction) + this.children.stream().mapToInt((child) -> {
            Alignment align = child.getAlign(direction);
            return align instanceof Alignment.Both ? 0 : (align instanceof Alignment.Center ? child.getSize(direction) : this.getChildPos(child, direction) - this.getPaddingMin(direction) + child.getSize(direction));
         }).max().orElse(0));
      }
   }

   public boolean onMouseDown(int mouseButton, int mouseX, int mouseY) {
      return this.isVisible() && this.isEnabled() && this.foreachFindChildMousePosReversed(mouseX, mouseY, (child, x, y, mx, my) -> {
         return child.contains(mx, my) && child.onMouseDown(mouseButton, mx, my);
      }) != null || super.onMouseDown(mouseButton, mouseX, mouseY);
   }

   public void onMouseMove(int mouseX, int mouseY) {
      if (this.isVisible()) {
         this.foreachChildMousePos(mouseX, mouseY, (child, x, y, mx, my) -> {
            if (child.contains(mx, my)) {
               child.onMouseMove(mx, my);
            }

         });
         super.onMouseMove(mouseX, mouseY);
      }
   }

   public boolean onMouseScroll(int scroll, int mouseX, int mouseY) {
      return this.isVisible() && this.isEnabled() && this.foreachFindChildMousePosReversed(mouseX, mouseY, (child, x, y, mx, my) -> {
         return child.contains(mx, my) && child.onMouseScroll(scroll, mx, my);
      }) != null || super.onMouseScroll(scroll, mouseX, mouseY);
   }

   public void draw(int mouseX, int mouseY, float partialTicks) {
      if (this.isVisible()) {
         this.foreachChildMousePos(mouseX, mouseY, (child, x, y, mx, my) -> {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y, 0.0F);
            child.draw(mx, my, partialTicks);
            GlStateManager.popMatrix();
         });
      }
   }

   protected void foreachChildPos(GuiContainer.ChildPosConsumer consumer) {
      for(GuiElementBase child : this.children) {
         consumer.apply(child, this.getChildX(child), this.getChildY(child));
      }

   }

   protected void foreachChildMousePos(int mouseX, int mouseY, GuiContainer.ChildPosMouseConsumer consumer) {
      for(GuiElementBase child : this.children) {
         int x = this.getChildX(child);
         int y = this.getChildY(child);
         int mx = mouseX - x;
         int my = mouseY - y;
         consumer.apply(child, x, y, mx, my);
      }

   }

   protected GuiElementBase foreachFindChildMousePosReversed(int mouseX, int mouseY, GuiContainer.ChildPosMousePredicate consumer) {
      int size = this.children.size();

      for(int i = 0; i < size; ++i) {
         GuiElementBase child = (GuiElementBase)this.children.get(size - 1 - i);
         int x = this.getChildX(child);
         int y = this.getChildY(child);
         int mx = mouseX - x;
         int my = mouseY - y;
         if (consumer.apply(child, x, y, mx, my)) {
            return child;
         }
      }

      return null;
   }

   @FunctionalInterface
   protected interface ChildPosConsumer {
      void apply(GuiElementBase var1, int var2, int var3);
   }

   @FunctionalInterface
   protected interface ChildPosMouseConsumer {
      void apply(GuiElementBase var1, int var2, int var3, int var4, int var5);
   }

   @FunctionalInterface
   protected interface ChildPosMousePredicate {
      boolean apply(GuiElementBase var1, int var2, int var3, int var4, int var5);
   }
}
