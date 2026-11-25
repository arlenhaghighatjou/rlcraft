package noppes.vc.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.VCRecipes;
import noppes.vc.shared.gui.GuiBasic;
import noppes.vc.shared.gui.GuiButtonNextPage;
import noppes.vc.shared.gui.GuiButtonNop;
import noppes.vc.shared.gui.GuiLabel;

@SideOnly(Side.CLIENT)
public class GuiRecipes extends GuiBasic {
   private static final ResourceLocation resource = new ResourceLocation("variedcommodities", "textures/gui/slot.png");
   private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/furnace.png");
   private int page = 0;
   private boolean npcRecipes = true;
   private GuiLabel label;
   private GuiButtonNop left;
   private GuiButtonNop right;
   private List<VCRecipes.RecipeContainer> recipes = new ArrayList();

   public GuiRecipes() {
      this.ySize = 182;
      this.xSize = 256;
      this.setBackground("recipes.png");
      this.closeOnEsc = true;
      this.recipes = VCRecipes.List;
   }

   public void initGui() {
      super.initGui();
      this.addLabel(new GuiLabel(0, "Recipe List", this.guiLeft + 5, this.guiTop + 5));
      this.addLabel(this.label = new GuiLabel(1, "", this.guiLeft + 5, this.guiTop + 168));
      this.addButton(this.left = new GuiButtonNextPage(1, this.guiLeft + 150, this.guiTop + 164, true));
      this.addButton(this.right = new GuiButtonNextPage(2, this.guiLeft + 80, this.guiTop + 164, false));
      this.updateButton();
   }

   private void updateButton() {
      this.right.visible = this.right.enabled = this.page > 0;
      this.left.visible = this.left.enabled = this.page + 1 < MathHelper.ceil((float)this.recipes.size() / 4.0F);
   }

   protected void actionPerformed(GuiButton button) {
      if (button.enabled) {
         if (button == this.right) {
            --this.page;
         }

         if (button == this.left) {
            ++this.page;
         }

         this.updateButton();
      }
   }

   public void drawScreen(int xMouse, int yMouse, float f) {
      super.drawScreen(xMouse, yMouse, f);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.renderEngine.bindTexture(resource);
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();
      GlStateManager.enableLighting();
      this.label.label = this.page + 1 + "/" + MathHelper.ceil((float)this.recipes.size() / 4.0F);
      this.label.x = this.guiLeft + (256 - Minecraft.getMinecraft().fontRenderer.getStringWidth(this.label.label)) / 2;

      for(int i = 0; i < 4; ++i) {
         int index = i + this.page * 4;
         if (index >= this.recipes.size()) {
            break;
         }

         VCRecipes.RecipeContainer recipe = (VCRecipes.RecipeContainer)this.recipes.get(index);
         int x = this.guiLeft + 5 + i / 2 * 126;
         int y = this.guiTop + 15 + i % 2 * 76;
         this.drawItem(recipe.output(), x + 98, y + 28, xMouse, yMouse);
         x = x + (72 - recipe.width * 18) / 2;
         y = y + (72 - recipe.height * 18) / 2;
         if (recipe.isFurnace()) {
            ;
         }

         List<ItemStack> input = recipe.input();

         for(int j = 0; j < input.size(); ++j) {
            ItemStack item = (ItemStack)input.get(j);
            int x2 = x + j % recipe.width * 18;
            int y2 = y + j / recipe.width * 18;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(resource);
            this.drawTexturedModalRect(x2, y2, 0, 0, 18, 18);
            if (item != null && !item.isEmpty()) {
               this.drawItem(item, x2 + 1, y2 + 1, xMouse, yMouse);
            }
         }
      }

      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      RenderHelper.enableStandardItemLighting();

      for(int i = 0; i < 4; ++i) {
         int index = i + this.page * 4;
         if (index >= this.recipes.size()) {
            break;
         }

         VCRecipes.RecipeContainer recipe = (VCRecipes.RecipeContainer)this.recipes.get(index);
         int x = this.guiLeft + 5 + i / 2 * 126;
         int y = this.guiTop + 15 + i % 2 * 76;
         this.drawOverlay(recipe.output(), x + 98, y + 22, xMouse, yMouse);
         x = x + (72 - recipe.width * 18) / 2;
         y = y + (72 - recipe.height * 18) / 2;

         for(int j = 0; j < recipe.width; ++j) {
            for(int k = 0; k < recipe.height; ++k) {
               int l = j + k * recipe.width;
               if (l >= recipe.input().size()) {
                  break;
               }

               ItemStack item = (ItemStack)recipe.input().get(l);
               if (item != null && !item.isEmpty()) {
                  this.drawOverlay(item, x + j * 18 + 1, y + k * 18 + 1, xMouse, yMouse);
               }
            }
         }
      }

   }

   private void drawItem(ItemStack item, int x, int y, int xMouse, int yMouse) {
      if (Item.getItemFromBlock(Blocks.WOOL) != Items.AIR && item.getItemDamage() == 32767) {
         item = item.copy();
         item.setItemDamage(0);
      }

      GlStateManager.pushMatrix();
      this.itemRender.zLevel = 100.0F;
      this.itemRender.renderItemAndEffectIntoGUI(item, x, y);
      this.itemRender.renderItemOverlays(this.fontRenderer, item, x, y);
      this.itemRender.zLevel = 0.0F;
      GlStateManager.popMatrix();
   }

   private void drawOverlay(ItemStack item, int x, int y, int xMouse, int yMouse) {
      if (this.isPointInRegion(x - this.guiLeft, y - this.guiTop, 16, 16, xMouse, yMouse)) {
         this.renderToolTip(item, xMouse, yMouse);
      }

   }

   protected boolean isPointInRegion(int p_146978_1_, int p_146978_2_, int p_146978_3_, int p_146978_4_, int p_146978_5_, int p_146978_6_) {
      int k1 = this.guiLeft;
      int l1 = this.guiTop;
      p_146978_5_ = p_146978_5_ - k1;
      p_146978_6_ = p_146978_6_ - l1;
      return p_146978_5_ >= p_146978_1_ - 1 && p_146978_5_ < p_146978_1_ + p_146978_3_ + 1 && p_146978_6_ >= p_146978_2_ - 1 && p_146978_6_ < p_146978_2_ + p_146978_4_ + 1;
   }

   public void save() {
   }
}
