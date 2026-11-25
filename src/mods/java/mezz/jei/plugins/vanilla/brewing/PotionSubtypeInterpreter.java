package mezz.jei.plugins.vanilla.brewing;

import mezz.jei.api.ISubtypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;

public class PotionSubtypeInterpreter implements ISubtypeRegistry.ISubtypeInterpreter {
   public static final PotionSubtypeInterpreter INSTANCE = new PotionSubtypeInterpreter();

   private PotionSubtypeInterpreter() {
   }

   public String apply(ItemStack itemStack) {
      if (!itemStack.hasTagCompound()) {
         return "";
      } else {
         PotionType potionType = PotionUtils.getPotionFromItem(itemStack);
         String potionTypeString = potionType.getNamePrefixed("");
         StringBuilder stringBuilder = new StringBuilder(potionTypeString);

         for(PotionEffect effect : PotionUtils.getEffectsFromStack(itemStack)) {
            stringBuilder.append(";").append(effect);
         }

         return stringBuilder.toString();
      }
   }
}
