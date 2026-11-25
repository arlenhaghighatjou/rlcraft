package com.creativemd.itemphysic;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemPhysicClient {
   public static KeyBinding pickup = new KeyBinding("key.pickup.item", 0, "key.categories.gameplay");

   public ItemPhysicClient() {
   }

   public static void init(FMLInitializationEvent event) {
      ClientRegistry.registerKeyBinding(pickup);
   }
}
