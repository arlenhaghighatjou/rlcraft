package com.fantasticsource.setbonus.common.bonusrequirements;

import com.fantasticsource.mctools.DoubleRequirement;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.player.EntityPlayer;

public class AttributeRequirement extends ABonusRequirement {
   public String attributeName;
   public DoubleRequirement requirement;

   protected AttributeRequirement(Pair<String, DoubleRequirement> pair) {
      this.attributeName = (String)pair.getKey();
      this.requirement = (DoubleRequirement)pair.getValue();
   }

   public static AttributeRequirement getInstance(String parseableAttributeRequirement) {
      Pair<String, DoubleRequirement> pair = DoubleRequirement.parse(parseableAttributeRequirement);
      return pair == null ? null : new AttributeRequirement(pair);
   }

   public int active(EntityPlayer player) {
      try {
         return this.requirement.check(player.getAttributeMap().getAttributeInstanceByName(this.attributeName).getAttributeValue()) ? 1 : 0;
      } catch (Exception var3) {
         return 0;
      }
   }

   public int required() {
      return 1;
   }
}
