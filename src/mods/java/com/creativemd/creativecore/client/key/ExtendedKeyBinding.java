package com.creativemd.creativecore.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class ExtendedKeyBinding extends KeyBinding {
   public static final int timeToWaitFirst = 500;
   public static final int timeToWait = 20;
   protected long lastTimePressed = -1L;
   protected boolean pressedBefore = false;
   protected boolean pressedFirst = false;

   public ExtendedKeyBinding(String description, int keyCode, String category) {
      super(description, keyCode, category);
   }

   public ExtendedKeyBinding(String description, IKeyConflictContext keyConflictContext, int keyCode, String category) {
      super(description, keyConflictContext, keyCode, category);
   }

   public ExtendedKeyBinding(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode, String category) {
      super(description, keyConflictContext, keyModifier, keyCode, category);
   }

   public boolean isPressed(boolean repeated) {
      return repeated ? this.isPressed() : super.isPressed();
   }

   public boolean isPressed() {
      boolean isKeyDown = this.isKeyDown();
      if (isKeyDown) {
         if (!this.pressedBefore) {
            this.lastTimePressed = System.currentTimeMillis();
            this.pressedFirst = false;
         } else {
            long elapsed = System.currentTimeMillis() - this.lastTimePressed;
            if (!this.pressedFirst) {
               if (elapsed >= 500L) {
                  this.pressedFirst = true;
                  this.lastTimePressed = System.currentTimeMillis();
                  return true;
               }
            } else if (elapsed >= 20L) {
               this.lastTimePressed = System.currentTimeMillis();
               return true;
            }
         }
      } else {
         this.pressedFirst = false;
      }

      this.pressedBefore = isKeyDown;
      return super.isPressed();
   }
}
