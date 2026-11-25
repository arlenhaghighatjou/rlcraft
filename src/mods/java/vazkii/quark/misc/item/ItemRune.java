package vazkii.quark.misc.item;

import java.awt.Color;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.api.ICustomEnchantColor;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.misc.feature.ColorRunes;

public class ItemRune extends ItemMod implements IItemColorProvider, ICustomEnchantColor, IQuarkItem {
   private static final String[] VARIANTS = new String[]{"rune_white", "rune_orange", "rune_magenta", "rune_light_blue", "rune_yellow", "rune_lime", "rune_pink", "rune_gray", "rune_silver", "rune_cyan", "rune_purple", "rune_blue", "rune_brown", "rune_green", "rune_red", "rune_black", "rune_rainbow"};

   public ItemRune(boolean stack) {
      super("rune", VARIANTS);
      if (!stack) {
         this.setMaxStackSize(1);
      }

      this.setCreativeTab(CreativeTabs.MISC);
   }

   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   public String getUniqueModel() {
      return "rune";
   }

   public int getEnchantEffectColor(ItemStack stack) {
      return getColor(stack.getItemDamage());
   }

   public static int getColor(int meta) {
      return meta > 15 ? Color.HSBtoRGB(ClientTicker.total * 0.005F, 1.0F, 0.6F) : ItemDye.DYE_COLORS[15 - meta];
   }

   @SideOnly(Side.CLIENT)
   public IItemColor getItemColor() {
      return (stack, tintIndex) -> {
         return tintIndex == 1 ? ColorRunes.getColorFromStack(stack) : 16777215;
      };
   }
}
