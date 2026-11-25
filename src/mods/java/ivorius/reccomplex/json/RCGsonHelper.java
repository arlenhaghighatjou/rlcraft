package ivorius.reccomplex.json;

import ivorius.ivtoolkit.tools.IvGsonHelper;

public class RCGsonHelper {
   public RCGsonHelper() {
   }

   public static <E extends Enum> E enumForNameIgnoreCase(String serializedName, E[] values) {
      for(E anEnum : values) {
         if (IvGsonHelper.serializedName(anEnum).equalsIgnoreCase(serializedName)) {
            return anEnum;
         }
      }

      return (E)null;
   }
}
