package betterquesting.api2.client.gui.panels.content;

import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.client.gui.controls.PanelButtonStorage;
import betterquesting.api2.client.gui.misc.GuiPadding;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.textures.ColorTexture;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.resources.textures.ItemTexture;
import betterquesting.api2.client.gui.resources.textures.LayeredTexture;
import betterquesting.api2.client.gui.resources.textures.OreDictTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class PanelItemSlot extends PanelButtonStorage<BigItemStack> {
   private final boolean showCount;
   private final boolean oreDict;
   private final List<BigItemStack> oreVariants;

   public PanelItemSlot(IGuiRect rect, int id, BigItemStack value) {
      this(rect, id, value, false, false);
   }

   public PanelItemSlot(IGuiRect rect, int id, BigItemStack value, boolean showCount) {
      this(rect, id, value, showCount, false);
   }

   public PanelItemSlot(IGuiRect rect, int id, BigItemStack value, boolean showCount, boolean oreDict) {
      super(rect, id, "", value);
      this.oreVariants = new ArrayList();
      this.showCount = showCount;
      this.oreDict = oreDict;
      this.setTextures(PresetTexture.ITEM_FRAME.getTexture(), PresetTexture.ITEM_FRAME.getTexture(), new LayeredTexture(new IGuiTexture[]{PresetTexture.ITEM_FRAME.getTexture(), new ColorTexture(PresetColor.ITEM_HIGHLIGHT.getColor(), new GuiPadding(1, 1, 1, 1))}));
      this.setStoredValue(value);
   }

   public PanelItemSlot setStoredValue(BigItemStack value) {
      super.setStoredValue(value);
      if (value != null) {
         Minecraft mc = Minecraft.getMinecraft();
         this.setIcon((IGuiTexture)(!this.oreDict && value.getBaseStack().getItemDamage() != 32767 ? new ItemTexture(value, this.showCount, true) : new OreDictTexture(1.0F, value, this.showCount, true)), 1);
         this.setTooltip(value.getBaseStack().getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL));
      } else {
         this.setIcon((IGuiTexture)null);
         this.setTooltip((List)null);
      }

      this.updateOreStacks();
      return this;
   }

   public List<String> getTooltip(int mx, int my) {
      if (this.getStoredValue() != null && this.getTransform().contains(mx, my)) {
         BigItemStack ttStack = (BigItemStack)this.getStoredValue();
         if (this.oreDict && this.oreVariants.size() > 0) {
            ttStack = (BigItemStack)this.oreVariants.get((int)((double)System.currentTimeMillis() / 1000.0D) % this.oreVariants.size());
         }

         Minecraft mc = Minecraft.getMinecraft();
         return ttStack.getBaseStack().getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL);
      } else {
         return null;
      }
   }

   private void updateOreStacks() {
      if (this.oreVariants != null) {
         this.oreVariants.clear();
         BigItemStack stack = (BigItemStack)this.getStoredValue();
         if (stack != null) {
            if (!stack.hasOreDict()) {
               if (stack.getBaseStack().getItemDamage() == 32767) {
                  NonNullList<ItemStack> subItems = NonNullList.create();
                  stack.getBaseStack().getItem().getSubItems(CreativeTabs.SEARCH, subItems);
                  subItems.forEach((is) -> {
                     BigItemStack bis = new BigItemStack(is);
                     bis.stackSize = stack.stackSize;
                     this.oreVariants.add(bis);
                  });
               } else {
                  this.oreVariants.add(stack);
               }

            } else {
               for(ItemStack iStack : stack.getOreIngredient().getMatchingStacks()) {
                  if (iStack.getItemDamage() == 32767) {
                     NonNullList<ItemStack> subItems = NonNullList.create();
                     iStack.getItem().getSubItems(CreativeTabs.SEARCH, subItems);

                     for(ItemStack sStack : subItems) {
                        BigItemStack bStack = new BigItemStack(sStack);
                        bStack.stackSize = stack.stackSize;
                        this.oreVariants.add(bStack);
                     }
                  } else {
                     BigItemStack bStack = new BigItemStack(iStack);
                     bStack.stackSize = stack.stackSize;
                     this.oreVariants.add(bStack);
                  }
               }

            }
         }
      }
   }
}
