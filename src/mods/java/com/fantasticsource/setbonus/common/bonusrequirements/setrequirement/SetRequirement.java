package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import com.fantasticsource.setbonus.server.ServerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class SetRequirement extends ABonusRequirement {
   public Set set;
   public int num;

   protected SetRequirement(Set set, int num) {
      this.set = set;
      this.num = num;
   }

   public static SetRequirement getInstance(String parseableSetRequirement, Side side) throws Exception {
      String[] tokens2 = parseableSetRequirement.split("\\.");
      Set set = side == Side.SERVER ? (Set)ServerData.sets.get(tokens2[0].trim()) : (Set)ClientData.sets.get(tokens2[0].trim());
      if (set == null) {
         return null;
      } else if (tokens2.length == 1) {
         return new SetRequirement(set, set.getMaxNumber());
      } else {
         int num = Integer.parseInt(tokens2[1].trim());
         if (num > 0) {
            return new SetRequirement(set, num);
         } else {
            throw new Exception();
         }
      }
   }

   public int active(EntityPlayer player) {
      return this.set.getNumberEquipped(player);
   }

   public int required() {
      return this.num;
   }
}
