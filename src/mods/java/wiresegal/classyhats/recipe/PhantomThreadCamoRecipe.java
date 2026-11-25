package wiresegal.classyhats.recipe;

import com.teamwizardry.librarianlib.features.helpers.NBTHelper;
import com.teamwizardry.librarianlib.features.kotlin.CommonUtilMethods;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
import wiresegal.classyhats.item.ItemPhantomThread;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u00012\u00020\u0002B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007H\u0016J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\b\u0010\r\u001a\u00020\nH\u0016J\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\n0\u000f2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\b\u0010\u0010\u001a\u00020\u0005H\u0016J\u001a\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0016\u00a8\u0006\u0014"},
   d2 = {"Lwiresegal/classyhats/recipe/PhantomThreadCamoRecipe;", "Lnet/minecraftforge/registries/IForgeRegistryEntry$Impl;", "Lnet/minecraft/item/crafting/IRecipe;", "()V", "canFit", "", "width", "", "height", "getCraftingResult", "Lnet/minecraft/item/ItemStack;", "inv", "Lnet/minecraft/inventory/InventoryCrafting;", "getRecipeOutput", "getRemainingItems", "Lnet/minecraft/util/NonNullList;", "isDynamic", "matches", "worldIn", "Lnet/minecraft/world/World;", "classyhats"}
)
public final class PhantomThreadCamoRecipe extends Impl<IRecipe> implements IRecipe {
   public static final PhantomThreadCamoRecipe INSTANCE;

   @NotNull
   public NonNullList<ItemStack> getRemainingItems(@NotNull InventoryCrafting inv) {
      Intrinsics.checkParameterIsNotNull(inv, "inv");
      NonNullList ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
      int i = 0;
      Intrinsics.checkExpressionValueIsNotNull(ret, "ret");

      for(int var4 = ((Collection)ret).size(); i < var4; ++i) {
         ItemStack stack;
         boolean var10000;
         label38: {
            stack = inv.getStackInSlot(i);
            if (stack.hasTagCompound()) {
               Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
               if (NBTHelper.getBoolean(stack, "classy_hat_invisible", false)) {
                  var10000 = true;
                  break label38;
               }
            }

            var10000 = false;
         }

         boolean phantom = var10000;
         Object[] $receiver$iv = EntityEquipmentSlot.values();
         Object var9 = $receiver$iv;
         int var10 = $receiver$iv.length;
         int var11 = 0;

         while(true) {
            if (var11 >= var10) {
               var10000 = false;
               break;
            }

            label48: {
               Object element$iv = ((Object[])var9)[var11];
               if (((EntityEquipmentSlot)element$iv).getSlotType() == Type.ARMOR) {
                  Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
                  if (stack.getItem().isValidArmor(stack, (EntityEquipmentSlot)element$iv, (Entity)null)) {
                     var10000 = true;
                     break label48;
                  }
               }

               var10000 = false;
            }

            if (var10000) {
               var10000 = true;
               break;
            }

            ++var11;
         }

         boolean armor = var10000;
         if (phantom) {
            ItemStack var19 = new ItemStack;
            Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
            NBTTagCompound var10002 = NBTHelper.getCompound(stack, "classy_hat_disguise");
            if (var10002 == null) {
               var10002 = new NBTTagCompound();
            }

            var19.<init>(var10002);
            $receiver$iv = var19;
            ret.set(i, $receiver$iv);
         } else if (!armor) {
            ret.set(i, ForgeHooks.getContainerItem(inv.getStackInSlot(i)));
         }
      }

      return ret;
   }

   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   @NotNull
   public ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
      Intrinsics.checkParameterIsNotNull(inv, "inv");
      Set threadedTypes = (Set)(new LinkedHashSet());
      ItemStack var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      Object armor = var10000;
      var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      ItemStack threaded = var10000;
      var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      ItemStack thread = var10000;
      int armorCopy = 0;

      label100:
      for(int var7 = inv.getSizeInventory(); armorCopy < var7; ++armorCopy) {
         ItemStack stack = inv.getStackInSlot(armorCopy);

         for(EntityEquipmentSlot equipType : EntityEquipmentSlot.values()) {
            if (equipType.getSlotType() == Type.ARMOR) {
               Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
               if (stack.getItem().isValidArmor(stack, equipType, (Entity)null)) {
                  boolean phantom = stack.hasTagCompound() && NBTHelper.getBoolean(stack, "classy_hat_invisible", false);
                  if (!phantom) {
                     if (CommonUtilMethods.isNotEmpty((ItemStack)armor)) {
                        var10000 = ItemStack.EMPTY;
                        Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
                        return var10000;
                     }

                     armor = stack;
                     continue label100;
                  }

                  if (CommonUtilMethods.isNotEmpty(threaded)) {
                     var10000 = ItemStack.EMPTY;
                     Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
                     return var10000;
                  }

                  threaded = stack;
                  EntityEquipmentSlot[] $receiver$iv = EntityEquipmentSlot.values();
                  Collection destination$iv$iv = (Collection)(new ArrayList());

                  for(Object element$iv$iv : $receiver$iv) {
                     if (((EntityEquipmentSlot)element$iv$iv).getSlotType() == Type.ARMOR && stack.getItem().isValidArmor(stack, (EntityEquipmentSlot)element$iv$iv, (Entity)null)) {
                        destination$iv$iv.add(element$iv$iv);
                     }
                  }

                  List var26 = (List)destination$iv$iv;
                  threadedTypes.addAll((Collection)var26);
                  continue label100;
               }
            }
         }

         Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
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

      Iterable $receiver$iv = (Iterable)threadedTypes;
      boolean var38;
      if ($receiver$iv instanceof Collection && ((Collection)$receiver$iv).isEmpty()) {
         var38 = true;
      } else {
         Iterator var29 = $receiver$iv.iterator();

         while(true) {
            if (!var29.hasNext()) {
               var38 = true;
               break;
            }

            Object element$iv = var29.next();
            EntityEquipmentSlot it = (EntityEquipmentSlot)element$iv;
            if (((ItemStack)armor).getItem().isValidArmor((ItemStack)armor, it, (Entity)null)) {
               var38 = false;
               break;
            }
         }
      }

      if (var38) {
         ItemStack var39 = ItemStack.EMPTY;
         Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
         return var39;
      } else {
         ItemStack armorCopy = threaded.copy();
         Intrinsics.checkExpressionValueIsNotNull(armorCopy, "armorCopy");
         NBTTagCompound var10002 = ((ItemStack)armor).writeToNBT(new NBTTagCompound());
         Intrinsics.checkExpressionValueIsNotNull(var10002, "armor.writeToNBT(NBTTagCompound())");
         NBTHelper.setCompound(armorCopy, "classy_hat_disguise", var10002);
         return armorCopy;
      }
   }

   @NotNull
   public ItemStack getRecipeOutput() {
      ItemStack var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      return var10000;
   }

   public boolean matches(@NotNull InventoryCrafting inv, @Nullable World worldIn) {
      Intrinsics.checkParameterIsNotNull(inv, "inv");
      Set threadedTypes = (Set)(new LinkedHashSet());
      ItemStack var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      Object armor = var10000;
      var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      ItemStack threaded = var10000;
      var10000 = ItemStack.EMPTY;
      Intrinsics.checkExpressionValueIsNotNull(ItemStack.EMPTY, "ItemStack.EMPTY");
      ItemStack thread = var10000;
      int $receiver$iv = 0;

      label109:
      for(int var8 = inv.getSizeInventory(); $receiver$iv < var8; ++$receiver$iv) {
         ItemStack stack = inv.getStackInSlot($receiver$iv);

         for(EntityEquipmentSlot equipType : EntityEquipmentSlot.values()) {
            if (equipType.getSlotType() == Type.ARMOR) {
               Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
               if (stack.getItem().isValidArmor(stack, equipType, (Entity)null)) {
                  boolean phantom = stack.hasTagCompound() && NBTHelper.getBoolean(stack, "classy_hat_invisible", false);
                  if (!phantom) {
                     if (CommonUtilMethods.isNotEmpty((ItemStack)armor)) {
                        return false;
                     }

                     armor = stack;
                     continue label109;
                  }

                  if (CommonUtilMethods.isNotEmpty(threaded)) {
                     return false;
                  }

                  threaded = stack;
                  EntityEquipmentSlot[] $receiver$iv = EntityEquipmentSlot.values();
                  Collection destination$iv$iv = (Collection)(new ArrayList());

                  for(Object element$iv$iv : $receiver$iv) {
                     if (((EntityEquipmentSlot)element$iv$iv).getSlotType() == Type.ARMOR && stack.getItem().isValidArmor(stack, (EntityEquipmentSlot)element$iv$iv, (Entity)null)) {
                        destination$iv$iv.add(element$iv$iv);
                     }
                  }

                  List var27 = (List)destination$iv$iv;
                  threadedTypes.addAll((Collection)var27);
                  continue label109;
               }
            }
         }

         Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
         if (stack.getItem() instanceof ItemPhantomThread) {
            if (CommonUtilMethods.isNotEmpty(thread)) {
               return false;
            }

            thread = stack;
         } else if (CommonUtilMethods.isNotEmpty(stack)) {
            return false;
         }
      }

      Iterable $receiver$iv = (Iterable)threadedTypes;
      boolean var34;
      if ($receiver$iv instanceof Collection && ((Collection)$receiver$iv).isEmpty()) {
         var34 = true;
      } else {
         Iterator var29 = $receiver$iv.iterator();

         while(true) {
            if (!var29.hasNext()) {
               var34 = true;
               break;
            }

            Object element$iv = var29.next();
            EntityEquipmentSlot it = (EntityEquipmentSlot)element$iv;
            if (((ItemStack)armor).getItem().isValidArmor((ItemStack)armor, it, (Entity)null)) {
               var34 = false;
               break;
            }
         }
      }

      if (var34) {
         return false;
      } else {
         return CommonUtilMethods.isNotEmpty((ItemStack)armor) && CommonUtilMethods.isNotEmpty(threaded);
      }
   }

   public boolean isDynamic() {
      return true;
   }

   private PhantomThreadCamoRecipe() {
   }

   static {
      PhantomThreadCamoRecipe var0 = new PhantomThreadCamoRecipe();
      INSTANCE = var0;
      var0.setRegistryName(new ResourceLocation("classyhats", "phantom_camo"));
   }
}
