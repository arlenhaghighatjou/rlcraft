package codersafterdark.reskillable;

import codersafterdark.reskillable.advancement.ReskillableAdvancements;
import codersafterdark.reskillable.api.event.LevelUpEvent;
import codersafterdark.reskillable.api.event.UnlockUnlockableEvent;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import codersafterdark.reskillable.skill.SkillAgility;
import codersafterdark.reskillable.skill.SkillAttack;
import codersafterdark.reskillable.skill.SkillBuilding;
import codersafterdark.reskillable.skill.SkillDefense;
import codersafterdark.reskillable.skill.SkillFarming;
import codersafterdark.reskillable.skill.SkillGathering;
import codersafterdark.reskillable.skill.SkillMagic;
import codersafterdark.reskillable.skill.SkillMining;
import codersafterdark.reskillable.skill.agility.TraitHillWalker;
import codersafterdark.reskillable.skill.agility.TraitRoadWalk;
import codersafterdark.reskillable.skill.agility.TraitSidestep;
import codersafterdark.reskillable.skill.attack.TraitBattleSpirit;
import codersafterdark.reskillable.skill.attack.TraitNeutralissse;
import codersafterdark.reskillable.skill.building.TraitPerfectRecover;
import codersafterdark.reskillable.skill.building.TraitTransmutation;
import codersafterdark.reskillable.skill.defense.TraitEffectTwist;
import codersafterdark.reskillable.skill.defense.TraitUndershirt;
import codersafterdark.reskillable.skill.farming.TraitGreenThumb;
import codersafterdark.reskillable.skill.farming.TraitHungryFarmer;
import codersafterdark.reskillable.skill.farming.TraitMoreWheat;
import codersafterdark.reskillable.skill.gathering.TraitDropGuarantee;
import codersafterdark.reskillable.skill.gathering.TraitLuckyFisherman;
import codersafterdark.reskillable.skill.magic.TraitGoldenOsmosis;
import codersafterdark.reskillable.skill.magic.TraitSafePort;
import codersafterdark.reskillable.skill.mining.TraitFossilDigger;
import codersafterdark.reskillable.skill.mining.TraitObsidianSmasher;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.NewRegistry;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.RegistryBuilder;

@EventBusSubscriber(
   modid = "reskillable"
)
public class ReskillableRegistryHandler {
   public ReskillableRegistryHandler() {
   }

   @SubscribeEvent
   public static void buildRegistry(NewRegistry newRegistryEvent) {
      (new RegistryBuilder()).setName(new ResourceLocation("reskillable", "skill")).setType(Skill.class).create();
      (new RegistryBuilder()).setName(new ResourceLocation("reskillable", "unlockable")).setType(Unlockable.class).create();
   }

   @SubscribeEvent
   public static void registerSkills(Register<Skill> skillRegister) {
      skillRegister.getRegistry().registerAll(new Skill[]{new SkillMining(), new SkillGathering(), new SkillAttack(), new SkillDefense(), new SkillBuilding(), new SkillFarming(), new SkillAgility(), new SkillMagic()});
   }

   @SubscribeEvent
   public static void registerTraits(Register<Unlockable> unlockablesRegister) {
      unlockablesRegister.getRegistry().registerAll(new Unlockable[]{new TraitHillWalker(), new TraitRoadWalk(), new TraitSidestep(), new TraitBattleSpirit(), new TraitNeutralissse(), new TraitPerfectRecover(), new TraitTransmutation(), new TraitEffectTwist(), new TraitUndershirt(), new TraitGreenThumb(), new TraitHungryFarmer(), new TraitMoreWheat(), new TraitDropGuarantee(), new TraitLuckyFisherman(), new TraitGoldenOsmosis(), new TraitSafePort(), new TraitFossilDigger(), new TraitObsidianSmasher()});
   }

   @SubscribeEvent
   public static void skillAdvancementHandling(LevelUpEvent.Post postEvent) {
      if (postEvent.getEntityPlayer() instanceof EntityPlayerMP) {
         ReskillableAdvancements.SKILL_LEVEL.trigger((EntityPlayerMP)postEvent.getEntityPlayer(), postEvent.getSkill(), postEvent.getLevel());
      }

   }

   @SubscribeEvent
   public static void unlockableAdvancementHandling(UnlockUnlockableEvent.Post event) {
      if (event.getEntityPlayer() instanceof EntityPlayerMP) {
         ReskillableAdvancements.UNLOCK_UNLOCKABLE.trigger((EntityPlayerMP)event.getEntityPlayer(), event.getUnlockable());
      }

   }
}
