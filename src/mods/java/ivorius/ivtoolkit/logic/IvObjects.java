package ivorius.ivtoolkit.logic;

public class IvObjects {
   public IvObjects() {
   }

   public static boolean equals(Object a, Object b) {
      boolean var10000;
      if (a != b) {
         label29: {
            if (a == null) {
               if (b.equals(a)) {
                  break label29;
               }
            } else if (a.equals(b)) {
               break label29;
            }

            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }
}
