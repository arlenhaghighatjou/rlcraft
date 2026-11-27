package knightminer.inspirations.library.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraftforge.registries.IRegistryDelegate;

public final class ReflectionUtil {
   private ReflectionUtil() {
   }

   @Nullable
   public static PotionType getMixPredicateInput(@Nonnull Object mixPredicate) {
      Object val = ((PotionHelper.MixPredicate)mixPredicate).input;
      return val instanceof IRegistryDelegate ? (PotionType)((IRegistryDelegate)val).get() : (PotionType)val;
   }

   @Nullable
   public static Ingredient getMixPredicateReagent(@Nonnull Object mixPredicate) {
      return ((PotionHelper.MixPredicate)mixPredicate).reagent;
   }

   @Nullable
   public static PotionType getMixPredicateOutput(@Nonnull Object mixPredicate) {
      Object val = ((PotionHelper.MixPredicate)mixPredicate).output;
      return val instanceof IRegistryDelegate ? (PotionType)((IRegistryDelegate)val).get() : (PotionType)val;
   }

   @Nullable
   public static ItemStack invokeGetSilkTouchDrop(@Nonnull Block block, IBlockState state) {
      return block.callGetSilkTouchDrop(state);
   }
}
