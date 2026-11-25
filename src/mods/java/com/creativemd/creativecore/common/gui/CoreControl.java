package com.creativemd.creativecore.common.gui;

import com.creativemd.creativecore.common.gui.container.IControlParent;
import com.creativemd.creativecore.common.gui.event.ControlEvent;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.translation.I18n;

public abstract class CoreControl {
   public IControlParent parent;
   public String name;
   public boolean enabled;
   private int id = -1;

   public void setID(int id) {
      this.id = id;
   }

   public int getID() {
      return this.id;
   }

   public CoreControl(String name) {
      this.name = name;
      this.enabled = true;
   }

   public CoreControl setEnabled(boolean enabled) {
      this.enabled = enabled;
      return this;
   }

   public void onOpened() {
   }

   public void onClosed() {
   }

   public void onTick() {
   }

   public boolean is(String... name) {
      for(int i = 0; i < name.length; ++i) {
         if (this.name.equalsIgnoreCase(name[i])) {
            return true;
         }
      }

      return false;
   }

   public boolean hasParent() {
      return this.parent != null;
   }

   public EntityPlayer getPlayer() {
      return this.hasParent() ? this.parent.getPlayer() : null;
   }

   public boolean isRemote() {
      return this.getPlayer().world.isRemote;
   }

   public boolean raiseEvent(ControlEvent event) {
      return this.parent != null ? ((CoreControl)this.parent).raiseEvent(event) : false;
   }

   public void addListener(Object listener) {
      if (this.parent != null) {
         ((CoreControl)this.parent).addListener(listener);
      }

   }

   public void removeListener(Object listener) {
      if (this.parent != null) {
         ((CoreControl)this.parent).removeListener(listener);
      }

   }

   public boolean isInteractable() {
      return this.enabled && (!this.hasParent() || ((CoreControl)this.parent).isInteractable());
   }

   public void moveControlAbove(CoreControl controlInBack) {
      if (this.hasParent()) {
         this.parent.moveControlAbove(this, controlInBack);
      }

   }

   public void moveControlBehind(CoreControl controlInFront) {
      if (this.hasParent()) {
         this.parent.moveControlBehind(this, controlInFront);
      }

   }

   public void moveControlToBottom() {
      if (this.hasParent()) {
         this.parent.moveControlToBottom(this);
      }

   }

   public void moveControlToTop() {
      if (this.hasParent()) {
         this.parent.moveControlToTop(this);
      }

   }

   public static String translateOrDefault(String text, String defaultText) {
      return I18n.canTranslate(text) ? translate(text) : defaultText;
   }

   public static String translateOrNull(String text) {
      return I18n.canTranslate(text) ? translate(text) : null;
   }

   public static String translate(String text) {
      return I18n.translateToLocal(text);
   }

   public static List<String> translate(List<String> lines) {
      for(int i = 0; i < lines.size(); ++i) {
         lines.set(i, translate((String)lines.get(i)));
      }

      return lines;
   }
}
