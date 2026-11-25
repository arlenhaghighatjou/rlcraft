package codersafterdark.compatskills.common.compats.minecraft.drops;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.minecraft.MinecraftCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.compatskills.BlockDropsLock")
@ZenRegister
public class BlockDropsTweaker {
   public BlockDropsTweaker() {
   }

   @ZenMethod
   public static void addBlockDropLock(IItemStack stack, String... requirements) {
      if (MinecraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new BlockDropsTweaker.AddBlockDropLock(stack, requirements));
      }

   }

   private static class AddBlockDropLock implements IAction {
      private final IItemStack stack;
      private final String[] requirements;

      private AddBlockDropLock(IItemStack stack, String... requirements) {
         this.stack = stack;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkIItemstack(this.stack) && CheckMethods.checkStringArray(this.requirements)) {
            MinecraftCompatHandler.addMCLock(new ItemStackDropKey(CraftTweakerMC.getItemStack(this.stack)), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Added Block Drop Lock for ItemStack: " + this.stack.getDisplayName() + ", With Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
