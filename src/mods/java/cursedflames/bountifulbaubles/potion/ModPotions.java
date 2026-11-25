package cursedflames.bountifulbaubles.potion;

import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ModPotions {
   public static Potion sin = null;

   public ModPotions() {
   }

   @SubscribeEvent
   public static void registerPotions(Register<Potion> event) {
      IForgeRegistry<Potion> r = event.getRegistry();
      r.register(sin = new PotionSin());
   }
}
