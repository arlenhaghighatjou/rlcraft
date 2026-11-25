package net.ilexiconn.llibrary.client.gui.element;

import java.util.Collection;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IElementGUI {
   void addElement(Element var1);

   void addElements(Collection<Element> var1);

   void removeElement(Element var1);

   void clearElements();

   void sendElementToBack(Element var1);

   void sendElementToFront(Element var1);

   boolean isElementOnTop(Element var1);

   void playClickSound();

   FontRenderer getFontRenderer();

   TextureManager getTextureManager();

   void drawTexturedModalRect(int var1, int var2, int var3, int var4, int var5, int var6);

   void drawTexturedModalRect(float var1, float var2, int var3, int var4, int var5, int var6);

   void drawTexturedModalRect(int var1, int var2, EaglerTextureAtlasSprite var3, int var4, int var5);

   int getHeight();

   int getWidth();
}
