package crafttweaker.mc1120.mods;

import crafttweaker.api.mods.ILoadedMods;
import crafttweaker.api.mods.IMod;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import stanhebben.zenscript.util.MappingIterator;

public class MCLoadedMods implements ILoadedMods {
   public MCLoadedMods() {
   }

   public boolean contains(String name) {
      return Loader.isModLoaded(name);
   }

   public IMod get(String name) {
      for(ModContainer mod : Loader.instance().getActiveModList()) {
         if (mod.getModId().equals(name)) {
            return new MCMod(mod);
         }
      }

      return null;
   }

   public Iterator<IMod> iterator() {
      List<ModContainer> mods = Loader.instance().getActiveModList();
      return new MappingIterator<ModContainer, IMod>(mods.iterator()) {
         public IMod map(ModContainer value) {
            return new MCMod(value);
         }
      };
   }
}
