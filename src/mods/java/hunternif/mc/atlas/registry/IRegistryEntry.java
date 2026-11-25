package hunternif.mc.atlas.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public interface IRegistryEntry {
   void setRegistryName(ResourceLocation var1);

   ResourceLocation getRegistryName();

   public static class Impl implements IRegistryEntry {
      private ResourceLocation registryName = null;

      public Impl() {
      }

      public final void setRegistryName(String name) {
         int index = name.lastIndexOf(58);
         String prefix = index == -1 ? "" : name.substring(0, index);
         name = index == -1 ? name : name.substring(index + 1);
         if (index == -1) {
            ModContainer mc = Loader.instance().activeModContainer();
            prefix = mc != null && (!(mc instanceof InjectedModContainer) || !(((InjectedModContainer)mc).wrappedContainer instanceof FMLContainer)) ? mc.getModId().toLowerCase() : "minecraft";
         }

         this.registryName = new ResourceLocation(prefix, name);
      }

      public final void setRegistryName(ResourceLocation name) {
         this.setRegistryName(name.toString());
      }

      public final void setRegistryName(String modID, String name) {
         this.setRegistryName(modID + ":" + name);
      }

      public final ResourceLocation getRegistryName() {
         return this.registryName;
      }
   }
}
