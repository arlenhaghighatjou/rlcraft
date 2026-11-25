package org.jwaresoftware.mcmods.lib.integration;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Loader;

public final class BaublesHelper {
   private static final String _MOD_ID = "baubles";

   public BaublesHelper() {
   }

   public static final ItemStack findBauble(@Nonnull Predicate<ItemStack> test, @Nonnull EntityPlayer player) {
      if (Loader.isModLoaded("baubles")) {
         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

         for(int i = 0; i < baubles.getSlots(); ++i) {
            ItemStack bauble = baubles.getStackInSlot(i);
            if (test.apply(bauble)) {
               return bauble;
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public static final void iterateBaubles(@Nonnull Function<BaublesHelper.OwnedBauble, Void> function, @Nonnull EntityPlayer player) {
      if (Loader.isModLoaded("baubles")) {
         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

         for(int i = 0; i < baubles.getSlots(); ++i) {
            ItemStack bauble = baubles.getStackInSlot(i);
            function.apply(new BaublesHelper.OwnedBauble(player, bauble));
         }
      }

   }

   public static final <T> T iterateBaubles(@Nonnull Function<BaublesHelper.OwnedBauble, T> function, @Nonnull EntityPlayer player, T r) {
      if (Loader.isModLoaded("baubles")) {
         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

         for(int i = 0; i < baubles.getSlots(); ++i) {
            ItemStack bauble = baubles.getStackInSlot(i);
            r = (T)function.apply(new BaublesHelper.OwnedBauble(player, bauble, r));
         }
      }

      return r;
   }

   public static final class GenericAmuletImpl implements ICapabilityProvider {
      public GenericAmuletImpl() {
      }

      public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
         return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
      }

      @Nullable
      public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
         return (T)(capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? (bauble) -> {
            return (T)BaubleType.AMULET;
         } : null);
      }
   }

   public static final class GenericLuckyCharmImpl implements ICapabilityProvider {
      public GenericLuckyCharmImpl() {
      }

      public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
         return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
      }

      @Nullable
      public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
         return (T)(capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? (bauble) -> {
            return (T)BaubleType.CHARM;
         } : null);
      }
   }

   public static final class GenericTrinketImpl implements ICapabilityProvider {
      public GenericTrinketImpl() {
      }

      public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
         return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
      }

      @Nullable
      public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
         return (T)(capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? (bauble) -> {
            return (T)BaubleType.TRINKET;
         } : null);
      }
   }

   public static final class OwnedBauble {
      @Nonnull
      public final EntityPlayer owner;
      @Nonnull
      public final ItemStack bauble;
      public Object ticket;

      OwnedBauble(EntityPlayer player, ItemStack stack) {
         this.owner = player;
         this.bauble = stack;
      }

      OwnedBauble(EntityPlayer player, ItemStack stack, Object r) {
         this(player, stack);
         this.ticket = r;
      }
   }
}
