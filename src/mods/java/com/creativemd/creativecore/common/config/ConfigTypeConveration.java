package com.creativemd.creativecore.common.config;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.gui.GuiConfigSubControl;
import com.creativemd.creativecore.common.config.gui.GuiConfigSubControlHolder;
import com.creativemd.creativecore.common.config.holder.ConfigHolderDynamic;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject;
import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAnalogeSlider;
import com.creativemd.creativecore.common.gui.controls.gui.GuiButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiListBoxBase;
import com.creativemd.creativecore.common.gui.controls.gui.GuiStateButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiSteppedSlider;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.utils.mc.ChatFormatting;
import com.creativemd.creativecore.common.utils.type.Pair;
import com.creativemd.creativecore.common.utils.type.PairList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ConfigTypeConveration<T> {
   private static HashMap<Class, ConfigTypeConveration> types = new HashMap();
   private static PairList<Predicate<Class>, ConfigTypeConveration> specialTypes = new PairList<Predicate<Class>, ConfigTypeConveration>();
   private static final ICreativeConfigHolder fakeParent = new ICreativeConfigHolder() {
      public ConfigSynchronization synchronization() {
         return ConfigSynchronization.UNIVERSAL;
      }

      public JsonObject save(boolean saveDefault, boolean ignoreRestart, Side side) {
         return null;
      }

      public void restoreDefault(Side side, boolean ignoreRestart) {
      }

      public String[] path() {
         return new String[0];
      }

      public ICreativeConfigHolder parent() {
         return null;
      }

      public Collection<String> names() {
         return null;
      }

      public void load(boolean loadDefault, boolean ignoreRestart, JsonObject json, Side side) {
      }

      public boolean isEmptyWithoutForce(Side side) {
         return false;
      }

      public boolean isEmpty(Side side) {
         return false;
      }

      public boolean isDefault(Side side) {
         return false;
      }

      public ConfigKey getField(String key) {
         return null;
      }

      public Object get(String key) {
         return null;
      }

      public Collection<? extends ConfigKey> fields() {
         return null;
      }
   };
   private static ConfigTypeConveration<ConfigHolderObject> holderConveration = registerType(ConfigHolderObject.class, new ConfigTypeConveration<ConfigHolderObject>() {
      public ConfigHolderObject readElement(ConfigHolderObject defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKey.ConfigKeyField key) {
         if (element.isJsonObject()) {
            defaultValue.load(loadDefault, ignoreRestart, (JsonObject)element, side);
         } else {
            defaultValue.restoreDefault(side, ignoreRestart);
         }

         return defaultValue;
      }

      public JsonElement writeElement(ConfigHolderObject value, ConfigHolderObject defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKey.ConfigKeyField key) {
         return value.save(saveDefault, ignoreRestart, side);
      }

      @SideOnly(Side.CLIENT)
      public void createControls(GuiParent parent, @Nullable ConfigKey.ConfigKeyField key, Class clazz, int recommendedWidth) {
      }

      @SideOnly(Side.CLIENT)
      public void loadValue(ConfigHolderObject value, GuiParent parent, @Nullable ConfigKey.ConfigKeyField key) {
      }

      @SideOnly(Side.CLIENT)
      protected ConfigHolderObject saveValue(GuiParent parent, Class clazz, @Nullable ConfigKey.ConfigKeyField key) {
         return null;
      }

      public ConfigHolderObject set(ConfigKey.ConfigKeyField key, ConfigHolderObject value) {
         return null;
      }

      public ConfigHolderObject createPrimitiveDefault(Class clazz) {
         return null;
      }
   });

   public ConfigTypeConveration() {
   }

   public static <T, U extends T> ConfigTypeConveration<T> registerType(Class<U> clazz, ConfigTypeConveration<T> type) {
      types.put(clazz, type);
      return type;
   }

   public static void registerSpecialType(Predicate<Class> predicate, ConfigTypeConveration type) {
      specialTypes.add(predicate, type);
   }

   public static boolean has(Class typeClass) {
      if (types.containsKey(typeClass)) {
         return true;
      } else if (typeClass.isAnnotationPresent(CreativeConfig.class)) {
         return false;
      } else {
         for(int i = 0; i < specialTypes.size(); ++i) {
            if (((Predicate)((Pair)specialTypes.get(i)).key).test(typeClass)) {
               return true;
            }
         }

         return false;
      }
   }

   public static ConfigTypeConveration get(Class typeClass) {
      ConfigTypeConveration converation = (ConfigTypeConveration)types.get(typeClass);
      if (converation != null) {
         return converation;
      } else {
         for(int i = 0; i < specialTypes.size(); ++i) {
            if (((Predicate)((Pair)specialTypes.get(i)).key).test(typeClass)) {
               return (ConfigTypeConveration)((Pair)specialTypes.get(i)).value;
            }
         }

         throw new RuntimeException("Could not find converation for " + typeClass.getName());
      }
   }

   public static ConfigTypeConveration getUnsafe(Class typeClass) {
      ConfigTypeConveration converation = (ConfigTypeConveration)types.get(typeClass);
      if (converation != null) {
         return converation;
      } else {
         for(int i = 0; i < specialTypes.size(); ++i) {
            if (((Predicate)((Pair)specialTypes.get(i)).key).test(typeClass)) {
               return (ConfigTypeConveration)((Pair)specialTypes.get(i)).value;
            }
         }

         return null;
      }
   }

   public static Object read(Class typeClass, Object defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKey.ConfigKeyField key) {
      return get(typeClass).readElement(defaultValue, loadDefault, ignoreRestart, element, side, key);
   }

   public static JsonElement write(Class typeClass, Object value, Object defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKey.ConfigKeyField key) {
      return get(typeClass).writeElement(value, defaultValue, saveDefault, ignoreRestart, side, key);
   }

   public abstract T createPrimitiveDefault(Class var1);

   public abstract T readElement(T var1, boolean var2, boolean var3, JsonElement var4, Side var5, @Nullable ConfigKey.ConfigKeyField var6);

   public abstract JsonElement writeElement(T var1, T var2, boolean var3, boolean var4, Side var5, @Nullable ConfigKey.ConfigKeyField var6);

   @SideOnly(Side.CLIENT)
   public abstract void createControls(GuiParent var1, @Nullable ConfigKey.ConfigKeyField var2, Class var3, int var4);

   @SideOnly(Side.CLIENT)
   public abstract void loadValue(T var1, GuiParent var2, @Nullable ConfigKey.ConfigKeyField var3);

   @SideOnly(Side.CLIENT)
   protected abstract T saveValue(GuiParent var1, Class var2, @Nullable ConfigKey.ConfigKeyField var3);

   public abstract T set(ConfigKey.ConfigKeyField var1, T var2);

   @SideOnly(Side.CLIENT)
   public T save(GuiParent parent, Class clazz, @Nullable ConfigKey.ConfigKeyField key) {
      T value = (T)this.saveValue(parent, clazz, key);
      return (T)(value != null && key != null ? this.set(key, value) : value);
   }

   public boolean areEqual(T one, T two) {
      return one.equals(two);
   }

   public static Object parseObject(ICreativeConfigHolder parent, ConfigSynchronization synchronization, String key, Object object) {
      return has(object.getClass()) ? object : new ConfigHolderObject(parent, synchronization, key, object);
   }

   static {
      ConfigTypeConveration<Boolean> booleanType = new ConfigTypeConveration.SimpleConfigTypeConveration<Boolean>() {
         public Boolean readElement(Boolean defaultValue, boolean loadDefault, JsonElement element) {
            return element.isJsonPrimitive() && ((JsonPrimitive)element).isBoolean() ? element.getAsBoolean() : defaultValue;
         }

         public JsonElement writeElement(Boolean value, Boolean defaultValue, boolean saveDefault) {
            return new JsonPrimitive(value);
         }

         @SideOnly(Side.CLIENT)
         public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
            parent.addControl(new GuiStateButton("data", 0, 0, 0, recommendedWidth, new String[]{ChatFormatting.RED + "false", ChatFormatting.GREEN + "true"}));
         }

         @SideOnly(Side.CLIENT)
         public void loadValue(Boolean value, GuiParent parent) {
            GuiStateButton button = (GuiStateButton)parent.get("data");
            button.setState(value.booleanValue() ? 1 : 0);
         }

         @SideOnly(Side.CLIENT)
         protected Boolean saveValue(GuiParent parent, Class clazz) {
            GuiStateButton button = (GuiStateButton)parent.get("data");
            return button.getState() == 1;
         }

         public Boolean set(ConfigKey.ConfigKeyField key, Boolean value) {
            return value;
         }

         public Boolean createPrimitiveDefault(Class clazz) {
            return Boolean.FALSE;
         }
      };
      registerType(Boolean.TYPE, booleanType);
      registerType(Boolean.class, booleanType);
      ConfigTypeConveration<Number> numberType = new ConfigTypeConveration.SimpleConfigTypeConveration<Number>() {
         public Number readElement(Number defaultValue, boolean loadDefault, JsonElement element) {
            if (element.isJsonPrimitive() && ((JsonPrimitive)element).isNumber()) {
               Class clazz = defaultValue.getClass();
               if (clazz != Float.class && clazz != Float.TYPE) {
                  if (clazz != Double.class && clazz != Double.TYPE) {
                     if (clazz != Byte.class && clazz != Byte.TYPE) {
                        if (clazz != Short.class && clazz != Short.TYPE) {
                           if (clazz != Integer.class && clazz != Integer.TYPE) {
                              return (Number)(clazz != Long.class && clazz != Long.TYPE ? element.getAsNumber() : element.getAsLong());
                           } else {
                              return element.getAsInt();
                           }
                        } else {
                           return element.getAsShort();
                        }
                     } else {
                        return element.getAsByte();
                     }
                  } else {
                     return element.getAsDouble();
                  }
               } else {
                  return element.getAsFloat();
               }
            } else {
               return defaultValue;
            }
         }

         public JsonElement writeElement(Number value, Number defaultValue, boolean saveDefault) {
            return new JsonPrimitive(value);
         }

         public boolean isDecimal(Class clazz) {
            return clazz == Float.class || clazz == Float.TYPE || clazz == Double.class || clazz == Double.TYPE;
         }

         @SideOnly(Side.CLIENT)
         public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
         }

         @SideOnly(Side.CLIENT)
         public void createControls(GuiParent parent, @Nullable ConfigKey.ConfigKeyField key, Class clazz, int recommendedWidth) {
            boolean decimal = this.isDecimal(clazz);
            if (key != null) {
               if (decimal) {
                  CreativeConfig.DecimalRange decRange = (CreativeConfig.DecimalRange)key.field.getAnnotation(CreativeConfig.DecimalRange.class);
                  if (decRange != null && decRange.slider()) {
                     parent.addControl(new GuiAnalogeSlider("data", 0, 0, recommendedWidth, 14, decRange.min(), decRange.min(), decRange.max()));
                     return;
                  }
               } else {
                  CreativeConfig.IntRange intRange = (CreativeConfig.IntRange)key.field.getAnnotation(CreativeConfig.IntRange.class);
                  if (intRange != null && intRange.slider()) {
                     parent.addControl(new GuiSteppedSlider("data", 0, 0, recommendedWidth, 14, intRange.min(), intRange.min(), intRange.max()));
                     return;
                  }
               }
            }

            GuiTextfield textfield = new GuiTextfield("data", "", 0, 0, recommendedWidth, 14);
            if (decimal) {
               textfield.setFloatOnly();
            } else {
               textfield.setNumbersIncludingNegativeOnly();
            }

            parent.addControl(textfield);
         }

         @SideOnly(Side.CLIENT)
         public void loadValue(Number value, GuiParent parent) {
            GuiControl control = parent.get("data");
            if (control instanceof GuiSteppedSlider) {
               GuiSteppedSlider button = (GuiSteppedSlider)control;
               button.setValue((double)value.intValue());
            } else if (control instanceof GuiAnalogeSlider) {
               GuiAnalogeSlider button = (GuiAnalogeSlider)control;
               button.setValue(value.doubleValue());
            } else {
               GuiTextfield button = (GuiTextfield)control;
               button.text = value.toString();
               button.setCursorPositionZero();
            }

         }

         public Number parseDecimal(Class clazz, double decimal) {
            return (Number)(clazz != Float.class && clazz != Float.TYPE ? decimal : (float)decimal);
         }

         public Number parseInt(Class clazz, int number) {
            if (clazz != Byte.class && clazz != Byte.TYPE) {
               if (clazz != Short.class && clazz != Short.TYPE) {
                  return (Number)(clazz != Long.class && clazz != Long.TYPE ? number : (long)number);
               } else {
                  return (short)number;
               }
            } else {
               return (byte)number;
            }
         }

         public Number parseNumber(Class clazz, String text) {
            if (clazz != Float.class && clazz != Float.TYPE) {
               if (clazz != Double.class && clazz != Double.TYPE) {
                  if (clazz != Byte.class && clazz != Byte.TYPE) {
                     if (clazz != Short.class && clazz != Short.TYPE) {
                        if (clazz != Integer.class && clazz != Integer.TYPE) {
                           if (clazz != Long.class && clazz != Long.TYPE) {
                              return Integer.valueOf(0);
                           } else {
                              try {
                                 return Long.parseLong(text);
                              } catch (NumberFormatException var4) {
                                 return 0L;
                              }
                           }
                        } else {
                           try {
                              return Integer.parseInt(text);
                           } catch (NumberFormatException var5) {
                              return Integer.valueOf(0);
                           }
                        }
                     } else {
                        try {
                           return Short.parseShort(text);
                        } catch (NumberFormatException var6) {
                           return 0;
                        }
                     }
                  } else {
                     try {
                        return Byte.parseByte(text);
                     } catch (NumberFormatException var7) {
                        return 0;
                     }
                  }
               } else {
                  try {
                     return Double.parseDouble(text);
                  } catch (NumberFormatException var8) {
                     return 0.0D;
                  }
               }
            } else {
               try {
                  return Float.parseFloat(text);
               } catch (NumberFormatException var9) {
                  return 0.0F;
               }
            }
         }

         @SideOnly(Side.CLIENT)
         protected Number saveValue(GuiParent parent, Class clazz) {
            this.isDecimal(clazz);
            GuiControl control = parent.get("data");
            String text;
            if (control instanceof GuiSteppedSlider) {
               GuiSteppedSlider button = (GuiSteppedSlider)control;
               text = "" + (int)button.value;
            } else if (control instanceof GuiAnalogeSlider) {
               GuiAnalogeSlider button = (GuiAnalogeSlider)control;
               text = "" + button.value;
            } else {
               GuiTextfield button = (GuiTextfield)control;
               text = button.text;
            }

            return this.parseNumber(clazz, text);
         }

         public Number set(ConfigKey.ConfigKeyField key, Number value) {
            Class clazz = key.getType();
            boolean decimal = this.isDecimal(clazz);
            if (key != null) {
               if (decimal) {
                  CreativeConfig.DecimalRange decRange = (CreativeConfig.DecimalRange)key.field.getAnnotation(CreativeConfig.DecimalRange.class);
                  if (decRange != null) {
                     return this.parseDecimal(clazz, MathHelper.clamp(value.doubleValue(), decRange.min(), decRange.max()));
                  }
               } else {
                  CreativeConfig.IntRange intRange = (CreativeConfig.IntRange)key.field.getAnnotation(CreativeConfig.IntRange.class);
                  if (intRange != null) {
                     return this.parseInt(clazz, MathHelper.clamp(value.intValue(), intRange.min(), intRange.max()));
                  }
               }
            }

            return value;
         }

         public Number createPrimitiveDefault(Class clazz) {
            if (clazz != Float.class && clazz != Float.TYPE) {
               if (clazz != Double.class && clazz != Double.TYPE) {
                  if (clazz != Byte.class && clazz != Byte.TYPE) {
                     if (clazz != Short.class && clazz != Short.TYPE) {
                        if (clazz != Integer.class && clazz != Integer.TYPE) {
                           return (Number)(clazz != Long.class && clazz != Long.TYPE ? Integer.valueOf(0) : new Long(0L));
                        } else {
                           return new Integer(0);
                        }
                     } else {
                        return new Short((short)0);
                     }
                  } else {
                     return new Byte((byte)0);
                  }
               } else {
                  return new Double(0.0D);
               }
            } else {
               return new Float(0.0F);
            }
         }
      };
      registerType(Byte.TYPE, numberType);
      registerType(Byte.class, numberType);
      registerType(Short.TYPE, numberType);
      registerType(Short.class, numberType);
      registerType(Integer.TYPE, numberType);
      registerType(Integer.class, numberType);
      registerType(Long.TYPE, numberType);
      registerType(Long.class, numberType);
      registerType(Float.TYPE, numberType);
      registerType(Float.class, numberType);
      registerType(Double.TYPE, numberType);
      registerType(Double.class, numberType);
      registerType(String.class, new ConfigTypeConveration.SimpleConfigTypeConveration<String>() {
         public String readElement(String defaultValue, boolean loadDefault, JsonElement element) {
            return element.isJsonPrimitive() && ((JsonPrimitive)element).isString() ? element.getAsString() : defaultValue;
         }

         public JsonElement writeElement(String value, String defaultValue, boolean saveDefault) {
            return new JsonPrimitive(value);
         }

         @SideOnly(Side.CLIENT)
         public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
            parent.addControl(new GuiTextfield("data", "", 0, 0, recommendedWidth, 14));
         }

         @SideOnly(Side.CLIENT)
         public void loadValue(String value, GuiParent parent) {
            GuiTextfield button = (GuiTextfield)parent.get("data");
            button.text = value;
            button.setCursorPositionZero();
         }

         @SideOnly(Side.CLIENT)
         protected String saveValue(GuiParent parent, Class clazz) {
            GuiTextfield button = (GuiTextfield)parent.get("data");
            return button.text;
         }

         public String set(ConfigKey.ConfigKeyField key, String value) {
            return value;
         }

         public String createPrimitiveDefault(Class clazz) {
            return "";
         }
      });
      registerType(ConfigHolderDynamic.class, new ConfigTypeConveration<ConfigHolderDynamic>() {
         public ConfigHolderDynamic readElement(ConfigHolderDynamic defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKey.ConfigKeyField key) {
            if (element.isJsonObject()) {
               defaultValue.load(loadDefault, ignoreRestart, (JsonObject)element, side);
            } else {
               defaultValue.restoreDefault(side, ignoreRestart);
            }

            return defaultValue;
         }

         public JsonElement writeElement(ConfigHolderDynamic value, ConfigHolderDynamic defaultValue, boolean saveDefault, boolean ignoreRestart, Side side, @Nullable ConfigKey.ConfigKeyField key) {
            return value.save(saveDefault, ignoreRestart, side);
         }

         @SideOnly(Side.CLIENT)
         public void createControls(GuiParent parent, @Nullable ConfigKey.ConfigKeyField key, Class clazz, int recommendedWidth) {
         }

         @SideOnly(Side.CLIENT)
         public void loadValue(ConfigHolderDynamic value, GuiParent parent, @Nullable ConfigKey.ConfigKeyField key) {
         }

         @SideOnly(Side.CLIENT)
         protected ConfigHolderDynamic saveValue(GuiParent parent, Class clazz, @Nullable ConfigKey.ConfigKeyField key) {
            return null;
         }

         public ConfigHolderDynamic set(ConfigKey.ConfigKeyField key, ConfigHolderDynamic value) {
            return null;
         }

         public ConfigHolderDynamic createPrimitiveDefault(Class clazz) {
            return null;
         }
      });
      registerSpecialType((x) -> {
         if (x.isArray()) {
            if (has(x.getComponentType())) {
               return true;
            } else {
               throw new RuntimeException("Array with holders are not permitted");
            }
         } else {
            return false;
         }
      }, new ConfigTypeConveration() {
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
               array.add(write(value.getClass().getComponentType(), Array.get(value, i), Array.get(defaultValue, i), saveDefault, ignoreRestart, side, (ConfigKey.ConfigKeyField)null));
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

         public boolean areEqual(Object one, Object two) {
            int lengthOne = Array.getLength(one);
            int lengthTwo = Array.getLength(two);
            if (lengthOne != lengthTwo) {
               return false;
            } else {
               for(int i = 0; i < lengthOne; ++i) {
                  Object entryOne = Array.get(one, i);
                  Object entryTwo = Array.get(two, i);
                  if (entryOne.getClass().isArray()) {
                     if (!entryTwo.getClass().isArray()) {
                        return false;
                     }

                     if (!this.areEqual(entryOne, entryTwo)) {
                        return false;
                     }
                  }

                  if (!entryOne.equals(entryTwo)) {
                     return false;
                  }
               }

               return true;
            }
         }

         public Object createPrimitiveDefault(Class clazz) {
            return null;
         }
      });
      registerSpecialType((x) -> {
         return x.isEnum();
      }, new ConfigTypeConveration.SimpleConfigTypeConveration<Enum>() {
         public Enum readElement(Enum defaultValue, boolean loadDefault, JsonElement element) {
            return element.isJsonPrimitive() && ((JsonPrimitive)element).isString() ? Enum.valueOf(defaultValue.getDeclaringClass(), element.getAsString()) : defaultValue;
         }

         public JsonElement writeElement(Enum value, Enum defaultValue, boolean saveDefault) {
            return new JsonPrimitive(value.name());
         }

         @SideOnly(Side.CLIENT)
         public void createControls(GuiParent parent, Class clazz, int recommendedWidth) {
            Object[] possibleValues = clazz.getEnumConstants();
            List<String> lines = new ArrayList(possibleValues.length);

            for(int i = 0; i < possibleValues.length; ++i) {
               lines.add(((Enum)possibleValues[i]).name());
            }

            parent.addControl(new GuiComboBox("data", 0, 0, recommendedWidth, lines));
         }

         @SideOnly(Side.CLIENT)
         public void loadValue(Enum value, GuiParent parent) {
            GuiComboBox box = (GuiComboBox)parent.get("data");
            box.select(value.ordinal());
         }

         @SideOnly(Side.CLIENT)
         protected Enum saveValue(GuiParent parent, Class clazz) {
            GuiComboBox box = (GuiComboBox)parent.get("data");
            return (Enum)clazz.getEnumConstants()[box.index];
         }

         public Enum set(ConfigKey.ConfigKeyField key, Enum value) {
            return value;
         }

         public Enum createPrimitiveDefault(Class clazz) {
            return null;
         }
      });
      registerSpecialType((x) -> {
         return List.class.isAssignableFrom(x) || x == ArrayList.class;
      }, new ConfigTypeConveration<List>() {
         private ConfigHolderObject constructHolder(Side side, Object value) {
            return new ConfigHolderObject(ConfigTypeConveration.fakeParent, side.isClient() ? ConfigSynchronization.CLIENT : ConfigSynchronization.SERVER, "", value);
         }

         private Object constructEmpty(Class clazz) {
            try {
               Constructor con = clazz.getConstructor();
               return con.newInstance();
            } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException var3) {
               throw new RuntimeException(var3);
            }
         }

         public List readElement(List defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKey.ConfigKeyField key) {
            if (element.isJsonArray()) {
               JsonArray array = (JsonArray)element;
               Class clazz = this.getListType(key);
               List list = new ArrayList(array.size());
               ConfigTypeConveration conversation = getUnsafe(clazz);

               for(int i = 0; i < array.size(); ++i) {
                  if (conversation != null) {
                     list.add(conversation.readElement(conversation.createPrimitiveDefault(clazz), loadDefault, ignoreRestart, array.get(i), side, (ConfigKey.ConfigKeyField)null));
                  } else {
                     Object value = this.constructEmpty(clazz);
                     ConfigTypeConveration.holderConveration.readElement(this.constructHolder(side, value), loadDefault, ignoreRestart, array.get(i), side, (ConfigKey.ConfigKeyField)null);
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
                  array.add(conversation.writeElement(value.get(i), (Object)null, saveDefault, ignoreRestart, side, key));
               } else {
                  array.add(ConfigTypeConveration.holderConveration.writeElement(this.constructHolder(side, value.get(i)), (Object)null, saveDefault, ignoreRestart, side, key));
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
                  } else {
                     Object value = constructEmpty(subClass);
                     ConfigHolderObject holder = constructHolder(Side.SERVER, value);
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
                  converation.createControls(control, (ConfigKey.ConfigKeyField)null, clazz, Math.min(100, control.width - 35));
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

         public List createPrimitiveDefault(Class clazz) {
            return null;
         }
      });
   }

   public abstract static class SimpleConfigTypeConveration<T> extends ConfigTypeConveration<T> {
      public SimpleConfigTypeConveration() {
      }

      public T readElement(T defaultValue, boolean loadDefault, boolean ignoreRestart, JsonElement element, Side side, @Nullable ConfigKey.ConfigKeyField key) {
         return (T)this.readElement(defaultValue, loadDefault, element);
      }

      public abstract T readElement(T var1, boolean var2, JsonElement var3);

      public JsonElement writeElement(T value, T defaultValue, boolean ignoreRestart, boolean saveDefault, Side side, @Nullable ConfigKey.ConfigKeyField key) {
         return this.writeElement(value, defaultValue, saveDefault);
      }

      public abstract JsonElement writeElement(T var1, T var2, boolean var3);

      @SideOnly(Side.CLIENT)
      public void createControls(GuiParent parent, @Nullable ConfigKey.ConfigKeyField key, Class clazz, int recommendedWidth) {
         this.createControls(parent, clazz, recommendedWidth);
      }

      @SideOnly(Side.CLIENT)
      public abstract void createControls(GuiParent var1, Class var2, int var3);

      @SideOnly(Side.CLIENT)
      public void loadValue(T value, GuiParent parent, @Nullable ConfigKey.ConfigKeyField key) {
         this.loadValue(value, parent);
      }

      @SideOnly(Side.CLIENT)
      public abstract void loadValue(T var1, GuiParent var2);

      @SideOnly(Side.CLIENT)
      protected T saveValue(GuiParent parent, Class clazz, @Nullable ConfigKey.ConfigKeyField key) {
         return (T)this.saveValue(parent, clazz);
      }

      @SideOnly(Side.CLIENT)
      protected abstract T saveValue(GuiParent var1, Class var2);
   }
}
