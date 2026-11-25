package com.fantasticsource.setbonus.common.bonuselements;

import com.fantasticsource.mctools.attributes.AttributeMods;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.server.ServerData;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

public class ModifierBonus extends ABonusElement {
   private Multimap<String, AttributeModifier> modifiers = ArrayListMultimap.create();

   private ModifierBonus(String parsableBonusElement, Bonus bonus, ArrayList<AttributeModifier> modifiers) {
      super(parsableBonusElement, bonus);

      for(AttributeModifier modifier : modifiers) {
         this.modifiers.put(modifier.getName(), modifier.setSaved(false));
      }

   }

   public static ModifierBonus getInstance(String parsableModifierBonus, Side side) {
      String[] tokens = parsableModifierBonus.split(",");
      if (tokens.length < 2) {
         System.err.println(I18n.translateToLocalFormatted("setbonus.error.notEnoughAttribBonusArgs", new Object[]{parsableModifierBonus}));
         return null;
      } else {
         Bonus bonus = side == Side.SERVER ? (Bonus)ServerData.bonuses.get(tokens[0].trim()) : (Bonus)ClientData.bonuses.get(tokens[0].trim());
         if (bonus == null) {
            System.err.println(I18n.translateToLocalFormatted("setbonus.error.attribBonusIDNotFound", new Object[]{tokens[0].trim(), parsableModifierBonus}));
            return null;
         } else {
            ArrayList<AttributeModifier> modifiers = AttributeMods.parseMods((String[])Arrays.copyOfRange(tokens, 1, tokens.length));
            return modifiers == null ? null : new ModifierBonus(parsableModifierBonus, bonus, modifiers);
         }
      }
   }

   public void activate(EntityPlayer player) {
      float hpRatio = player.getHealth() / player.getMaxHealth();
      player.getAttributeMap().applyAttributeModifiers(this.modifiers);
      if (hpRatio <= Float.MIN_VALUE) {
         hpRatio = 0.0F;
      } else if (hpRatio >= 1.0F) {
         hpRatio = 1.0F;
      }

      player.setHealth(hpRatio * player.getMaxHealth());
   }

   public void deactivate(EntityPlayer player) {
      float hpRatio = player.getHealth() / player.getMaxHealth();
      player.getAttributeMap().removeAttributeModifiers(this.modifiers);
      if (hpRatio <= Float.MIN_VALUE) {
         hpRatio = 0.0F;
      } else if (hpRatio >= 1.0F) {
         hpRatio = 1.0F;
      }

      player.setHealth(hpRatio * player.getMaxHealth());
   }

   public void updateActive(EntityPlayer player) {
   }
}
