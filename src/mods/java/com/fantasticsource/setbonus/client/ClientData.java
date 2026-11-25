package com.fantasticsource.setbonus.client;

import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.common.bonuselements.ModifierBonus;
import com.fantasticsource.setbonus.common.bonuselements.PotionBonus;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Equip;
import com.fantasticsource.setbonus.common.bonusrequirements.setrequirement.Set;
import java.util.LinkedHashMap;
import net.minecraftforge.fml.relauncher.Side;

public class ClientData {
   public static LinkedHashMap<String, Equip> equipment = new LinkedHashMap();
   public static LinkedHashMap<String, Set> sets = new LinkedHashMap();
   public static LinkedHashMap<String, ClientBonus> bonuses = new LinkedHashMap();

   public ClientData() {
   }

   public static void clear() {
      ClientBonus.dropAll();
      equipment.clear();
      sets.clear();
   }

   public static void update(Network.ConfigPacket packet) {
      ClientBonus.dropAll();
      equipment.clear();
      sets.clear();

      for(String equipString : packet.equipment) {
         Equip equip = Equip.getInstance(equipString);
         if (equip != null) {
            equipment.put(equip.id, equip);
         }
      }

      for(String setString : packet.sets) {
         Set set = Set.getInstance(setString, Side.CLIENT);
         if (set != null) {
            sets.put(set.id, set);
         }
      }

      for(String bonusString : packet.bonuses) {
         ClientBonus bonus = ClientBonus.getInstance(bonusString);
         if (bonus != null) {
            bonuses.put(bonus.id, bonus);
         }
      }

      for(String modifierString : packet.attributeMods) {
         ModifierBonus.getInstance(modifierString, Side.CLIENT);
      }

      for(String potionString : packet.potions) {
         PotionBonus.getInstance(potionString, Side.CLIENT);
      }

   }

   public static void update(Network.DiscoverBonusPacket packet) {
      for(String equipString : packet.equipment) {
         Equip equip = Equip.getInstance(equipString);
         if (equip != null) {
            equipment.put(equip.id, equip);
         }
      }

      for(String setString : packet.sets) {
         Set set = Set.getInstance(setString, Side.CLIENT);
         if (set != null) {
            sets.put(set.id, set);
         }
      }

      ClientBonus bonus = ClientBonus.getInstance(packet.bonusString);
      if (bonus != null) {
         bonuses.put(bonus.id, bonus);
      }

      for(String modifierString : packet.attributeMods) {
         ModifierBonus.getInstance(modifierString, Side.CLIENT);
      }

      for(String potionString : packet.potions) {
         PotionBonus.getInstance(potionString, Side.CLIENT);
      }

   }
}
