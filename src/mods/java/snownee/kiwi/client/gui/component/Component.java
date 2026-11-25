package snownee.kiwi.client.gui.component;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.NBTTagCompound;
import snownee.kiwi.client.gui.GuiControl;

public abstract class Component {
   private int zLevel;
   public int width;
   public int height;
   public int top;
   public int left;
   public boolean visible;
   protected GuiControl parent;

   public Component(GuiControl parent, int width, int height) {
      this.parent = parent;
      this.width = width;
      this.height = height;
      this.visible = true;
   }

   public int getZLevel() {
      return this.zLevel;
   }

   public abstract void drawScreen(int var1, int var2, int var3, int var4, float var5);

   public void keyTyped(char typedChar, int keyCode) {
   }

   public void handleMouseInput(int relMouseX, int relMouseY) {
   }

   @OverridingMethodsMustInvokeSuper
   public void onDestroy() {
      this.parent = null;
   }

   public int sendMessage(int param1, int param2) {
      return this.parent.messageHandler.messageReceived(this.parent, this, param1, param2);
   }

   public int sendMessage(NBTTagCompound data) {
      return this.parent.messageHandler.messageReceived(this.parent, this, data);
   }

   public void setTooltip(List<String> tooltip, @Nullable FontRenderer fontRenderer) {
      this.parent.messageHandler.setTooltip(this.parent, this, tooltip, fontRenderer);
   }
}
