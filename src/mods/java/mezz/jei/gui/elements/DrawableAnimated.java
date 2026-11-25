package mezz.jei.gui.elements;

import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.gui.TickTimer;
import net.minecraft.client.Minecraft;

public class DrawableAnimated implements IDrawableAnimated {
   private final IDrawableStatic drawable;
   private final ITickTimer tickTimer;
   private final IDrawableAnimated.StartDirection startDirection;

   public DrawableAnimated(IDrawableStatic drawable, int ticksPerCycle, IDrawableAnimated.StartDirection startDirection, boolean inverted) {
      IDrawableAnimated.StartDirection animationStartDirection = startDirection;
      if (inverted) {
         if (startDirection == IDrawableAnimated.StartDirection.TOP) {
            animationStartDirection = IDrawableAnimated.StartDirection.BOTTOM;
         } else if (startDirection == IDrawableAnimated.StartDirection.BOTTOM) {
            animationStartDirection = IDrawableAnimated.StartDirection.TOP;
         } else if (startDirection == IDrawableAnimated.StartDirection.LEFT) {
            animationStartDirection = IDrawableAnimated.StartDirection.RIGHT;
         } else {
            animationStartDirection = IDrawableAnimated.StartDirection.LEFT;
         }
      }

      int tickTimerMaxValue;
      if (animationStartDirection != IDrawableAnimated.StartDirection.TOP && animationStartDirection != IDrawableAnimated.StartDirection.BOTTOM) {
         tickTimerMaxValue = drawable.getWidth();
      } else {
         tickTimerMaxValue = drawable.getHeight();
      }

      this.drawable = drawable;
      this.tickTimer = new TickTimer(ticksPerCycle, tickTimerMaxValue, !inverted);
      this.startDirection = animationStartDirection;
   }

   public DrawableAnimated(IDrawableStatic drawable, ITickTimer tickTimer, IDrawableAnimated.StartDirection startDirection) {
      this.drawable = drawable;
      this.tickTimer = tickTimer;
      this.startDirection = startDirection;
   }

   public int getWidth() {
      return this.drawable.getWidth();
   }

   public int getHeight() {
      return this.drawable.getHeight();
   }

   public void draw(Minecraft minecraft, int xOffset, int yOffset) {
      int maskLeft = 0;
      int maskRight = 0;
      int maskTop = 0;
      int maskBottom = 0;
      int animationValue = this.tickTimer.getValue();
      switch(this.startDirection) {
      case TOP:
         maskBottom = animationValue;
         break;
      case BOTTOM:
         maskTop = animationValue;
         break;
      case LEFT:
         maskRight = animationValue;
         break;
      case RIGHT:
         maskLeft = animationValue;
         break;
      default:
         throw new IllegalStateException("Unknown startDirection " + this.startDirection);
      }

      this.drawable.draw(minecraft, xOffset, yOffset, maskTop, maskBottom, maskLeft, maskRight);
   }
}
