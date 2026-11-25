package ivorius.ivtoolkit.tools;

import com.google.gson.annotations.SerializedName;

public class IvGsonHelper {
   public IvGsonHelper() {
   }

   public static String serializedName(Enum anEnum) {
      String name = anEnum.name();

      try {
         SerializedName serializedName = (SerializedName)anEnum.getClass().getField(name).getAnnotation(SerializedName.class);
         if (serializedName != null) {
            name = serializedName.value();
         }
      } catch (NoSuchFieldException var3) {
         ;
      }

      return name;
   }

   public static <E extends Enum> E enumForName(String serializedName, E[] values) {
      for(E anEnum : values) {
         if (serializedName(anEnum).equals(serializedName)) {
            return anEnum;
         }
      }

      return (E)null;
   }

   public static <E extends Enum> E enumForNameIgnoreCase(String serializedName, E[] values) {
      for(E anEnum : values) {
         if (serializedName(anEnum).equalsIgnoreCase(serializedName)) {
            return anEnum;
         }
      }

      return (E)null;
   }
}
