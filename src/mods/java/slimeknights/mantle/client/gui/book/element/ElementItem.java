package slimeknights.mantle.client.gui.book.element;

import java.util.Collection;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.action.StringActionProcessor;

@SideOnly(Side.CLIENT)
public class ElementItem extends SizedBookElement {
   public static final int ITEM_SIZE_HARDCODED = 16;
   public static final int ITEM_SWITCH_TICKS = 90;
   public NonNullList<ItemStack> itemCycle;
   public float scale;
   public String action;
   public List<String> tooltip;
   public int renderTick;
   public int currentItem;

   public ElementItem(int x, int y, float scale, Item item) {
      this(x, y, scale, new ItemStack(item));
   }

   public ElementItem(int x, int y, float scale, Block item) {
      this(x, y, scale, new ItemStack(item));
   }

   public ElementItem(int x, int y, float scale, ItemStack item) {
      this(x, y, scale, item);
   }

   public ElementItem(int x, int y, float scale, Collection<ItemStack> itemCycle) {
      this(x, y, scale, (ItemStack[])itemCycle.toArray(new ItemStack[itemCycle.size()]));
   }

   public ElementItem(int x, int y, float scale, Collection<ItemStack> itemCycle, String action) {
      this(x, y, scale, (ItemStack[])itemCycle.toArray(new ItemStack[itemCycle.size()]), action);
   }

   public ElementItem(int x, int y, float scale, ItemStack... itemCycle) {
      this(x, y, scale, itemCycle, (String)null);
   }

   public ElementItem(int x, int y, float scale, ItemStack[] itemCycle, String action) {
      super(x, y, MathHelper.floor(16.0F * scale), MathHelper.floor(16.0F * scale));
      this.renderTick = 0;
      this.currentItem = 0;
      NonNullList<ItemStack> nonNullStacks = NonNullList.withSize(itemCycle.length, ItemStack.EMPTY);

      for(int i = 0; i < itemCycle.length; ++i) {
         if (!itemCycle[i].isEmpty()) {
            nonNullStacks.set(i, itemCycle[i].copy());
         }
      }

      this.itemCycle = nonNullStacks;
      this.scale = scale;
      this.action = action;
   }

   public void draw(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
      ++this.renderTick;
      if (this.renderTick > 90) {
         this.renderTick = 0;
         ++this.currentItem;
         if (this.currentItem >= this.itemCycle.size()) {
            this.currentItem = 0;
         }
      }

      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)this.x, (float)this.y, 0.0F);
      GlStateManager.scale(this.scale, this.scale, 1.0F);
      if (this.currentItem < this.itemCycle.size()) {
         this.mc.getRenderItem().renderItemAndEffectIntoGUI((ItemStack)this.itemCycle.get(this.currentItem), 0, 0);
      }

      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
   }

   public void drawOverlay(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
      if (this.isHovered(mouseX, mouseY) && this.currentItem < this.itemCycle.size()) {
         if (this.tooltip != null) {
            this.drawHoveringText(this.tooltip, mouseX, mouseY, fontRenderer);
         } else {
            this.renderToolTip(fontRenderer, (ItemStack)this.itemCycle.get(this.currentItem), mouseX, mouseY);
         }
      }

   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0 && this.isHovered(mouseX, mouseY) && this.currentItem < this.itemCycle.size()) {
         if (this.action != null) {
            StringActionProcessor.process(this.action, this.parent);
         } else {
            this.parent.itemClicked((ItemStack)this.itemCycle.get(this.currentItem));
         }
      }

   }
}
