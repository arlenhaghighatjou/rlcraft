package wiresegal.classyhats.container;

import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wiresegal.classyhats.capability.data.IHatContainer;
import wiresegal.classyhats.item.ItemHat;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u0007\u001a\u00020\bH\u0016J\u0012\u0010\t\u001a\u00020\b2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0016J\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000fH\u0016J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\u0010\u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u0014\u001a\u00020\u000fH\u0016J\b\u0010\u0015\u001a\u00020\u000fH\u0016J\b\u0010\u0016\u001a\u00020\u000fH\u0016J\b\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u000fH\u0016J\u0010\u0010\u001a\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\b\u0010\u001b\u001a\u00020\u001cH\u0016J\b\u0010\u001d\u001a\u00020\u001cH\u0016J\u0018\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u001f\u001a\u00020\rH\u0016J\u0010\u0010 \u001a\u00020\u001c2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\b\u0010!\u001a\u00020\bH\u0016J\u0010\u0010\"\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\u0010\u0010#\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0018\u0010$\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u000f2\u0006\u0010%\u001a\u00020\u000fH\u0016J\u0018\u0010&\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u001f\u001a\u00020\rH\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006¨\u0006'"},
   d2 = {"Lwiresegal/classyhats/container/HatInventoryWrapper;", "Lnet/minecraft/inventory/IInventory;", "hats", "Lwiresegal/classyhats/capability/data/IHatContainer;", "(Lwiresegal/classyhats/capability/data/IHatContainer;)V", "getHats", "()Lwiresegal/classyhats/capability/data/IHatContainer;", "clear", "", "closeInventory", "player", "Lnet/minecraft/entity/player/EntityPlayer;", "decrStackSize", "Lnet/minecraft/item/ItemStack;", "index", "", "count", "getDisplayName", "Lnet/minecraft/util/text/TextComponentTranslation;", "getField", "id", "getFieldCount", "getInventoryStackLimit", "getName", "", "getSizeInventory", "getStackInSlot", "hasCustomName", "", "isEmpty", "isItemValidForSlot", "stack", "isUsableByPlayer", "markDirty", "openInventory", "removeStackFromSlot", "setField", "value", "setInventorySlotContents", "classyhats"}
)
public final class HatInventoryWrapper implements IInventory {
   @NotNull
   private final IHatContainer hats;

   public int getField(int id) {
      return 0;
   }

   public boolean hasCustomName() {
      return false;
   }

   public void markDirty() {
   }

   @NotNull
   public ItemStack getStackInSlot(int index) {
      return this.hats.getEquipped();
   }

   @NotNull
   public ItemStack decrStackSize(int index, int count) {
      ItemStack var10000 = ItemStackHelper.getAndSplit(CollectionsKt.mutableListOf(new ItemStack[]{this.hats.getEquipped()}), 0, count);
      Intrinsics.checkExpressionValueIsNotNull(var10000, "ItemStackHelper.getAndSp…hats.equipped), 0, count)");
      return var10000;
   }

   public void clear() {
      IHatContainer var10000 = this.hats;
      ItemStack var10001 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      var10000.setEquipped(var10001);
   }

   public int getSizeInventory() {
      return 1;
   }

   @NotNull
   public String getName() {
      return "classyhats.container.HATS";
   }

   public boolean isEmpty() {
      return this.hats.getEquipped().isEmpty();
   }

   @NotNull
   public TextComponentTranslation getDisplayName() {
      return new TextComponentTranslation(this.getName(), new Object[0]);
   }

   public boolean isItemValidForSlot(int index, @NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      return stack.getItem() instanceof ItemHat;
   }

   public int getInventoryStackLimit() {
      return 1;
   }

   public boolean isUsableByPlayer(@NotNull EntityPlayer player) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      return !player.isDead;
   }

   public void openInventory(@NotNull EntityPlayer player) {
      Intrinsics.checkParameterIsNotNull(player, "player");
   }

   public void setField(int id, int value) {
   }

   public void closeInventory(@Nullable EntityPlayer player) {
   }

   public void setInventorySlotContents(int index, @NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      this.hats.setEquipped(stack);
   }

   @NotNull
   public ItemStack removeStackFromSlot(int index) {
      ItemStack prev = this.hats.getEquipped();
      IHatContainer var10000 = this.hats;
      ItemStack var10001 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      var10000.setEquipped(var10001);
      return prev;
   }

   public int getFieldCount() {
      return 0;
   }

   @NotNull
   public final IHatContainer getHats() {
      return this.hats;
   }

   public HatInventoryWrapper(@NotNull IHatContainer hats) {
      Intrinsics.checkParameterIsNotNull(hats, "hats");
      super();
      this.hats = hats;
   }
}
