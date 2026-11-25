package ejektaflex.bountiful;

import ejektaflex.bountiful.api.IBountifulAPI;
import ejektaflex.bountiful.api.block.ITileEntityBountyBoard;
import ejektaflex.bountiful.api.config.IBountifulConfig;
import ejektaflex.bountiful.api.data.IBountyData;
import ejektaflex.bountiful.api.enum.EnumBountyRarity;
import ejektaflex.bountiful.api.ext.ExtItemHandlerKt;
import ejektaflex.bountiful.api.logic.IBountyHolder;
import ejektaflex.bountiful.block.TileEntityBountyBoard;
import ejektaflex.bountiful.data.BountyData;
import ejektaflex.bountiful.logic.BountyCreator;
import ejektaflex.bountiful.registry.BountyRegistry;
import ejektaflex.bountiful.registry.RewardRegistry;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0016J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J \u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\u0016\u0018\u00010\u001a2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u001cH\u0016J\u001a\u0010\u001d\u001a\u0004\u0018\u00010\u001e2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u001cH\u0016J\u0010\u0010\u001f\u001a\u00020\u00182\u0006\u0010 \u001a\u00020\u0016H\u0016R\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\b8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0014\u0010\u000b\u001a\u00020\fX\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006!"},
   d2 = {"Lejektaflex/bountiful/InternalAPI;", "Lejektaflex/bountiful/api/IBountifulAPI;", "()V", "bountyRegistry", "Lejektaflex/bountiful/registry/BountyRegistry;", "getBountyRegistry", "()Lejektaflex/bountiful/registry/BountyRegistry;", "config", "Lejektaflex/bountiful/api/config/IBountifulConfig;", "getConfig", "()Lejektaflex/bountiful/api/config/IBountifulConfig;", "rewardRegistry", "Lejektaflex/bountiful/registry/RewardRegistry;", "getRewardRegistry", "()Lejektaflex/bountiful/registry/RewardRegistry;", "createBountyData", "Lejektaflex/bountiful/data/BountyData;", "worldIn", "Lnet/minecraft/world/World;", "rarity", "Lejektaflex/bountiful/api/enum/EnumBountyRarity;", "dataToStack", "Lnet/minecraft/item/ItemStack;", "data", "Lejektaflex/bountiful/api/data/IBountyData;", "getBountiesAt", "", "pos", "Lnet/minecraft/util/math/BlockPos;", "getBountyBoard", "Lejektaflex/bountiful/api/block/ITileEntityBountyBoard;", "toBountyData", "stack", "Bountiful"}
)
public final class InternalAPI implements IBountifulAPI {
   @NotNull
   private static final BountyRegistry bountyRegistry = BountyRegistry.INSTANCE;
   @NotNull
   private static final RewardRegistry rewardRegistry = RewardRegistry.INSTANCE;
   public static final InternalAPI INSTANCE;

   @NotNull
   public BountyRegistry getBountyRegistry() {
      return bountyRegistry;
   }

   @NotNull
   public RewardRegistry getRewardRegistry() {
      return rewardRegistry;
   }

   @Nullable
   public List<ItemStack> getBountiesAt(@NotNull World worldIn, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(worldIn, "worldIn");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      ITileEntityBountyBoard var10000 = this.getBountyBoard(worldIn, pos);
      List var5;
      if (var10000 != null) {
         IBountyHolder var3 = var10000.getInventory();
         if (var3 != null) {
            ItemStackHandler var4 = var3.getHandler();
            if (var4 != null) {
               var5 = ExtItemHandlerKt.getStacks((IItemHandler)var4);
               return var5;
            }
         }
      }

      var5 = null;
      return var5;
   }

   @Nullable
   public ITileEntityBountyBoard getBountyBoard(@NotNull World worldIn, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(worldIn, "worldIn");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      TileEntity var10000 = worldIn.getTileEntity(pos);
      if (!(var10000 instanceof TileEntityBountyBoard)) {
         var10000 = null;
      }

      return (TileEntityBountyBoard)var10000;
   }

   @NotNull
   public IBountyData toBountyData(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      return BountyData.Companion.from(stack);
   }

   @NotNull
   public ItemStack dataToStack(@NotNull IBountyData data) {
      Intrinsics.checkParameterIsNotNull(data, "data");
      ItemStack var2 = new ItemStack(ContentRegistry.INSTANCE.getBounty());
      var2.setTagCompound((NBTTagCompound)data.serializeNBT());
      return var2;
   }

   @NotNull
   public IBountifulConfig getConfig() {
      return Bountiful.INSTANCE.getConfig();
   }

   @Nullable
   public BountyData createBountyData(@NotNull World worldIn, @Nullable EnumBountyRarity rarity) {
      Intrinsics.checkParameterIsNotNull(worldIn, "worldIn");
      return BountyCreator.INSTANCE.create(worldIn, rarity);
   }

   private InternalAPI() {
   }

   static {
      InternalAPI var0 = new InternalAPI();
      INSTANCE = var0;
   }
}
