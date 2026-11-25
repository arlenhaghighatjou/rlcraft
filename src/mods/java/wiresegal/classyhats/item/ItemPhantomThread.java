package wiresegal.classyhats.item;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.core.common.LibCommonProxy;
import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J0\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\rH\u0017J\u0010\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u0006H\u0016J\u0012\u0010\u0010\u001a\u00020\u00112\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u0016J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u0014\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0005\u001a\u00020\u0006H\u0016\u00a8\u0006\u0018"},
   d2 = {"Lwiresegal/classyhats/item/ItemPhantomThread;", "Lcom/teamwizardry/librarianlib/features/base/item/ItemMod;", "()V", "addInformation", "", "stack", "Lnet/minecraft/item/ItemStack;", "world", "Lnet/minecraft/world/World;", "tooltip", "", "", "flag", "Lnet/minecraft/client/util/ITooltipFlag;", "getContainerItem", "itemStack", "getItemStackLimit", "", "getRarity", "Lnet/minecraft/item/EnumRarity;", "getTranslationKey", "hasContainerItem", "", "hasEffect", "classyhats"}
)
public final class ItemPhantomThread extends ItemMod {
   @NotNull
   public String getUnlocalizedName(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      return "item.classyhats.phantom_thread";
   }

   public int getItemStackLimit(@Nullable ItemStack stack) {
      return 1;
   }

   public boolean hasContainerItem(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      return true;
   }

   @NotNull
   public ItemStack getContainerItem(@NotNull ItemStack itemStack) {
      Intrinsics.checkParameterIsNotNull(itemStack, "itemStack");
      ItemStack var2 = itemStack.copy();
      var2.setCount(1);
      Intrinsics.checkExpressionValueIsNotNull(var2, "itemStack.copy().apply { count = 1 }");
      return var2;
   }

   public boolean hasEffect(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      return true;
   }

   @NotNull
   public EnumRarity getRarity(@NotNull ItemStack stack) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      return EnumRarity.UNCOMMON;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(@NotNull ItemStack stack, @Nullable World world, @NotNull List<String> tooltip, @NotNull ITooltipFlag flag) {
      Intrinsics.checkParameterIsNotNull(stack, "stack");
      Intrinsics.checkParameterIsNotNull(tooltip, "tooltip");
      Intrinsics.checkParameterIsNotNull(flag, "flag");
      String desc = stack.getUnlocalizedName() + ".desc";
      String used = LibrarianLib.INSTANCE.getPROXY().canTranslate(desc) ? desc : desc + '0';
      if (LibrarianLib.INSTANCE.getPROXY().canTranslate(used)) {
         TooltipHelper.addToTooltip(tooltip, used, new Object[0]);
         int i = 0;

         while(true) {
            LibCommonProxy var10000 = LibrarianLib.INSTANCE.getPROXY();
            StringBuilder var10001 = (new StringBuilder()).append(desc);
            ++i;
            if (!var10000.canTranslate(var10001.append(i).toString())) {
               break;
            }

            TooltipHelper.addToTooltip(tooltip, desc + i, new Object[0]);
         }
      }

   }

   public ItemPhantomThread() {
      super("phantom_thread", new String[0]);
   }
}
