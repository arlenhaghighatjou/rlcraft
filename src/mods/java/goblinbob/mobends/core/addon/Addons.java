package goblinbob.mobends.core.addon;

import goblinbob.mobends.core.CoreClient;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Addons {
   private static final Addons INSTANCE = new Addons();
   private final List<IAddon> addons = new ArrayList();

   public Addons() {
   }

   public static void registerAddon(String modId, IAddon addon) {
      if (!INSTANCE.addons.contains(addon)) {
         INSTANCE.addons.add(addon);
         if (CoreClient.getInstance() != null) {
            addon.registerContent(new AddonAnimationRegistry(modId));
         }

      }
   }

   public static Iterable<IAddon> getRegistered() {
      return INSTANCE.addons;
   }

   public static void onRenderTick(float partialTicks) {
      INSTANCE.addons.forEach((addon) -> {
         addon.onRenderTick(partialTicks);
      });
   }

   public static void onClientTick() {
      INSTANCE.addons.forEach(IAddon::onClientTick);
   }

   public static void onRefresh() {
      INSTANCE.addons.forEach(IAddon::onRefresh);
   }
}
