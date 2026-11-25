package yalter.mousetweaks;

import org.lwjgl.input.Mouse;

public class SimpleMouseState implements IMouseState {
   private int scrollAmount = 0;

   public SimpleMouseState() {
   }

   public boolean isButtonPressed(MouseButton mouseButton) {
      return Mouse.isButtonDown(mouseButton.getValue());
   }

   public void clear() {
      this.scrollAmount = 0;
   }

   public int consumeScrollAmount() {
      this.scrollAmount += Main.config.scrollItemScaling.scale(Mouse.getDWheel());
      int amountConsumed = this.scrollAmount / 120;
      this.scrollAmount -= amountConsumed * 120;
      return amountConsumed;
   }
}
