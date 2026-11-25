package codersafterdark.reskillable.api.toast;

import codersafterdark.reskillable.api.unlockable.Unlockable;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class UnlockableToast extends AbstractToast {
   private final Unlockable unlockable;

   public UnlockableToast(Unlockable unlockable) {
      super(unlockable.getName(), (new TextComponentTranslation("reskillable.toast.unlockable_desc", new Object[0])).getUnformattedComponentText());
      this.unlockable = unlockable;
   }

   protected void renderImage(GuiToast guiToast) {
      this.bindImage(guiToast, this.unlockable.getIcon());
      Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
   }

   protected boolean hasImage() {
      return true;
   }
}
