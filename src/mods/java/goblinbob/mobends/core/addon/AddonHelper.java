package goblinbob.mobends.core.addon;

public class AddonHelper {
   public AddonHelper() {
   }

   public static void registerAddon(String modId, IAddon addon) {
      Addons.registerAddon(modId, addon);
   }
}
