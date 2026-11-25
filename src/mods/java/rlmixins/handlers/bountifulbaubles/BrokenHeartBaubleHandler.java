package rlmixins.handlers.bountifulbaubles;

import baubles.api.BaublesApi;
import cursedflames.bountifulbaubles.item.ModItems;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.event.FirstAidLivingDamageEvent;
import ichttt.mods.firstaid.common.network.MessageUpdatePart;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BrokenHeartBaubleHandler {
   public static final UUID MODIFIER_UUID = UUID.fromString("554f3929-4193-4ae5-a4da-4b528a89ca32");

   public BrokenHeartBaubleHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void onFirstAidLivingDamageLow(FirstAidLivingDamageEvent event) {
      if (event.getEntityPlayer() != null && !event.getEntityPlayer().world.isRemote) {
         if (event.getUndistributedDamage() <= 1000.0F) {
            if (BaublesApi.isBaubleEquipped(event.getEntityPlayer(), ModItems.trinketBrokenHeart) != -1) {
               EntityPlayer player = event.getEntityPlayer();
               boolean failed = false;
               List<AbstractDamageablePart> parts = new ArrayList();

               for(AbstractDamageablePart part : event.getAfterDamage()) {
                  if (part.canCauseDeath && part.currentHealth <= 0.0F) {
                     if (part.getMaxHealth() >= 4) {
                        parts.add(part);
                     } else {
                        failed = true;
                     }
                  }
               }

               if (!failed && !parts.isEmpty()) {
                  IAttributeInstance maxHealth = player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
                  AttributeModifier modifier = maxHealth.getModifier(MODIFIER_UUID);
                  double prevMaxHealthDamage = 0.0D;
                  if (modifier != null) {
                     prevMaxHealthDamage = modifier.getAmount();
                  }

                  double curMaxHealth = maxHealth.getBaseValue();

                  for(AttributeModifier mod : maxHealth.getModifiersByOperation(0)) {
                     curMaxHealth += mod.getAmount();
                  }

                  double originalMaxHealth = curMaxHealth - prevMaxHealthDamage;
                  double healthToRemove = originalMaxHealth * 0.3D + (double)(parts.size() * 2) + (double)event.getUndistributedDamage();
                  if (healthToRemove > curMaxHealth - 2.0D) {
                     return;
                  }

                  for(AbstractDamageablePart part : parts) {
                     part.heal(1.0F, (EntityPlayer)null, false);
                     if (event.getEntityPlayer() instanceof EntityPlayerMP) {
                        FirstAid.NETWORKING.sendTo(new MessageUpdatePart(part), (EntityPlayerMP)event.getEntityPlayer());
                     }
                  }

                  if (modifier != null) {
                     maxHealth.removeModifier(modifier);
                  }

                  modifier = new AttributeModifier(MODIFIER_UUID, "Broken Heart MaxHP drain", prevMaxHealthDamage - healthToRemove, 0);
                  maxHealth.applyModifier(modifier);
                  player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_IRONGOLEM_HURT, SoundCategory.PLAYERS, 1.2F, (player.world.rand.nextFloat() - player.world.rand.nextFloat()) * 0.1F + 0.8F);
               }

            }
         }
      }
   }
}
