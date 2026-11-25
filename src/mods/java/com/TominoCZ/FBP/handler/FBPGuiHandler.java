package com.TominoCZ.FBP.handler;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.gui.FBPGuiNote;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FBPGuiHandler {
   public FBPGuiHandler() {
   }

   @SubscribeEvent
   public void onRenderGui(Post evt) {
      if (evt.getType() == ElementType.EXPERIENCE) {
         if (FBP.frozen && FBP.isEnabled()) {
            new FBPGuiNote();
         }

      }
   }

   @SubscribeEvent
   public void onGuiMouseInput(MouseInputEvent e) {
   }

   @SubscribeEvent
   public void onGuiKeyboardInput(KeyboardInputEvent e) {
   }
}
