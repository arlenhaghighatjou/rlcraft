package com.creativemd.creativecore.common.config.converation;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.holder.ConfigHolderDynamic;
import com.creativemd.creativecore.common.config.holder.ConfigHolderObject;
import com.creativemd.creativecore.common.config.holder.ConfigKey;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.config.premade.NamedList;
import com.creativemd.creativecore.common.config.premade.Permission;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.controls.gui.GuiAnalogeSlider;
import com.creativemd.creativecore.common.gui.controls.gui.GuiComboBox;
import com.creativemd.creativecore.common.gui.controls.gui.GuiStateButton;
import com.creativemd.creativecore.common.gui.controls.gui.GuiSteppedSlider;
import com.creativemd.creativecore.common.gui.controls.gui.GuiTextfield;
import com.creativemd.creativecore.common.utils.mc.ChatFormatting;
import com.creativemd.creativecore.common.utils.type.Pair;
import com.creativemd.creativecore.common.utils.type.PairList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ConfigTypeConveration<T> {
   private static HashMap<Class, Supplier> typeCreators = new HashMap();
   private static HashMap<Class, ConfigTypeConveration> types = new HashMap();
   private static PairList<Predicate<Class>, ConfigTypeConveration> specialTypes = new PairList<Predicate<Class>, ConfigTypeConveration>();
   public static final ICreativeConfigHolder fakeParent = new ICreativeConfigHolder() {
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
   public static ConfigTypeConveration<ConfigHolderObject> holderConveration = registerType(ConfigHolderObject.class, new ConfigTypeConveration<ConfigHolderObject>() {
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
   });

   public ConfigTypeConveration() {
   }

   public static <T, U extends T> void registerTypeCreator(Class<U> clazz, Supplier<T> type) {
      typeCreators.put(clazz, type);
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

   public static Object createObject(Class clazz) {
      Supplier supplier = (Supplier)typeCreators.get(clazz);
      if (supplier != null) {
         return supplier.get();
      } else {
         try {
            return clazz.getConstructor().newInstance();
         } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException var3) {
            return null;
         }
      }
   }

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

   public boolean areEqual(T one, T two, @Nullable ConfigKey.ConfigKeyField key) {
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
      };
      registerType(Boolean.TYPE, booleanType);
      registerType(Boolean.class, booleanType);
      registerTypeCreator(Boolean.TYPE, () -> {
         return false;
      });
      registerTypeCreator(Boolean.class, () -> {
         return Boolean.FALSE;
      });
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
      registerTypeCreator(Byte.TYPE, () -> {
         return 0;
      });
      registerTypeCreator(Byte.class, () -> {
         return new Byte((byte)0);
      });
      registerTypeCreator(Short.TYPE, () -> {
         return 0;
      });
      registerTypeCreator(Short.class, () -> {
         return new Short((short)0);
      });
      registerTypeCreator(Integer.TYPE, () -> {
         return Integer.valueOf(0);
      });
      registerTypeCreator(Integer.class, () -> {
         return new Integer(0);
      });
      registerTypeCreator(Long.TYPE, () -> {
         return 0L;
      });
      registerTypeCreator(Long.class, () -> {
         return new Long(0L);
      });
      registerTypeCreator(Float.TYPE, () -> {
         return 0.0F;
      });
      registerTypeCreator(Float.class, () -> {
         return new Float(0.0F);
      });
      registerTypeCreator(Double.TYPE, () -> {
         return 0.0D;
      });
      registerTypeCreator(Double.class, () -> {
         return new Double(0.0D);
      });
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
      });
      registerTypeCreator(String.class, () -> {
         return "";
      });
      registerType(NamedList.class, new ConfigTypeNamedList());
      registerType(Permission.class, new ConfigTypePermission());
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
      }, new ConfigTypeArray());
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
      });
      registerSpecialType((x) -> {
         return List.class.isAssignableFrom(x) || x == ArrayList.class;
      }, new ConfigTypeList());
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
