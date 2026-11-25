package me.ichun.mods.ichunutil.client.gui.config.window.element;

import me.ichun.mods.ichunutil.client.gui.config.window.WindowSetKeyBind;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class ElementKeyBindHook extends Element {
   public WindowSetKeyBind bind;

   public ElementKeyBindHook(WindowSetKeyBind window, int x, int y, int w, int h, int ID, boolean igMin) {
      super(window, x, y, w, h, ID, igMin);
      this.bind = window;
   }

   public void draw(int mouseX, int mouseY, boolean hover) {
   }

   public void keyInput(char c, int i) {
      if (i != 42 && i != 54) {
         label62: {
            if (Minecraft.IS_RUNNING_ON_MAC) {
               if (i == 219 || i == 220) {
                  break label62;
               }
            } else if (i == 29 || i == 157) {
               break label62;
            }

            if (i != 56 && i != 184) {
               StringBuilder sb = new StringBuilder();
               sb.append(i);
               if (GuiScreen.isShiftKeyDown()) {
                  sb.append(":SHIFT");
               }

               if (GuiScreen.isCtrlKeyDown()) {
                  sb.append(":CTRL");
               }

               if (Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184)) {
                  sb.append(":ALT");
               }

               try {
                  KeyBind bind = (KeyBind)this.bind.prop.field.get(this.bind.config);
                  KeyBind newKey = iChunUtil.proxy.registerKeyBind(new KeyBind(i, GuiScreen.isShiftKeyDown(), GuiScreen.isCtrlKeyDown(), Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184), bind.ignoreHold), bind);
                  this.bind.prop.field.set(this.bind.config, newKey);
                  this.bind.config.onConfigChange(this.bind.prop.field, newKey);
                  this.bind.elementTriggered(this);
               } catch (Exception var6) {
                  ;
               }

               return;
            }
         }
      }

      if (this.bind.lastKeyHeld != i) {
         this.bind.keyHeldTime = 0;
      }

      this.bind.lastKeyHeld = i;
   }
}
