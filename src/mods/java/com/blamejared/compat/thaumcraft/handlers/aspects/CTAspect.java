package com.blamejared.compat.thaumcraft.handlers.aspects;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenSetter;
import thaumcraft.api.aspects.Aspect;

@ZenRegister
@ModOnly("thaumcraft")
@ZenClass("thaumcraft.aspect.CTAspect")
public class CTAspect {
   private final Aspect internal;

   public CTAspect(Aspect internal) {
      this.internal = internal;
   }

   @ZenGetter("name")
   public String getName() {
      return this.internal.getName();
   }

   @ZenSetter("chatColour")
   public void setChatColour(String chatColour) {
      this.internal.setChatcolor(chatColour);
   }

   @ZenGetter("chatColour")
   public String getChatColour() {
      return this.internal.getChatcolor();
   }

   public Aspect getInternal() {
      return this.internal;
   }
}
