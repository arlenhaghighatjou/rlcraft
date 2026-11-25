package snownee.kiwi.client.gui;

import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.NBTTagCompound;
import snownee.kiwi.client.gui.component.Component;

public interface IMessageHandler {
   int messageReceived(GuiControl var1, Component var2, int var3, int var4);

   int messageReceived(GuiControl var1, Component var2, NBTTagCompound var3);

   void setTooltip(GuiControl var1, Component var2, List<String> var3, FontRenderer var4);
}
