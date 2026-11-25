package snownee.kiwi.client.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.nbt.NBTTagCompound;
import snownee.kiwi.client.gui.component.Component;

public class GuiControl implements IMessageHandler {
   protected final List<Component> components = new ArrayList();
   public Minecraft mc;
   public IMessageHandler messageHandler;
   public int offsetX;
   public int offsetY;
   public int width;
   public int height;

   public GuiControl(Minecraft mc, int width, int height, IMessageHandler messageHandler) {
      this.mc = mc;
      this.width = width;
      this.height = height;
      this.messageHandler = messageHandler;
   }

   public void addComponent(Component component) {
      boolean flag = true;

      for(int i = 0; i < this.components.size(); ++i) {
         Component c = (Component)this.components.get(i);
         if (component.getZLevel() >= c.getZLevel()) {
            this.components.add(i, component);
            flag = true;
            break;
         }
      }

      if (flag) {
         this.components.add(component);
      }

   }

   public boolean removeComponent(int index) {
      Component component = null;
      if (index >= 0 && index < this.components.size()) {
         component = (Component)this.components.remove(index);
      }

      if (component != null) {
         component.onDestroy();
      }

      return component != null;
   }

   public boolean removeComponent(Component component) {
      boolean flag = this.components.remove(component);
      if (flag) {
         component.onDestroy();
      }

      return flag;
   }

   public void removeAllComponents() {
      for(Component c : this.components) {
         c.onDestroy();
      }

      this.components.clear();
   }

   @Nullable
   public int getComponentSize(@Nullable Class<? extends Component> clazz) {
      return clazz != null && clazz != Component.class ? 0 : this.components.size();
   }

   @Nullable
   public Component getComponent(int index) {
      return (Component)this.components.get(index);
   }

   @Nullable
   public <T extends Component> T getComponent(Class<T> clazz) {
      for(Component component : this.components) {
         if (clazz.isInstance(component)) {
            return (T)component;
         }
      }

      return (T)null;
   }

   public <T extends Component> List<T> getComponents(Class<T> clazz) {
      List<T> list = new ArrayList(Math.max(4, this.components.size() / 4));

      for(Component component : this.components) {
         if (clazz.isInstance(component)) {
            list.add(component);
         }
      }

      return list;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      for(Component c : this.components) {
         if (c.visible) {
            c.drawScreen(this.offsetX, this.offsetY, mouseX - this.offsetX, mouseY - this.offsetY, partialTicks);
         }
      }

   }

   public void keyTyped(char typedChar, int keyCode) {
      for(Component c : this.components) {
         if (c.visible) {
            c.keyTyped(typedChar, keyCode);
         }
      }

   }

   public void handleMouseInput(int mouseX, int mouseY) {
      for(Component c : this.components) {
         if (c.visible) {
            c.handleMouseInput(mouseX - this.offsetX, mouseY - this.offsetY);
         }
      }

   }

   @OverridingMethodsMustInvokeSuper
   public void onDestroy() {
      for(Component c : this.components) {
         c.onDestroy();
      }

      this.components.clear();
      this.mc = null;
      this.messageHandler = null;
   }

   public int messageReceived(GuiControl control, Component component, int param1, int param2) {
      return this.messageHandler.messageReceived(control, component, param1, param2);
   }

   public int messageReceived(GuiControl control, Component component, NBTTagCompound data) {
      return this.messageHandler.messageReceived(control, component, data);
   }

   public void setTooltip(GuiControl control, Component component, List<String> tooltip, FontRenderer fontRenderer) {
      this.messageHandler.setTooltip(control, component, tooltip, fontRenderer);
   }
}
