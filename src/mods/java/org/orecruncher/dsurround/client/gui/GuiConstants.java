package org.orecruncher.dsurround.client.gui;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Localization;

@SideOnly(Side.CLIENT)
public final class GuiConstants {
   public static final String TOKEN_CULL = "cull";
   public static final String TOKEN_BLOCK = "block";
   public static final String TEXT_CULL = Localization.format("dsurround.format.Cull", new Object[0]);
   public static final String TEXT_BLOCK = Localization.format("dsurround.format.Block", new Object[0]);
   public static final String TEXT_PLAY = Localization.format("dsurround.format.Play", new Object[0]);
   public static final String TEXT_STOP = TextFormatting.RED + Localization.format("dsurround.format.Stop", new Object[0]);

   private GuiConstants() {
   }
}
