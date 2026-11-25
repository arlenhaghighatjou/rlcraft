package com.creativemd.creativecore.common.gui.mc;

import com.creativemd.creativecore.common.gui.container.SubGui;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IVanillaGUI {
   List<SubGui> getLayers();

   void removeLayer(SubGui var1);

   void addLayer(SubGui var1);

   int getMaxScale(Minecraft var1);

   boolean hasTopLayer();

   SubGui getTopLayer();

   boolean isTopLayer(SubGui var1);

   boolean isOpen(Class<? extends SubGui> var1);

   <T extends SubGui> T get(Class<T> var1);

   int getGuiLeft();

   int getGuiTop();

   void onLayerClosed();

   void sendChat(String var1);
}
