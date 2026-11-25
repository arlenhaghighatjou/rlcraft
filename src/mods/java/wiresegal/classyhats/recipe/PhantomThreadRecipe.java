package wiresegal.classyhats.recipe;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.kotlin.CommonUtilMethods;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.EntityEquipmentSlot.Type;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wiresegal.classyhats.item.ItemHat;
import wiresegal.classyhats.item.ItemPhantomThread;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u00012\u00020\u0002B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007H\u0016J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\b\u0010\r\u001a\u00020\nH\u0016J\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\n0\u000f2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\b\u0010\u0010\u001a\u00020\u0005H\u0016J\u001a\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0016\u00a8\u0006\u0014"},
   d2 = {"Lwiresegal/classyhats/recipe/PhantomThreadRecipe;", "Lnet/minecraftforge/registries/IForgeRegistryEntry$Impl;", "Lnet/minecraft/item/crafting/IRecipe;", "()V", "canFit", "", "width", "", "height", "getCraftingResult", "Lnet/minecraft/item/ItemStack;", "inv", "Lnet/minecraft/inventory/InventoryCrafting;", "getRecipeOutput", "getRemainingItems", "Lnet/minecraft/util/NonNullList;", "isDynamic", "matches", "worldIn", "Lnet/minecraft/world/World;", "classyhats"}
)
public final class PhantomThreadRecipe extends Impl<IRecipe> implements IRecipe {
   public static final PhantomThreadRecipe INSTANCE;

   @NotNull
   public NonNullList<ItemStack> getRemainingItems(@NotNull InventoryCrafting inv) {
      Intrinsics.checkParameterIsNotNull(inv, "inv");
      NonNullList ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
      int i = 0;
      Intrinsics.checkExpressionValueIsNotNull(ret, "ret");

      for(int var4 = ((Collection)ret).size(); i < var4; ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack.hasTagCompound()) {
            Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
            if (NBTHelper.getBoolean(stack, "classy_hat_invisible", false)) {
               ItemStack var10000 = new ItemStack;
               NBTTagCompound var10002 = NBTHelper.getCompound(stack, "classy_hat_disguise");
               if (var10002 == null) {
                  var10002 = new NBTTagCompound();
               }

               var10000.<init>(var10002);
               ItemStack container = var10000;
               ret.set(i, container);
               continue;
            }
         }

         ret.set(i, ForgeHooks.getContainerItem(inv.getStackInSlot(i)));
      }

      return ret;
   }

   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   @NotNull
   public ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
      Intrinsics.checkParameterIsNotNull(inv, "inv");
      ItemStack var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      ItemStack armor = var10000;
      var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      ItemStack thread = var10000;
      int armorCopy = 0;

      label69:
      for(int phantom = inv.getSizeInventory(); armorCopy < phantom; ++armorCopy) {
         ItemStack stack = inv.getStackInSlot(armorCopy);
         Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
         if (!(stack.getItem() instanceof ItemHat)) {
            for(EntityEquipmentSlot equipType : EntityEquipmentSlot.values()) {
               if (equipType.getSlotType() == Type.ARMOR && stack.getItem().isValidArmor(stack, equipType, (Entity)null)) {
                  if (CommonUtilMethods.isNotEmpty(armor)) {
                     var10000 = ItemStack.EMPTY;
                     Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
                     return var10000;
                  }

                  armor = stack;
                  continue label69;
               }
            }
         }

         if (stack.getItem() instanceof ItemPhantomThread) {
            if (CommonUtilMethods.isNotEmpty(thread)) {
               var10000 = ItemStack.EMPTY;
               Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
               return var10000;
            }

            thread = stack;
         } else if (CommonUtilMethods.isNotEmpty(stack)) {
            var10000 = ItemStack.EMPTY;
            Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
            return var10000;
         }
      }

      ItemStack armorCopy = armor.copy();
      Intrinsics.checkExpressionValueIsNotNull(armorCopy, "armorCopy");
      boolean phantom = NBTHelper.getBoolean(armorCopy, "classy_hat_invisible", false);
      var10000 = new ItemStack;
      NBTTagCompound var10002 = NBTHelper.getCompound(armorCopy, "classy_hat_disguise");
      if (var10002 == null) {
         var10002 = new NBTTagCompound();
      }

      var10000.<init>(var10002);
      boolean camo = CommonUtilMethods.isNotEmpty(var10000);
      if (!camo || CommonUtilMethods.isNotEmpty(thread)) {
         if (phantom) {
            NBTHelper.removeNBTEntry(armorCopy, "classy_hat_invisible");
         } else {
            NBTHelper.setBoolean(armorCopy, "classy_hat_invisible", true);
         }
      }

      if (camo) {
         NBTHelper.removeNBTEntry(armorCopy, "classy_hat_disguise");
      }

      NBTTagCompound tag = armorCopy.getTagCompound();
      if (tag != null && tag.getSize() == 0) {
         armorCopy.setTagCompound((NBTTagCompound)null);
      }

      return armorCopy;
   }

   @NotNull
   public ItemStack getRecipeOutput() {
      ItemStack var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      return var10000;
   }

   public boolean matches(@NotNull InventoryCrafting inv, @Nullable World worldIn) {
      Intrinsics.checkParameterIsNotNull(inv, "inv");
      ItemStack var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      ItemStack armor = var10000;
      var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      ItemStack thread = var10000;
      int camo = 0;

      label61:
      for(int var6 = inv.getSizeInventory(); camo < var6; ++camo) {
         ItemStack stack = inv.getStackInSlot(camo);
         Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
         if (!(stack.getItem() instanceof ItemHat)) {
            for(EntityEquipmentSlot equipType : EntityEquipmentSlot.values()) {
               if (equipType.getSlotType() == Type.ARMOR && stack.getItem().isValidArmor(stack, equipType, (Entity)null)) {
                  if (CommonUtilMethods.isNotEmpty(armor)) {
                     return false;
                  }

                  armor = stack;
                  continue label61;
               }
            }
         }

         if (stack.getItem() instanceof ItemPhantomThread) {
            if (CommonUtilMethods.isNotEmpty(thread)) {
               return false;
            }

            thread = stack;
         } else if (CommonUtilMethods.isNotEmpty(stack)) {
            return false;
         }
      }

      var10000 = new ItemStack;
      NBTTagCompound var10002 = NBTHelper.getCompound(armor, "classy_hat_disguise");
      if (var10002 == null) {
         var10002 = new NBTTagCompound();
      }

      var10000.<init>(var10002);
      camo = CommonUtilMethods.isNotEmpty(var10000);
      return (camo || CommonUtilMethods.isNotEmpty(thread)) && CommonUtilMethods.isNotEmpty(armor);
   }

   public boolean isDynamic() {
      return true;
   }

   private PhantomThreadRecipe() {
   }

   static {
      PhantomThreadRecipe var0 = new PhantomThreadRecipe();
      INSTANCE = var0;
      var0.setRegistryName(new ResourceLocation("classyhats", "phantom_invis"));
   }
}
