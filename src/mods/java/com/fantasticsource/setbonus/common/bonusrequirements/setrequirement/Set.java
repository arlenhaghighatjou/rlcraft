package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.tools.datastructures.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

public class Set {
   public String parsedString;
   public String id;
   public String name;
   public LinkedHashMap<String, ItemFilter> involvedEquips = new LinkedHashMap();
   private ArrayList<SlotData> slotData = new ArrayList();
   private LinkedHashMap<EntityPlayer, Pair<Integer, Long>> numEquipped = new LinkedHashMap();

   private Set() {
   }

   public static Set getInstance(String parsableSet, Side side) {
      Set result = new Set();
      String[] tokens = parsableSet.split(",");
      if (tokens.length < 3) {
         System.err.println(I18n.translateToLocalFormatted("setbonus.error.notEnoughSetArgs", new Object[]{parsableSet}));
         return null;
      } else {
         result.id = tokens[0].trim();
         if (result.id.equals("")) {
            System.err.println(I18n.translateToLocalFormatted("setbonus.error.noSetID", new Object[]{parsableSet}));
            return null;
         } else {
            result.name = tokens[1].trim();

            for(String string : (String[])Arrays.copyOfRange(tokens, 2, tokens.length)) {
               SlotData data = SlotData.getInstance(string, result.involvedEquips, side);
               if (data == null) {
                  return null;
               }

               result.slotData.add(data);
            }

            if (result.slotData.size() == 0) {
               System.err.println(I18n.translateToLocalFormatted("setbonus.error.noSetReqs", new Object[]{parsableSet}));
               return null;
            } else {
               result.parsedString = parsableSet;
               return result;
            }
         }
      }
   }

   public int getNumberEquipped(EntityPlayer player) {
      long tick = player.world.getTotalWorldTime();
      Pair<Integer, Long> pair = (Pair)this.numEquipped.computeIfAbsent(player, (k) -> {
         return new Pair(Integer.valueOf(0), tick - 1L);
      });
      if (((Long)pair.getValue()).longValue() != tick) {
         int result = this.getNumberEquippedInternal(player);
         pair.set(Integer.valueOf(result), Long.valueOf(tick));
         return result;
      } else {
         return ((Integer)pair.getKey()).intValue();
      }
   }

   private int getNumberEquippedInternal(EntityPlayer player) {
      int result = 0;
      ArrayList<Integer> blocked = new ArrayList();

      for(SlotData data : this.slotData) {
         int slot = data.equipped(player, blocked);
         if (slot != Integer.MIN_VALUE) {
            blocked.add(Integer.valueOf(slot));
            ++result;
         }
      }

      return result;
   }

   public int getMaxNumber() {
      return this.slotData.size();
   }
}
