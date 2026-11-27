package ichttt.mods.firstaid.common.damagesystem.distribution;

import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.tuple.Pair;

public class StandardDamageDistribution extends DamageDistribution {
   private final List<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> partList;
   private final boolean shuffle;
   private final boolean doNeighbours;
   private final EnumSet<EnumPlayerPart> blockedParts;

   public StandardDamageDistribution(List<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> partList, boolean shuffle, boolean doNeighbours) {
      this.partList = partList;

      for(Pair<EntityEquipmentSlot, EnumPlayerPart[]> pair : partList) {
         for(EnumPlayerPart part : (EnumPlayerPart[])pair.getRight()) {
            if (part.slot != pair.getLeft()) {
               throw new RuntimeException(part + " is not a member of " + pair.getLeft());
            }
         }
      }

      this.shuffle = shuffle;
      this.doNeighbours = doNeighbours;
      this.blockedParts = EnumSet.noneOf(EnumPlayerPart.class);
   }

   private StandardDamageDistribution(List<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> partList, boolean shuffle, boolean doNeighbours, EnumSet<EnumPlayerPart> blockedParts) {
      this.partList = partList;
      this.shuffle = shuffle;
      this.doNeighbours = doNeighbours;
      this.blockedParts = blockedParts;
   }

   @Nonnull
   protected List<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> getPartList() {
      if (this.shuffle) {
         Collections.shuffle(this.partList);
      }

      return this.partList;
   }

   public float distributeDamage(float damage, @Nonnull EntityPlayer player, @Nonnull DamageSource source, boolean addStat) {
      float rest = super.distributeDamage(damage, player, source, addStat);
      if (rest > 0.0F && this.doNeighbours) {
         EnumSet<EnumPlayerPart> neighboursSet = EnumSet.noneOf(EnumPlayerPart.class);
         EnumSet<EnumPlayerPart> blockedParts = EnumSet.copyOf(this.blockedParts);

         for(Pair<EntityEquipmentSlot, EnumPlayerPart[]> pair : this.partList) {
            blockedParts.addAll(Arrays.asList(pair.getRight()));
         }

         for(int i = this.partList.size() - 1; i >= 0; --i) {
            EnumPlayerPart[] parts = (EnumPlayerPart[])((Pair)this.partList.get(i)).getRight();

            for(EnumPlayerPart part : parts) {
               neighboursSet.addAll(part.getNeighbours());
            }

            neighboursSet.removeIf(blockedParts::contains);
            if (!neighboursSet.isEmpty()) {
               List<EnumPlayerPart> neighbours = new ArrayList(neighboursSet);
               Collections.shuffle(neighbours);
               Map<EntityEquipmentSlot, List<EnumPlayerPart>> neighbourMapping = new LinkedHashMap();

               for(EnumPlayerPart neighbour : neighbours) {
                  ((List)neighbourMapping.computeIfAbsent(neighbour.slot, (type) -> {
                     return new ArrayList(3);
                  })).add(neighbour);
               }

               List<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> neighbourDistributions = new ArrayList();

               for(Entry<EntityEquipmentSlot, List<EnumPlayerPart>> entry : neighbourMapping.entrySet()) {
                  neighbourDistributions.add(Pair.of(entry.getKey(), ((List<EnumPlayerPart>)entry.getValue()).toArray(new EnumPlayerPart[0])));
               }

               StandardDamageDistribution remainingDistribution = new StandardDamageDistribution(neighbourDistributions, false, true, blockedParts);
               rest = remainingDistribution.distributeDamage(rest, player, source, addStat);
               if (rest <= 0.0F) {
                  break;
               }

               blockedParts = remainingDistribution.blockedParts;
               neighboursSet.clear();
            }
         }
      }

      return rest;
   }
}
