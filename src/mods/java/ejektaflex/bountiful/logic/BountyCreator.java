package ejektaflex.bountiful.logic;

import ejektaflex.bountiful.Bountiful;
import ejektaflex.bountiful.ContentRegistry;
import ejektaflex.bountiful.api.enum.EnumBountyRarity;
import ejektaflex.bountiful.api.ext.ExtMiscKt;
import ejektaflex.bountiful.api.logic.IBountyCreator;
import ejektaflex.bountiful.api.logic.ItemRange;
import ejektaflex.bountiful.api.logic.pickable.PickableEntry;
import ejektaflex.bountiful.api.logic.picked.IPickedEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack;
import ejektaflex.bountiful.data.BountyData;
import ejektaflex.bountiful.item.ItemBounty;
import ejektaflex.bountiful.logic.error.BountyCreationException;
import ejektaflex.bountiful.registry.BountyRegistry;
import ejektaflex.bountiful.registry.RewardRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0005\u001a\u00020\u0006H\u0016J\u001c\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0006H\u0016J\u0012\u0010\f\u001a\u00020\b2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0006H\u0002J\u001a\u0010\r\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0006H\u0002J\u001a\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\t\u001a\u00020\n2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0006H\u0016J\u001e\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\t\u001a\u00020\nH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0018"},
   d2 = {"Lejektaflex/bountiful/logic/BountyCreator;", "Lejektaflex/bountiful/api/logic/IBountyCreator;", "()V", "rand", "Ljava/util/Random;", "calcRarity", "Lejektaflex/bountiful/api/enum/EnumBountyRarity;", "create", "Lejektaflex/bountiful/data/BountyData;", "world", "Lnet/minecraft/world/World;", "inRarity", "createPremadeBounty", "createRandomBounty", "createStack", "Lnet/minecraft/item/ItemStack;", "rarity", "findRewards", "", "Lejektaflex/bountiful/api/logic/picked/PickedEntryStack;", "n", "", "precheckRegistries", "", "Bountiful"}
)
public final class BountyCreator implements IBountyCreator {
   private static final Random rand = new Random();
   public static final BountyCreator INSTANCE;

   @NotNull
   public ItemStack createStack(@NotNull World world, @Nullable EnumBountyRarity rarity) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      ItemBounty var3 = ContentRegistry.INSTANCE.getBounty();
      ItemStack var5 = new ItemStack(var3);
      var3.ensureBounty(var5, world, rarity);
      return var5;
   }

   @NotNull
   public EnumBountyRarity calcRarity() {
      int level = 0;
      double chance = Bountiful.INSTANCE.getConfig().getRarityChance();
      int i = 0;

      for(byte var5 = 3; i < var5 && (double)rand.nextFloat() < chance; ++i) {
         ++level;
      }

      return EnumBountyRarity.Companion.getRarityFromInt(level);
   }

   private final void precheckRegistries(World world) {
      if (RewardRegistry.INSTANCE.validRewards(world).isEmpty()) {
         throw (Throwable)(new BountyCreationException("There are no valid rewards in the reward registry!"));
      } else if (BountyRegistry.INSTANCE.validBounties(world).size() < Bountiful.INSTANCE.getConfig().getBountyAmountRange().getLast()) {
         throw (Throwable)(new BountyCreationException("There are not enough valid bounties in the bounty registry! (At least " + Bountiful.INSTANCE.getConfig().getBountyAmountRange() + " must be valid at any one time)."));
      }
   }

   private final BountyData createRandomBounty(World world, EnumBountyRarity inRarity) {
      this.precheckRegistries(world);
      List pickedAlready = (List)(new ArrayList());
      IntRange pool = Bountiful.INSTANCE.getConfig().getBountyAmountRange();
      int toPick = RangesKt.random(pool, (kotlin.random.Random)kotlin.random.Random.Default);

      while(pickedAlready.size() < toPick) {
         Iterable $receiver$iv = (Iterable)BountyRegistry.INSTANCE.validBounties(world);
         Collection destination$iv$iv = (Collection)(new ArrayList());

         for(Object element$iv$iv : $receiver$iv) {
            PickableEntry it = (PickableEntry)element$iv$iv;
            if (!pickedAlready.contains(it)) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         List pool = (List)destination$iv$iv;
         PickableEntry toAdd = (PickableEntry)ExtMiscKt.getWeightedRandom(pool);
         pickedAlready.add(PickableEntry.copy$default(toAdd, (String)null, (ItemRange)null, 0, 0, (String)null, (List)null, 63, (Object)null));
      }

      BountyData var26 = new BountyData();
      BountyData $receiver = var26;
      var26.setRarity(inRarity != null ? inRarity.getLevel() : INSTANCE.calcRarity().getLevel());
      var26.setWorth(0);
      double preBountyTime = 0.0D;

      for(Object element$iv : (Iterable)pickedAlready) {
         PickableEntry it = (PickableEntry)element$iv;
         IPickedEntry picked = it.pick();
         if (picked.getContentObj() == null) {
            throw (Throwable)(new BountyCreationException("You tried to create a bounty but the item was invalid! Item was: " + picked.getContent()));
         }

         $receiver.getToGet().add(picked);
         $receiver.setWorth($receiver.getWorth() + picked.getAmount() * it.getUnitWorth());
         preBountyTime += (double)(picked.getAmount() * it.getUnitWorth()) * picked.timeMult();
      }

      long $i$f$filterTo = (long)(preBountyTime * Bountiful.INSTANCE.getConfig().getTimeMultiplier());
      long var15 = (long)Bountiful.INSTANCE.getConfig().getBountyTimeMin();
      long var18 = Math.max($i$f$filterTo, var15);
      $receiver.setBountyTime(var18);
      $receiver.setWorth((int)((double)$receiver.getWorth() * EnumBountyRarity.Companion.getRarityFromInt($receiver.getRarity()).getBountyMult()));

      for(Object element$iv : (Iterable)INSTANCE.findRewards(world, $receiver.getWorth())) {
         PickedEntryStack it = (PickedEntryStack)element$iv;
         $receiver.getRewards().add(it);
      }

      return var26;
   }

   private final BountyData createPremadeBounty(EnumBountyRarity inRarity) {
      return new BountyData();
   }

   @Nullable
   public BountyData create(@NotNull World world, @Nullable EnumBountyRarity inRarity) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      return Bountiful.INSTANCE.getConfig().getRandomBounties() ? this.createRandomBounty(world, inRarity) : this.createPremadeBounty(inRarity);
   }

   private final List<PickedEntryStack> findRewards(World world, int n) {
      int worthLeft = n;
      List toRet = (List)(new ArrayList());
      List picked = (List)(new ArrayList());
      List validRewards = RewardRegistry.INSTANCE.validRewards(world, n, picked);

      while(true) {
         Collection lowestWorthItem = (Collection)validRewards;
         if (lowestWorthItem.isEmpty()) {
            if (toRet.isEmpty()) {
               Iterable $receiver$iv = (Iterable)RewardRegistry.INSTANCE.validRewards(world);
               Iterator iterator$iv = $receiver$iv.iterator();
               Object var28;
               if (!iterator$iv.hasNext()) {
                  var28 = null;
               } else {
                  Object minElem$iv = iterator$iv.next();
                  PickedEntryStack it = (PickedEntryStack)minElem$iv;
                  int minValue$iv = it.getAmount();

                  while(iterator$iv.hasNext()) {
                     Object e$iv = iterator$iv.next();
                     PickedEntryStack it = (PickedEntryStack)e$iv;
                     int v$iv = it.getAmount();
                     if (minValue$iv > v$iv) {
                        minElem$iv = e$iv;
                        minValue$iv = v$iv;
                     }
                  }

                  var28 = minElem$iv;
               }

               if (var28 == null) {
                  Intrinsics.throwNpe();
               }

               PickedEntryStack lowestWorthItem = (PickedEntryStack)var28;
               String var10005 = lowestWorthItem.getContent();
               ItemRange var10006 = lowestWorthItem.getGenericPick().getRange();
               int var29 = var10006 != null ? var10006.getMin() : 1;
               NBTTagCompound var10008 = lowestWorthItem.getTag();
               toRet.add(new PickedEntryStack(new PickedEntry(var10005, var29, 0, var10008 != null ? var10008.toString() : null, (List)null, (ItemRange)null, 52, (DefaultConstructorMarker)null)));
            }

            return toRet;
         }

         int $receiver$iv = Bountiful.INSTANCE.getConfig().getGreedyRewards();
         PickedEntryStack var10000;
         if ($receiver$iv) {
            var10000 = (PickedEntryStack)CollectionsKt.last(validRewards);
         } else {
            if ($receiver$iv) {
               throw new NoWhenBranchMatchedException();
            }

            var10000 = (PickedEntryStack)ExtMiscKt.getWeightedRandom(validRewards);
         }

         PickedEntryStack reward = var10000;
         maxNumCouldGive = worthLeft / reward.getAmount();
         ItemRange minMaxRange = reward.getGenericPick().getRange();
         int var27;
         if (minMaxRange != null) {
            if (!Bountiful.INSTANCE.getConfig().getTryMaxRewardQuantity()) {
               if (minMaxRange.getMin() < minMaxRange.getMax() && minMaxRange.getMin() >= 0) {
                  ItemRange newRange = new ItemRange(minMaxRange.getMin(), minMaxRange.getMin() + world.rand.nextInt(minMaxRange.getMax() - minMaxRange.getMin()));
                  var27 = ExtMiscKt.clampTo($receiver$iv, newRange.toIntRange());
               } else {
                  var27 = ExtMiscKt.clampTo($receiver$iv, minMaxRange.toIntRange());
               }
            } else {
               var27 = ExtMiscKt.clampTo($receiver$iv, minMaxRange.toIntRange());
            }
         } else {
            var27 = $receiver$iv;
         }

         int numCanGive = var27;
         int worthSated = reward.getAmount() * numCanGive;
         worthLeft -= worthSated;
         String var10004 = reward.getContent();
         NBTTagCompound var10007 = reward.getTag();
         PickedEntryStack rewardClone = new PickedEntryStack(new PickedEntry(var10004, numCanGive, 0, var10007 != null ? var10007.toString() : null, reward.getStages(), (ItemRange)null, 36, (DefaultConstructorMarker)null));
         picked.add(rewardClone.getContent());
         toRet.add(rewardClone);
         validRewards = RewardRegistry.INSTANCE.validRewards(world, worthLeft, picked);
      }
   }

   private BountyCreator() {
   }

   static {
      BountyCreator var0 = new BountyCreator();
      INSTANCE = var0;
   }
}
