package stanhebben.zenscript;

import stanhebben.zenscript.util.ZenPosition;

public interface IZenErrorLogger extends IZenLogger {
   void error(ZenPosition var1, String var2);

   void warning(ZenPosition var1, String var2);

   void info(ZenPosition var1, String var2);
}
