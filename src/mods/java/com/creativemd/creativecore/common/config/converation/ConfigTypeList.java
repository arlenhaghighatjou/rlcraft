package com.creativemd.creativecore.common.config.converation;

import com.creativemd.creativecore.common.config.gui.GuiConfigSubControl;
import com.creativemd.creativecore.common.config.gui.GuiConfigSubControlHolder;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject;
import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBoxBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigTypeList extends ConfigTypeConveration<List> {
   public ConfigTypeList() {
   }

   private ConfigHolderObject constructHolder(Side side, Object value) {
      return new ConfigHolderObject(fakeParent, side.isClient() ? ConfigSynchronization.CLIENT : ConfigSynchronization.SERVER, "", value);
   }

   public List readElement(List defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKey.ConfigKeyField key) {
      if (element.isJsonArray()) {
         JsonArray array = (JsonArray)element;
         Class clazz = this.getListType(key);
         List list = new ArrayList(array.size());
         ConfigTypeConveration conversation = getUnsafe(clazz);

         for(int i = 0; i < array.size(); ++i) {
            if (conversation != null) {
               list.add(conversation.readElement(ConfigTypeConveration.createObject(clazz), loadDefault, ignoreRestart, array.get(i), side, (ConfigKey.ConfigKeyField)null));
            } else {
               Object value = ConfigTypeConveration.createObject(clazz);
               holderConveration.readElement(this.constructHolder(side, value), loadDefault, ignoreRestart, array.get(i), side, (ConfigKey.ConfigKeyField)null);
               list.add(value);
            }
         }

         return list;
      } else {
         return defaultValue;
      }
   }

   public JsonElement writeElement(List value, List defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKey.ConfigKeyField key) {
      JsonArray array = new JsonArray();
      Class clazz = this.getListType(key);
      ConfigTypeConveration conversation = getUnsafe(clazz);

      for(int i = 0; i < value.size(); ++i) {
         if (conversation != null) {
            array.add(conversation.writeElement(value.get(i), (Object)null, true, ignoreRestart, side, key));
         } else {
            array.add(holderConveration.writeElement(this.constructHolder(side, value.get(i)), (Object)null, true, ignoreRestart, side, key));
         }
      }

      return array;
   }

   @SideOnly(Side.CLIENT)
   public void createControls(GuiParent parent, @Nullable ConfigKey.ConfigKeyField key, Class clazz, int recommendedWidth) {
      parent.height = 160;
      final GuiListBoxBase<GuiConfigSubControl> listBox = new GuiListBoxBase<GuiConfigSubControl>("data", 0, 0, parent.width - 10, 130, true, new ArrayList());
      parent.addControl(listBox);
      final Class subClass = this.getListType(key);
      final ConfigTypeConveration converation = getUnsafe(subClass);
      final int parentWidth = parent.width - 10;
      parent.addControl(new GuiButton("add", 0, 140) {
         public void onClicked(int x, int y, int button) {
            GuiConfigSubControl control;
            if (converation != null) {
               control = new GuiConfigSubControl("0", 2, 0, parentWidth, 14);
               converation.createControls(control, (ConfigKey.ConfigKeyField)null, subClass, Math.max(100, control.width - 35));
            } else {
               Object value = ConfigTypeConveration.createObject(subClass);
               ConfigHolderObject holder = ConfigTypeList.this.constructHolder(Side.SERVER, value);
               control = new GuiConfigSubControlHolder("0", 2, 0, parentWidth, 14, holder, value);
               ((GuiConfigSubControlHolder)control).createControls();
            }

            listBox.add(control);
         }
      });
   }

   @SideOnly(Side.CLIENT)
   public void loadValue(List value, GuiParent parent, @Nullable ConfigKey.ConfigKeyField key) {
      GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase)parent.get("data");
      if (!box.isEmpty()) {
         box.clear();
      }

      Class clazz = this.getListType(key);
      ConfigTypeConveration converation = getUnsafe(clazz);
      int parentWidth = parent.width - 10;
      List<GuiConfigSubControl> controls = new ArrayList(value.size());

      for(int i = 0; i < value.size(); ++i) {
         Object entry = value.get(i);
         GuiConfigSubControl control;
         if (converation != null) {
            control = new GuiConfigSubControl("" + i, 2, 0, parentWidth, 14);
            converation.createControls(control, (ConfigKey.ConfigKeyField)null, clazz, Math.max(100, control.width - 35));
            converation.loadValue(entry, control, (ConfigKey.ConfigKeyField)null);
         } else {
            control = new GuiConfigSubControlHolder("0", 2, 0, parentWidth, 14, this.constructHolder(Side.SERVER, entry), entry);
            ((GuiConfigSubControlHolder)control).createControls();
         }

         controls.add(control);
      }

      box.addAll(controls);
   }

   @SideOnly(Side.CLIENT)
   protected List saveValue(GuiParent parent, Class clazz, @Nullable ConfigKey.ConfigKeyField key) {
      Class subClass = this.getListType(key);
      ConfigTypeConveration converation = getUnsafe(subClass);
      GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase)parent.get("data");
      List value = new ArrayList(box.size());

      for(int i = 0; i < box.size(); ++i) {
         if (converation != null) {
            value.add(converation.save(box.get(i), subClass, (ConfigKey.ConfigKeyField)null));
         } else {
            ((GuiConfigSubControlHolder)box.get(i)).save();
            value.add(((GuiConfigSubControlHolder)box.get(i)).value);
         }
      }

      return value;
   }

   public List set(ConfigKey.ConfigKeyField key, List value) {
      return value;
   }

   public Class getListType(ConfigKey.ConfigKeyField key) {
      ParameterizedType type = (ParameterizedType)key.field.getGenericType();
      return (Class)type.getActualTypeArguments()[0];
   }

   public boolean areEqual(List one, List two, @Nullable ConfigKey.ConfigKeyField key) {
      Class clazz = this.getListType(key);
      ConfigTypeConveration conversation = getUnsafe(clazz);
      if (one.size() != two.size()) {
         return false;
      } else {
         for(int i = 0; i < one.size(); ++i) {
            Object entryOne = one.get(i);
            Object entryTwo = two.get(i);
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
