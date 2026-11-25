package shadows.placebo;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.model.ModelLoader;
import shadows.placebo.client.RenamedStateMapper;

public class ClientProxy extends Proxy {
   public ClientProxy() {
   }

   public String translate(String lang, Object... args) {
      return I18n.format(lang, args);
   }

   public void useRenamedMapper(Block b, String path) {
      ModelLoader.setCustomStateMapper(b, new RenamedStateMapper(b.getRegistryName().getResourceDomain(), path));
   }

   public void useRenamedMapper(Block b, String path, String append) {
      ModelLoader.setCustomStateMapper(b, new RenamedStateMapper(b.getRegistryName().getResourceDomain(), path, append));
   }

   public void useRenamedMapper(Block b, String path, String append, String variant) {
      ModelLoader.setCustomStateMapper(b, new RenamedStateMapper(b.getRegistryName().getResourceDomain(), path, append, variant));
   }
}
