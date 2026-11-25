package mezz.jei.plugins.vanilla.ingredients.item;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.Log;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IRarity;

public class ItemStackRenderer implements IIngredientRenderer<ItemStack> {
   public ItemStackRenderer() {
   }

   public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable ItemStack ingredient) {
      if (ingredient != null) {
         GlStateManager.enableDepth();
         RenderHelper.enableGUIStandardItemLighting();
         FontRenderer font = this.getFontRenderer(minecraft, ingredient);
         minecraft.getRenderItem().renderItemAndEffectIntoGUI((EntityLivingBase)null, ingredient, xPosition, yPosition);
         minecraft.getRenderItem().renderItemOverlayIntoGUI(font, ingredient, xPosition, yPosition, (String)null);
         GlStateManager.disableBlend();
         RenderHelper.disableStandardItemLighting();
      }

   }

   public List<String> getTooltip(Minecraft minecraft, ItemStack ingredient, ITooltipFlag tooltipFlag) {
      EntityPlayer player = minecraft.player;

      List<String> list;
      try {
         list = ingredient.getTooltip(player, tooltipFlag);
      } catch (LinkageError | RuntimeException var10) {
         String itemStackInfo = ErrorUtil.getItemStackInfo(ingredient);
         Log.get().error("Failed to get tooltip: {}", itemStackInfo, var10);
         list = new ArrayList();
         list.add(TextFormatting.RED + Translator.translateToLocal("jei.tooltip.error.crash"));
         return list;
      }

      IRarity rarity;
      try {
         rarity = ingredient.getItem().getForgeRarity(ingredient);
      } catch (LinkageError | RuntimeException var9) {
         String itemStackInfo = ErrorUtil.getItemStackInfo(ingredient);
         Log.get().error("Failed to get rarity: {}", itemStackInfo, var9);
         rarity = EnumRarity.COMMON;
      }

      for(int k = 0; k < list.size(); ++k) {
         if (k == 0) {
            list.set(k, rarity.getColor() + (String)list.get(k));
         } else {
            list.set(k, TextFormatting.GRAY + (String)list.get(k));
         }
      }

      return list;
   }

   public FontRenderer getFontRenderer(Minecraft minecraft, ItemStack ingredient) {
      FontRenderer fontRenderer = ingredient.getItem().getFontRenderer(ingredient);
      if (fontRenderer == null) {
         fontRenderer = minecraft.fontRenderer;
      }

      return fontRenderer;
   }
}
