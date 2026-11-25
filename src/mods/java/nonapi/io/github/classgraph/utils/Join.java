package nonapi.io.github.classgraph.utils;

public final class Join {
   private Join() {
   }

   public static void join(StringBuilder buf, String addAtBeginning, String sep, String addAtEnd, Iterable<?> iterable) {
      if (!addAtBeginning.isEmpty()) {
         buf.append(addAtBeginning);
      }

      boolean first = true;

      for(Object item : iterable) {
         if (first) {
            first = false;
         } else {
            buf.append(sep);
         }

         buf.append(item == null ? "null" : item.toString());
      }

      if (!addAtEnd.isEmpty()) {
         buf.append(addAtEnd);
      }

   }

   public static String join(String sep, Iterable<?> iterable) {
      StringBuilder buf = new StringBuilder();
      join(buf, "", sep, "", iterable);
      return buf.toString();
   }

   public static String join(String sep, Object... items) {
      StringBuilder buf = new StringBuilder();
      boolean first = true;

      for(Object item : items) {
         if (first) {
            first = false;
         } else {
            buf.append(sep);
         }

         buf.append(item.toString());
      }

      return buf.toString();
   }
}
