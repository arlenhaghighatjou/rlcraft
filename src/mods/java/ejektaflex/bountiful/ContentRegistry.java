package ejektaflex.bountiful;

import ejektaflex.bountiful.block.BlockBountyBoard;
import ejektaflex.bountiful.block.BlockTileEntity;
import ejektaflex.bountiful.item.ItemBounty;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0016\u001a\u00020\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0019H\u0007J\u0016\u0010\u001b\u001a\u00020\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00140\u0019H\u0007R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0007\u00a8\u0006\u001c"},
   d2 = {"Lejektaflex/bountiful/ContentRegistry;", "", "()V", "blocks", "", "Lejektaflex/bountiful/block/BlockBountyBoard;", "getBlocks", "()Ljava/util/List;", "bounty", "Lejektaflex/bountiful/item/ItemBounty;", "getBounty", "()Lejektaflex/bountiful/item/ItemBounty;", "bountyBlock", "getBountyBlock", "()Lejektaflex/bountiful/block/BlockBountyBoard;", "bountyItemBlock", "Lnet/minecraft/item/ItemBlock;", "getBountyItemBlock", "()Lnet/minecraft/item/ItemBlock;", "items", "Lnet/minecraft/item/Item;", "getItems", "registerBlocks", "", "event", "Lnet/minecraftforge/event/RegistryEvent$Register;", "Lnet/minecraft/block/Block;", "registerItems", "Bountiful"}
)
public final class ContentRegistry {
   @NotNull
   private static final ItemBounty bounty;
   @NotNull
   private static final BlockBountyBoard bountyBlock;
   @NotNull
   private static final ItemBlock bountyItemBlock;
   @NotNull
   private static final List<BlockBountyBoard> blocks;
   @NotNull
   private static final List<Item> items;
   public static final ContentRegistry INSTANCE;

   @NotNull
   public final ItemBounty getBounty() {
      return bounty;
   }

   @NotNull
   public final BlockBountyBoard getBountyBlock() {
      return bountyBlock;
   }

   @NotNull
   public final ItemBlock getBountyItemBlock() {
      return bountyItemBlock;
   }

   @NotNull
   public final List<BlockBountyBoard> getBlocks() {
      return blocks;
   }

   @NotNull
   public final List<Item> getItems() {
      return items;
   }

   @SubscribeEvent
   public final void registerBlocks(@NotNull Register<Block> event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      IForgeRegistry var10000 = event.getRegistry();
      Collection $receiver$iv = (Collection)blocks;
      IForgeRegistry var8 = var10000;
      if ($receiver$iv == null) {
         throw new TypeCastException("null cannot be cast to non-null type java.util.Collection<T>");
      } else {
         Object[] var11 = $receiver$iv.toArray(new BlockBountyBoard[0]);
         if (var11 == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
         } else {
            Object[] var9 = var11;
            var8.registerAll((Block[])Arrays.copyOf((Block[])var9, ((Block[])var9).length));

            for(Object element$iv : (Iterable)blocks) {
               BlockBountyBoard it = (BlockBountyBoard)element$iv;
               if (it instanceof BlockTileEntity) {
                  GameRegistry.registerTileEntity(it.getTileEntityClass(), String.valueOf(it.getRegistryName()));
               }
            }

         }
      }
   }

   @SubscribeEvent
   public final void registerItems(@NotNull Register<Item> event) {
      Intrinsics.checkParameterIsNotNull(event, "event");
      IForgeRegistry var10000 = event.getRegistry();
      Collection $receiver$iv = (Collection)items;
      IForgeRegistry var5 = var10000;
      if ($receiver$iv == null) {
         throw new TypeCastException("null cannot be cast to non-null type java.util.Collection<T>");
      } else {
         Object[] var7 = $receiver$iv.toArray(new Item[0]);
         if (var7 == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
         } else {
            Object[] var6 = var7;
            var5.registerAll((Item[])Arrays.copyOf((Item[])var6, ((Item[])var6).length));
         }
      }
   }

   private ContentRegistry() {
   }

   static {
      ContentRegistry var0 = new ContentRegistry();
      INSTANCE = var0;
      ItemBounty var1 = new ItemBounty();
      var1.setRegistryName(new ResourceLocation("bountiful", "bounty"));
      var1.setUnlocalizedName("bountiful.bounty");
      var1.setMaxStackSize(1);
      bounty = var1;
      BlockBountyBoard var4 = new BlockBountyBoard();
      var4.setRegistryName(new ResourceLocation("bountiful", "bountyboard"));
      var4.setUnlocalizedName("bountiful.bountyboardblock");
      bountyBlock = var4;
      ItemBlock var5 = new ItemBlock(bountyBlock);
      var5.setRegistryName(new ResourceLocation("bountiful", "bountyboarditem"));
      var5.setUnlocalizedName("bountiful.bountyboarditem");
      bountyItemBlock = var5;
      blocks = CollectionsKt.listOf(bountyBlock);
      items = CollectionsKt.listOf(new Item[]{bounty, (Item)bountyItemBlock});
   }
}
