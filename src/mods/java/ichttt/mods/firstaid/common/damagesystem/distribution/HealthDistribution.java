package ichttt.mods.firstaid.common.damagesystem.distribution;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.network.MessageAddHealth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;

public class HealthDistribution {
   private static final List<EnumPlayerPart> parts;

   public HealthDistribution() {
   }

   public static void manageHealth(float health, AbstractPlayerDamageModel damageModel, EntityPlayer player, boolean sendChanges, boolean distribute) {
      if (sendChanges && player.world.isRemote) {
         FirstAid.LOGGER.warn("The sendChanges flag was set on the client, it can however only work on the server!", new RuntimeException("sendChanges flag set on the client, this is not supported!"));
         sendChanges = false;
      } else if (sendChanges && !(player instanceof EntityPlayerMP)) {
         sendChanges = false;
      }

      float toHeal = distribute ? health / 8.0F : health;
      player.world.rand.shuffle(parts);
      List<AbstractDamageablePart> damageableParts = new ArrayList(parts.size());

      for(EnumPlayerPart part : parts) {
         damageableParts.add(damageModel.getFromEnum(part));
      }

      if (distribute) {
         damageableParts.sort(Comparator.comparingDouble((value) -> {
            return (double)((float)value.getMaxHealth() - value.currentHealth);
         }));
      }

      float[] healingDone = new float[8];

      for(int i = 0; i < 8; ++i) {
         AbstractDamageablePart part = (AbstractDamageablePart)damageableParts.get(i);
         float diff = toHeal - part.heal(toHeal, player, !player.world.isRemote);
         diff = (float)Math.round(diff * 10000.0F) / 10000.0F;
         healingDone[part.part.id - 1] = diff;
         health -= diff;
         if (distribute) {
            if (i < 7) {
               toHeal = health / (7.0F - (float)i);
            }
         } else {
            toHeal -= diff;
            if (toHeal <= 0.0F) {
               break;
            }
         }
      }

      if (sendChanges) {
         EntityPlayerMP playerMP = (EntityPlayerMP)player;
         if (playerMP.connection != null && playerMP.connection.netManager != null) {
            FirstAid.NETWORKING.sendTo(new MessageAddHealth(healingDone), (EntityPlayerMP)player);
         } else {
            damageModel.scheduleResync();
         }
      }

   }

   public static void distributeHealth(float health, EntityPlayer player, boolean sendChanges) {
      AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null);
      manageHealth(health, damageModel, player, sendChanges, true);
   }

   public static void addRandomHealth(float health, EntityPlayer player, boolean sendChanges) {
      AbstractPlayerDamageModel damageModel = (AbstractPlayerDamageModel)player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, (EnumFacing)null);
      manageHealth(health, damageModel, player, sendChanges, false);
   }

   static {
      EnumPlayerPart[] partArray = EnumPlayerPart.VALUES;
      parts = new ArrayList(partArray.length);
      parts.addAll(Arrays.asList(partArray));
   }
}
