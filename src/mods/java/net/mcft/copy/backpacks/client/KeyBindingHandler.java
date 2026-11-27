package net.mcft.copy.backpacks.client;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.mcft.copy.backpacks.WearableBackpacks;
import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.config.ModConfig;
import net.mcft.copy.backpacks.misc.util.ClientUtils;
import net.mcft.copy.backpacks.network.MessageOpenBackpack;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindingHandler {
   public static final String CATEGORY = "Wearable Backpacks RLCraft Edition";
   public static final KeyBinding openBackpack = new KeyBinding("key.wearablebackpacks.open", KeyConflictContext.IN_GAME, 48, "Wearable Backpacks RLCraft Edition");

   public KeyBindingHandler() {
      ClientRegistry.registerKeyBinding(openBackpack);
   }

   public void register(EventBus bus) {
      bus.register(KeyInputEvent.class, this::onKey);
   }

   public void onKey(KeyInputEvent event) {
      if (openBackpack.isPressed() && BackpackHelper.getBackpack(ClientUtils.getPlayer()) != null && ModConfig.server.enableSelfInteraction) {
         WearableBackpacks.CHANNEL.sendToServer(MessageOpenBackpack.create());
      }

   }
}
