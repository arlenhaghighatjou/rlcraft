package com.creativemd.creativecore.common.config.converation;

import com.creativemd.creativecore.common.config.gui.GuiConfigSubControl;
import com.creativemd.creativecore.common.config.gui.GuiConfigSubControlHolder;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject;
import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.config.premade.NamedList;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBoxBase;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigTypeNamedList extends ConfigTypeConveration<NamedList> {
   public ConfigTypeNamedList() {
   }

   private ConfigHolderObject constructHolder(Side side, Object value) {
      return new ConfigHolderObject(fakeParent, side.isClient() ? ConfigSynchronization.CLIENT : ConfigSynchronization.SERVER, "", value);
   }

   protected void addToList(NamedList list, String name, Object object) {
      list.put(name, object);
   }

   protected NamedList create(Class clazz) {
      return new NamedList();
   }

   public NamedList readElement(NamedList defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKey.ConfigKeyField key) {
      if (element.isJsonObject()) {
         JsonObject object = (JsonObject)element;
         Class clazz = this.getListType(key);
         NamedList list = this.create(clazz);
         ConfigTypeConveration conversation = getUnsafe(clazz);

         for(Entry<String, JsonElement> entry : object.entrySet()) {
            if (conversation != null) {
               this.addToList(list, (String)entry.getKey(), conversation.readElement(ConfigTypeConveration.createObject(clazz), loadDefault, ignoreRestart, (JsonElement)entry.getValue(), side, (ConfigKey.ConfigKeyField)null));
            } else {
               Object value = ConfigTypeConveration.createObject(clazz);
               holderConveration.readElement(this.constructHolder(side, value), loadDefault, ignoreRestart, (JsonElement)entry.getValue(), side, (ConfigKey.ConfigKeyField)null);
               this.addToList(list, (String)entry.getKey(), value);
            }
         }

         return list;
      } else {
         return defaultValue;
      }
   }

   public JsonElement writeElement(NamedList value, NamedList defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKey.ConfigKeyField key) {
      JsonObject array = new JsonObject();
      Class clazz = this.getListType(key);
      ConfigTypeConveration conversation = getUnsafe(clazz);

      for(Entry<String, ?> entry : value.entrySet()) {
         if (conversation != null) {
            array.add((String)entry.getKey(), conversation.writeElement(entry.getValue(), (Object)null, true, ignoreRestart, side, key));
         } else {
            array.add((String)entry.getKey(), holderConveration.writeElement(this.constructHolder(side, entry.getValue()), (Object)null, true, ignoreRestart, side, key));
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
               converation.createControls(control, (ConfigKey.ConfigKeyField)null, subClass, Math.min(100, control.width - 35));
               control.addNameTextfield("");
            } else {
               Object value = ConfigTypeConveration.createObject(subClass);
               ConfigHolderObject holder = ConfigTypeNamedList.this.constructHolder(Side.SERVER, value);
               control = new GuiConfigSubControlHolder("0", 2, 0, parentWidth, 14, holder, value);
               ((GuiConfigSubControlHolder)control).createControls();
               control.addNameTextfield("");
            }

            listBox.add(control);
         }
      });
   }

   @SideOnly(Side.CLIENT)
   public void loadValue(NamedList value, GuiParent parent, @Nullable ConfigKey.ConfigKeyField key) {
      GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase)parent.get("data");
      if (!box.isEmpty()) {
         box.clear();
      }

      Class clazz = this.getListType(key);
      ConfigTypeConveration converation = getUnsafe(clazz);
      int parentWidth = parent.width - 10;
      List<GuiConfigSubControl> controls = new ArrayList(value.size());
      int i = 0;

      for(Entry<String, ?> entry : value.entrySet()) {
         GuiConfigSubControl control;
         if (converation != null) {
            control = new GuiConfigSubControl("" + i, 2, 0, parentWidth, 14);
            converation.createControls(control, (ConfigKey.ConfigKeyField)null, clazz, Math.min(100, control.width - 35));
            converation.loadValue(entry.getValue(), control, (ConfigKey.ConfigKeyField)null);
            control.addNameTextfield((String)entry.getKey());
         } else {
            control = new GuiConfigSubControlHolder("0", 2, 0, parentWidth, 14, this.constructHolder(Side.SERVER, entry.getValue()), entry.getValue());
            ((GuiConfigSubControlHolder)control).createControls();
            control.addNameTextfield((String)entry.getKey());
         }

         controls.add(control);
         ++i;
      }

      box.addAll(controls);
   }

   @SideOnly(Side.CLIENT)
   protected NamedList saveValue(GuiParent parent, Class clazz, @Nullable ConfigKey.ConfigKeyField key) {
      Class subClass = this.getListType(key);
      ConfigTypeConveration converation = getUnsafe(subClass);
      GuiListBoxBase<GuiConfigSubControl> box = (GuiListBoxBase)parent.get("data");
      NamedList value = this.create(subClass);

      for(int i = 0; i < box.size(); ++i) {
         if (converation != null) {
            this.addToList(value, ((GuiConfigSubControl)box.get(i)).getName(), converation.save(box.get(i), subClass, (ConfigKey.ConfigKeyField)null));
         } else {
            ((GuiConfigSubControlHolder)box.get(i)).save();
            this.addToList(value, ((GuiConfigSubControl)box.get(i)).getName(), ((GuiConfigSubControlHolder)box.get(i)).value);
         }
      }

      return value;
   }

   public NamedList set(ConfigKey.ConfigKeyField key, NamedList value) {
      return value;
   }

   public Class getListType(ConfigKey.ConfigKeyField key) {
      ParameterizedType type = (ParameterizedType)key.field.getGenericType();
      return (Class)type.getActualTypeArguments()[0];
   }

   public boolean areEqual(NamedList one, NamedList two, @Nullable ConfigKey.ConfigKeyField key) {
      Class clazz = this.getListType(key);
      ConfigTypeConveration conversation = getUnsafe(clazz);
      if (one.size() != two.size()) {
         return false;
      } else {
         for(Entry<String, ?> entry : one.entrySet()) {
            Object other = two.get(entry.getKey());
            if (conversation != null && !conversation.areEqual(entry.getValue(), other, (ConfigKey.ConfigKeyField)null)) {
               return false;
            }

            if (conversation == null && !entry.getValue().equals(other)) {
               return false;
            }
         }

         return true;
      }
   }
}
