package vazkii.quark.world.client.gui;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiArchaeologist extends GuiMerchant {
   private int mouseX;
   private int mouseY;

   public GuiArchaeologist(InventoryPlayer player, IMerchant merchant, World world) {
      super(player, merchant, world);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.mouseX = mouseX;
      this.mouseY = mouseY;
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void drawHoveringText(List<String> textLines, int x, int y, @Nonnull FontRenderer font) {
      if (textLines.size() == 1 && x == this.mouseX && y == this.mouseY && ((String)textLines.get(0)).equals(I18n.format("merchant.deprecated", new Object[0]))) {
         textLines.set(0, I18n.format("quarkmisc.item_out_of_stock", new Object[0]));
      }

      super.drawHoveringText(textLines, x, y, font);
   }
}
