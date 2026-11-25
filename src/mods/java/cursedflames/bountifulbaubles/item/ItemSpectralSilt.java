package cursedflames.bountifulbaubles.item;

import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.item.base.GenericItemBB;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.api.ICustomEnchantColor;

public class ItemSpectralSilt extends GenericItemBB implements ICustomEnchantColor {
   public ItemSpectralSilt() {
      super("spectralSilt", BountifulBaubles.TAB);
   }

   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public int getEnchantEffectColor(ItemStack stack) {
      long time = Minecraft.getMinecraft().world.getTotalWorldTime();
      return Color.HSBtoRGB((float)(time % 360L) / 180.0F, 1.0F, 0.75F);
   }

   public boolean shouldTruncateColorBrightness(ItemStack stack) {
      return false;
   }
}
