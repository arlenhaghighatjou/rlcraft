package me.ichun.mods.ichunutil.client.gui.config.window;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;
import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementTextInputNumber;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.types.NestedIntArray;

public class WindowSetNestedIntArray extends Window {
   public GuiConfigs parent;
   public ConfigBase config;
   public ConfigBase.PropInfo prop;

   public WindowSetNestedIntArray(GuiConfigs parent, int w, int h, int minW, int minH, ConfigBase conf, ConfigBase.PropInfo info) {
      super(parent, 0, 0, w, h, minW, minH, "ichunutil.config.gui.setIntArray", true);
      this.parent = parent;
      this.config = conf;
      this.prop = info;
      this.elements.add(new ElementButton(this, this.width / 2 - 30, this.height - 25, 60, 16, 3, false, 2, 1, "gui.done"));

      try {
         NestedIntArray vals = (NestedIntArray)info.field.get(this.config);
         int i = 0;

         for(Entry<Integer, ArrayList<Integer>> e : vals.values.entrySet()) {
            this.elements.add(new ElementTextInputNumber(this, 10, i * 18 + 20, 40, 12, i * 100, this.prop.comment, Integer.toString(((Integer)e.getKey()).intValue()), false));
            ArrayList<Integer> nest = (ArrayList)e.getValue();

            for(int j = 0; j < nest.size(); ++j) {
               this.elements.add(new ElementTextInputNumber(this, 52 + j * 39, i * 18 + 20, 40, 12, i * 100 + j + 1, this.prop.comment, Integer.toString(((Integer)nest.get(j)).intValue()), false));
            }

            ++i;
         }
      } catch (Exception var14) {
         ;
      }

   }

   public void draw(int mouseX, int mouseY) {
      super.draw(mouseX, mouseY);
   }

   public void update() {
      int i = -1;
      ElementTextInputNumber parentNum = null;
      boolean lastIsEmpty = false;
      ArrayList<Element> elementsToAdd = new ArrayList();
      ArrayList<Element> elementsToRemove = new ArrayList();

      for(Element e : this.elements) {
         if (e instanceof ElementTextInputNumber && e.id % 100 == 0) {
            if (e.id > i) {
               if (parentNum != null) {
                  lastIsEmpty = parentNum.textField.getText().isEmpty();
                  if (lastIsEmpty) {
                     boolean remove = true;

                     for(Element e1 : this.elements) {
                        if (e1 instanceof ElementTextInputNumber && e1.id - parentNum.id > 0 && e1.id - parentNum.id < 100 && !((ElementTextInputNumber)e1).textField.getText().isEmpty()) {
                           remove = false;
                           lastIsEmpty = false;
                        }
                     }

                     if (remove) {
                        for(Element e1 : this.elements) {
                           if (e1 instanceof ElementTextInputNumber && e1.id - parentNum.id > 0 && e1.id - parentNum.id < 100) {
                              elementsToRemove.add(e1);
                           }
                        }
                     }
                  }
               }

               i = e.id;
               parentNum = (ElementTextInputNumber)e;
            }

            int j = -1;
            ElementTextInputNumber childNum = (ElementTextInputNumber)e;
            boolean lastChildIsEmpty = false;

            for(Element e1 : this.elements) {
               if (e1 instanceof ElementTextInputNumber && e1.id - e.id > 0 && e1.id - e.id < 100 && e1.id > j) {
                  if (childNum != (ElementTextInputNumber)e) {
                     lastChildIsEmpty = childNum.textField.getText().isEmpty();
                  }

                  j = e1.id - e.id - 1;
                  childNum = (ElementTextInputNumber)e1;
               }
            }

            if (!((ElementTextInputNumber)e).textField.getText().isEmpty() && (j == -1 || !childNum.textField.getText().isEmpty())) {
               elementsToAdd.add(new ElementTextInputNumber(this, 52 + (j + 1) * 39, i / 100 * 18 + 20, 40, 12, i / 100 * 100 + j + 2, this.prop.comment, "", false));
            } else if (j > 0 && lastChildIsEmpty) {
               elementsToRemove.add(childNum);
            }
         }
      }

      if (i != -1 && parentNum.textField.getText().isEmpty()) {
         if (i > 0 && lastIsEmpty) {
            this.elements.remove(parentNum);
         }
      } else {
         this.elements.add(new ElementTextInputNumber(this, 10, (i / 100 + 1) * 18 + 20, 40, 12, (i / 100 + 1) * 100, this.prop.comment, "", false));
      }

      this.elements.removeAll(elementsToRemove);
      this.elements.addAll(elementsToAdd);
   }

   public void elementTriggered(Element element) {
      try {
         if (!(element instanceof ElementTextInputNumber)) {
            TreeMap<Integer, ArrayList<ElementTextInputNumber>> elementMap = new TreeMap();

            for(Element e : this.elements) {
               if (e instanceof ElementTextInputNumber) {
                  ArrayList<ElementTextInputNumber> nums = (ArrayList)elementMap.get(Integer.valueOf(e.id - e.id % 100));
                  if (nums == null) {
                     nums = new ArrayList();
                     elementMap.put(Integer.valueOf(e.id - e.id % 100), nums);
                  }

                  String text = ((ElementTextInputNumber)e).textField.getText();
                  if (!text.isEmpty() && !text.equals("-") && !text.equals(".")) {
                     nums.add((ElementTextInputNumber)e);
                  }
               }
            }

            try {
               NestedIntArray vals = (NestedIntArray)this.prop.field.get(this.config);
               vals.values = new TreeMap();

               for(Entry<Integer, ArrayList<ElementTextInputNumber>> e : elementMap.entrySet()) {
                  ArrayList<Integer> list = new ArrayList();
                  int parent = -32736;

                  for(ElementTextInputNumber e1 : (ArrayList)e.getValue()) {
                     String text = e1.textField.getText();
                     int val = 0;
                     if (!text.isEmpty()) {
                        if (!text.equals("-") && !text.equals(".")) {
                           val = Integer.parseInt(text);
                        }

                        if (e1.id % 100 == 0) {
                           parent = val;
                        } else {
                           list.add(Integer.valueOf(val));
                        }
                     }
                  }

                  if (parent != -32736) {
                     vals.values.put(Integer.valueOf(parent), list);
                  }
               }

               ConfigProp propInfo = (ConfigProp)this.prop.field.getAnnotation(ConfigProp.class);
               if (!propInfo.changeable() || propInfo.useSession()) {
                  this.parent.needsRestart();
               }

               this.config.onConfigChange(this.prop.field, vals);
               this.parent.windowSetter.props.saveTimeout = 10;
               this.parent.keyBindTimeout = 5;
               this.parent.removeWindow(this, true);
               this.parent.elementSelected = null;
            } catch (Exception var12) {
               ;
            }
         }
      } catch (Exception var13) {
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
