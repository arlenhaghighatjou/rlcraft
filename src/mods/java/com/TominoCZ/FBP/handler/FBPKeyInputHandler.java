package com.TominoCZ.FBP.handler;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.gui.FBPGuiBlacklist;
import com.TominoCZ.FBP.gui.FBPGuiMenuPage0;
import com.TominoCZ.FBP.keys.FBPKeyBindings;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class FBPKeyInputHandler {
   Minecraft mc = Minecraft.getMinecraft();
   public static FBPKeyInputHandler INSTANCE;
   boolean wasOpened = false;

   public FBPKeyInputHandler() {
      INSTANCE = this;
   }

   @SubscribeEvent
   public void onKeyboardInput(KeyInputEvent e) {
      this.onInput();
   }

   @SubscribeEvent
   public void onMouseInput(MouseInputEvent e) {
      this.onInput();
   }

   public void onInput() {
      if (FBPKeyBindings.FBPMenu.isPressed()) {
         Minecraft.getMinecraft().displayGuiScreen(new FBPGuiMenuPage0());
      }

      if (FBPKeyBindings.FBPToggle.isPressed()) {
         FBP.setEnabled(!FBP.enabled);
      }

      if (FBPKeyBindings.FBPFreeze.isPressed()) {
         if (FBP.isEnabled()) {
            FBP.frozen = !FBP.frozen;
         } else {
            FBP.frozen = false;
         }
      }

      boolean isShiftDown = Keyboard.isKeyDown(42);
      boolean isKeyDown = FBPKeyBindings.FBPFastAdd.isKeyDown();
      if ((!isShiftDown || !isKeyDown) && !isKeyDown) {
         if (this.wasOpened) {
            this.wasOpened = false;
         }
      } else {
         Block b = null;
         ItemStack stack = null;
         boolean useHeldBlock = isShiftDown && this.mc.player.getHeldItemMainhand() != null && this.mc.player.getHeldItemMainhand().getItem() != null && (b = Block.getBlockFromName((stack = this.mc.player.getHeldItemMainhand()).getItem().getRegistryName().toString())) != null && b != Blocks.AIR;
         if (!this.wasOpened && isKeyDown && (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit.equals(Type.BLOCK) || useHeldBlock)) {
            this.mc.displayGuiScreen(useHeldBlock ? new FBPGuiBlacklist(stack) : new FBPGuiBlacklist(this.mc.objectMouseOver.getBlockPos()));
            Mouse.setGrabbed(true);
            this.wasOpened = true;
         }
      }

   }
}
