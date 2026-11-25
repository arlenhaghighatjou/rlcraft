package com.yungnickyoung.minecraft.yungslaw.integration.modules;

import com.yungnickyoung.minecraft.yungslaw.YungsLaw;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.common.Loader;

public abstract class CompatModule {
   protected final String MOD_ID;
   protected boolean enabled = false;
   public List<IBlockState> ores = new ArrayList();

   public CompatModule(String modId) {
      this.MOD_ID = modId;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void disable() {
      this.enabled = false;
   }

   public void enable() {
      this.enabled = true;
   }

   public boolean shouldBeEnabled() {
      return Loader.isModLoaded(this.MOD_ID);
   }

   protected <T> void addIfAbsent(List<T> list, T item) {
      if (!list.contains(item)) {
         list.add(item);
      }

   }

   protected <T> void addIfAbsent(List<T> list, Collection<T> items) {
      items.forEach((item) -> {
         this.addIfAbsent(list, item);
      });
   }

   protected <T> void addIfAbsentAndAble(List<T> list, T item) {
      try {
         this.addIfAbsent(list, item);
      } catch (Exception var4) {
         YungsLaw.LOGGER.error("Unable to add item {} for mod {}: {}", item.toString(), this.MOD_ID, var4);
      }

   }
}
