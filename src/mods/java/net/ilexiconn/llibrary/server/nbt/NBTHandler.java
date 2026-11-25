package net.ilexiconn.llibrary.server.nbt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.nbt.parser.INBTParser;
import net.ilexiconn.llibrary.server.nbt.parser.NBTParsers;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public enum NBTHandler {
   INSTANCE;

   private Map<Class<?>, INBTParser<?, ?>> nbtParserMap = new HashMap();

   private NBTHandler() {
   }

   public <V, T extends NBTBase> void registerNBTParser(Class<V> type, INBTParser<V, T> nbtParser) {
      this.nbtParserMap.put(type, nbtParser);
   }

   public void loadNBTData(Object object, NBTTagCompound compound) {
      Class<?> clazz = object.getClass();

      while(true) {
         Field[] fields = clazz.getDeclaredFields();

         for(Field field : fields) {
            if (field.isAnnotationPresent(NBTProperty.class)) {
               field.setAccessible(true);
               String name = ((NBTProperty)field.getAnnotation(NBTProperty.class)).name();
               if (name.isEmpty()) {
                  name = field.getName();
               }

               this.readFromNBTToField(object, field, name, compound);
            } else if (field.isAnnotationPresent(NBTMutatorProperty.class)) {
               NBTMutatorProperty mutatorProperty = (NBTMutatorProperty)field.getAnnotation(NBTMutatorProperty.class);
               String name = mutatorProperty.name();
               if (name.isEmpty()) {
                  name = field.getName();
               }

               Class<?> type = mutatorProperty.type();
               Method setter = this.getSetter(clazz, name, type, mutatorProperty.setter());
               this.readFromNBTToSetter(object, setter, name, compound);
            }
         }

         if ((clazz = clazz.getSuperclass()) == null) {
            break;
         }
      }

   }

   public void saveNBTData(Object object, NBTTagCompound compound) {
      Class<?> clazz = object.getClass();

      while(true) {
         Field[] fields = clazz.getDeclaredFields();

         for(Field field : fields) {
            if (field.isAnnotationPresent(NBTProperty.class)) {
               field.setAccessible(true);
               String name = ((NBTProperty)field.getAnnotation(NBTProperty.class)).name();
               if (name.isEmpty()) {
                  name = field.getName();
               }

               NBTBase tag = this.writeFieldToNBT(object, field);
               if (tag != null) {
                  compound.setTag(name, tag);
               }
            } else if (field.isAnnotationPresent(NBTMutatorProperty.class)) {
               NBTMutatorProperty mutatorProperty = (NBTMutatorProperty)field.getAnnotation(NBTMutatorProperty.class);
               String name = mutatorProperty.name();
               if (name.isEmpty()) {
                  name = field.getName();
               }

               Class<?> type = mutatorProperty.type();
               Method getter = this.getGetter(clazz, name, type, mutatorProperty.getter());
               NBTBase tag = this.writeGetterValueToNBT(object, getter);
               if (tag != null) {
                  compound.setTag(name, tag);
               }
            }
         }

         if ((clazz = clazz.getSuperclass()) == null) {
            break;
         }
      }

   }

   public <V, T extends NBTBase> INBTParser<V, T> getParserForType(Class<V> type) {
      INBTParser<V, T> nbtParser = NBTParsers.<V, T>getBuiltinParser(type);
      if (nbtParser != null) {
         return nbtParser;
      } else {
         return this.nbtParserMap.containsKey(type) ? (INBTParser)this.nbtParserMap.get(type) : null;
      }
   }

   private <V, T extends NBTBase> T writeFieldToNBT(V object, Field field) {
      V value;
      try {
         value = (V)field.get(object);
      } catch (Exception var5) {
         LLibrary.LOGGER.fatal(var5);
         return (T)null;
      }

      return (T)this.writeToNBT(field.getType(), value);
   }

   private <V, T extends NBTBase> T writeGetterValueToNBT(V object, Method getter) {
      V value;
      try {
         value = (V)getter.invoke(object);
      } catch (Exception var5) {
         LLibrary.LOGGER.fatal(var5);
         return (T)null;
      }

      return (T)this.writeToNBT(getter.getReturnType(), value);
   }

   private <V, T extends NBTBase> T writeToNBT(Class<V> type, V value) {
      if (value == null) {
         return (T)null;
      } else {
         INBTParser<V, T> nbtParser = this.<V, T>getParserForType(type);
         if (nbtParser != null) {
            return nbtParser.parseValue(value);
         } else {
            NBTTagCompound compound = new NBTTagCompound();
            this.saveNBTData(value, compound);
            return (T)compound;
         }
      }
   }

   private void readFromNBTToField(Object object, Field field, String name, NBTTagCompound compound) {
      NBTBase valueNBT = compound.getTag(name);
      if (valueNBT != null) {
         Object value = this.readFromNBT(field.getType(), valueNBT);

         try {
            field.set(object, value);
         } catch (Exception var8) {
            LLibrary.LOGGER.fatal(var8);
         }

      }
   }

   private void readFromNBTToSetter(Object object, Method setter, String name, NBTTagCompound compound) {
      NBTBase valueNBT = compound.getTag(name);
      if (valueNBT != null) {
         Object value = this.readFromNBT(setter.getParameters()[0].getType(), valueNBT);

         try {
            setter.invoke(object, value);
         } catch (Exception var8) {
            LLibrary.LOGGER.fatal(var8);
         }

      }
   }

   private <V, T extends NBTBase> V readFromNBT(Class<V> type, T tag) {
      INBTParser<V, T> nbtParser = this.<V, T>getParserForType(type);
      return (V)(nbtParser != null ? nbtParser.parseTag(tag) : null);
   }

   private Method getSetter(Class<?> clazz, String name, Class<?> type, String setter) {
      String setterName = this.getConventionalSetterName(name, setter);
      return this.resolveMutator(clazz, setterName, type);
   }

   private Method getGetter(Class<?> clazz, String name, Class<?> type, String getter) {
      String getterName = this.getConventionalGetterName(name, type, getter);
      return this.resolveMutator(clazz, getterName);
   }

   private Method resolveMutator(Class<?> clazz, String name, Class<?>... param) {
      Method method = null;
      Class<?> methodClass = clazz;

      while(true) {
         try {
            method = methodClass.getDeclaredMethod(name, param);
            break;
         } catch (NoSuchMethodException var7) {
            if ((methodClass = methodClass.getSuperclass()) == null) {
               break;
            }
         }
      }

      if (method == null) {
         String message = clazz.getName() + "." + name + "(" + (param.length == 0 ? "" : (param[0] == null ? "null" : param[0].getName())) + ")";
         LLibrary.LOGGER.fatal(new RuntimeException(message));
         return null;
      } else {
         method.setAccessible(true);
         return method;
      }
   }

   private String getConventionalSetterName(String name, String setter) {
      return this.getConventionalMutatorName("set", name, setter);
   }

   private String getConventionalGetterName(String name, Class<?> type, String getter) {
      return this.getConventionalMutatorName(type != Boolean.class && type != Boolean.TYPE ? "get" : "is", name, getter);
   }

   private String getConventionalMutatorName(String verb, String name, String mutator) {
      return mutator.isEmpty() ? verb + Character.toUpperCase(name.charAt(0)) + name.substring(1) : mutator;
   }
}
