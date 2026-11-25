package reborncore.client.guibuilder;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IGuiTile {
   @SideOnly(Side.CLIENT)
   void drawGuiContainerForegroundLayer(GuiScreen var1, int var2, int var3, int var4, int var5);

   @SideOnly(Side.CLIENT)
   void drawGuiContainerBackgroundLayer(GuiScreen var1, int var2, int var3, int var4, int var5, float var6, int var7, int var8);

   @SideOnly(Side.CLIENT)
   GuiContainer getGui(EntityPlayer var1);

   void opengui(EntityPlayer var1, Object var2, World var3, BlockPos var4);

   Container getContainer(EntityPlayer var1);

   @Nullable
   List<Slot> getSlots();
}
