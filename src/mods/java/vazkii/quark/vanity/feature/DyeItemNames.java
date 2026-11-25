package vazkii.quark.vanity.feature;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderTooltipEvent.PostText;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.DyeUtils;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.module.Feature;
import vazkii.quark.management.feature.FavoriteItems;

public class DyeItemNames extends Feature {
   private static final String TAG_DYE = "Quark:ItemNameDye";

   public DyeItemNames() {
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void makeTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.getItemStack();
      if (!stack.isEmpty() && stack.hasTagCompound() && ItemNBTHelper.getInt(stack, "Quark:ItemNameDye", -1) != -1) {
         FontRenderer font = Minecraft.getMinecraft().fontRenderer;
         int len = font.getStringWidth(stack.getDisplayName());

         String spaces;
         for(spaces = ""; font.getStringWidth(spaces) < len; spaces = spaces + " ") {
            ;
         }

         event.getToolTip().set(0, spaces);
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void renderTooltip(PostText event) {
      ItemStack stack = event.getStack();
      if (!stack.isEmpty() && stack.hasTagCompound()) {
         int dye = ItemNBTHelper.getInt(stack, "Quark:ItemNameDye", -1);
         if (dye != -1) {
            int rgb = ItemDye.DYE_COLORS[Math.min(15, dye)];
            Color color = new Color(rgb);
            String name = stack.getDisplayName();
            if (FavoriteItems.isItemFavorited(stack)) {
               name = "   " + name;
            }

            if (stack.hasDisplayName()) {
               name = TextFormatting.ITALIC + name;
            }

            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(name, (float)event.getX(), (float)event.getY(), color.getRGB());
         }
      }

   }

   @SubscribeEvent
   public void onAnvilUpdate(AnvilUpdateEvent event) {
      ItemStack left = event.getLeft();
      ItemStack right = event.getRight();
      if (!left.isEmpty() && !right.isEmpty()) {
         int meta = DyeUtils.rawDyeDamageFromStack(right);
         if (meta != -1) {
            ItemStack out = left.copy();
            String name = event.getName();
            if (!name.trim().isEmpty()) {
               out.setStackDisplayName(name.trim());
            }

            ItemNBTHelper.setInt(out, "Quark:ItemNameDye", right.getItemDamage());
            event.setOutput(out);
            event.setMaterialCost(1);
            event.setCost(3);
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
