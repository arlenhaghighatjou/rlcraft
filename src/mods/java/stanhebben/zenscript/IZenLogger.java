package stanhebben.zenscript;

public interface IZenLogger {
   void error(String var1);

   void error(String var1, Throwable var2);

   void warning(String var1);

   void info(String var1);
}
