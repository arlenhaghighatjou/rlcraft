package com.endertech.minecraft.forge.units;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.UnitConfig;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class UnitsInit<T> {
   private final ForgeMod mod;
   protected final Set<T> units = new HashSet();

   public UnitsInit(ForgeMod mod) {
      this.mod = mod;
   }

   public T add(T unit) {
      this.units.add(unit);
      return unit;
   }

   public void add(T... units) {
      for(T unit : units) {
         this.add(unit);
      }

   }

   public Set<T> getAll() {
      return Collections.unmodifiableSet(this.units);
   }

   public ForgeMod getMod() {
      return this.mod;
   }

   public static Path getConfigsDir(ForgeMod mod, Class<?> clazz) {
      return mod.getConfig().getConfigDir().resolve(clazz.getSimpleName());
   }

   public Path getConfigsDir() {
      return getConfigsDir(this.getMod(), this.getClass());
   }

   public UnitConfig createConfigFor(Class<? extends T> clazz) {
      return new UnitConfig(this.getConfigsDir(), IForgeUnit.getClassRegName(clazz));
   }
}
