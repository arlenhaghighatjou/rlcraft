package ichttt.mods.firstaid.common.apiimpl.distribution;

import ichttt.mods.firstaid.api.IDamageDistribution;
import ichttt.mods.firstaid.api.distribution.IStandardDamageDistributionBuilder;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.common.damagesystem.distribution.StandardDamageDistribution;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.apache.commons.lang3.tuple.Pair;

public class StandardDamageDistributionBuilder extends BaseDamageDistributionBuilder implements IStandardDamageDistributionBuilder {
   private final ArrayList<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> partList = new ArrayList();
   private boolean ignoreOrder;
   private boolean disableNeighbourRestDistribution;

   public StandardDamageDistributionBuilder() {
   }

   @Nonnull
   public IStandardDamageDistributionBuilder addDistributionLayer(EntityEquipmentSlot slot, EnumPlayerPart... parts) {
      this.partList.add(Pair.of(slot, parts));
      return this;
   }

   @Nonnull
   public IStandardDamageDistributionBuilder ignoreOrder() {
      this.ignoreOrder = true;
      return this;
   }

   @Nonnull
   public IStandardDamageDistributionBuilder disableNeighbourRestDistribution() {
      this.disableNeighbourRestDistribution = true;
      return this;
   }

   public IDamageDistribution build() {
      this.partList.trimToSize();
      if (this.partList.size() == 0) {
         throw new IllegalArgumentException("Missing parts!");
      } else {
         return new StandardDamageDistribution(this.partList, this.ignoreOrder, !this.disableNeighbourRestDistribution);
      }
   }
}
