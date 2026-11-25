package slimeknights.mantle.typesafe.config.impl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import slimeknights.mantle.typesafe.config.Config;
import slimeknights.mantle.typesafe.config.ConfigException;
import slimeknights.mantle.typesafe.config.ConfigList;
import slimeknights.mantle.typesafe.config.ConfigMemorySize;
import slimeknights.mantle.typesafe.config.ConfigObject;
import slimeknights.mantle.typesafe.config.ConfigValue;
import slimeknights.mantle.typesafe.config.ConfigValueType;
import slimeknights.mantle.typesafe.config.Optional;

public class ConfigBeanImpl {
   public ConfigBeanImpl() {
   }

   public static <T> T createInternal(Config config, Class<T> clazz) {
      if (((SimpleConfig)config).root().resolveStatus() != ResolveStatus.RESOLVED) {
         throw new ConfigException.NotResolved("need to Config#resolve() a config before using it to initialize a bean, see the API docs for Config#resolve()");
      } else {
         Map<String, AbstractConfigValue> configProps = new HashMap();
         Map<String, String> originalNames = new HashMap();

         for(Entry<String, ConfigValue> configProp : config.root().entrySet()) {
            String originalName = (String)configProp.getKey();
            String camelName = ConfigImplUtil.toCamelCase(originalName);
            if (!originalNames.containsKey(camelName) || originalName.equals(camelName)) {
               configProps.put(camelName, (AbstractConfigValue)configProp.getValue());
               originalNames.put(camelName, originalName);
            }
         }

         BeanInfo beanInfo = null;

         try {
            beanInfo = Introspector.getBeanInfo(clazz);
         } catch (IntrospectionException var18) {
            throw new ConfigException.BadBean("Could not get bean information for class " + clazz.getName(), var18);
         }

         try {
            List<PropertyDescriptor> beanProps = new ArrayList();

            for(PropertyDescriptor beanProp : beanInfo.getPropertyDescriptors()) {
               if (beanProp.getReadMethod() != null && beanProp.getWriteMethod() != null) {
                  beanProps.add(beanProp);
               }
            }

            List<ConfigException.ValidationProblem> problems = new ArrayList();

            for(PropertyDescriptor beanProp : beanProps) {
               Method setter = beanProp.getWriteMethod();
               Class<?> parameterClass = setter.getParameterTypes()[0];
               ConfigValueType expectedType = getValueTypeOrNull(parameterClass);
               if (expectedType != null) {
                  String name = (String)originalNames.get(beanProp.getName());
                  if (name == null) {
                     name = beanProp.getName();
                  }

                  Path path = Path.newKey(name);
                  AbstractConfigValue configValue = (AbstractConfigValue)configProps.get(beanProp.getName());
                  if (configValue != null) {
                     SimpleConfig.checkValid(path, expectedType, configValue, problems);
                  } else if (!isOptionalProperty(clazz, beanProp)) {
                     SimpleConfig.addMissing(problems, expectedType, path, config.origin());
                  }
               }
            }

            if (!problems.isEmpty()) {
               throw new ConfigException.ValidationFailed(problems);
            } else {
               T bean = (T)clazz.newInstance();

               for(PropertyDescriptor beanProp : beanProps) {
                  Method setter = beanProp.getWriteMethod();
                  Type parameterType = setter.getGenericParameterTypes()[0];
                  Class<?> parameterClass = setter.getParameterTypes()[0];
                  String configPropName = (String)originalNames.get(beanProp.getName());
                  if (configPropName == null) {
                     if (!isOptionalProperty(clazz, beanProp)) {
                        throw new ConfigException.Missing(beanProp.getName());
                     }
                  } else {
                     Object unwrapped = getValue(clazz, parameterType, parameterClass, config, configPropName);
                     setter.invoke(bean, unwrapped);
                  }
               }

               return bean;
            }
         } catch (InstantiationException var15) {
            throw new ConfigException.BadBean(clazz.getName() + " needs a public no-args constructor to be used as a bean", var15);
         } catch (IllegalAccessException var16) {
            throw new ConfigException.BadBean(clazz.getName() + " getters and setters are not accessible, they must be for use as a bean", var16);
         } catch (InvocationTargetException var17) {
            throw new ConfigException.BadBean("Calling bean method on " + clazz.getName() + " caused an exception", var17);
         }
      }
   }

   private static Object getValue(Class<?> beanClass, Type parameterType, Class<?> parameterClass, Config config, String configPropName) {
      if (parameterClass != Boolean.class && parameterClass != Boolean.TYPE) {
         if (parameterClass != Integer.class && parameterClass != Integer.TYPE) {
            if (parameterClass != Double.class && parameterClass != Double.TYPE) {
               if (parameterClass != Long.class && parameterClass != Long.TYPE) {
                  if (parameterClass == String.class) {
                     return config.getString(configPropName);
                  } else if (parameterClass == Duration.class) {
                     return config.getDuration(configPropName);
                  } else if (parameterClass == ConfigMemorySize.class) {
                     return config.getMemorySize(configPropName);
                  } else if (parameterClass == Object.class) {
                     return config.getAnyRef(configPropName);
                  } else if (parameterClass == List.class) {
                     return getListValue(beanClass, parameterType, parameterClass, config, configPropName);
                  } else if (parameterClass == Map.class) {
                     Type[] typeArgs = ((ParameterizedType)parameterType).getActualTypeArguments();
                     if (typeArgs[0] == String.class && typeArgs[1] == Object.class) {
                        return config.getObject(configPropName).unwrapped();
                     } else {
                        throw new ConfigException.BadBean("Bean property '" + configPropName + "' of class " + beanClass.getName() + " has unsupported Map<" + typeArgs[0] + "," + typeArgs[1] + ">, only Map<String,Object> is supported right now");
                     }
                  } else if (parameterClass == Config.class) {
                     return config.getConfig(configPropName);
                  } else if (parameterClass == ConfigObject.class) {
                     return config.getObject(configPropName);
                  } else if (parameterClass == ConfigValue.class) {
                     return config.getValue(configPropName);
                  } else if (parameterClass == ConfigList.class) {
                     return config.getList(configPropName);
                  } else if (parameterClass.isEnum()) {
                     Enum enumValue = config.getEnum(parameterClass, configPropName);
                     return enumValue;
                  } else if (hasAtLeastOneBeanProperty(parameterClass)) {
                     return createInternal(config.getConfig(configPropName), parameterClass);
                  } else {
                     throw new ConfigException.BadBean("Bean property " + configPropName + " of class " + beanClass.getName() + " has unsupported type " + parameterType);
                  }
               } else {
                  return config.getLong(configPropName);
               }
            } else {
               return config.getDouble(configPropName);
            }
         } else {
            return config.getInt(configPropName);
         }
      } else {
         return config.getBoolean(configPropName);
      }
   }

   private static Object getListValue(Class<?> beanClass, Type parameterType, Class<?> parameterClass, Config config, String configPropName) {
      Type elementType = ((ParameterizedType)parameterType).getActualTypeArguments()[0];
      if (elementType == Boolean.class) {
         return config.getBooleanList(configPropName);
      } else if (elementType == Integer.class) {
         return config.getIntList(configPropName);
      } else if (elementType == Double.class) {
         return config.getDoubleList(configPropName);
      } else if (elementType == Long.class) {
         return config.getLongList(configPropName);
      } else if (elementType == String.class) {
         return config.getStringList(configPropName);
      } else if (elementType == Duration.class) {
         return config.getDurationList(configPropName);
      } else if (elementType == ConfigMemorySize.class) {
         return config.getMemorySizeList(configPropName);
      } else if (elementType == Object.class) {
         return config.getAnyRefList(configPropName);
      } else if (elementType == Config.class) {
         return config.getConfigList(configPropName);
      } else if (elementType == ConfigObject.class) {
         return config.getObjectList(configPropName);
      } else if (elementType == ConfigValue.class) {
         return config.getList(configPropName);
      } else if (((Class)elementType).isEnum()) {
         List<Enum> enumValues = config.<Enum>getEnumList((Class)elementType, configPropName);
         return enumValues;
      } else if (!hasAtLeastOneBeanProperty((Class)elementType)) {
         throw new ConfigException.BadBean("Bean property '" + configPropName + "' of class " + beanClass.getName() + " has unsupported list element type " + elementType);
      } else {
         List<Object> beanList = new ArrayList();

         for(Config listMember : config.getConfigList(configPropName)) {
            beanList.add(createInternal(listMember, (Class)elementType));
         }

         return beanList;
      }
   }

   private static ConfigValueType getValueTypeOrNull(Class<?> parameterClass) {
      if (parameterClass != Boolean.class && parameterClass != Boolean.TYPE) {
         if (parameterClass != Integer.class && parameterClass != Integer.TYPE) {
            if (parameterClass != Double.class && parameterClass != Double.TYPE) {
               if (parameterClass != Long.class && parameterClass != Long.TYPE) {
                  if (parameterClass == String.class) {
                     return ConfigValueType.STRING;
                  } else if (parameterClass == Duration.class) {
                     return null;
                  } else if (parameterClass == ConfigMemorySize.class) {
                     return null;
                  } else if (parameterClass == List.class) {
                     return ConfigValueType.LIST;
                  } else if (parameterClass == Map.class) {
                     return ConfigValueType.OBJECT;
                  } else if (parameterClass == Config.class) {
                     return ConfigValueType.OBJECT;
                  } else if (parameterClass == ConfigObject.class) {
                     return ConfigValueType.OBJECT;
                  } else {
                     return parameterClass == ConfigList.class ? ConfigValueType.LIST : null;
                  }
               } else {
                  return ConfigValueType.NUMBER;
               }
            } else {
               return ConfigValueType.NUMBER;
            }
         } else {
            return ConfigValueType.NUMBER;
         }
      } else {
         return ConfigValueType.BOOLEAN;
      }
   }

   private static boolean hasAtLeastOneBeanProperty(Class<?> clazz) {
      BeanInfo beanInfo = null;

      try {
         beanInfo = Introspector.getBeanInfo(clazz);
      } catch (IntrospectionException var6) {
         return false;
      }

      for(PropertyDescriptor beanProp : beanInfo.getPropertyDescriptors()) {
         if (beanProp.getReadMethod() != null && beanProp.getWriteMethod() != null) {
            return true;
         }
      }

      return false;
   }

   private static boolean isOptionalProperty(Class beanClass, PropertyDescriptor beanProp) {
      Field field = getField(beanClass, beanProp.getName());
      return ((Optional[])field.getAnnotationsByType(Optional.class)).length > 0;
   }

   private static Field getField(Class beanClass, String fieldName) {
      try {
         Field field = beanClass.getDeclaredField(fieldName);
         field.setAccessible(true);
         return field;
      } catch (NoSuchFieldException var3) {
         beanClass = beanClass.getSuperclass();
         return beanClass == null ? null : getField(beanClass, fieldName);
      }
   }
}
