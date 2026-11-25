package codersafterdark.compatskills.common.compats.gamestages.gamestageunlockable;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.common.compats.gamestages.GameStageCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.formatting.IFormattedText;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.Level;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;
import stanhebben.zenscript.annotations.ZenSetter;

@ModOnly("gamestages")
@ZenClass("mods.compatskills.GameStageUnlockable")
@ZenRegister
public class GameStageUnlockable extends Unlockable {
   @ZenProperty
   public IFormattedText name;
   @ZenProperty
   public IFormattedText description;
   private String gameStage;

   private GameStageUnlockable(String gameStage, ResourceLocation name, int x, int y, ResourceLocation skillName, int cost, String... defaultRequirements) {
      super(name, x, y, skillName, cost, defaultRequirements);
      this.gameStage = gameStage;
      ReskillableRegistries.UNLOCKABLES.register(this);
   }

   @ZenMethod
   public static GameStageUnlockable addGameStageUnlockable(String gameStage, String name, int x, int y, String skillName, int cost, @Optional String... requirements) {
      if (GameStageCompatHandler.ENABLED & CheckMethods.checkString(gameStage) & CheckMethods.checkString(name) & CheckMethods.checkIntX(x) & CheckMethods.checkIntY(y) & CheckMethods.checkParentSkillsString(skillName) & CheckMethods.checkInt(cost) & CheckMethods.checkOptionalRequirements(requirements)) {
         ResourceLocation skillLoc = new ResourceLocation(skillName);
         ResourceLocation loc = new ResourceLocation("compatskills", name);
         if (requirements == null) {
            requirements = new String[0];
         }

         GameStageUnlockable dummyTrait = new GameStageUnlockable(gameStage, loc, x, y, skillLoc, cost, requirements);
         if (ReskillableRegistries.UNLOCKABLES.containsKey(loc)) {
            Unlockable value = (Unlockable)ReskillableRegistries.UNLOCKABLES.getValue(loc);
            if (value instanceof GameStageUnlockable) {
               dummyTrait = (GameStageUnlockable)value;
               String updated = "";
               if (!dummyTrait.gameStage.equals(gameStage)) {
                  dummyTrait.gameStage = gameStage;
                  updated = updated + " - Updated GameStage: " + gameStage;
               }

               if (dummyTrait.getX() != x) {
                  dummyTrait.unlockableConfig.setX(x);
                  updated = updated + " - Updated X Pos: " + x;
               }

               if (dummyTrait.getY() != y) {
                  dummyTrait.unlockableConfig.setY(y);
                  updated = updated + " - Updated Y Pos: " + y;
               }

               if (!skillLoc.equals(dummyTrait.getParentSkill().getRegistryName())) {
                  dummyTrait.setParentSkill(skillLoc);
                  updated = updated + " - Updated Parent Skill: " + skillLoc;
               }

               if (dummyTrait.getCost() != cost) {
                  dummyTrait.unlockableConfig.setCost(cost);
                  updated = updated + " - Updated Cost: " + cost;
               }

               String reqs = Utils.formatRequirements(requirements);
               RequirementHolder holder = RequirementHolder.fromStringList(requirements);
               if (!holder.equals(dummyTrait.getRequirements())) {
                  dummyTrait.unlockableConfig.setRequirementHolder(holder);
                  updated = updated + " - Updated Requirements: " + reqs;
               }

               if (!updated.isEmpty()) {
                  CraftTweakerAPI.logInfo("Loaded Dummy Trait: " + loc + updated);
               } else {
                  CraftTweakerAPI.logInfo("Created or Loaded Dummy Trait: " + loc + " - for GameStage: " + gameStage + " - With Pos: " + x + ", " + y + " -  With Cost: " + cost + " - Requirements: " + reqs);
               }
            }
         }

         return dummyTrait;
      } else {
         return null;
      }
   }

   public void onUnlock(EntityPlayer player) {
      MinecraftServer server = player.getServer();
      if (server != null) {
         GameStageHelper.addStage(player, this.gameStage);
         if (player instanceof EntityPlayerMP) {
            GameStageHelper.syncPlayer((EntityPlayerMP)player);
         }

         player.sendStatusMessage(new TextComponentString("Unlocked GameStage: " + this.gameStage), true);
      } else {
         CompatSkills.logger.log(Level.ERROR, "WHY IS THE SERVER NULL!?");
      }

   }

   public String getName() {
      return this.name == null ? super.getName() : this.name.getText();
   }

   public String getDescription() {
      return this.description == null ? super.getDescription() : this.description.getText();
   }

   @ZenGetter("icon")
   @ZenMethod
   public String retrieveIcon() {
      return this.getIcon().toString();
   }

   @ZenSetter("icon")
   @ZenMethod
   public void changeIcon(String resourceLocation) {
      if (CheckMethods.checkResourceLocation(resourceLocation)) {
         this.setIcon(new ResourceLocation(resourceLocation));
      }

   }
}
