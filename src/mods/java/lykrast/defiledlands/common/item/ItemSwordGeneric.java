package lykrast.defiledlands.common.item;

import java.util.List;
import javax.annotation.Nullable;
import lykrast.defiledlands.common.util.LocUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemSwordGeneric extends ItemSword {
   public ItemSwordGeneric(ToolMaterial material) {
      super(material);
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      String k = this.getUnlocalizedName(stack) + ".tooltip";
      if (I18n.hasKey(k)) {
         tooltip.addAll(LocUtils.getTooltips(TextFormatting.GRAY.toString() + I18n.format(k, new Object[0])));
      }

   }
}
