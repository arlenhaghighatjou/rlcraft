package net.silentchaos512.lib.collection;

import net.minecraftforge.common.config.Configuration;

public interface IMatchList<T> {
   boolean matches(T var1);

   void loadConfig(Configuration var1, String var2, String var3, String var4);
}
