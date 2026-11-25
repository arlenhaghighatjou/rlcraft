package ejektaflex.bountiful.item;

import ejektaflex.bountiful.Bountiful;
import ejektaflex.bountiful.api.BountifulAPI;
import ejektaflex.bountiful.api.data.IBountyData;
import ejektaflex.bountiful.api.enum.EnumBountyRarity;
import ejektaflex.bountiful.api.ext.ExtMiscKt;
import ejektaflex.bountiful.api.item.IItemBounty;
import ejektaflex.bountiful.api.logic.BountyNBT;
import ejektaflex.bountiful.api.stats.BountifulStats;
import ejektaflex.bountiful.data.BountyData;
import ejektaflex.bountiful.logic.BountyChecker;
import ejektaflex.bountiful.logic.error.BountyCreationException;
import ejektaflex.compat.FacadeGameStages;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u000b\u0018\u00002\u00020\u00012\u00020\u0002B\u0005¢\u0006\u0002\u0010\u0003J0\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0017J \u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\b\b\u0002\u0010\u0015\u001a\u00020\u0010J\"\u0010\u0016\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0016J\u0016\u0010\u0019\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\u001c\u001a\u00020\f2\u0006\u0010\u0006\u001a\u00020\u0007H\u0017J\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J&\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00070 2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010!\u001a\u00020\u00122\u0006\u0010\"\u001a\u00020\u0014H\u0016J2\u0010#\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010$\u001a\u0004\u0018\u00010%2\u0006\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020\u0010H\u0016J \u0010)\u001a\u00020\u00102\u0006\u0010*\u001a\u00020\u00072\u0006\u0010+\u001a\u00020\u00072\u0006\u0010,\u001a\u00020\u0010H\u0016J\u0010\u0010-\u001a\u00020\u00102\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J \u0010.\u001a\u00020\u00102\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010/\u001a\u00020'2\u0006\u00100\u001a\u00020\fH\u0002J\u0010\u00101\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u00062"},
   d2 = {"Lejektaflex/bountiful/item/ItemBounty;", "Lnet/minecraft/item/Item;", "Lejektaflex/bountiful/api/item/IItemBounty;", "()V", "addInformation", "", "stack", "Lnet/minecraft/item/ItemStack;", "worldIn", "Lnet/minecraft/world/World;", "tooltip", "", "", "flagIn", "Lnet/minecraft/client/util/ITooltipFlag;", "cashIn", "", "player", "Lnet/minecraft/entity/player/EntityPlayer;", "hand", "Lnet/minecraft/util/EnumHand;", "atBoard", "ensureBounty", "rarity", "Lejektaflex/bountiful/api/enum/EnumBountyRarity;", "ensureTimerStarted", "getBountyData", "Lejektaflex/bountiful/data/BountyData;", "getItemStackDisplayName", "getRarity", "Lnet/minecraft/item/EnumRarity;", "onItemRightClick", "Lnet/minecraft/util/ActionResult;", "playerIn", "handIn", "onUpdate", "entityIn", "Lnet/minecraft/entity/Entity;", "itemSlot", "", "isSelected", "shouldCauseReequipAnimation", "oldStack", "newStack", "slotChanged", "tickBoardTime", "tickNumber", "amount", "key", "tryExpireBountyTime", "Bountiful"}
)
public final class ItemBounty extends Item implements IItemBounty {
   @SideOnly(Side.CLIENT)
   public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, @NotNull List<String> tooltip, @NotNull ITooltipFlag flagIn) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      Intrinsics.checkParameterIsNotNull(tooltip, "tooltip");
      Intrinsics.checkParameterIsNotNull(flagIn, "flagIn");
      if (stack.hasTagCompound()) {
         BountyData bountyTipInfo = new BountyData();
         NBTTagCompound var10001 = stack.getTagCompound();
         if (var10001 == null) {
            Intrinsics.throwNpe();
         }

         Intrinsics.checkExpressionValueIsNotNull(var10001, "stack.tagCompound!!");
         bountyTipInfo.deserializeNBT(var10001);
         if (worldIn == null) {
            Intrinsics.throwNpe();
         }

         for(String line : bountyTipInfo.tooltipInfo(worldIn, Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54))) {
            tooltip.add(line);
         }
      }

   }

   @NotNull
   public BountyData getBountyData(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      if (stack.hasTagCompound() && stack.getItem() instanceof ItemBounty) {
         BountyData var2 = new BountyData();
         NBTTagCompound var10001 = stack.getTagCompound();
         if (var10001 == null) {
            Intrinsics.throwNpe();
         }

         Intrinsics.checkExpressionValueIsNotNull(var10001, "stack.tagCompound!!");
         var2.deserializeNBT(var10001);
         return var2;
      } else {
         throw (Throwable)(new Exception(stack.getDisplayName() + " is not an ItemBounty or has no NBT data!"));
      }
   }

   @NotNull
   public EnumRarity getRarity(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      EnumRarity var2;
      if (stack.hasTagCompound()) {
         NBTTagCompound var10000 = stack.getTagCompound();
         if (var10000 == null) {
            Intrinsics.throwNpe();
         }

         if (var10000.hasKey(BountyNBT.Rarity.getKey())) {
            EnumBountyRarity.Companion var3 = EnumBountyRarity.Companion;
            NBTTagCompound var10001 = stack.getTagCompound();
            if (var10001 == null) {
               Intrinsics.throwNpe();
            }

            var2 = var3.getRarityFromInt(var10001.getInteger(BountyNBT.Rarity.getKey())).getItemRarity();
            return var2;
         }
      }

      var2 = super.getRarity(stack);
      Intrinsics.checkExpressionValueIsNotNull(var2, "super.getRarity(stack)");
      return var2;
   }

   private final boolean tickNumber(ItemStack stack, int amount, String key) {
      if (stack.hasTagCompound()) {
         NBTTagCompound var10000 = stack.getTagCompound();
         if (var10000 == null) {
            Intrinsics.throwNpe();
         }

         if (var10000.hasKey(key)) {
            var10000 = stack.getTagCompound();
            if (var10000 == null) {
               Intrinsics.throwNpe();
            }

            int time = var10000.getInteger(key);
            if (time > 0) {
               time -= amount;
            }

            if (time < 0) {
               time = 0;
            }

            var10000 = stack.getTagCompound();
            if (var10000 == null) {
               Intrinsics.throwNpe();
            }

            var10000.setInteger(key, time);
            return time <= 0;
         }
      }

      return true;
   }

   public void tryExpireBountyTime(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      if (stack.hasTagCompound()) {
         NBTTagCompound var10000 = stack.getTagCompound();
         if (var10000 == null) {
            Intrinsics.throwNpe();
         }

         if (var10000.hasKey(BountyNBT.BountyTime.getKey())) {
            var10000 = stack.getTagCompound();
            if (var10000 == null) {
               Intrinsics.throwNpe();
            }

            var10000.setInteger(BountyNBT.BountyTime.getKey(), 0);
         }
      }

   }

   public boolean tickBoardTime(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      return this.tickNumber(stack, (int)20L, BountyNBT.BoardStamp.getKey());
   }

   @SideOnly(Side.CLIENT)
   @NotNull
   public String getItemStackDisplayName(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      StringBuilder var10000 = (new StringBuilder()).append(super.getItemStackDisplayName(stack));
      String var4;
      if (stack.hasTagCompound()) {
         NBTTagCompound var10001 = stack.getTagCompound();
         if (var10001 == null) {
            Intrinsics.throwNpe();
         }

         if (var10001.hasKey(BountyNBT.Rarity.getKey())) {
            EnumBountyRarity.Companion var5 = EnumBountyRarity.Companion;
            NBTTagCompound var10002 = stack.getTagCompound();
            if (var10002 == null) {
               Intrinsics.throwNpe();
            }

            EnumBountyRarity rarity = var5.getRarityFromInt(var10002.getInteger(BountyNBT.Rarity.getKey()));
            String localizedRarity = I18n.format("bountiful.rarity." + rarity.name(), new Object[0]);
            var4 = " (" + localizedRarity + ')';
            return var10000.append(var4).toString();
         }
      }

      var4 = "";
      return var10000.append(var4).toString();
   }

   public final void ensureTimerStarted(@NotNull ItemStack stack, @NotNull World worldIn) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      Intrinsics.checkParameterIsNotNull(worldIn, "worldIn");
      if (stack.getItem() instanceof ItemBounty && stack.hasTagCompound()) {
         NBTTagCompound var10000 = stack.getTagCompound();
         if (var10000 == null) {
            Intrinsics.throwNpe();
         }

         if (!var10000.hasKey(BountyNBT.BountyStamp.getKey())) {
            var10000 = stack.getTagCompound();
            if (var10000 == null) {
               Intrinsics.throwNpe();
            }

            var10000.setLong(BountyNBT.BountyStamp.getKey(), worldIn.getTotalWorldTime());
         }
      }

   }

   public void onUpdate(@NotNull ItemStack stack, @NotNull World worldIn, @Nullable Entity entityIn, int itemSlot, boolean isSelected) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      Intrinsics.checkParameterIsNotNull(worldIn, "worldIn");
      if (worldIn.getTotalWorldTime() % 20L == 1L) {
         this.ensureTimerStarted(stack, worldIn);
      }

   }

   public void ensureBounty(@NotNull ItemStack stack, @NotNull World worldIn, @Nullable EnumBountyRarity rarity) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      Intrinsics.checkParameterIsNotNull(worldIn, "worldIn");

      IBountyData var5;
      try {
         var5 = BountifulAPI.INSTANCE.createBountyData(worldIn, rarity);
      } catch (BountyCreationException var10) {
         return;
      }

      if (stack.getItem() instanceof ItemBounty) {
         if (!stack.hasTagCompound() && var5 != null) {
            NBTBase var11 = var5.serializeNBT();
            NBTTagCompound $receiver = (NBTTagCompound)var11;
            $receiver.removeTag(BountyNBT.BountyStamp.getKey());
            $receiver.setLong(BountyNBT.BoardStamp.getKey(), worldIn.getTotalWorldTime());
            stack.setTagCompound((NBTTagCompound)var11);
         }

      } else {
         throw (Throwable)(new Exception(stack.getDisplayName() + " is not an ItemBounty, so you cannot generate bounty data for it!"));
      }
   }

   public final boolean cashIn(@NotNull EntityPlayer player, @NotNull EnumHand hand, boolean atBoard) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      Intrinsics.checkParameterIsNotNull(hand, "hand");
      ItemStack bountyItem = player.getHeldItem(hand);
      if (!bountyItem.hasTagCompound()) {
         ItemStack var14 = player.getHeldItem(hand);
         Intrinsics.checkExpressionValueIsNotNull(var14, "player.getHeldItem(hand)");
         World var10002 = player.world;
         Intrinsics.checkExpressionValueIsNotNull(player.world, "player.world");
         IItemBounty.DefaultImpls.ensureBounty$default(this, var14, var10002, (EnumBountyRarity)null, 4, (Object)null);
         return false;
      } else {
         NonNullList inv = player.inventory.mainInventory;
         List invItemsAffected = new BountyData();
         Intrinsics.checkExpressionValueIsNotNull(bountyItem, "bountyItem");
         NBTTagCompound var10001 = bountyItem.getTagCompound();
         if (var10001 == null) {
            Intrinsics.throwNpe();
         }

         Intrinsics.checkExpressionValueIsNotNull(var10001, "bountyItem.tagCompound!!");
         invItemsAffected.deserializeNBT(var10001);
         BountyData bounty = invItemsAffected;
         if (Bountiful.INSTANCE.getConfig().isRunningGameStages()) {
            invItemsAffected = (Collection)FacadeGameStages.INSTANCE.stagesStillNeededFor(player, invItemsAffected);
            if (!invItemsAffected.isEmpty()) {
               ExtMiscKt.sendTranslation((ICommandSender)player, "bountiful.tooltip.requirements");
               return false;
            }
         }

         World var13 = player.world;
         Intrinsics.checkExpressionValueIsNotNull(player.world, "player.world");
         if (invItemsAffected.hasExpired(var13)) {
            ExtMiscKt.sendTranslation((ICommandSender)player, "bountiful.bounty.expired");
            return false;
         } else {
            BountyChecker var10000 = BountyChecker.INSTANCE;
            Intrinsics.checkExpressionValueIsNotNull(inv, "inv");
            List var11 = var10000.hasItems(player, inv, invItemsAffected);
            if (var11 != null) {
               invItemsAffected = var11;
               boolean entitiesFulfilled = BountyChecker.INSTANCE.hasEntitiesFulfilled(bounty);
               if (!entitiesFulfilled) {
                  ExtMiscKt.sendTranslation((ICommandSender)player, "bountiful.requirements.mobs.needed");
                  return false;
               } else {
                  boolean var12;
                  if (!atBoard && Bountiful.INSTANCE.getConfig().getCashInAtBountyBoard()) {
                     ExtMiscKt.sendTranslation((ICommandSender)player, "bountiful.requirements.met");
                     var12 = false;
                  } else {
                     ExtMiscKt.sendTranslation((ICommandSender)player, "bountiful.bounty.fulfilled");
                     BountyChecker.INSTANCE.takeItems(player, inv, bounty, invItemsAffected);
                     player.setHeldItem(hand, ItemStack.EMPTY);
                     BountyChecker.INSTANCE.rewardItems(player, bounty, bountyItem);
                     EnumBountyRarity bountyRarity = EnumBountyRarity.Companion.getRarityFromInt(bounty.getRarity());
                     player.addStat((StatBase)BountifulStats.INSTANCE.getBountiesCompleted());
                     player.addStat((StatBase)bountyRarity.getStat());
                     player.addExperience(bountyRarity.getXp());
                     var12 = true;
                  }

                  return var12;
               }
            } else {
               return false;
            }
         }
      }
   }

   @NotNull
   public ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, @NotNull EntityPlayer playerIn, @NotNull EnumHand handIn) {
      Intrinsics.checkParameterIsNotNull(worldIn, "worldIn");
      Intrinsics.checkParameterIsNotNull(playerIn, "playerIn");
      Intrinsics.checkParameterIsNotNull(handIn, "handIn");
      if (worldIn.isRemote) {
         ActionResult var4 = super.onItemRightClick(worldIn, playerIn, handIn);
         Intrinsics.checkExpressionValueIsNotNull(var4, "super.onItemRightClick(worldIn, playerIn, handIn)");
         return var4;
      } else {
         this.cashIn(playerIn, handIn, false);
         ActionResult var10000 = super.onItemRightClick(worldIn, playerIn, handIn);
         Intrinsics.checkExpressionValueIsNotNull(var10000, "super.onItemRightClick(worldIn, playerIn, handIn)");
         return var10000;
      }
   }

   public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
      Intrinsics.checkParameterIsNotNull(oldStack, "oldStack");
      Intrinsics.checkParameterIsNotNull(newStack, "newStack");
      return slotChanged;
   }

   public ItemBounty() {
   }
}
