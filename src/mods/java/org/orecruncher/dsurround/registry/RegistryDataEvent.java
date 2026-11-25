package org.orecruncher.dsurround.registry;

import javax.annotation.Nonnull;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RegistryDataEvent extends Event {
   public RegistryDataEvent() {
   }

   public static class Reload extends RegistryDataEvent {
      public final Registry reg;

      public Reload(@Nonnull Registry reg) {
         this.reg = reg;
      }
   }

   @SideOnly(Side.CLIENT)
   public static class Resources extends RegistryDataEvent {
      public final IResourceManager manager;

      public Resources(IResourceManager manager) {
         this.manager = manager;
      }
   }
}
