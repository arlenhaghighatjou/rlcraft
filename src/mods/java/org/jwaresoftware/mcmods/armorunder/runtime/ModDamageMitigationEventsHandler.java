package org.jwaresoftware.mcmods.armorunder.runtime;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jwaresoftware.mcmods.lib.Armory;
import org.jwaresoftware.mcmods.lib.Effects;
import org.jwaresoftware.mcmods.lib.Potions;
import org.jwaresoftware.mcmods.lib.SharedGlue;
import org.jwaresoftware.mcmods.lib.Armory.XLining;
import org.jwaresoftware.mcmods.lib.Armory.XLining.Def;

public final class ModDamageMitigationEventsHandler {
   private static final int _DOUSING_CHECK_MODULO = 4 * SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER();
   private static final int _EFFECTS_CHECK_MODULO = 1 * SharedGlue.SECS_TO_TICKS_DURATION_MULTIPLIER();

   public ModDamageMitigationEventsHandler() {
   }

   private int isProtecting(EntityPlayer player, boolean cold) {
      int n = 0;

      for(ItemStack armor : player.getArmorInventoryList()) {
         Def liningd = Armory.getXLining(armor);
         if (cold) {
            if (liningd.name == XLining.ANTIFREEZE_SHIELD) {
               ++n;
            }
         } else if (liningd.name == XLining.OBSIDIAN_SHIELD) {
            ++n;
         }
      }

      return n;
   }

   private boolean isHeatRelated(DamageSource what) {
      if (Armory.isDamageAbsolute(what)) {
         return false;
      } else {
         return what.isFireDamage() || what.isExplosion() || what == SharedGlue.DamageSource_lightingBolt || what == SharedGlue.DamageSource_dragonBreath;
      }
   }

   @SubscribeEvent
   public void onPlayerProtectedByObsidianShielding(LivingDamageEvent event) {
      EntityPlayer player = SharedGlue.getRealPlayerOrNull(event.getEntity());
      if (player != null && !SharedGlue.isaClientWorld(player.getEntityWorld())) {
         DamageSource what = event.getSource();
         int pieces = 0;
         if (this.isHeatRelated(what) && (pieces = this.isProtecting(player, false)) > 0) {
            float amount = event.getAmount();
            if (pieces <= 3 && (pieces != 3 || amount > 1.0F)) {
               float nullified = event.getAmount() * ((float)pieces / 4.0F);
               event.setAmount(event.getAmount() - nullified);
            } else {
               event.setCanceled(true);
            }
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onPlayerDousedByFireproofShielding(LivingUpdateEvent event) {
      EntityPlayer player = SharedGlue.getRealPlayerOrNull(event.getEntity());
      if (player != null && !player.isPotionActive(SharedGlue.Potion_fireResistance)) {
         if (player.ticksExisted % _DOUSING_CHECK_MODULO == 0 && player.isBurning() && !player.isInLava() && (Armory.hasXLining(XLining.OBSIDIAN_SHIELD, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST)) || Armory.hasXLining(XLining.OBSIDIAN_SHIELD, player.getItemStackFromSlot(EntityEquipmentSlot.LEGS)))) {
            player.extinguish();
            Effects.playExtinguishedBurning(player.getEntityWorld(), player);
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onPlayerProtectedByAntifreezeShielding(LivingUpdateEvent event) {
      EntityPlayer player = SharedGlue.getRealPlayerOrNull(event.getEntity());
      if (player != null && !SharedGlue.isaClientWorld(player.getEntityWorld())) {
         if (player.ticksExisted % _EFFECTS_CHECK_MODULO == 0 && SharedGlue.hasPotionsInEffect(player)) {
            int pieces = 0;
            if ((pieces = this.isProtecting(player, true)) > 0 && (pieces > 3 || player.getRNG().nextFloat() < (float)pieces * 0.25F)) {
               Potions.cureFreezingEffects(player, true);
            }
         }

      }
   }
}
