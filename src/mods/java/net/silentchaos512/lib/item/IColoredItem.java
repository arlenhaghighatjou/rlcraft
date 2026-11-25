package net.silentchaos512.lib.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IColoredItem {
   @SideOnly(Side.CLIENT)
   IItemColor getColorHandler();
}
