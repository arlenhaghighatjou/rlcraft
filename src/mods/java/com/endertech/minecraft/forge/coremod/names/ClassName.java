package com.endertech.minecraft.forge.coremod.names;

public class ClassName extends Name {
   public static final String JAVA_DELIMITER = ".";
   public static final String INTERNAL_DELIMITER = "/";
   public final String java;

   protected ClassName(String normal, String obfuscated) {
      super(normal, obfuscated);
      this.java = getJavaName(normal);
   }

   public static String join(String... names) {
      return String.join(".", names);
   }

   public static ClassName from(String name) {
      String normal = getInternalName(name);
      String obfuscated = (String)NotchMcp.CLASSES.getOrDefault(normal, normal);
      return new ClassName(normal, obfuscated);
   }

   public static String getInternalName(String javaName) {
      return javaName.replace(".", "/");
   }

   public static String getJavaName(String internalName) {
      return internalName.replace("/", ".");
   }

   public String getSimpleName() {
      return this.java.substring(this.java.lastIndexOf(".") + 1);
   }

   public boolean complyWith(String string) {
      return super.complyWith(string) || this.java.equals(string);
   }
}
