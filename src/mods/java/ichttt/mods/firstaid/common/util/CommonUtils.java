package ichttt.mods.firstaid.common.util;

import com.creativemd.playerrevive.api.IRevival;
import com.creativemd.playerrevive.api.capability.CapaRevive;
import com.google.common.primitives.Ints;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.DataManagerWrapper;
import ichttt.mods.firstaid.common.damagesystem.distribution.HealthDistribution;
import ichttt.mods.firstaid.common.network.MessageSyncDamageModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class CommonUtils {
   @Nonnull
   public static final EntityEquipmentSlot[] ARMOR_SLOTS = new EntityEquipmentSlot[4];
   @Nonnull
   private static final Map<EntityEquipmentSlot, List<EnumPlayerPart>> slotToParts = new EnumMap(EntityEquipmentSlot.class);

   public CommonUtils() {
   }

   public static List<EnumPlayerPart> getPartListForSlot(EntityEquipmentSlot slot) {
      return new ArrayList((Collection)slotToParts.get(slot));
   }

   public static EnumPlayerPart[] getPartArrayForSlot(EntityEquipmentSlot slot) {
      return (EnumPlayerPart[])getPartListForSlot(slot).toArray(new EnumPlayerPart[0]);
   }

   public static void killPlayer(@Nonnull AbstractPlayerDamageModel damageModel, @Nonnull EntityPlayer player, @Nullable DamageSource source) {
      if (player.world.isRemote) {
         try {
            throw new RuntimeException("Tried to kill the player on the client!");
         } catch (RuntimeException var10) {
            FirstAid.LOGGER.warn("Tried to kill the player on the client! This should only happen on the server! Ignoring...", var10);
         }
      }

      if (source != null && FirstAidConfig.externalHealing.allowOtherHealingItems) {
         DataManagerWrapper wrapper = (DataManagerWrapper)player.dataManager;
         wrapper.toggleTracking(false);

         boolean protection;
         try {
            protection = player.checkTotemDeathProtection(source);
         } finally {
            wrapper.toggleTracking(true);
         }

         if (protection) {
            for(AbstractDamageablePart part : damageModel) {
               if (part.canCauseDeath) {
                  part.currentHealth = Math.max(part.currentHealth, 1.0F);
               }
            }

            if (player instanceof EntityPlayerMP) {
               FirstAid.NETWORKING.sendTo(new MessageSyncDamageModel(damageModel, false), (EntityPlayerMP)player);
            }

            return;
         }
      }

      IRevival revival = getRevivalIfPossible(player);
      if (revival != null) {
         revival.startBleeding(player, source);
      } else {
         ((DataManagerWrapper)player.dataManager).set_impl(EntityPlayer.HEALTH, Float.valueOf(0.0F));
      }

   }

   @Nullable
   public static IRevival getRevivalIfPossible(@Nullable EntityPlayer player) {
      if (player != null && CapaRevive.reviveCapa != null) {
         MinecraftServer server = player.getServer();
         if (server == null) {
            return null;
         } else {
            IRevival revival = (IRevival)player.getCapability(CapaRevive.reviveCapa, (EnumFacing)null);
            return revival != null && server.getPlayerList().getCurrentPlayerCount() > 1 ? revival : null;
         }
      } else {
         return null;
      }
   }

   public static boolean isValidArmorSlot(EntityEquipmentSlot slot) {
      return slot.getSlotType() == Type.ARMOR;
   }

   public static boolean isSurvivalOrAdventure(EntityPlayer player) {
      return !player.isSpectator() && !player.isCreative();
   }

   @Nonnull
   public static String getActiveModidSafe() {
      ModContainer activeModContainer = Loader.instance().activeModContainer();
      return activeModContainer == null ? "UNKNOWN-NULL" : activeModContainer.getModId();
   }

   public static void healPlayerByPercentage(double percentage, AbstractPlayerDamageModel damageModel, EntityPlayer player) {
      Objects.requireNonNull(damageModel);
      int healValue = Ints.checkedCast(Math.round((double)damageModel.getCurrentMaxHealth() * percentage));
      HealthDistribution.manageHealth((float)healValue, damageModel, player, true, false);
   }

   public static void debugLogStacktrace(String name) {
      if (FirstAidConfig.debug) {
         try {
            throw new RuntimeException("DEBUG:" + name);
         } catch (RuntimeException var2) {
            FirstAid.LOGGER.info("DEBUG: " + name, var2);
         }
      }
   }

   static {
      ARMOR_SLOTS[3] = EntityEquipmentSlot.HEAD;
      ARMOR_SLOTS[2] = EntityEquipmentSlot.CHEST;
      ARMOR_SLOTS[1] = EntityEquipmentSlot.LEGS;
      ARMOR_SLOTS[0] = EntityEquipmentSlot.FEET;
      slotToParts.put(EntityEquipmentSlot.HEAD, Collections.singletonList(EnumPlayerPart.HEAD));
      slotToParts.put(EntityEquipmentSlot.CHEST, Arrays.asList(EnumPlayerPart.LEFT_ARM, EnumPlayerPart.RIGHT_ARM, EnumPlayerPart.BODY));
      slotToParts.put(EntityEquipmentSlot.LEGS, Arrays.asList(EnumPlayerPart.LEFT_LEG, EnumPlayerPart.RIGHT_LEG));
      slotToParts.put(EntityEquipmentSlot.FEET, Arrays.asList(EnumPlayerPart.LEFT_FOOT, EnumPlayerPart.RIGHT_FOOT));
   }
}
