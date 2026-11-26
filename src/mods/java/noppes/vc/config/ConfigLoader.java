package noppes.vc.config;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.function.Supplier;
import java.util.function.Consumer;
import java.io.StringReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.LinkedList;

public class ConfigLoader {
   private boolean updateFile = false;
   private VFile2 dir;
   private String fileName;
   private LinkedList<ConfigLoader.Prop> configFields;

   public ConfigLoader(VFile2 dir, String fileName, ConfigLoader.Prop... props) {
      this.dir = dir;
      this.configFields = new LinkedList();
      this.fileName = fileName + ".cfg";

      for(ConfigLoader.Prop field : props) {
         this.configFields.add(field);
      }

   }

   public void loadConfig() {
      try {
         VFile2 configFile = new VFile2(this.dir, this.fileName);
         HashMap<String, ConfigLoader.Prop> types = new HashMap();

         for(ConfigLoader.Prop field : this.configFields) {
            types.put(field.name, field);
         }

         if (configFile.exists()) {
            HashMap<String, Object> properties = this.parseConfig(configFile, types);

            for(String prop : properties.keySet()) {
               ConfigLoader.Prop field = (ConfigLoader.Prop)types.get(prop);
               Object obj = properties.get(prop);
               if (!obj.equals(field.getter.get())) {
                  field.setter.accept(obj);
               }
            }

            for(String type : types.keySet()) {
               if (!properties.containsKey(type)) {
                  this.updateFile = true;
               }
            }
         } else {
            this.updateFile = true;
         }
      } catch (Exception var8) {
         this.updateFile = true;
      }

      if (this.updateFile) {
         this.updateConfig();
      }

      this.updateFile = false;
   }

   private HashMap<String, Object> parseConfig(VFile2 file, HashMap<String, ConfigLoader.Prop> types) throws Exception {
      HashMap<String, Object> config = new HashMap();
      BufferedReader reader = new BufferedReader(new StringReader(file.getAllChars()));

      String strLine;
      while((strLine = reader.readLine()) != null) {
         if (!strLine.startsWith("#") && strLine.length() != 0) {
            int index = strLine.indexOf("=");
            if (index > 0 && index != strLine.length()) {
               String name = strLine.substring(0, index);
               String prop = strLine.substring(index + 1);
               if (!types.containsKey(name)) {
                  this.updateFile = true;
               } else {
                  Object obj = null;
                  Class<?> class2 = ((ConfigLoader.Prop)types.get(name)).type;
                  if (class2 == String.class) {
                     obj = prop;
                  } else if (class2 == Integer.TYPE) {
                     obj = Integer.parseInt(prop);
                  } else if (class2 == Short.TYPE) {
                     obj = Short.parseShort(prop);
                  } else if (class2 == Byte.TYPE) {
                     obj = Byte.parseByte(prop);
                  } else if (class2 == Boolean.TYPE) {
                     obj = Boolean.parseBoolean(prop);
                  } else if (class2 == Float.TYPE) {
                     obj = Float.parseFloat(prop);
                  } else if (class2 == Double.TYPE) {
                     obj = Double.parseDouble(prop);
                  }

                  if (obj != null) {
                     config.put(name, obj);
                  }
               }
            } else {
               this.updateFile = true;
            }
         }
      }

      reader.close();
      return config;
   }

   public void updateConfig() {
      VFile2 file = new VFile2(this.dir, this.fileName);
      StringBuilder out = new StringBuilder();

      for(ConfigLoader.Prop field : this.configFields) {
         if (field.info.length() != 0) {
            out.append("#" + field.info + System.getProperty("line.separator"));
         }

         out.append(field.name + "=" + field.getter.get().toString() + System.getProperty("line.separator"));
         out.append(System.getProperty("line.separator"));
      }

      file.setAllChars(out.toString());
   }

   public static class Prop {
      public final String name;
      public final String info;
      public final Class<?> type;
      public final Supplier<Object> getter;
      public final Consumer<Object> setter;

      public Prop(String name, String info, Class<?> type, Supplier<Object> getter, Consumer<Object> setter) {
         this.name = name;
         this.info = info;
         this.type = type;
         this.getter = getter;
         this.setter = setter;
      }
   }
}
