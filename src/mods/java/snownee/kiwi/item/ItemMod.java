package snownee.kiwi.item;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import snownee.kiwi.KiwiConfig;

public class ItemMod extends Item implements IModItem {
   private final String name;

   public ItemMod(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void register(String modid) {
      this.setRegistryName(modid, this.getName());
      this.setUnlocalizedName(modid + "." + this.getName());
   }

   public Item cast() {
      return this;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      addTip(stack, tooltip);
   }

   @SideOnly(Side.CLIENT)
   public static void addTip(ItemStack stack, List<String> tooltip) {
      if (tooltip.size() > 0 && I18n.hasKey(stack.getUnlocalizedName() + ".tip")) {
         if (KiwiConfig.GENERAL.tooltipRequiresShift && !Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
            if (KiwiConfig.GENERAL.tooltipRequiresShift) {
               tooltip.add(I18n.format("kiwi.tip.press_shift", new Object[0]));
            }
         } else {
            FontRenderer fontRenderer = stack.getItem().getFontRenderer(stack);
            if (fontRenderer == null) {
               fontRenderer = Minecraft.getMinecraft().fontRenderer;
            }

            int width = fontRenderer.getStringWidth((String)tooltip.get(0));
            tooltip.addAll(fontRenderer.listFormattedStringToWidth(I18n.format(stack.getUnlocalizedName() + ".tip", new Object[0]), Math.max(width, KiwiConfig.GENERAL.tooltipWrapWidth)));
         }
      }

   }
}
