package codersafterdark.reskillable.api.requirement;

import codersafterdark.reskillable.api.data.PlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public abstract class Requirement {
   protected String tooltip = "";

   public Requirement() {
   }

   public abstract boolean achievedByPlayer(EntityPlayer var1);

   public String getToolTip(PlayerData data) {
      try {
         return String.format(this.internalToolTip(), data != null && data.requirementAchieved(this) ? TextFormatting.GREEN : TextFormatting.RED);
      } catch (IllegalArgumentException var3) {
         return this.internalToolTip();
      }
   }

   public RequirementComparision matches(Requirement other) {
      return this.equals(other) ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean isEnabled() {
      return true;
   }

   public final String internalToolTip() {
      return this.tooltip;
   }

   public boolean isCacheable() {
      return true;
   }
}
