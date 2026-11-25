package codersafterdark.compatskills.common.compats.minecraft.tileentity;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.minecraft.MinecraftCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.RequirementHolder;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.compatskills.TileEntityLock")
@ZenRegister
public class TileEntityLockTweaker {
   public TileEntityLockTweaker() {
   }

   @ZenMethod
   public static void addTileEntityLock(String tileName, String... locked) {
      if (MinecraftCompatHandler.ENABLED) {
         CompatSkills.LATE_ADDITIONS.add(new TileEntityLockTweaker.AddTileEntity(tileName, locked));
      }

   }

   private static class AddTileEntity implements IAction {
      private final String tileName;
      private final String[] requirements;

      private AddTileEntity(String tileName, String... requirements) {
         this.tileName = tileName;
         this.requirements = requirements;
      }

      public void apply() {
         if (CheckMethods.checkValidTileEntityName(this.tileName) & CheckMethods.checkStringArray(this.requirements)) {
            MinecraftCompatHandler.addMCLock(new TileEntityLockKey(this.tileName), RequirementHolder.fromStringList(this.requirements));
         }

      }

      public String describe() {
         return "Setting the requirement of Tile Entity: " + this.tileName + " to Requirements: " + Utils.formatRequirements(this.requirements);
      }
   }
}
