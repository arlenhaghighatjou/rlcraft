package yalter.mousetweaks;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ReflectionCache {
   private HashMap<String, Class> classes = new HashMap();
   private HashMap<String, Method> methods = new HashMap();
   private HashMap<String, Field> fields = new HashMap();

   public ReflectionCache() {
   }

   public boolean isInstance(Object obj, String name) {
      Class clazz = (Class)this.classes.get(name);
      return clazz.isInstance(obj);
   }

   public Object invokeMethod(Object obj, String name, Object... args) throws InvocationTargetException {
      Method method = (Method)this.methods.get(name);

      try {
         return method.invoke(obj, args);
      } catch (IllegalAccessException var7) {
         b crashreport = b.a(var7, "Invoking method in MouseTweaks' reflection");
         throw new f(crashreport);
      }
   }

   public Object getFieldValue(Object obj, String name) {
      Field field = (Field)this.fields.get(name);

      try {
         return field.get(obj);
      } catch (IllegalAccessException var6) {
         b crashreport = b.a(var6, "Getting field value in MouseTweaks' reflection");
         throw new f(crashreport);
      }
   }

   public void setFieldValue(Object obj, String name, Object value) {
      Field field = (Field)this.fields.get(name);

      try {
         field.set(obj, value);
      } catch (IllegalAccessException var7) {
         b crashreport = b.a(var7, "Setting field value in MouseTweaks' reflection");
         throw new f(crashreport);
      }
   }

   void storeClass(String name, Class clazz) {
      this.classes.put(name, clazz);
   }

   void storeMethod(String name, Method method) {
      this.methods.put(name, method);
   }

   void storeField(String name, Field field) {
      this.fields.put(name, field);
   }
}
