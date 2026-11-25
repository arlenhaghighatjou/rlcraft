package com.creativemd.creativecore.common.config.converation;

import com.creativemd.creativecore.common.config.gui.GuiConfigSubControl;
import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBoxBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigTypeArray extends ConfigTypeConveration {
   public ConfigTypeArray() {
   }

   public Object readElement(Object defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKey.ConfigKeyField key) {
      if (!element.isJsonArray()) {
         return defaultValue;
      } else {
         JsonArray array = (JsonArray)element;
         int size = Math.min(array.size(), Array.getLength(defaultValue));
         Object object = Array.newInstance(defaultValue.getClass().getComponentType(), size);

         for(int i = 0; i < size; ++i) {
            Array.set(object, i, read(defaultValue.getClass().getComponentType(), Array.get(defaultValue, i), loadDefault, ignoreRestart, array.get(i), side, (ConfigKey.ConfigKeyField)null));
         }

         return object;
      }
   }

   public JsonElement writeElement(Object value, Object defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKey.ConfigKeyField key) {
      int length = Array.getLength(value);
      JsonArray array = new JsonArray();

      for(int i = 0; i < length; ++i) {
         array.add(write(value.getClass().getComponentType(), Array.get(value, i), Array.get(defaultValue, i), true, ignoreRestart, side, (ConfigKey.ConfigKeyField)null));
      }

      return array;
   }

   @SideOnly(Side.CLIENT)
   public void createControls(GuiParent parent, @Nullable ConfigKey.ConfigKeyField key, Class clazz, int recommendedWidth) {
      parent.height = 160;
      GuiListBoxBase<GuiConfigSubControl> listBox = new GuiListBoxBase<GuiConfigSubControl>("data", 0, 0, parent.width - 10, 150, false, new ArrayList());
      parent.addControl(listBox);
   }

   @SideOnly(Side.CLIENT)
   public void loadValue(Object value, GuiParent parent, @Nullable ConfigKey.ConfigKeyField key) {
      GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase)parent.get("data");
      if (!box.isEmpty()) {
         box.clear();
      }

      Class clazz = value.getClass().getComponentType();
      ConfigTypeConveration converation = get(clazz);
      int length = Array.getLength(value);
      List<GuiConfigSubControl> controls = new ArrayList(length);

      for(int i = 0; i < length; ++i) {
         Object entry = Array.get(value, i);
         GuiConfigSubControl control = new GuiConfigSubControl("" + i, 2, 0, parent.width, 14);
         converation.createControls(control, (ConfigKey.ConfigKeyField)null, clazz, Math.min(100, control.width));
         converation.loadValue(entry, control, (ConfigKey.ConfigKeyField)null);
         controls.add(control);
      }

      box.addAll(controls);
   }

   @SideOnly(Side.CLIENT)
   protected Object saveValue(GuiParent parent, Class clazz, @Nullable ConfigKey.ConfigKeyField key) {
      Class subClass = clazz.getComponentType();
      ConfigTypeConveration converation = get(subClass);
      GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase)parent.get("data");
      Object value = Array.newInstance(subClass, box.size());

      for(int i = 0; i < box.size(); ++i) {
         Array.set(value, i, converation.save(box.get(i), subClass, (ConfigKey.ConfigKeyField)null));
      }

      return value;
   }

   public Object set(ConfigKey.ConfigKeyField key, Object value) {
      return value;
   }

   public boolean areEqual(Object one, Object two, @Nullable ConfigKey.ConfigKeyField key) {
      int lengthOne = Array.getLength(one);
      int lengthTwo = Array.getLength(two);
      Class clazz = key.getDefault().getClass().getComponentType();
      ConfigTypeConveration conversation = getUnsafe(clazz);
      if (lengthOne != lengthTwo) {
         return false;
      } else {
         for(int i = 0; i < lengthOne; ++i) {
            Object entryOne = Array.get(one, i);
            Object entryTwo = Array.get(two, i);
            if (conversation != null && !conversation.areEqual(entryOne, entryTwo, (ConfigKey.ConfigKeyField)null)) {
               return false;
            }

            if (conversation == null && !entryOne.equals(entryTwo)) {
               return false;
            }
         }

         return true;
      }
   }
}
