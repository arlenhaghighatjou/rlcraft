package ichttt.mods.firstaid.api.distribution;

import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import javax.annotation.Nonnull;
import net.minecraft.inventory.EntityEquipmentSlot;

public interface IStandardDamageDistributionBuilder extends IDamageDistributionBuilder {
   @Nonnull
   IStandardDamageDistributionBuilder addDistributionLayer(EntityEquipmentSlot var1, EnumPlayerPart... var2);

   @Nonnull
   IStandardDamageDistributionBuilder ignoreOrder();

   @Nonnull
   IStandardDamageDistributionBuilder disableNeighbourRestDistribution();
}
