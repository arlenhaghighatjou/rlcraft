package ivorius.ivtoolkit.gui;

import net.minecraft.client.gui.Gui;

public interface GuiControlListener<G extends Gui> {
   void valueChanged(G var1);
}
