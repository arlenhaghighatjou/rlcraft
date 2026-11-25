package rlmixins.handlers.reskillable;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import com.charles445.simpledifficulty.api.SDDamageSources;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.event.FirstAidLivingDamageEvent;
import ichttt.mods.firstaid.common.network.MessageUpdatePart;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UndershirtHandler {
   private static final List<DamageSource> nonAttackSources = Arrays.asList(DamageSource.FALL, DamageSource.OUT_OF_WORLD, DamageSource.DROWN, DamageSource.HOT_FLOOR, DamageSource.IN_FIRE, DamageSource.ON_FIRE, DamageSource.LAVA, DamageSource.STARVE, SDDamageSources.DEHYDRATION, SDDamageSources.HYPERTHERMIA, SDDamageSources.HYPOTHERMIA, SDDamageSources.PARASITES);

   public UndershirtHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onFirstAidLivingDamageHigh(FirstAidLivingDamageEvent event) {
      if (event.getEntityPlayer() != null && !event.getEntityPlayer().world.isRemote) {
         if (event.getUndistributedDamage() <= 1000.0F) {
            if (!nonAttackSources.contains(event.getSource())) {
               List<AbstractDamageablePart> parts = new ArrayList();

               for(AbstractDamageablePart part : event.getAfterDamage()) {
                  if (part.canCauseDeath && part.currentHealth <= 0.0F) {
                     parts.add(part);
                  }
               }

               if (!parts.isEmpty() && PlayerDataHandler.get(event.getEntityPlayer()).getSkillInfo((Skill)ReskillableRegistries.SKILLS.getValue(new ResourceLocation("reskillable", "defense"))).isUnlocked((Unlockable)ReskillableRegistries.UNLOCKABLES.getValue(new ResourceLocation("reskillable", "undershirt"))) && event.getEntityPlayer().getEntityData().getInteger("skillable:UndershirtCD") <= 0) {
                  boolean saved = false;

                  for(AbstractDamageablePart part : parts) {
                     if ((double)event.getBeforeDamage().getFromEnum(part.part).currentHealth >= 4.0D) {
                        part.heal(1.0F, (EntityPlayer)null, false);
                        if (event.getEntityPlayer() instanceof EntityPlayerMP) {
                           FirstAid.NETWORKING.sendTo(new MessageUpdatePart(part), (EntityPlayerMP)event.getEntityPlayer());
                        }

                        saved = true;
                     }
                  }

                  if (saved) {
                     event.getEntityPlayer().getEntityData().setInteger("skillable:UndershirtCD", 1200);
                     event.getEntityPlayer().world.playSound((EntityPlayer)null, event.getEntityPlayer().posX, event.getEntityPlayer().posY, event.getEntityPlayer().posZ, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 0.8F, (event.getEntityPlayer().world.rand.nextFloat() - event.getEntityPlayer().world.rand.nextFloat()) * 0.1F + 0.8F);
                  }
               }

            }
         }
      }
   }
}
