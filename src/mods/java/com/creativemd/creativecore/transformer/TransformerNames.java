package com.creativemd.creativecore.transformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;

public class TransformerNames {
   public static boolean obfuscated = false;
   private static ArrayList<TransformerNames.ClassName> classes = new ArrayList();
   private static ArrayList<TransformerNames.MethodName> methods = new ArrayList();
   private static ArrayList<TransformerNames.FieldName> fields = new ArrayList();

   public TransformerNames() {
   }

   public static void emptyLists() {
      classes.clear();
      fields.clear();
      methods.clear();
   }

   public static String patchDESC(String desc) {
      if (obfuscated) {
         for(int i = 0; i < classes.size(); ++i) {
            desc = desc.replace(((TransformerNames.ClassName)classes.get(i)).name, ((TransformerNames.ClassName)classes.get(i)).obfname);
         }
      }

      return desc;
   }

   public static String patchClassName(String className) {
      if (obfuscated) {
         for(int i = 0; i < classes.size(); ++i) {
            if (className.equals(((TransformerNames.ClassName)classes.get(i)).name)) {
               return ((TransformerNames.ClassName)classes.get(i)).obfname;
            }
         }
      }

      return className;
   }

   public static String patchFieldName(String fieldName, Class parent) {
      return patchFieldName(fieldName, patchClassName(parent.getName()));
   }

   public static String patchFieldName(String fieldName, String parentName) {
      if (obfuscated) {
         for(int i = 0; i < fields.size(); ++i) {
            if (fieldName.equals(((TransformerNames.FieldName)fields.get(i)).name) && parentName.equals(((TransformerNames.FieldName)fields.get(i)).parent)) {
               return ((TransformerNames.FieldName)fields.get(i)).obfname;
            }
         }
      }

      return fieldName;
   }

   public static String patchMethodName(String methodName, String desc, Class parent) {
      return patchMethodName(methodName, desc, patchClassName(parent.getName()));
   }

   public static String patchMethodName(String methodName, String desc, String parentName) {
      if (obfuscated) {
         String obfdesc = patchDESC(desc);

         for(int i = 0; i < methods.size(); ++i) {
            if (methodName.equals(((TransformerNames.MethodName)methods.get(i)).name) && parentName.equals(((TransformerNames.MethodName)methods.get(i)).parent) && ((TransformerNames.MethodName)methods.get(i)).desc.equals(obfdesc)) {
               return ((TransformerNames.MethodName)methods.get(i)).obfname;
            }
         }
      }

      return methodName;
   }

   static {
      try {
         InputStreamReader reader = new InputStreamReader(TransformerNames.class.getResourceAsStream("notch-mcp.srg"));
         BufferedReader bf = new BufferedReader(reader);
         String line = null;

         while((line = bf.readLine()) != null) {
            String[] parts = line.split(" ");
            if (line.startsWith("CL:")) {
               if (parts.length == 3) {
                  classes.add(new TransformerNames.ClassName(parts[2], parts[1]));
               }
            } else if (line.startsWith("FD:")) {
               if (parts.length == 3) {
                  String[] splitted = parts[2].split("/");
                  String name = splitted[splitted.length - 1];
                  splitted = parts[1].split("/");
                  fields.add(new TransformerNames.FieldName(name, splitted[1], splitted[0]));
               }
            } else if (line.startsWith("MD:") && parts.length == 5) {
               String[] splitted = parts[3].split("/");
               String name = splitted[splitted.length - 1];
               splitted = parts[1].split("/");
               methods.add(new TransformerNames.MethodName(name, splitted[1], splitted[0], parts[2]));
            }
         }

         bf.close();
         reader.close();
         classes.sort(new Comparator<TransformerNames.ClassName>() {
            public int compare(TransformerNames.ClassName arg0, TransformerNames.ClassName arg1) {
               if (arg0.name.length() > arg1.name.length()) {
                  return -1;
               } else {
                  return arg0.name.length() == arg1.name.length() ? 0 : 1;
               }
            }
         });
      } catch (IOException var6) {
         var6.printStackTrace();
      }

   }

   private static class ClassName {
      public final String name;
      public final String obfname;

      public ClassName(String name, String obfname) {
         this.name = name;
         this.obfname = obfname;
      }
   }

   private static class FieldName extends TransformerNames.ClassName {
      public final String parent;

      public FieldName(String name, String obfname, String parent) {
         super(name, obfname);
         this.parent = parent;
      }
   }

   private static class MethodName extends TransformerNames.FieldName {
      public final String desc;

      public MethodName(String name, String obfname, String parent, String desc) {
         super(name, obfname, parent);
         this.desc = desc;
      }
   }
}
