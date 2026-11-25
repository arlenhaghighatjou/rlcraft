package de.impelon.disenchanter.gui;

import de.impelon.disenchanter.DisenchanterConfig;
import de.impelon.disenchanter.DisenchantingProperties;
import de.impelon.disenchanter.inventory.ContainerDisenchantmentBase;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDisenchantment extends GuiContainer {
   private static final ResourceLocation guiResource = new ResourceLocation("disenchanter", "textures/gui/container/disenchanting_table.png");
   private String tableName;
   private String inventoryName;
   private List<String> descriptions;

   public GuiDisenchantment(InventoryPlayer playerInventory, World world, BlockPos position, String tableName) {
      super(ContainerDisenchantmentBase.create(playerInventory, world, position));
      this.tableName = tableName;
      this.inventoryName = playerInventory.getDisplayName().getUnformattedText();
      this.descriptions = DisenchantingProperties.getPropertiesFromStateAt(world, position).getTableVariantDescriptions();
   }

   public void drawScreen(int x, int y, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(x, y, partialTicks);
      this.renderHoveredToolTip(x, y);
   }

   protected void drawGuiContainerForegroundLayer(int x, int y) {
      if (this.tableName != null) {
         this.fontRenderer.drawString(this.tableName, 8, 5, 4210752);
      }

      if (DisenchanterConfig.visual.showUpgradesInGUI) {
         float offset = 0.0F;

         for(String description : this.descriptions) {
            this.drawString(description, (float)(this.xSize - 8), 5.0F + offset, DisenchanterConfig.visual.descriptionInGUIColor, false, GuiDisenchantment.TextAlignment.RIGHT_ALIGNED);
            offset += (float)(this.fontRenderer.FONT_HEIGHT + 1);
         }
      }

      this.fontRenderer.drawString(this.inventoryName, 8, this.ySize - 94, 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(guiResource);
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
   }

   protected int drawString(String text, float x, float y, int color, boolean dropShadow, GuiDisenchantment.TextAlignment alignment) {
      float offset = 0.0F;
      switch(alignment) {
      case CENTERED:
         offset = (float)(this.fontRenderer.getStringWidth(text) / 2);
      case RIGHT_ALIGNED:
         offset = (float)this.fontRenderer.getStringWidth(text);
      default:
         return this.fontRenderer.drawString(text, x - offset, y, color, dropShadow);
      }
   }

   public static enum TextAlignment {
      CENTERED,
      LEFT_ALIGNED,
      RIGHT_ALIGNED;

      private TextAlignment() {
      }
   }
}
