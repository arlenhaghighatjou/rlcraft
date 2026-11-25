package de.Whitedraco.switchbow;

import de.Whitedraco.switchbow.potion.PotionFreeze;
import de.Whitedraco.switchbow.potion.PotionLove;
import java.util.ArrayList;
import net.minecraft.potion.Potion;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.RegistryEvent.Register;

public class PotionInit {
   public static Potion PotionFreeze;
   public static Potion PotionLove;
   public static ArrayList<Potion> potionList = new ArrayList();

   public PotionInit() {
   }

   public static void preparePotion() {
      PotionFreeze = ((Potion)(new PotionFreeze(true, 0)).setIconIndex(0, 0).setRegistryName("Freeze")).setPotionName(I18n.translateToLocal("Potion.Freeze"));
      potionList.add(PotionFreeze);
      PotionLove = ((Potion)(new PotionLove(true, 0)).setIconIndex(7, 2).setRegistryName("Love")).setPotionName(I18n.translateToLocal("Potion.Love"));
      potionList.add(PotionLove);
   }

   public static void registerPotion(Register<Potion> event) {
      for(Potion potion : potionList) {
         event.getRegistry().register(potion);
      }

   }
}
