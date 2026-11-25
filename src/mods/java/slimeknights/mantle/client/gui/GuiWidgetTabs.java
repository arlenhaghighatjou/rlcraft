package slimeknights.mantle.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Mouse;

@SideOnly(Side.CLIENT)
public class GuiWidgetTabs extends GuiWidget {
   private static final ResourceLocation creativeInventoryTabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private final GuiElement[] tabActive = new GuiElement[3];
   private final GuiElement[] tab = new GuiElement[3];
   public ResourceLocation tabsResource;
   public int yOffset;
   public int spacing;
   public int selected;
   public int highlighted;
   protected List<ItemStack> icons;
   private final GuiMultiModule parent;
   private boolean clicked;

   public GuiWidgetTabs(GuiMultiModule parent, GuiElement tabLeft, GuiElement tabCenter, GuiElement tabRight, GuiElement activeLeft, GuiElement activeCenter, GuiElement activeRight) {
      this.tabsResource = creativeInventoryTabs;
      this.yOffset = 4;
      this.spacing = 2;
      this.icons = Lists.newArrayList();
      this.clicked = false;
      this.parent = parent;
      this.tab[0] = tabLeft;
      this.tab[1] = tabCenter;
      this.tab[2] = tabRight;
      this.tabActive[0] = activeLeft;
      this.tabActive[1] = activeCenter;
      this.tabActive[2] = activeRight;
      this.selected = 0;
   }

   public void addTab(ItemStack icon) {
      this.icons.add(icon);
   }

   public void clear() {
      this.selected = 0;
      this.icons.clear();
   }

   public void update(int mouseX, int mouseY) {
      boolean mouseDown = Mouse.isButtonDown(0);
      mouseX = mouseX - this.xPos;
      mouseY = mouseY - this.yPos;
      this.highlighted = -1;
      if (mouseY >= 0 && mouseY <= this.tab[1].h) {
         int x = 0;

         for(int i = 0; i < this.icons.size(); ++i) {
            if (mouseX >= x && mouseX < x + this.tab[1].w) {
               this.highlighted = i;
               break;
            }

            x = x + this.tab[1].w;
            x = x + this.spacing;
         }
      }

      if (this.clicked) {
         if (!mouseDown) {
            this.clicked = false;
         }
      } else if (mouseDown) {
         this.clicked = true;
         if (this.highlighted > -1) {
            this.selected = this.highlighted;
         }

      }
   }

   public void draw() {
      int y = this.yPos + this.yOffset;

      for(int i = 0; i < this.icons.size(); ++i) {
         int x = this.xPos + i * this.tab[0].w;
         if (i > 0) {
            x += i * this.spacing;
         }

         GuiElement[] toDraw;
         if (i == this.selected) {
            toDraw = this.tabActive;
         } else {
            toDraw = this.tab;
         }

         GuiElement actualTab;
         if (i == 0 && x == this.parent.cornerX) {
            actualTab = toDraw[0];
         } else if (x == this.parent.cornerX + this.parent.width) {
            actualTab = toDraw[2];
         } else {
            actualTab = toDraw[1];
         }

         Minecraft.getMinecraft().getTextureManager().bindTexture(this.tabsResource);
         actualTab.draw(x, y);
         ItemStack icon = (ItemStack)this.icons.get(i);
         if (icon != null) {
            RenderHelper.enableGUIStandardItemLighting();
            this.drawItemStack(icon, x + (actualTab.w - 16) / 2, y + (actualTab.h - 16) / 2);
            RenderHelper.disableStandardItemLighting();
         }
      }

   }

   private void drawItemStack(ItemStack stack, int x, int y) {
      RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
      GlStateManager.translate(0.0F, 0.0F, 32.0F);
      itemRender.zLevel = 200.0F;
      itemRender.renderItemAndEffectIntoGUI(stack, x, y);
      itemRender.zLevel = 0.0F;
   }
}
