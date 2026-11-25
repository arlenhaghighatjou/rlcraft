package yalter.mousetweaks.forge;

import com.google.common.base.MoreObjects;
import java.util.EnumSet;
import java.util.function.Predicate;
import org.lwjgl.input.Mouse;
import yalter.mousetweaks.IMouseState;
import yalter.mousetweaks.Main;
import yalter.mousetweaks.MouseButton;

public class ForgeMouseState implements IMouseState {
   private final EnumSet<MouseButton> pressedButtons = EnumSet.noneOf(MouseButton.class);
   private int scrollAmount = 0;
   public boolean simpleScrolling = false;

   public ForgeMouseState() {
   }

   public void update() {
      MouseButton eventButton = getEventButton();
      if (eventButton != null) {
         if (Mouse.getEventButtonState()) {
            this.pressedButtons.add(eventButton);
         } else {
            this.pressedButtons.remove(eventButton);
         }
      } else if (!this.simpleScrolling) {
         this.scrollAmount += Main.config.scrollItemScaling.scale(Mouse.getEventDWheel());
      }

      this.pressedButtons.removeIf((mouseButton) -> {
         return !Mouse.isButtonDown(mouseButton.getValue());
      });
   }

   private static MouseButton getEventButton() {
      int eventButton = Mouse.getEventButton();
      if (eventButton == 0) {
         return MouseButton.LEFT;
      } else {
         return eventButton == 1 ? MouseButton.RIGHT : null;
      }
   }

   public void clear() {
      this.pressedButtons.clear();
      this.scrollAmount = 0;
   }

   public boolean isButtonPressed(MouseButton mouseButton) {
      return this.pressedButtons.contains(mouseButton);
   }

   public int consumeScrollAmount() {
      if (this.simpleScrolling) {
         this.scrollAmount += Main.config.scrollItemScaling.scale(Mouse.getDWheel());
      }

      int amountConsumed = this.scrollAmount / 120;
      this.scrollAmount -= amountConsumed * 120;
      return amountConsumed;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("pressedButtons", this.pressedButtons.toArray()).add("scrollAmount", this.scrollAmount).toString();
   }
}
