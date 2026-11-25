package leviathan143.loottweaker.common;

public interface ErrorHandler {
   void error(String var1, Object... var2);

   void error(String var1);

   void warn(String var1, Object... var2);

   void warn(String var1);
}
