package net.silentchaos512.lib.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

public abstract class KeyTrackerSL {
   final String modName;

   public KeyTrackerSL(String modName) {
      this.modName = modName;
   }

   public void register(EventBus bus) {
      bus.register(KeyInputEvent.class, this::onKeyInput);
   }

   public abstract void onKeyInput(KeyInputEvent var1);

   protected KeyBinding createBinding(String name, IKeyConflictContext keyConflictContext, KeyModifier keyModifier, int keyCode) {
      KeyBinding binding = new KeyBinding(name, keyConflictContext, keyModifier, keyCode, this.modName);
      ClientRegistry.registerKeyBinding(binding);
      return binding;
   }

   public static boolean isShiftDown() {
      return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
   }

   public static boolean isControlDown() {
      return Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
   }

   public static boolean isAltDown() {
      return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
   }
}
