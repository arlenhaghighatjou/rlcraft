package org.orecruncher.dsurround.client.gui;

import javax.annotation.Nonnull;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CheckBoxButton extends GuiButtonExt {
   private static final String CHECKED = " [" + TextFormatting.RED + TextFormatting.BOLD + "X" + TextFormatting.RESET + "]";
   private static final String UNCHECKED = " [  ]";
   private final String prefix;
   private final boolean initialState;
   private final boolean defaultState;
   private boolean currentState;

   public CheckBoxButton(int id, @Nonnull String prefix, boolean initialState, boolean defaultState) {
      super(id, 0, 0, 68, 18, "");
      this.prefix = prefix;
      this.initialState = initialState;
      this.defaultState = defaultState;
      this.currentState = initialState;
      this.updateDisplayString();
   }

   protected void updateDisplayString() {
      this.displayString = this.prefix + (this.currentState ? CHECKED : " [  ]");
   }

   public boolean isDefault() {
      return this.currentState == this.defaultState;
   }

   public boolean isChanged() {
      return this.currentState != this.initialState;
   }

   public void setToDefault() {
      this.currentState = this.defaultState;
      this.updateDisplayString();
   }

   public void undoChanges() {
      this.currentState = this.initialState;
      this.updateDisplayString();
   }

   public void toggleState() {
      this.currentState = !this.currentState;
      this.updateDisplayString();
   }

   public boolean getValue() {
      return this.currentState;
   }
}
