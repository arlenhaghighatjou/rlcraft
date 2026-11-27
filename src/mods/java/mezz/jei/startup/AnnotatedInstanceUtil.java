package mezz.jei.startup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import mezz.jei.api.IModPlugin;
import mezz.jei.plugins.jei.JEIInternalPlugin;
import mezz.jei.plugins.vanilla.VanillaPlugin;

public final class AnnotatedInstanceUtil {
   private static final List<Supplier<IModPlugin>> MOD_PLUGINS = new ArrayList();

   private AnnotatedInstanceUtil() {
   }

   public static List<IModPlugin> getModPlugins() {
      List<IModPlugin> instances = new ArrayList();

      for(int i = 0; i < MOD_PLUGINS.size(); ++i) {
         instances.add(MOD_PLUGINS.get(i).get());
      }

      return instances;
   }

   static {
      MOD_PLUGINS.add(VanillaPlugin::new);
      MOD_PLUGINS.add(JEIInternalPlugin::new);
      MOD_PLUGINS.add(com.alcatrazescapee.notreepunching.integration.jei.JeiPlugin::new);
      MOD_PLUGINS.add(gigaherz.toolbelt.integration.JeiPlugin::new);
      MOD_PLUGINS.add(lykrast.defiledlands.common.compat.jei.DefiledLandsJEI::new);
      MOD_PLUGINS.add(net.theawesomegem.fishingmadebetter.client.jei.JeiPlugin::new);
      MOD_PLUGINS.add(org.jwaresoftware.mcmods.armorunder.integration.jei.ModJeiPlugin::new);
      MOD_PLUGINS.add(vazkii.arl.client.jei.JeiPlugin::new);
      MOD_PLUGINS.add(com.oblivioussp.spartanweaponry.compat.jei.SpartanWeaponryPlugin::new);
      MOD_PLUGINS.add(knightminer.inspirations.plugins.jei.JEIPlugin::new);
   }
}
