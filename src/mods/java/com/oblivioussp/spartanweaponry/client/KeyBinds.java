package com.oblivioussp.spartanweaponry.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBinds {
   public static final KeyBinding KEY_ACCESS_QUIVER = new KeyBinding("key.spartanweaponry.access_quiver", KeyConflictContext.IN_GAME, 23, "key.spartanweaponry:category.title");

   public KeyBinds() {
   }
}
