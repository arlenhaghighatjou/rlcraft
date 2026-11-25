package com.teamacronymcoders.base.registrysystem.config;

@FunctionalInterface
public interface IConfigListener {
   void onConfigChange(String var1, ConfigEntry var2);
}
