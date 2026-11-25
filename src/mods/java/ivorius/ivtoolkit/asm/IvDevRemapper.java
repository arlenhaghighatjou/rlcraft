package ivorius.ivtoolkit.asm;

import java.util.Hashtable;

public class IvDevRemapper {
   public static Hashtable<String, String> fakeMappings = new Hashtable();
   private static boolean isSetUp;

   public IvDevRemapper() {
   }

   public static void setUp() {
      isSetUp = true;
   }

   public boolean isSetUp() {
      return isSetUp;
   }

   public static String getSRGName(String name) {
      String mapping = (String)fakeMappings.get(name);
      return mapping != null ? mapping : name;
   }
}
