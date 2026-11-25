package codersafterdark.compatskills.common.compats.gamestages.gamestagerequirement;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class GameStageRequirement extends Requirement {
   private final String gamestage;

   public GameStageRequirement(String gamestage) {
      this.gamestage = gamestage;
      this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.BLUE + (new TextComponentTranslation("compatskills.requirements.format.gamestage", new Object[]{"%s", gamestage})).getUnformattedComponentText();
   }

   public boolean achievedByPlayer(EntityPlayer player) {
      return GameStageHelper.hasStage(player, this.gamestage);
   }

   public RequirementComparision matches(Requirement other) {
      return other instanceof GameStageRequirement && this.gamestage.equals(((GameStageRequirement)other).gamestage) ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object o) {
      return o == this || o instanceof GameStageRequirement && this.gamestage.equals(((GameStageRequirement)o).gamestage);
   }

   public int hashCode() {
      return this.gamestage.hashCode();
   }
}
