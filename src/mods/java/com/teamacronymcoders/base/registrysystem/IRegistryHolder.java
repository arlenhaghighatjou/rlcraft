package com.teamacronymcoders.base.registrysystem;

import java.util.Map;

public interface IRegistryHolder {
   Map<String, Registry> getAllRegistries();

   void addRegistry(String var1, Registry var2);

   <R extends Registry> R getRegistry(Class<R> var1, String var2);
}
