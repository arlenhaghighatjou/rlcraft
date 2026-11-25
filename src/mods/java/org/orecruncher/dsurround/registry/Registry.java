package org.orecruncher.dsurround.registry;

import javax.annotation.Nonnull;
import net.minecraftforge.common.MinecraftForge;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.registry.config.ConfigData;
import org.orecruncher.dsurround.registry.config.ModConfiguration;

public abstract class Registry {
   private final String name;

   public Registry(@Nonnull String name) {
      this.name = name;
      RegistryManager.REGISTRIES.add(this);
      MinecraftForge.EVENT_BUS.register(this);
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   protected void preInit() {
   }

   protected abstract void init(@Nonnull ModConfiguration var1);

   protected void postInit() {
   }

   protected void complete() {
   }

   final void initialize(@Nonnull ConfigData data) {
      ModBase.log().info("Initializing registry [%s]", new Object[]{this.getName()});
      this.preInit();

      for(ModConfiguration mcf : data) {
         this.init(mcf);
      }

      this.postInit();
      MinecraftForge.EVENT_BUS.post(new RegistryDataEvent.Reload(this));
   }

   public void reload() {
      ModBase.log().info("Reloading registry [%s]...", new Object[]{this.getName()});
      this.preInit();

      for(ModConfiguration mcf : (ConfigData)RegistryManager.DATA.get()) {
         this.init(mcf);
      }

      this.postInit();
      this.complete();
   }
}
