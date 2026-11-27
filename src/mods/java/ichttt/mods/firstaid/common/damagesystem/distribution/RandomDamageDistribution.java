package ichttt.mods.firstaid.common.damagesystem.distribution;

import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.util.CommonUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.apache.commons.lang3.tuple.Pair;

public class RandomDamageDistribution extends DamageDistribution {
   public static final RandomDamageDistribution NEAREST_NOKILL = new RandomDamageDistribution(true, true);
   public static final RandomDamageDistribution NEAREST_KILL = new RandomDamageDistribution(true, false);
   public static final RandomDamageDistribution ANY_NOKILL = new RandomDamageDistribution(false, true);
   public static final RandomDamageDistribution ANY_KILL = new RandomDamageDistribution(false, false);
   private static final EaglercraftRandom RANDOM = new EaglercraftRandom();
   private final boolean nearestFirst;
   private final boolean tryNoKill;

   public static RandomDamageDistribution getDefault() {
      return FirstAidConfig.useFriendlyRandomDistribution ? NEAREST_NOKILL : NEAREST_KILL;
   }

   protected RandomDamageDistribution(boolean nearestFirst, boolean tryNoKill) {
      this.nearestFirst = nearestFirst;
      this.tryNoKill = tryNoKill;
   }

   protected float minHealth(@Nonnull EntityPlayer player, @Nonnull AbstractDamageablePart playerPart) {
      return this.tryNoKill && playerPart.canCauseDeath ? 1.0F : 0.0F;
   }

   @Nonnull
   protected List<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> getPartList() {
      if (this.nearestFirst) {
         int startValue = RANDOM.nextInt(4);
         return addAllRandom(startValue, RANDOM.nextBoolean());
      } else {
         List<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> partList = new ArrayList();
         List<EntityEquipmentSlot> slots = Arrays.asList(EntityEquipmentSlot.values());
         RANDOM.shuffle(slots);

         for(EntityEquipmentSlot slot : slots) {
            if (CommonUtils.isValidArmorSlot(slot)) {
               List<EnumPlayerPart> parts = CommonUtils.getPartListForSlot(slot);
               Collections.shuffle(parts);
               partList.add(Pair.of(slot, parts.toArray(new EnumPlayerPart[0])));
            }
         }

         return partList;
      }
   }

   public static List<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> addAllRandom(int startValue, boolean up) {
      List<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> partList = new ArrayList();

      for(int i = 0; i < CommonUtils.ARMOR_SLOTS.length; ++i) {
         int posInArray = Math.abs(i - (up ? 0 : 3)) + startValue;
         if (posInArray > 3) {
            posInArray -= 4;
         }

         EntityEquipmentSlot slot = CommonUtils.ARMOR_SLOTS[posInArray];
         List<EnumPlayerPart> parts = CommonUtils.getPartListForSlot(slot);
         Collections.shuffle(parts);
         partList.add(Pair.of(slot, parts.toArray(new EnumPlayerPart[0])));
      }

      return partList;
   }
}
