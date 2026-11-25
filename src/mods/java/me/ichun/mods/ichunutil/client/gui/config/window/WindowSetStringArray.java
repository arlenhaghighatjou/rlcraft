package me.ichun.mods.ichunutil.client.gui.config.window;

import java.util.ArrayList;
import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementTextInput;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;

public class WindowSetStringArray extends Window {
   public GuiConfigs parent;
   public ConfigBase config;
   public ConfigBase.PropInfo prop;

   public WindowSetStringArray(GuiConfigs parent, int w, int h, int minW, int minH, ConfigBase conf, ConfigBase.PropInfo info) {
      super(parent, 0, 0, w, h, minW, minH, "ichunutil.config.gui.setStringArray", true);
      this.parent = parent;
      this.config = conf;
      this.prop = info;
      this.elements.add(new ElementButton(this, this.width / 2 - 30, this.height - 25, 60, 16, 3, false, 2, 1, "gui.done"));

      try {
         String[] vals = (String[])info.field.get(this.config);

         for(int i = 0; i < vals.length; ++i) {
            this.elements.add(new ElementTextInput(this, 10, i * 18 + 20, this.width - 20, 12, i, this.prop.comment, vals[i]));
         }
      } catch (Exception var10) {
         ;
      }

   }

   public void draw(int mouseX, int mouseY) {
      super.draw(mouseX, mouseY);
   }

   public void update() {
      int i = -1;
      ElementTextInput text = null;
      boolean lastIsEmpty = false;

      for(Element e : this.elements) {
         if (e instanceof ElementTextInput && e.id > i) {
            if (text != null) {
               lastIsEmpty = text.textField.getText().isEmpty();
            }

            i = e.id;
            text = (ElementTextInput)e;
         }
      }

      if (i != -1 && text.textField.getText().isEmpty()) {
         if (i > 0 && lastIsEmpty) {
            this.elements.remove(text);
         }
      } else {
         this.elements.add(new ElementTextInput(this, 10, (i + 1) * 18 + 20, this.width - 20, 12, i + 1, this.prop.comment, ""));
      }

   }

   public void elementTriggered(Element element) {
      try {
         if (!(element instanceof ElementTextInput)) {
            ArrayList<String> strings = new ArrayList();

            for(Element e : this.elements) {
               if (e instanceof ElementTextInput && !((ElementTextInput)e).textField.getText().isEmpty()) {
                  strings.add(((ElementTextInput)e).textField.getText());
               }
            }

            String[] array = new String[strings.size()];

            for(int i = 0; i < array.length; ++i) {
               array[i] = (String)strings.get(i);
            }

            ConfigProp propInfo = (ConfigProp)this.prop.field.getAnnotation(ConfigProp.class);
            if (!propInfo.changeable() || propInfo.useSession()) {
               this.parent.needsRestart();
            }

            String[] ori = (String[])this.prop.field.get(this.config);
            this.prop.field.set(this.config, array);
            this.config.onConfigChange(this.prop.field, ori);
            this.parent.windowSetter.props.saveTimeout = 10;
            this.parent.keyBindTimeout = 5;
            this.parent.removeWindow(this, true);
            this.parent.elementSelected = null;
         }
      } catch (Exception var6) {
         ;
      }

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
