package com.fantasticsource.setbonus.server;

import com.fantasticsource.setbonus.common.bonuselements.ModifierBonus;
import com.fantasticsource.setbonus.common.bonuselements.PotionBonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import com.fantasticsource.setbonus.config.SetBonusConfig;
import java.util.LinkedHashMap;
import net.minecraftforge.fml.relauncher.Side;

public class ServerData {
   public static LinkedHashMap<String, Equip> equipment = null;
   public static LinkedHashMap<String, Set> sets;
   public static LinkedHashMap<String, ServerBonus> bonuses = new LinkedHashMap();

   public ServerData() {
   }

   public static void update() {
      ServerBonus.dropAll();
      equipment = new LinkedHashMap();
      sets = new LinkedHashMap();

      for(String equipString : SetBonusConfig.serverSettings.getEquipment()) {
         Equip equip = Equip.getInstance(equipString);
         if (equip != null) {
            equipment.put(equip.id, equip);
         }
      }

      for(String setString : SetBonusConfig.serverSettings.getSets()) {
         Set set = Set.getInstance(setString, Side.SERVER);
         if (set != null) {
            sets.put(set.id, set);
         }
      }

      for(String bonusString : SetBonusConfig.serverSettings.getBonuses()) {
         ServerBonus bonus = ServerBonus.getInstance(bonusString);
         if (bonus != null) {
            bonuses.put(bonus.id, bonus);
         }
      }

      for(String modifierString : SetBonusConfig.serverSettings.getAttributeMods()) {
         ModifierBonus.getInstance(modifierString, Side.SERVER);
      }

      for(String potionString : SetBonusConfig.serverSettings.getPotions()) {
         PotionBonus.getInstance(potionString, Side.SERVER);
      }

   }
}
