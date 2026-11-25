package svenhjol.charm.crafting.tile;

import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.items.ItemStackHandler;
import svenhjol.charm.crafting.block.BlockBookshelfChest;
import svenhjol.charm.crafting.feature.BookshelfChest;
import svenhjol.meson.MesonTileInventory;
import svenhjol.meson.helper.ItemHelper;
import vazkii.quark.api.IDropoffManager;

@Interface(
   iface = "vazkii.quark.api.IDropoffManager",
   modid = "quark"
)
public class TileBookshelfChest extends MesonTileInventory implements IDropoffManager {
   private int inventorySize;
   public static final int SIZE = 9;
   public ItemStackHandler inventory = new ItemStackHandler(9) {
      protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
         boolean allowed = !ItemHelper.getMatchingItemKey(BookshelfChest.validItems, stack).isEmpty();
         return allowed ? 64 : 0;
      }

      protected void onLoad() {
         super.onLoad();
         this.recalculateSize();
      }

      protected void onContentsChanged(int slot) {
         this.recalculateSize();
         TileBookshelfChest.this.markDirty();
         TileBookshelfChest.this.updateBlock();
      }

      protected void recalculateSize() {
         int occupied = 0;

         for(int i = 0; i < this.getSlots(); ++i) {
            if (!this.getStackInSlot(i).isEmpty()) {
               ++occupied;
            }
         }

         TileBookshelfChest.this.inventorySize = occupied;
      }
   };

   public TileBookshelfChest() {
   }

   protected void updateBlock() {
      if (this.world.getBlockState(this.pos).getBlock() == BookshelfChest.bookshelfChest) {
         int slots = this.getNumberOfFilledSlots();
         IBlockState state = this.world.getBlockState(this.pos).withProperty(BlockBookshelfChest.SLOTS, Integer.valueOf(slots));
         this.world.setBlockState(this.pos, state, 2);
      }

   }

   public boolean acceptsDropoff(EntityPlayer player) {
      return true;
   }

   public ItemStackHandler getInventory() {
      return this.inventory;
   }

   public int getInventorySize() {
      return 9;
   }

   public String getModId() {
      return "charm";
   }

   public int getNumberOfFilledSlots() {
      return this.hasLootTable() && this.lootSize > 0 ? this.lootSize : this.inventorySize;
   }

   public int getComparatorOutput() {
      return this.getNumberOfFilledSlots();
   }

   public String getDefaultName() {
      return I18n.translateToLocal("tile.charm:bookshelf_chest.name");
   }
}
