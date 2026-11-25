package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.mctools.potions.Potions;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.server.ServerData;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

public class PotionBonus extends ABonusElement {
   public ArrayList<FantasticPotionEffect> potions;

   protected PotionBonus(String parsableBonusElement, Bonus bonus, ArrayList<FantasticPotionEffect> potions) {
      super(parsableBonusElement, bonus);
      this.potions = potions;
   }

   public static PotionBonus getInstance(String parsablePotionBonus, Side side) {
      String[] tokens = parsablePotionBonus.split(",");
      if (tokens.length < 2) {
         System.err.println(I18n.translateToLocalFormatted("setbonus.error.notEnoughPotionBonusArgs", new Object[]{parsablePotionBonus}));
         return null;
      } else {
         Bonus bonus = side == Side.SERVER ? (Bonus)ServerData.bonuses.get(tokens[0].trim()) : (Bonus)ClientData.bonuses.get(tokens[0].trim());
         if (bonus == null) {
            System.err.println(I18n.translateToLocalFormatted("setbonus.error.potionBonusIDNotFound", new Object[]{tokens[0].trim(), parsablePotionBonus}));
            return null;
         } else {
            ArrayList<FantasticPotionEffect> potions = Potions.parsePotions((String[])Arrays.copyOfRange(tokens, 1, tokens.length), true);
            return potions.size() == 0 ? null : new PotionBonus(parsablePotionBonus, bonus, potions);
         }
      }
   }

   public void activate(EntityPlayer player) {
      long tick = ServerTickTimer.currentTick();

      for(FantasticPotionEffect potion : this.potions) {
         if (potion.interval == 0 || tick % (long)potion.interval == 0L) {
            player.addPotionEffect(new PotionEffect(potion));
         }
      }

   }

   public void deactivate(EntityPlayer player) {
      for(PotionEffect potion : this.potions) {
         player.removePotionEffect(potion.getPotion());
      }

   }

   public void updateActive(EntityPlayer player) {
      long tick = ServerTickTimer.currentTick();

      for(FantasticPotionEffect potion : this.potions) {
         if (potion.interval == 0 || tick % (long)potion.interval == 0L) {
            player.addPotionEffect(new PotionEffect(potion));
         }
      }

   }
}
