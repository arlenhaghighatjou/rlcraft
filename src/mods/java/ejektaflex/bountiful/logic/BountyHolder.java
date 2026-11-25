package ejektaflex.bountiful.logic;

import ejektaflex.bountiful.Bountiful;
import ejektaflex.bountiful.api.block.ITileEntityBountyBoard;
import ejektaflex.bountiful.api.enum.EnumBountyRarity;
import ejektaflex.bountiful.api.events.PopulateBountyBoardEvent;
import ejektaflex.bountiful.api.ext.ExtItemHandlerKt;
import ejektaflex.bountiful.api.logic.IBountyCreator;
import ejektaflex.bountiful.api.logic.IBountyHolder;
import ejektaflex.bountiful.data.BountyData;
import ejektaflex.bountiful.item.ItemBounty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import kotlin.ranges.IntRange;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\b\u0016\u0018\u00002\u00020\u00012\b\u0012\u0004\u0012\u00020\u00030\u0002B\r\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u001a\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016J\u0019\u0010\u000f\u001a\u00020\n2\u000e\u0010\u0010\u001a\n \u0011*\u0004\u0018\u00010\u00030\u0003H\u0096\u0001J\u0011\u0010\u0012\u001a\n \u0011*\u0004\u0018\u00010\u00030\u0003H\u0096\u0001J\u0010\u0010\u0013\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u001a\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u0016"},
   d2 = {"Lejektaflex/bountiful/logic/BountyHolder;", "Lejektaflex/bountiful/api/logic/IBountyHolder;", "Lnet/minecraftforge/common/util/INBTSerializable;", "Lnet/minecraft/nbt/NBTTagCompound;", "handler", "Lnet/minecraftforge/items/ItemStackHandler;", "(Lnet/minecraftforge/items/ItemStackHandler;)V", "getHandler", "()Lnet/minecraftforge/items/ItemStackHandler;", "addSingleBounty", "", "world", "Lnet/minecraft/world/World;", "te", "Lejektaflex/bountiful/api/block/ITileEntityBountyBoard;", "deserializeNBT", "p0", "kotlin.jvm.PlatformType", "serializeNBT", "tickBounties", "update", "", "Bountiful"}
)
public class BountyHolder implements IBountyHolder, INBTSerializable<NBTTagCompound> {
   @NotNull
   private final ItemStackHandler handler;

   public void tickBounties(@NotNull World world) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      List toRemove = (List)(new ArrayList());
      IntRange var10000 = ExtItemHandlerKt.getSlotRange((IItemHandler)this.getHandler());
      int $receiver$iv = var10000.getFirst();
      int var4 = var10000.getLast();
      if ($receiver$iv <= var4) {
         while(true) {
            ItemStack var13 = this.getHandler().getStackInSlot($receiver$iv);
            Intrinsics.checkExpressionValueIsNotNull(var13, "handler.getStackInSlot(slot)");
            ItemStack bounty = var13;
            if (bounty.getItem() instanceof ItemBounty && BountyData.Companion.isValidBounty(bounty)) {
               BountyData it = BountyData.Companion.from(bounty);
               Item var14 = bounty.getItem();
               if (var14 == null) {
                  throw new TypeCastException("null cannot be cast to non-null type ejektaflex.bountiful.item.ItemBounty");
               }

               ItemBounty bountyItem = (ItemBounty)var14;
               if (Bountiful.INSTANCE.getConfig().getShouldCountdownOnBoard()) {
                  bountyItem.ensureTimerStarted(bounty, world);
               }

               if (it.hasExpired(world) || it.boardTimeLeft(world) <= 0L) {
                  toRemove.add(Integer.valueOf($receiver$iv));
               }
            }

            if ($receiver$iv == var4) {
               break;
            }

            ++$receiver$iv;
         }
      }

      for(Object element$iv : (Iterable)toRemove) {
         int it = ((Number)element$iv).intValue();
         this.getHandler().setStackInSlot(it, ItemStack.EMPTY);
      }

   }

   public void addSingleBounty(@NotNull World world, @Nullable ITileEntityBountyBoard te) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      ItemStack newStack = IBountyCreator.DefaultImpls.createStack$default(BountyCreator.INSTANCE, world, (EnumBountyRarity)null, 2, (Object)null);
      PopulateBountyBoardEvent fired = PopulateBountyBoardEvent.Companion.fireEvent(newStack, te);
      if (!fired.isCanceled()) {
         List freeSlots = CollectionsKt.minus((Iterable)CollectionsKt.toList((Iterable)ExtItemHandlerKt.getSlotRange((IItemHandler)this.getHandler())), (Iterable)ExtItemHandlerKt.getFilledSlots((IItemHandlerModifiable)this.getHandler()));
         Collection var6 = (Collection)freeSlots;
         if (!var6.isEmpty()) {
            IItemHandlerModifiable var10000 = (IItemHandlerModifiable)this.getHandler();
            var6 = (Collection)freeSlots;
            IItemHandlerModifiable var7 = var10000;
            Object var8 = CollectionsKt.random(var6, (Random)Random.Default);
            ExtItemHandlerKt.set(var7, ((Number)var8).intValue(), newStack);
            if (te != null) {
               te.sendRedstonePulse();
            }
         }
      }

   }

   public boolean update(@NotNull World world, @Nullable ITileEntityBountyBoard te) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      if (world.getTotalWorldTime() % (long)20 == 3L) {
         this.tickBounties(world);
      }

      if (world.getTotalWorldTime() % Bountiful.INSTANCE.getConfig().getBoardAddFrequency() == 3L) {
         while(ExtItemHandlerKt.getFilledSlots((IItemHandlerModifiable)this.getHandler()).size() >= Bountiful.INSTANCE.getConfig().getMaxBountiesPerBoard()) {
            Collection pos = (Collection)ExtItemHandlerKt.getFilledSlots((IItemHandlerModifiable)this.getHandler());
            if (pos.isEmpty()) {
               break;
            }

            Collection distance = (Collection)ExtItemHandlerKt.getFilledSlots((IItemHandlerModifiable)this.getHandler());
            int slotPicked = ((Number)CollectionsKt.random(distance, (Random)Random.Default)).intValue();
            IItemHandlerModifiable var10000 = (IItemHandlerModifiable)this.getHandler();
            ItemStack var10002 = ItemStack.EMPTY;
            Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
            ExtItemHandlerKt.set(var10000, slotPicked, var10002);
         }

         if (!Bountiful.INSTANCE.getConfig().getBoardRequiresVillagers()) {
            this.addSingleBounty(world, te);
            return true;
         }

         BlockPos pos = te != null ? te.getBoardBlockPos() : null;
         int distance = Bountiful.INSTANCE.getConfig().getBoardVillagerDistance();
         if (pos != null) {
            AxisAlignedBB villageBox = new AxisAlignedBB(pos.add(-distance, -distance, -distance), pos.add(distance, distance, distance));
            double result = (double)world.getEntitiesWithinAABB(EntityVillager.class, villageBox).size();
            double chance = result / (double)Bountiful.INSTANCE.getConfig().getBoardRequiredVillagerMax();
            if (chance > world.rand.nextDouble()) {
               this.addSingleBounty(world, te);
               return true;
            }

            return false;
         }
      }

      return false;
   }

   @NotNull
   public ItemStackHandler getHandler() {
      return this.handler;
   }

   public BountyHolder(@NotNull ItemStackHandler handler) {
      Intrinsics.checkParameterIsNotNull(handler, "handler");
      super();
      this.handler = handler;
   }

   public void deserializeNBT(NBTTagCompound p0) {
      this.handler.deserializeNBT(p0);
   }

   public NBTTagCompound serializeNBT() {
      return this.handler.serializeNBT();
   }
}
