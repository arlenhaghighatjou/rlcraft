package org.orecruncher.dsurround.client.hud;

import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IGuiOverlay {
   default void doRender(Pre event) {
   }

   default void doRender(Post event) {
   }

   default void doTick(int tickRef) {
   }
}
