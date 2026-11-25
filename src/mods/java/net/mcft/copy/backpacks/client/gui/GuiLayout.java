package net.mcft.copy.backpacks.client.gui;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLayout extends GuiContainer {
   public final Direction direction;
   private int[] _spacing = new int[]{2};

   public GuiLayout(Direction direction) {
      this.direction = direction;
   }

   public int[] getSpacing() {
      return this._spacing;
   }

   public void setSpacing(int... value) {
      this._spacing = value;
   }

   public GuiElementBase get(int index) {
      return (GuiElementBase)this.children.get(index);
   }

   public void add(GuiElementBase element) {
      this.addWeighted(element);
   }

   public void addFixed(GuiElementBase element, int size) {
      this.insertFixed(this.children.size(), element, size);
   }

   public void addFixed(GuiElementBase element) {
      this.insertFixed(this.children.size(), element);
   }

   public void addWeighted(GuiElementBase element) {
      this.insertWeighted(this.children.size(), element);
   }

   public void addWeighted(GuiElementBase element, double weight) {
      this.insertWeighted(this.children.size(), element, weight);
   }

   public void addWeighted(GuiElementBase element, double weight, int minSize) {
      this.insertWeighted(this.children.size(), element, weight, minSize);
   }

   public void insertFixed(int index, GuiElementBase element, int size) {
      element.setSize(this.direction, size);
      this.insertFixed(index, element);
   }

   public void insertFixed(int index, GuiElementBase element) {
      this.insert(index, element, new GuiLayout.LayoutAlignment.Fixed());
   }

   public void insertWeighted(int index, GuiElementBase element) {
      this.insertWeighted(index, element, 1.0D);
   }

   public void insertWeighted(int index, GuiElementBase element, double weight) {
      this.insertWeighted(index, element, weight, 0);
   }

   public void insertWeighted(int index, GuiElementBase element, double weight, int minSize) {
      this.insert(index, element, new GuiLayout.LayoutAlignment.Weighted(weight, minSize));
   }

   protected void insert(int index, GuiElementBase element, GuiLayout.LayoutAlignment alignment) {
      if (element.getContext() != null) {
         throw new UnsupportedOperationException("The specified element already has a context set");
      } else {
         element.setAlign(this.direction, alignment);
         super.insert(index, element);
      }
   }

   public void onChildSizeChanged(GuiElementBase element, Direction direction) {
      super.onChildSizeChanged(element, direction);
      if (direction == this.direction) {
         this.updateChildSizes(direction);
      }

   }

   public void onChildAlignChanged(GuiElementBase element, Direction direction) {
      if (direction == this.direction && !(this.getAlign(direction) instanceof GuiLayout.LayoutAlignment)) {
         throw new UnsupportedOperationException("Unsupported Alignment '" + this.getAlign(direction).getClass() + "' in GuiLayout");
      } else {
         super.onChildAlignChanged(element, direction);
      }
   }

   public int getChildPos(GuiElementBase element, Direction direction) {
      return direction == this.direction ? this.getPaddingMin(direction) + ((GuiLayout.LayoutAlignment)element.getAlign(direction))._childPos : super.getChildPos(element, direction);
   }

   protected void updateChildSizes(Direction direction) {
      if (direction != this.direction) {
         super.updateChildSizes(direction);
      } else {
         int[] spacing = this.getSpacing();
         double remainingWeight = 0.0D;
         int availableSize = this.getSize(direction) - this.getPadding(direction);

         for(int i = 0; i < this.children.size(); ++i) {
            GuiElementBase child = (GuiElementBase)this.children.get(i);
            Alignment align = child.getAlign(direction);
            if (align instanceof GuiLayout.LayoutAlignment.Weighted) {
               GuiLayout.LayoutAlignment.Weighted weighted = (GuiLayout.LayoutAlignment.Weighted)align;
               remainingWeight += weighted.weight;
               availableSize -= weighted.minSize;
            } else {
               availableSize -= child.getSize(direction);
            }

            if (i < this.children.size() - 1) {
               availableSize -= spacing[Math.min(spacing.length - 1, i)];
            }
         }

         int currentPos = 0;

         for(int i = 0; i < this.children.size(); ++i) {
            GuiElementBase child = (GuiElementBase)this.children.get(i);
            int size = 0;
            GuiLayout.LayoutAlignment align = (GuiLayout.LayoutAlignment)child.getAlign(direction);
            if (align instanceof GuiLayout.LayoutAlignment.Weighted) {
               GuiLayout.LayoutAlignment.Weighted weighted = (GuiLayout.LayoutAlignment.Weighted)align;
               int var16 = Math.max(0, (int)((double)availableSize * weighted.weight / remainingWeight));
               remainingWeight -= weighted.weight;
               availableSize -= var16;
               size = var16 + weighted.minSize;
               child.setSize(direction, size);
            } else {
               size = child.getSize(direction);
            }

            align._childPos = currentPos;
            currentPos += size;
            if (i < this.children.size() - 1) {
               currentPos += spacing[Math.min(spacing.length - 1, i)];
            }
         }

         if (this.doesExpand(direction)) {
            this.setSize(direction, currentPos + this.getPadding(direction));
         }

      }
   }

   public abstract static class LayoutAlignment extends Alignment {
      private int _childPos;

      private LayoutAlignment() {
      }

      public static final class Fixed extends GuiLayout.LayoutAlignment {
         public Fixed() {
         }
      }

      public static final class Weighted extends GuiLayout.LayoutAlignment {
         public final double weight;
         public final int minSize;

         public Weighted(double weight, int minSize) {
            this.weight = weight;
            this.minSize = minSize;
         }

         public boolean canExpand() {
            return false;
         }
      }
   }
}
