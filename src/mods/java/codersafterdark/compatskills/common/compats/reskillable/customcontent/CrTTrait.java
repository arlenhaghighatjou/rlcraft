package codersafterdark.compatskills.common.compats.reskillable.customcontent;

import codersafterdark.compatskills.common.compats.reskillable.ReskillableCompatHandler;
import codersafterdark.compatskills.utils.CheckMethods;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.api.unlockable.Trait;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.BlockHarvestDropsEvent;
import crafttweaker.api.event.EnderTeleportEvent;
import crafttweaker.api.event.EntityLivingDeathDropsEvent;
import crafttweaker.api.event.EntityLivingDeathEvent;
import crafttweaker.api.event.EntityLivingHurtEvent;
import crafttweaker.api.event.PlayerBreakSpeedEvent;
import crafttweaker.api.event.PlayerRightClickBlockEvent;
import crafttweaker.api.event.PlayerTickEvent;
import crafttweaker.api.formatting.IFormattedText;
import crafttweaker.mc1120.events.handling.MCBlockHarvestDropsEvent;
import crafttweaker.mc1120.events.handling.MCEnderTeleportEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingDeathDropsEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingDeathEvent;
import crafttweaker.mc1120.events.handling.MCEntityLivingHurtEvent;
import crafttweaker.mc1120.events.handling.MCPlayerBreakSpeedEvent;
import crafttweaker.mc1120.events.handling.MCPlayerRightClickBlockEvent;
import crafttweaker.mc1120.events.handling.MCPlayerTickEvent;
import crafttweaker.util.IEventHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenRegister
@ZenClass("mods.compatskills.TraitCreator")
public class CrTTrait extends Trait {
   @ZenProperty
   public IEventHandler<BlockHarvestDropsEvent> onBlockDrops;
   @ZenProperty
   public IEventHandler<EnderTeleportEvent> onEnderTeleport;
   @ZenProperty
   public IEventHandler<PlayerTickEvent> onPlayerTick;
   @ZenProperty
   public IEventHandler<PlayerBreakSpeedEvent> getBreakSpeed;
   @ZenProperty
   public IEventHandler<EntityLivingDeathDropsEvent> onMobDrops;
   @ZenProperty
   public IEventHandler<EntityLivingHurtEvent> onAttackMob;
   @ZenProperty
   public IEventHandler<EntityLivingHurtEvent> onHurt;
   @ZenProperty
   public IEventHandler<PlayerRightClickBlockEvent> onRightClickBlock;
   @ZenProperty
   public IEventHandler<EntityLivingDeathEvent> onKillMob;
   @ZenProperty
   public IFormattedText name;
   @ZenProperty
   public IFormattedText description;

   public CrTTrait(ResourceLocation name, int x, int y, ResourceLocation skillName, int cost, String... requirements) {
      super(name, x, y, skillName, cost, requirements);
      ReskillableRegistries.UNLOCKABLES.register(this);
   }

   private static CrTTrait createTrait(ResourceLocation name, int x, int y, ResourceLocation skillName, int cost, String... requirements) {
      if (requirements == null) {
         requirements = new String[0];
      }

      CrTTrait customTrait = new CrTTrait(name, x, y, skillName, cost, requirements);
      if (ReskillableRegistries.UNLOCKABLES.containsKey(name)) {
         Unlockable value = (Unlockable)ReskillableRegistries.UNLOCKABLES.getValue(name);
         if (value instanceof CrTTrait) {
            String updated = "";
            if (customTrait.getX() != x) {
               customTrait.unlockableConfig.setX(x);
               updated = updated + " - Updated X Pos: " + x;
            }

            if (customTrait.getY() != y) {
               customTrait.unlockableConfig.setY(y);
               updated = updated + " - Updated Y Pos: " + y;
            }

            if (!skillName.equals(customTrait.getParentSkill().getRegistryName())) {
               customTrait.setParentSkill(skillName);
               updated = updated + " - Updated Parent Skill: " + skillName;
            }

            if (customTrait.getCost() != cost) {
               customTrait.unlockableConfig.setCost(cost);
               updated = updated + " - Updated Cost: " + cost;
            }

            String reqs = Utils.formatRequirements(requirements);
            RequirementHolder holder = RequirementHolder.fromStringList(requirements);
            if (!holder.equals(customTrait.getRequirements())) {
               customTrait.unlockableConfig.setRequirementHolder(holder);
               updated = updated + " - Updated Requirements: " + reqs;
            }

            if (!updated.isEmpty()) {
               CraftTweakerAPI.logInfo("Loaded Trait: " + name + updated);
            } else {
               CraftTweakerAPI.logInfo("Created or Loaded Trait: " + name + " - With Pos: " + x + ", " + y + " -  With Cost: " + cost + " - Requirements: " + reqs);
            }
         }
      }

      return customTrait;
   }

   @ZenMethod
   public static CrTTrait createTrait(String traitName, int x, int y, String skillLocation, int cost, @Optional String... requirements) {
      return ReskillableCompatHandler.ENABLED & CheckMethods.checkString(traitName) & CheckMethods.checkIntX(x) & CheckMethods.checkIntY(y) & CheckMethods.checkParentSkillsString(skillLocation) & CheckMethods.checkInt(cost) & CheckMethods.checkOptionalRequirements(requirements) ? createTrait(new ResourceLocation("compatskills", traitName), x, y, new ResourceLocation(skillLocation), cost, requirements) : null;
   }

   @ZenMethod
   public static CrTTrait createTrait(String traitName, int x, int y, CrTSkill parentSkill, int cost, @Optional String... requirements) {
      return ReskillableCompatHandler.ENABLED & CheckMethods.checkString(traitName) & CheckMethods.checkIntX(x) & CheckMethods.checkIntY(y) & CheckMethods.checkCrTSkillParent(parentSkill) & CheckMethods.checkInt(cost) & CheckMethods.checkOptionalRequirements(requirements) ? createTrait(new ResourceLocation("compatskills", traitName), x, y, parentSkill.getRegistryName(), cost, requirements) : null;
   }

   @ZenMethod
   public static CrTTrait createNewTrait(String traitLocation, int x, int y, String skillLocation, int cost, @Optional String... requirements) {
      return ReskillableCompatHandler.ENABLED & CheckMethods.checkString(traitLocation) & CheckMethods.checkIntX(x) & CheckMethods.checkIntY(y) & CheckMethods.checkParentSkillsString(skillLocation) & CheckMethods.checkInt(cost) & CheckMethods.checkOptionalRequirements(requirements) ? createTrait(new ResourceLocation(traitLocation), x, y, new ResourceLocation(skillLocation), cost, requirements) : null;
   }

   @ZenMethod
   public static CrTTrait createNewTrait(String traitLocation, int x, int y, CrTSkill parentSkill, int cost, @Optional String... requirements) {
      return ReskillableCompatHandler.ENABLED & CheckMethods.checkString(traitLocation) & CheckMethods.checkIntX(x) & CheckMethods.checkIntY(y) & CheckMethods.checkCrTSkillParent(parentSkill) & CheckMethods.checkInt(cost) & CheckMethods.checkOptionalRequirements(requirements) ? createTrait(new ResourceLocation(traitLocation), x, y, parentSkill.getRegistryName(), cost, requirements) : null;
   }

   @ZenGetter("enabled")
   @ZenMethod
   public boolean getEnabled() {
      return this.isEnabled();
   }

   @ZenSetter("enabled")
   @ZenMethod
   public void setEnabled(boolean enabled) {
      this.unlockableConfig.setEnabled(enabled);
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

   public void onPlayerTick(net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent event) {
      if (this.onPlayerTick != null) {
         this.onPlayerTick.handle(new MCPlayerTickEvent(event));
      }

      super.onPlayerTick(event);
   }

   public void onBlockDrops(HarvestDropsEvent event) {
      if (this.onBlockDrops != null) {
         this.onBlockDrops.handle(new MCBlockHarvestDropsEvent(event));
      }

      super.onBlockDrops(event);
   }

   public void getBreakSpeed(BreakSpeed event) {
      if (this.getBreakSpeed != null) {
         this.getBreakSpeed.handle(new MCPlayerBreakSpeedEvent(event));
      }

      super.getBreakSpeed(event);
   }

   public void onMobDrops(LivingDropsEvent event) {
      if (this.onMobDrops != null) {
         this.onMobDrops.handle(new MCEntityLivingDeathDropsEvent(event));
      }

      super.onMobDrops(event);
   }

   public void onAttackMob(LivingHurtEvent event) {
      if (this.onAttackMob != null) {
         this.onAttackMob.handle(new MCEntityLivingHurtEvent(event));
      }

      super.onAttackMob(event);
   }

   public void onHurt(LivingHurtEvent event) {
      if (this.onHurt != null) {
         this.onHurt.handle(new MCEntityLivingHurtEvent(event));
      }

      super.onHurt(event);
   }

   public void onRightClickBlock(RightClickBlock event) {
      if (this.onRightClickBlock != null) {
         this.onRightClickBlock.handle(new MCPlayerRightClickBlockEvent(event));
      }

      super.onRightClickBlock(event);
   }

   public void onEnderTeleport(net.minecraftforge.event.entity.living.EnderTeleportEvent event) {
      if (this.onEnderTeleport != null) {
         this.onEnderTeleport.handle(new MCEnderTeleportEvent(event));
      }

      super.onEnderTeleport(event);
   }

   public void onKillMob(LivingDeathEvent event) {
      if (this.onKillMob != null) {
         this.onKillMob.handle(new MCEntityLivingDeathEvent(event));
      }

      super.onKillMob(event);
   }

   public String getName() {
      return this.name == null ? super.getName() : this.name.getText();
   }

   public String getDescription() {
      return this.description == null ? super.getDescription() : this.description.getText();
   }
}
