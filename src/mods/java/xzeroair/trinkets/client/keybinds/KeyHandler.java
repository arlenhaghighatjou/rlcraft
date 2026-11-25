package xzeroair.trinkets.client.keybinds;

import java.util.function.Function;

public class KeyHandler {
   private boolean isDown = true;
   private boolean isPressed = false;
   private boolean isReleased = true;
   private boolean isDead;
   private boolean reset = false;
   private int ticks = 0;

   public KeyHandler() {
   }

   public int heldDuration() {
      return this.ticks;
   }

   public <T> boolean Pressed(T target, boolean keyDown, Function<T, Boolean> onPress) {
      return this.handler(target, keyDown, onPress, (Function)null, (Function)null);
   }

   public <T> boolean handler(T target, boolean keyDown, Function<T, Boolean> onPress, Function<T, Boolean> onDown, Function<T, Boolean> onRelease) {
      if (keyDown && this.isDown) {
         ++this.ticks;
         if (!this.isPressed && this.isReleased) {
            this.isPressed = true;
            this.isReleased = false;
            if (target != null && onPress != null) {
               this.isDown = ((Boolean)onPress.apply(target)).booleanValue();
               if (!this.isDown) {
                  this.isPressed = false;
                  this.isReleased = true;
               }
            }
         } else if (!this.isReleased) {
            if (target != null && onDown != null) {
               this.isReleased = !((Boolean)onDown.apply(target)).booleanValue();
            }

            if (this.isReleased && target != null && onRelease != null) {
               this.isDown = ((Boolean)onRelease.apply(target)).booleanValue();
            }
         }

         return true;
      } else {
         if (!this.isReleased) {
            if (target != null && onRelease != null) {
               this.isReleased = ((Boolean)onRelease.apply(target)).booleanValue();
            } else {
               this.isReleased = true;
            }
         }

         this.isPressed = false;
         this.isDown = true;
         this.ticks = 0;
         return false;
      }
   }

   public boolean handler(boolean keyDown, Function<Boolean, Boolean> onPress, Function<Boolean, Boolean> onDown, Function<Boolean, Boolean> onRelease) {
      boolean target = true;
      if (keyDown && this.isDown) {
         ++this.ticks;
         if (!this.isPressed && this.isReleased) {
            this.isPressed = true;
            this.isDead = false;
            if (onPress != null) {
               this.isReleased = !((Boolean)onPress.apply(Boolean.valueOf(true))).booleanValue();
            }
         } else if (!this.isReleased) {
            if (this.isPressed && onDown != null) {
               this.isReleased = !((Boolean)onDown.apply(Boolean.valueOf(true))).booleanValue();
            }
         } else if (!this.isPressed && onRelease != null) {
            onRelease.apply(Boolean.valueOf(true));
            this.isReleased = true;
         } else if (!this.isDead) {
            this.isDead = ((Boolean)onRelease.apply(Boolean.valueOf(true))).booleanValue();
         }

         return true;
      } else {
         if (!this.isReleased && onRelease != null && !this.isDead) {
            this.isDead = ((Boolean)onRelease.apply(Boolean.valueOf(true))).booleanValue();
         }

         this.isReleased = true;
         this.isPressed = false;
         this.isDown = true;
         this.ticks = 0;
         return false;
      }
   }
}
