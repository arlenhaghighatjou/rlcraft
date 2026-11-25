package me.ichun.mods.ichunutil.client.gui.config.window;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.element.ElementKeyBindHook;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.core.ProxyCommon;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.translation.I18n;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class WindowSetKeyBind extends Window {
   public GuiConfigs parent;
   public ConfigBase config;
   public ConfigBase.PropInfo prop;
   public boolean releasedMouse;
   public int lastKeyHeld;
   public int keyHeldTime;
   public String message;
   public ElementKeyBindHook hook;

   public WindowSetKeyBind(GuiConfigs parent, int w, int h, int minW, int minH, String msg, ConfigBase conf, ConfigBase.PropInfo info) {
      super(parent, 0, 0, w, h, minW, minH, "ichunutil.config.gui.setKeyBind", true);
      this.parent = parent;
      this.config = conf;
      this.prop = info;
      this.message = msg;
      this.hook = new ElementKeyBindHook(this, 0, 0, 0, 0, 0, true);
   }

   public void draw(int mouseX, int mouseY) {
      super.draw(mouseX, mouseY);
      this.workspace.getFontRenderer().drawString(I18n.translateToLocal(this.message), (float)(this.posX + (this.width - this.workspace.getFontRenderer().getStringWidth(I18n.translateToLocal(this.message))) / 2), (float)(this.posY + (this.height - 12 - 9) / 2), Theme.getAsHex(this.workspace.currentTheme.font), false);
      if (!this.releasedMouse) {
         this.releasedMouse = !Mouse.isButtonDown(0);
      } else {
         for(int i = 0; i < 16; ++i) {
            if (Mouse.isButtonDown(i)) {
               if (Minecraft.IS_RUNNING_ON_MAC && i == 0 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))) {
                  i = 1;
               }

               StringBuilder sb = new StringBuilder();
               sb.append(i - 100);
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
                  KeyBind bind = (KeyBind)this.prop.field.get(this.config);
                  ConfigProp propInfo = (ConfigProp)this.prop.field.getAnnotation(ConfigProp.class);
                  if (!propInfo.changeable() || propInfo.useSession()) {
                     this.parent.needsRestart();
                  }

                  KeyBind newKey = iChunUtil.proxy.registerKeyBind(new KeyBind(i - 100, GuiScreen.isShiftKeyDown(), GuiScreen.isCtrlKeyDown(), Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184), bind.ignoreHold), bind);
                  this.prop.field.set(this.config, newKey);
                  this.config.onConfigChange(this.prop.field, newKey);
                  this.parent.windowSetter.props.saveTimeout = 10;
                  this.parent.keyBindTimeout = 5;
                  this.parent.removeWindow(this, true);
                  this.parent.elementSelected = null;
               } catch (Exception var8) {
                  ;
               }
               break;
            }
         }
      }

   }

   public void update() {
      this.workspace.elementSelected = this.hook;
      if (GuiScreen.isShiftKeyDown() || GuiScreen.isCtrlKeyDown() || Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184)) {
         ++this.keyHeldTime;
         if (this.keyHeldTime >= 60) {
            this.keyHeldTime = 0;
            StringBuilder sb = new StringBuilder();
            sb.append(this.lastKeyHeld);
            if (GuiScreen.isShiftKeyDown() && this.lastKeyHeld != 42 && this.lastKeyHeld != 54) {
               sb.append(":SHIFT");
            }

            if (GuiScreen.isCtrlKeyDown()) {
               label115: {
                  if (Minecraft.IS_RUNNING_ON_MAC) {
                     if (this.lastKeyHeld == 219 || this.lastKeyHeld == 220) {
                        break label115;
                     }
                  } else if (this.lastKeyHeld == 29 || this.lastKeyHeld == 157) {
                     break label115;
                  }

                  sb.append(":CTRL");
               }
            }

            if ((Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184)) && this.lastKeyHeld != 56 && this.lastKeyHeld != 184) {
               sb.append(":ALT");
            }

            try {
               KeyBind bind;
               ProxyCommon var10000;
               KeyBind var10001;
               int var10003;
               boolean var10004;
               boolean var10005;
               label76: {
                  label75: {
                     bind = (KeyBind)this.prop.field.get(this.config);
                     var10000 = iChunUtil.proxy;
                     var10001 = new KeyBind;
                     var10003 = this.lastKeyHeld;
                     var10004 = GuiScreen.isShiftKeyDown() && this.lastKeyHeld != 42 && this.lastKeyHeld != 54;
                     if (GuiScreen.isCtrlKeyDown()) {
                        if (Minecraft.IS_RUNNING_ON_MAC) {
                           if (this.lastKeyHeld != 219 && this.lastKeyHeld != 220) {
                              break label75;
                           }
                        } else if (this.lastKeyHeld != 29 && this.lastKeyHeld != 157) {
                           break label75;
                        }
                     }

                     var10005 = false;
                     break label76;
                  }

                  var10005 = true;
               }

               var10001.<init>(var10003, var10004, var10005, Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184) && this.lastKeyHeld != 56 && this.lastKeyHeld != 184, bind.ignoreHold);
               KeyBind newKey = var10000.registerKeyBind(var10001, bind);
               this.prop.field.set(this.config, newKey);
               this.config.onConfigChange(this.prop.field, newKey);
               this.parent.windowSetter.props.saveTimeout = 10;
               this.parent.keyBindTimeout = 5;
               this.parent.removeWindow(this, true);
               this.parent.elementSelected = null;
            } catch (Exception var4) {
               ;
            }
         }
      }

   }

   public void elementTriggered(Element element) {
      this.parent.windowSetter.props.saveTimeout = 10;
      this.parent.keyBindTimeout = 5;
      this.parent.removeWindow(this, true);
      this.parent.elementSelected = null;
   }

   public boolean allowMultipleInstances() {
      return false;
   }

   public boolean canBeDragged() {
      return false;
   }

   public boolean canMinimize() {
      return false;
   }
}
