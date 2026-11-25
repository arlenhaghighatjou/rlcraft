package ejektaflex.bountiful.block;

import ejektaflex.bountiful.Bountiful;
import ejektaflex.bountiful.api.BountifulAPI;
import ejektaflex.bountiful.api.ext.ExtItemHandlerKt;
import ejektaflex.bountiful.api.ext.ExtMiscKt;
import ejektaflex.bountiful.gui.GuiHandler;
import ejektaflex.bountiful.item.ItemBounty;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0003J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0018\u0010\f\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u0016J \u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J(\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0010\u0010\u001a\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\\\u0010\u001b\u001a\u00020\t2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\u0010\u0013\u001a\u0004\u0018\u00010\u00142\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\b\u0010\u0018\u001a\u0004\u0018\u00010\u00192\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020!2\u0006\u0010#\u001a\u00020!H\u0016J \u0010$\u001a\u00020%2\u0006\u0010\u0012\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0010\u0010&\u001a\u00020\u00102\u0006\u0010'\u001a\u00020(H\u0016J\u0018\u0010)\u001a\u00020\u00102\u0006\u0010*\u001a\u00020\u00102\u0006\u0010'\u001a\u00020(H\u0016J(\u0010+\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u00172\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u0019H\u0016R\u001a\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00020\u00058VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006,"},
   d2 = {"Lejektaflex/bountiful/block/BlockBountyBoard;", "Lejektaflex/bountiful/block/BlockTileEntity;", "Lejektaflex/bountiful/block/TileEntityBountyBoard;", "()V", "tileEntityClass", "Ljava/lang/Class;", "getTileEntityClass", "()Ljava/lang/Class;", "canProvidePower", "", "state", "Lnet/minecraft/block/state/IBlockState;", "createTileEntity", "world", "Lnet/minecraft/world/World;", "getComparatorInputOverride", "", "blockState", "worldIn", "pos", "Lnet/minecraft/util/math/BlockPos;", "getWeakPower", "blockAccess", "Lnet/minecraft/world/IBlockAccess;", "side", "Lnet/minecraft/util/EnumFacing;", "hasComparatorInputOverride", "onBlockActivated", "player", "Lnet/minecraft/entity/player/EntityPlayer;", "hand", "Lnet/minecraft/util/EnumHand;", "hitX", "", "hitY", "hitZ", "onBlockAdded", "", "quantityDropped", "random", "Ljava/util/EaglercraftRandom;", "quantityDroppedWithBonus", "fortune", "shouldCheckWeakPower", "Bountiful"}
)
public final class BlockBountyBoard extends BlockTileEntity<TileEntityBountyBoard> {
   @NotNull
   public Class<TileEntityBountyBoard> getTileEntityClass() {
      return TileEntityBountyBoard.class;
   }

   public boolean onBlockActivated(@Nullable World world, @Nullable BlockPos pos, @Nullable IBlockState state, @Nullable EntityPlayer player, @Nullable EnumHand hand, @Nullable EnumFacing side, float hitX, float hitY, float hitZ) {
      if (world == null) {
         Intrinsics.throwNpe();
      }

      if (!world.isRemote && pos != null) {
         ItemStack var10000;
         if (player != null) {
            if (hand == null) {
               Intrinsics.throwNpe();
            }

            var10000 = player.getHeldItem(hand);
         } else {
            var10000 = null;
         }

         ItemStack holding = var10000;
         TileEntity var12 = world.getTileEntity(pos);
         if (var12 == null) {
            Intrinsics.throwNpe();
         }

         if (var12 == null) {
            throw new TypeCastException("null cannot be cast to non-null type ejektaflex.bountiful.block.TileEntityBountyBoard");
         }

         TileEntityBountyBoard tile = (TileEntityBountyBoard)var12;
         if (player == null) {
            Intrinsics.throwNpe();
         }

         if (!player.isSneaking()) {
            if (Bountiful.INSTANCE.getConfig().getCashInAtBountyBoard() && (holding != null ? holding.getItem() : null) instanceof ItemBounty) {
               Item var13 = holding.getItem();
               if (var13 == null) {
                  throw new TypeCastException("null cannot be cast to non-null type ejektaflex.bountiful.item.ItemBounty");
               }

               ItemBounty var14 = (ItemBounty)var13;
               if (hand == null) {
                  Intrinsics.throwNpe();
               }

               var14.cashIn(player, hand, true);
            } else if (ExtItemHandlerKt.getFilledSlots((IItemHandlerModifiable)tile.getInventory().getHandler()).isEmpty()) {
               ExtMiscKt.sendTranslation((ICommandSender)player, "bountiful.board.empty");
            } else {
               Bountiful var10001 = Bountiful.INSTANCE.getInstance();
               if (var10001 == null) {
                  Intrinsics.throwNpe();
               }

               player.openGui(var10001, GuiHandler.Companion.getBOARD_GUI(), world, pos.getX(), pos.getY(), pos.getZ());
            }
         }
      }

      return true;
   }

   public int getWeakPower(@NotNull IBlockState blockState, @NotNull IBlockAccess blockAccess, @NotNull BlockPos pos, @NotNull EnumFacing side) {
      Intrinsics.checkParameterIsNotNull(blockState, "blockState");
      Intrinsics.checkParameterIsNotNull(blockAccess, "blockAccess");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      Intrinsics.checkParameterIsNotNull(side, "side");
      int var6;
      if (blockAccess instanceof World) {
         TileEntity var10000 = this.getTileEntity(blockAccess, pos);
         if (var10000 == null) {
            throw new TypeCastException("null cannot be cast to non-null type ejektaflex.bountiful.block.TileEntityBountyBoard");
         }

         TileEntityBountyBoard tile = (TileEntityBountyBoard)var10000;
         var6 = tile.getPulseLeft() > 0 ? 15 : 0;
      } else {
         var6 = 0;
      }

      return var6;
   }

   public boolean shouldCheckWeakPower(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos, @NotNull EnumFacing side) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      Intrinsics.checkParameterIsNotNull(world, "world");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      Intrinsics.checkParameterIsNotNull(side, "side");
      return false;
   }

   public boolean canProvidePower(@NotNull IBlockState state) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      return true;
   }

   public boolean hasComparatorInputOverride(@NotNull IBlockState state) {
      Intrinsics.checkParameterIsNotNull(state, "state");
      return true;
   }

   public int quantityDropped(@NotNull EaglercraftRandom random) {
      Intrinsics.checkParameterIsNotNull(random, "random");
      return BountifulAPI.INSTANCE.getConfig().getBoardDrops() ? super.quantityDropped(random) : 0;
   }

   public int quantityDroppedWithBonus(int fortune, @NotNull EaglercraftRandom random) {
      Intrinsics.checkParameterIsNotNull(random, "random");
      return BountifulAPI.INSTANCE.getConfig().getBoardDrops() ? super.quantityDroppedWithBonus(fortune, random) : 0;
   }

   public int getComparatorInputOverride(@NotNull IBlockState blockState, @NotNull World worldIn, @NotNull BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(blockState, "blockState");
      Intrinsics.checkParameterIsNotNull(worldIn, "worldIn");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      TileEntity var10000 = this.getTileEntity((IBlockAccess)worldIn, pos);
      if (var10000 == null) {
         throw new TypeCastException("null cannot be cast to non-null type ejektaflex.bountiful.block.TileEntityBountyBoard");
      } else {
         TileEntityBountyBoard tile = (TileEntityBountyBoard)var10000;
         return ExtItemHandlerKt.getFilledSlots((IItemHandlerModifiable)tile.getInventory().getHandler()).size() / 2;
      }
   }

   public void onBlockAdded(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
      Intrinsics.checkParameterIsNotNull(worldIn, "worldIn");
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      Intrinsics.checkParameterIsNotNull(state, "state");
      TileEntity var10000 = this.getTileEntity((IBlockAccess)worldIn, pos);
      if (var10000 == null) {
         throw new TypeCastException("null cannot be cast to non-null type ejektaflex.bountiful.block.TileEntityBountyBoard");
      } else {
         TileEntityBountyBoard tile = (TileEntityBountyBoard)var10000;
         int i = 0;

         for(int var6 = Bountiful.INSTANCE.getConfig().getBountiesCreatedOnPlace(); i < var6; ++i) {
            tile.getInventory().addSingleBounty(worldIn, tile);
            tile.markDirty();
         }

      }
   }

   @NotNull
   public TileEntityBountyBoard createTileEntity(@NotNull World world, @NotNull IBlockState state) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      Intrinsics.checkParameterIsNotNull(state, "state");
      return new TileEntityBountyBoard();
   }

   public BlockBountyBoard() {
      Material var10001 = Material.WOOD;
      Intrinsics.checkExpressionValueIsNotNull(Material.WOOD, "Material.WOOD");
      super(var10001, "bountyboard");
      float hardToSet = Bountiful.INSTANCE.getConfig().getBountyBoardBreakable() ? 2.0F : -1.0F;
      this.setHardness(hardToSet);
      float resistToSet = Bountiful.INSTANCE.getConfig().getBountyBoardBreakable() ? 0.0F : 6000000.0F;
      this.setResistance(resistToSet);
   }
}
