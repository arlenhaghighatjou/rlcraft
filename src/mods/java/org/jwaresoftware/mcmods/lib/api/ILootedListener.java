package org.jwaresoftware.mcmods.lib.api;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ILootedListener {
   void notifyLooted(@Nonnull ItemStack var1, @Nonnull World var2, @Nonnull EntityPlayer var3, float var4);
}
