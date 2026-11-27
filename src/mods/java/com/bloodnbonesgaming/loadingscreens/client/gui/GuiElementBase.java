package com.bloodnbonesgaming.loadingscreens.client.gui;

import com.bloodnbonesgaming.lib.util.script.ScriptCall;
import com.bloodnbonesgaming.lib.util.script.ScriptTarget;
import net.minecraft.client.Minecraft;

public abstract class GuiElementBase implements ScriptTarget {
   protected final EnumGuiLocation location;
   protected double relXOffset = 0.0D;
   protected double relYOffset = 0.0D;
   protected int absXOffset = 0;
   protected int absYOffset = 0;

   public GuiElementBase(EnumGuiLocation location) {
      this.location = location;
   }

   public void setRelXOffset(double offset) {
      this.relXOffset = offset;
   }

   public void setRelYOffset(double offset) {
      this.relYOffset = offset;
   }

   public void setAbsXOffset(int offset) {
      this.absXOffset = offset;
   }

   public void setAbsYOffset(int offset) {
      this.absYOffset = offset;
   }

   public abstract void render(Minecraft var1, int var2, int var3);

   public Object call(String method, Object[] args) throws Exception {
      if (args.length == 1) {
         if (method.equals("setRelXOffset") && ScriptCall.isNumber(args[0])) {
            this.setRelXOffset(ScriptCall.toDouble(args[0]));
            return null;
         }

         if (method.equals("setRelYOffset") && ScriptCall.isNumber(args[0])) {
            this.setRelYOffset(ScriptCall.toDouble(args[0]));
            return null;
         }

         if (method.equals("setAbsXOffset") && ScriptCall.isNumber(args[0])) {
            this.setAbsXOffset(ScriptCall.toInt(args[0]));
            return null;
         }

         if (method.equals("setAbsYOffset") && ScriptCall.isNumber(args[0])) {
            this.setAbsYOffset(ScriptCall.toInt(args[0]));
            return null;
         }
      }

      return NO_METHOD;
   }
}
