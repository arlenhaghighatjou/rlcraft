package ichttt.mods.firstaid.api;

import ichttt.mods.firstaid.api.damagesystem.AbstractPartHealer;
import ichttt.mods.firstaid.api.debuff.IDebuff;
import ichttt.mods.firstaid.api.debuff.builder.IDebuffBuilder;
import ichttt.mods.firstaid.api.enums.EnumDebuffSlot;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import org.apache.commons.lang3.tuple.Pair;

public abstract class FirstAidRegistry {
   @Nullable
   private static FirstAidRegistry instance;

   public FirstAidRegistry() {
   }

   public static void setImpl(@Nonnull FirstAidRegistry registry) {
      instance = registry;
   }

   @Nullable
   public static FirstAidRegistry getImpl() {
      return instance;
   }

   /** @deprecated */
   @Deprecated
   public abstract void bindDamageSourceStandard(@Nonnull DamageSource var1, @Nonnull List<Pair<EntityEquipmentSlot, EnumPlayerPart[]>> var2, boolean var3);

   /** @deprecated */
   @Deprecated
   public abstract void bindDamageSourceRandom(@Nonnull DamageSource var1, boolean var2, boolean var3);

   /** @deprecated */
   @Deprecated
   public abstract void bindDamageSourceCustom(@Nonnull DamageSource var1, @Nonnull IDamageDistribution var2);

   public abstract void registerDebuff(@Nonnull EnumDebuffSlot var1, @Nonnull IDebuffBuilder var2);

   public abstract void registerDebuff(@Nonnull EnumDebuffSlot var1, @Nonnull Supplier<IDebuff> var2);

   public abstract void registerHealingType(@Nonnull Item var1, @Nonnull Function<ItemStack, AbstractPartHealer> var2, Function<ItemStack, Integer> var3);

   @Nullable
   public abstract AbstractPartHealer getPartHealer(@Nonnull ItemStack var1);

   public abstract Integer getPartHealingTime(@Nonnull ItemStack var1);

   /** @deprecated */
   @Deprecated
   @Nonnull
   public abstract IDamageDistribution getDamageDistribution(@Nonnull DamageSource var1);

   @Nullable
   public abstract IDamageDistribution getDamageDistributionForSource(@Nonnull DamageSource var1);

   @Nonnull
   public abstract IDebuff[] getDebuffs(@Nonnull EnumDebuffSlot var1);
}
