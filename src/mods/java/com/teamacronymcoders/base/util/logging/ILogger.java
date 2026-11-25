package com.teamacronymcoders.base.util.logging;

import org.apache.logging.log4j.Logger;

public interface ILogger {
   void warning(String var1);

   void info(String var1);

   void fatal(String var1);

   void error(String var1);

   void error(Throwable var1);

   void devWarning(String var1);

   void devInfo(String var1);

   void devFatal(String var1);

   void devError(String var1);

   Logger getLogger();
}
