package ejektaflex.bountiful.logic;

import ejektaflex.bountiful.Bountiful;
import ejektaflex.bountiful.api.ext.ExtMiscKt;
import ejektaflex.bountiful.api.logic.picked.IPickedEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntryEntity;
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack;
import ejektaflex.bountiful.data.BountyData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001a\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006H\u0002J\u0018\u0010\b\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0002J\u000e\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u000eJ,\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010\u00102\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\n0\u00142\u0006\u0010\r\u001a\u00020\u000eJ\u001e\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0017\u001a\u00020\nJ2\u0010\u0018\u001a\u00020\u00162\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\n0\u00142\u0006\u0010\r\u001a\u00020\u000e2\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\n0\u0010J&\u0010\u001a\u001a\u00020\u00162\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u001b\u001a\u00020\n2\u0006\u0010\u001c\u001a\u00020\u001dJ\u0018\u0010\u001e\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\nH\u0002\u00a8\u0006\u001f"},
   d2 = {"Lejektaflex/bountiful/logic/BountyChecker;", "", "()V", "checkNestedCompound", "", "bountyCompound", "Lnet/minecraft/nbt/NBTTagCompound;", "stackCompound", "compareStackTags", "stack", "Lnet/minecraft/item/ItemStack;", "bountystack", "hasEntitiesFulfilled", "data", "Lejektaflex/bountiful/data/BountyData;", "hasItems", "", "player", "Lnet/minecraft/entity/player/EntityPlayer;", "inv", "Lnet/minecraft/util/NonNullList;", "rewardItems", "", "bountyItem", "takeItems", "matched", "tryTakeEntities", "bounty", "entity", "Lnet/minecraft/entity/EntityLivingBase;", "validStackCheck", "Bountiful"}
)
public final class BountyChecker {
   public static final BountyChecker INSTANCE;

   private final boolean validStackCheck(ItemStack stack, ItemStack bountystack) {
      if (Bountiful.INSTANCE.getConfig().getRequireExactNbt()) {
         return stack.isItemEqualIgnoreDurability(bountystack) && ItemStack.areItemStackTagsEqual(stack, bountystack);
      } else {
         return stack.isItemEqualIgnoreDurability(bountystack) && this.compareStackTags(stack, bountystack);
      }
   }

   private final boolean compareStackTags(ItemStack stack, ItemStack bountystack) {
      NBTTagCompound bountyCompound = bountystack.getTagCompound();
      return bountyCompound != null && !bountyCompound.hasNoTags() ? this.checkNestedCompound(bountyCompound, stack.getTagCompound()) : true;
   }

   private final boolean checkNestedCompound(NBTTagCompound bountyCompound, NBTTagCompound stackCompound) {
      if (stackCompound == null) {
         return false;
      } else {
         Iterator var4 = bountyCompound.getKeySet().iterator();

         while(true) {
            if (!var4.hasNext()) {
               return true;
            }

            String key = (String)var4.next();
            NBTBase a = bountyCompound.getTag(key);
            NBTBase b = stackCompound.getTag(key);
            if (a != null) {
               if (a instanceof NBTTagCompound && b instanceof NBTTagCompound) {
                  if (!this.checkNestedCompound((NBTTagCompound)a, (NBTTagCompound)b)) {
                     break;
                  }
               } else if (!a.equals(b)) {
                  return false;
               }
            } else if (b != null) {
               return false;
            }
         }

         return false;
      }
   }

   @Nullable
   public final List<ItemStack> hasItems(@NotNull EntityPlayer player, @NotNull NonNullList<ItemStack> inv, @NotNull BountyData data) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      Intrinsics.checkParameterIsNotNull(inv, "inv");
      Intrinsics.checkParameterIsNotNull(data, "data");
      Iterable $receiver$iv = (Iterable)data.getToGet().getItems();
      Collection destination$iv$iv = (Collection)(new ArrayList());

      for(Object element$iv$iv$iv : $receiver$iv) {
         IPickedEntry it = (IPickedEntry)element$iv$iv$iv;
         IPickedEntry var10000 = it;
         if (!(it instanceof PickedEntryStack)) {
            var10000 = null;
         }

         PickedEntryStack var46 = (PickedEntryStack)var10000;
         if (var46 != null) {
            PickedEntryStack element$iv = var46;
            destination$iv$iv.add(element$iv);
         }
      }

      List stackPicked = (List)destination$iv$iv;
      System.out.println(stackPicked);
      Iterable $receiver$iv = (Iterable)inv;
      Collection destination$iv$iv = (Collection)(new ArrayList());

      for(Object element$iv$iv : $receiver$iv) {
         ItemStack invItem = (ItemStack)element$iv$iv;
         Iterable $receiver$iv = (Iterable)stackPicked;
         boolean var49;
         if ($receiver$iv instanceof Collection && ((Collection)$receiver$iv).isEmpty()) {
            var49 = false;
         } else {
            Iterator sum$iv = $receiver$iv.iterator();

            while(true) {
               if (!sum$iv.hasNext()) {
                  var49 = false;
                  break;
               }

               label103: {
                  Object element$iv = sum$iv.next();
                  PickedEntryStack picked = (PickedEntryStack)element$iv;
                  ItemStack var47 = picked.getItemStack();
                  if (var47 != null) {
                     if (var47.isItemEqualIgnoreDurability(invItem)) {
                        var49 = true;
                        break label103;
                     }
                  }

                  var49 = false;
               }

               if (var49) {
                  var49 = true;
                  break;
               }
            }
         }

         if (var49) {
            destination$iv$iv.add(element$iv$iv);
         }
      }

      List prereqItems = (List)destination$iv$iv;
      String var26 = "Prereq items: " + prereqItems;
      System.out.println(var26);
      Iterable $receiver$iv = (Iterable)stackPicked;
      boolean var51;
      if ($receiver$iv instanceof Collection && ((Collection)$receiver$iv).isEmpty()) {
         var51 = true;
      } else {
         Iterator var29 = $receiver$iv.iterator();

         while(true) {
            if (!var29.hasNext()) {
               var51 = true;
               break;
            }

            Object element$iv = var29.next();
            PickedEntryStack picked = (PickedEntryStack)element$iv;
            Iterable $receiver$iv = (Iterable)prereqItems;
            Collection destination$iv$iv = (Collection)(new ArrayList());

            for(Object element$iv$iv : $receiver$iv) {
               ItemStack it = (ItemStack)element$iv$iv;
               BountyChecker var50 = INSTANCE;
               Intrinsics.checkExpressionValueIsNotNull(it, "it");
               ItemStack var10002 = picked.getItemStack();
               if (var10002 == null) {
                  Intrinsics.throwNpe();
               }

               if (var50.validStackCheck(it, var10002)) {
                  destination$iv$iv.add(element$iv$iv);
               }
            }

            List stacksMatching = (List)destination$iv$iv;
            Iterable $receiver$iv = (Iterable)stacksMatching;
            int sum$iv = 0;

            for(Object element$iv : $receiver$iv) {
               ItemStack var45 = (ItemStack)element$iv;
               Intrinsics.checkExpressionValueIsNotNull(var45, "it");
               int $i$f$filterTo = var45.getCount();
               sum$iv += $i$f$filterTo;
            }

            boolean hasEnough = sum$iv >= picked.getAmount();
            if (!hasEnough) {
               ExtMiscKt.sendTranslation((ICommandSender)player, "bountiful.cannot.fulfill");
            }

            if (!hasEnough) {
               var51 = false;
               break;
            }
         }
      }

      boolean hasAllItems = var51;
      return hasAllItems ? prereqItems : null;
   }

   public final void takeItems(@NotNull EntityPlayer player, @NotNull NonNullList<ItemStack> inv, @NotNull BountyData data, @NotNull List<ItemStack> matched) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      Intrinsics.checkParameterIsNotNull(inv, "inv");
      Intrinsics.checkParameterIsNotNull(data, "data");
      Intrinsics.checkParameterIsNotNull(matched, "matched");
      Iterable $receiver$iv = (Iterable)data.getToGet().getItems();
      Collection destination$iv$iv = (Collection)(new ArrayList());

      for(Object element$iv$iv$iv : $receiver$iv) {
         IPickedEntry it = (IPickedEntry)element$iv$iv$iv;
         IPickedEntry var10000 = it;
         if (!(it instanceof PickedEntryStack)) {
            var10000 = null;
         }

         PickedEntryStack var31 = (PickedEntryStack)var10000;
         if (var31 != null) {
            PickedEntryStack it = var31;
            destination$iv$iv.add(it);
         }
      }

      for(destination$iv$iv : (Iterable)((List)destination$iv$iv)) {
         PickedEntryStack picked = (PickedEntryStack)destination$iv$iv;
         Iterable $receiver$iv = (Iterable)matched;
         Collection destination$iv$iv = (Collection)(new ArrayList());

         for(Object element$iv$iv : $receiver$iv) {
            ItemStack it = (ItemStack)element$iv$iv;
            BountyChecker var32 = INSTANCE;
            ItemStack var10002 = picked.getItemStack();
            if (var10002 == null) {
               Intrinsics.throwNpe();
            }

            if (var32.validStackCheck(it, var10002)) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         for(ItemStack stack : (List)destination$iv$iv) {
            if (picked.getAmount() == 0) {
               break;
            }

            int var28 = stack.getCount();
            int var29 = picked.getAmount();
            int amountToRemove = Math.min(var28, var29);
            stack.setCount(stack.getCount() - amountToRemove);
            picked.setAmount(picked.getAmount() - amountToRemove);
         }
      }

   }

   public final void tryTakeEntities(@NotNull EntityPlayer player, @NotNull BountyData data, @NotNull ItemStack bounty, @NotNull EntityLivingBase entity) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      Intrinsics.checkParameterIsNotNull(data, "data");
      Intrinsics.checkParameterIsNotNull(bounty, "bounty");
      Intrinsics.checkParameterIsNotNull(entity, "entity");
      World var10001 = player.world;
      Intrinsics.checkExpressionValueIsNotNull(player.world, "player.world");
      if (!data.hasExpired(var10001)) {
         Iterable $receiver$iv = (Iterable)data.getToGet().getItems();
         Collection destination$iv$iv = (Collection)(new ArrayList());

         for(Object element$iv$iv$iv : $receiver$iv) {
            IPickedEntry it = (IPickedEntry)element$iv$iv$iv;
            IPickedEntry var10000 = it;
            if (!(it instanceof PickedEntryEntity)) {
               var10000 = null;
            }

            PickedEntryEntity var24 = (PickedEntryEntity)var10000;
            if (var24 != null) {
               PickedEntryEntity var15 = var24;
               destination$iv$iv.add(var15);
            }
         }

         List bountyEntities = (List)destination$iv$iv;

         for(Object element$iv : (Iterable)bountyEntities) {
            PickedEntryEntity picked;
            String var27;
            label43: {
               picked = (PickedEntryEntity)element$iv;
               EntityEntry var25 = picked.getEntityEntry();
               if (var25 != null) {
                  ResourceLocation var26 = var25.getRegistryName();
                  if (var26 != null) {
                     var27 = var26.toString();
                     break label43;
                  }
               }

               var27 = null;
            }

            ResourceLocation var28 = ExtMiscKt.getRegistryName((Entity)entity);
            if (Intrinsics.areEqual(var27, var28 != null ? var28.toString() : null) && picked.getKilledAmount() < picked.getAmount()) {
               picked.setKilledAmount(picked.getKilledAmount() + 1);
            }
         }

         bounty.setTagCompound(data.serializeNBT());
      }
   }

   public final boolean hasEntitiesFulfilled(@NotNull BountyData data) {
      Intrinsics.checkParameterIsNotNull(data, "data");
      Iterable $receiver$iv = (Iterable)data.getToGet().getItems();
      Collection destination$iv$iv = (Collection)(new ArrayList());

      for(Object element$iv$iv$iv : $receiver$iv) {
         IPickedEntry it = (IPickedEntry)element$iv$iv$iv;
         IPickedEntry var10000 = it;
         if (!(it instanceof PickedEntryEntity)) {
            var10000 = null;
         }

         PickedEntryEntity var21 = (PickedEntryEntity)var10000;
         if (var21 != null) {
            PickedEntryEntity var12 = var21;
            destination$iv$iv.add(var12);
         }
      }

      List bountyEntities = (List)destination$iv$iv;
      boolean var22;
      if (bountyEntities.isEmpty()) {
         var22 = true;
      } else {
         $receiver$iv = (Iterable)bountyEntities;
         if ($receiver$iv instanceof Collection && ((Collection)$receiver$iv).isEmpty()) {
            var22 = true;
         } else {
            for(Object element$iv : $receiver$iv) {
               PickedEntryEntity it = (PickedEntryEntity)element$iv;
               if (it.getKilledAmount() != it.getAmount()) {
                  var22 = false;
                  return var22;
               }
            }

            var22 = true;
         }
      }

      return var22;
   }

   public final void rewardItems(@NotNull EntityPlayer player, @NotNull BountyData data, @NotNull ItemStack bountyItem) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      Intrinsics.checkParameterIsNotNull(data, "data");
      Intrinsics.checkParameterIsNotNull(bountyItem, "bountyItem");

      for(Object element$iv : (Iterable)data.getRewards().getItems()) {
         PickedEntryStack reward = (PickedEntryStack)element$iv;
         int amountNeededToGive = reward.getAmount();

         List stacksToGive;
         int stackSize;
         for(stacksToGive = (List)(new ArrayList()); amountNeededToGive > 0; amountNeededToGive -= stackSize) {
            int newStack = bountyItem.getMaxStackSize();
            stackSize = Math.min(amountNeededToGive, newStack);
            ItemStack var10000 = reward.getItemStack();
            if (var10000 == null) {
               Intrinsics.throwNpe();
            }

            ItemStack element$iv = var10000.copy();
            element$iv.setCount(stackSize);
            Intrinsics.checkExpressionValueIsNotNull(element$iv, "newStack");
            stacksToGive.add(element$iv);
         }

         for(Object element$iv : (Iterable)stacksToGive) {
            ItemStack stack = (ItemStack)element$iv;
            ItemHandlerHelper.giveItemToPlayer(player, stack);
         }
      }

   }

   private BountyChecker() {
   }

   static {
      BountyChecker var0 = new BountyChecker();
      INSTANCE = var0;
   }
}
