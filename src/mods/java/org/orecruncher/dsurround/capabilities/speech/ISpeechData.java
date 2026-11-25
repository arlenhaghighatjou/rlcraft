package org.orecruncher.dsurround.capabilities.speech;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public interface ISpeechData {
   void addMessage(@Nonnull String var1, int var2);

   ObjectArray<String> getText();

   RenderContext getRenderContext();

   void onUpdate(int var1);
}
