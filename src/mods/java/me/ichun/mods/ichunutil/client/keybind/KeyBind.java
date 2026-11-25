package me.ichun.mods.ichunutil.client.keybind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class KeyBind {
   public int keyIndex;
   public boolean holdShift;
   public boolean holdCtrl;
   public boolean holdAlt;
   public boolean ignoreHold;
   private boolean pressed;
   private boolean isMinecraftBind;
   public int usages;

   public KeyBind(int index, boolean shift, boolean ctrl, boolean alt, boolean ignoreHolding) {
      this.keyIndex = index;
      this.holdShift = shift;
      this.holdCtrl = ctrl;
      this.holdAlt = alt;
      this.ignoreHold = ignoreHolding;
   }

   public KeyBind(int index) {
      this(index, false, false, false, false);
   }

   public void tick() {
      boolean flag = this.pressed;
      this.pressed = this.checkPressed();
      if (this.pressed != flag) {
         this.triggerEvent();
      }

   }

   @SideOnly(Side.CLIENT)
   private boolean checkPressed() {
      boolean stateShift = this.holdShift && GuiScreen.isShiftKeyDown() || !this.holdShift && (this.ignoreHold || !GuiScreen.isShiftKeyDown() || this.keyIndex == 42 || this.keyIndex == 54);
      boolean stateCtrl = this.holdCtrl && GuiScreen.isCtrlKeyDown() || !this.holdCtrl && (this.ignoreHold || !GuiScreen.isCtrlKeyDown() || this.keyIndex == (Minecraft.IS_RUNNING_ON_MAC ? 219 : 29) || this.keyIndex == (Minecraft.IS_RUNNING_ON_MAC ? 220 : 157));
      boolean stateAlt = this.holdAlt && (Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184)) || !this.holdAlt && (this.ignoreHold || !Keyboard.isKeyDown(56) && !Keyboard.isKeyDown(184) || this.keyIndex == 56 || this.keyIndex == 184);
      return stateShift && stateCtrl && stateAlt ? isPressed(this.keyIndex) : false;
   }

   public void triggerEvent() {
      MinecraftForge.EVENT_BUS.post(new KeyEvent(this));
   }

   public KeyBind setIsMinecraftBind() {
      this.isMinecraftBind = true;
      return this;
   }

   public boolean isMinecraftBind() {
      return this.isMinecraftBind;
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof KeyBind)) {
         return false;
      } else {
         KeyBind bind = (KeyBind)obj;
         return bind.keyIndex == this.keyIndex && bind.holdShift == this.holdShift && bind.holdCtrl == this.holdCtrl && bind.holdAlt == this.holdAlt && bind.ignoreHold == this.ignoreHold;
      }
   }

   @SideOnly(Side.CLIENT)
   public static boolean isPressed(int key) {
      return key < 0 ? Mouse.isButtonDown(key + 100) : Keyboard.isKeyDown(key);
   }

   public boolean isPressed() {
      return this.pressed;
   }

   public String serialize() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.keyIndex);
      if (this.holdShift) {
         sb.append(":SHIFT");
      }

      if (this.holdCtrl) {
         sb.append(":CTRL");
      }

      if (this.holdAlt) {
         sb.append(":ALT");
      }

      return sb.toString();
   }

   public void deserialize(String s) {
      String[] strings = s.split(":");

      try {
         this.keyIndex = Integer.parseInt(strings[0].trim());
         this.holdShift = s.contains("SHIFT");
         this.holdCtrl = s.contains("CTRL");
         this.holdAlt = s.contains("ALT");
      } catch (NumberFormatException var4) {
         ;
      }

   }
}
