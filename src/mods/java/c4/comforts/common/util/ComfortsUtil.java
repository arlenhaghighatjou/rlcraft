package c4.comforts.common.util;

import c4.comforts.Comforts;
import c4.comforts.client.gui.ComfortsTab;
import c4.comforts.common.ConfigHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.logging.log4j.Level;

public class ComfortsUtil {
   public static List<PotionEffect> debuffs = new ArrayList();
   public static final ComfortsTab comfortsTab = new ComfortsTab();

   public ComfortsUtil() {
   }

   public static int getColor(int metadata) {
      return ItemDye.DYE_COLORS[15 - metadata];
   }

   public static void applyDebuffs(EntityPlayer player) {
      for(PotionEffect effect : getDebuffs()) {
         player.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier()));
      }

   }

   public static List<PotionEffect> getDebuffs() {
      return debuffs;
   }

   public static void parseDebuffs() {
      for(String s : ConfigHandler.sleepingBagDebuffs) {
         String[] elements = s.split("\\s+");
         Potion potion = Potion.getPotionFromResourceLocation(elements[0]);
         if (potion != null) {
            int duration = 0;
            int amp = 0;

            try {
               duration = Math.max(1, Math.min(Integer.parseInt(elements[1]), 1600));
               amp = Math.max(1, Math.min(Integer.parseInt(elements[2]), 4));
            } catch (Exception var9) {
               Comforts.logger.log(Level.ERROR, "Problem parsing sleeping bag debuffs in config!", var9);
            }

            debuffs.add(new PotionEffect(potion, duration * 20, amp - 1));
         }
      }

   }
}
