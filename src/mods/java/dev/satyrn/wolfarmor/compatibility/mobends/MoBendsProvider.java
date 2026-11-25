package dev.satyrn.wolfarmor.compatibility.mobends;

import dev.satyrn.wolfarmor.api.compatibility.CompatibilityProvider;
import dev.satyrn.wolfarmor.api.compatibility.Provider;
import goblinbob.mobends.core.addon.AddonHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Provider("mobends")
public class MoBendsProvider extends CompatibilityProvider {
   public MoBendsProvider() {
   }

   @SideOnly(Side.CLIENT)
   public void init_Client(FMLInitializationEvent event) {
      super.init_Client(event);
      AddonHelper.registerAddon("wolfarmor", new WolfArmorAndStorageAddon());
   }
}
