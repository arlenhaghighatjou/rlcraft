package codersafterdark.compatskills.common.compats.thaumcraft.requirements;

import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import thaumcraft.api.ThaumcraftApi;

public class WarpRequirement extends Requirement {
   private final int warp;

   public WarpRequirement(int warp) {
      this.warp = warp;
      this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.DARK_PURPLE + (new TextComponentTranslation("compatskills.requirements.format.warp", new Object[]{"%s", warp})).getUnformattedComponentText();
   }

   public static WarpRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No warp value given.");
      } else {
         int warp;
         try {
            warp = Integer.parseInt(input);
         } catch (NumberFormatException var3) {
            throw new RequirementException("Invalid warp level '" + input + "'.");
         }

         if (warp >= 0) {
            return new WarpRequirement(warp);
         } else {
            throw new RequirementException("Warp level must be positive, received: '" + input + "'.");
         }
      }
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayerMP) {
      return ThaumcraftApi.internalMethods.getActualWarp(entityPlayerMP) >= this.warp;
   }

   public RequirementComparision matches(Requirement other) {
      if (other instanceof WarpRequirement) {
         WarpRequirement warpRequirement = (WarpRequirement)other;
         if (this.warp == warpRequirement.warp) {
            return RequirementComparision.EQUAL_TO;
         } else {
            return this.warp > warpRequirement.warp ? RequirementComparision.GREATER_THAN : RequirementComparision.LESS_THAN;
         }
      } else {
         return RequirementComparision.NOT_EQUAL;
      }
   }

   public boolean equals(Object obj) {
      return obj == this || obj instanceof WarpRequirement && this.warp == ((WarpRequirement)obj).warp;
   }

   public int hashCode() {
      return Integer.hashCode(this.warp);
   }

   public boolean isCacheable() {
      return false;
   }
}
