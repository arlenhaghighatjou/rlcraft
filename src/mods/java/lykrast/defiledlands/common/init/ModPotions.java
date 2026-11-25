package lykrast.defiledlands.common.init;

import java.util.ArrayList;
import java.util.List;
import lykrast.defiledlands.common.potion.PotionGeneric;
import lykrast.defiledlands.common.util.LocUtils;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ModPotions {
   public static Potion vulnerability = register(new PotionGeneric(true, 7304841, 0, -1), "vulnerability");
   public static Potion grounded = register(new PotionGeneric(true, 6250071, 1, 0), "grounded");
   public static Potion bleeding = register(new PotionGeneric(true, 16711680, 2, 40), "bleeding");
   private static List<Potion> potionList = new ArrayList();

   public ModPotions() {
   }

   @SubscribeEvent
   public static void registerPotions(Register<Potion> event) {
      for(Potion p : potionList) {
         event.getRegistry().register(p);
      }

      potionList = null;
   }

   public static Potion register(Potion p, String name) {
      p.setPotionName("potion.effect." + LocUtils.prefix(name));
      p.setRegistryName("defiledlands", name);
      potionList.add(p);
      return p;
   }
}
