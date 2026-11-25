package com.blamejared.compat.thaumcraft.handlers.aspects;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenRegister
@ModOnly("thaumcraft")
@ZenClass("thaumcraft.aspect.CTAspectStack")
public class CTAspectStack {
   private final CTAspect internal;
   private final int amount;

   public CTAspectStack(CTAspect internal, int amount) {
      this.internal = internal;
      this.amount = amount;
   }

   @ZenOperator(OperatorType.MUL)
   @ZenMethod
   public CTAspectStack setAmount(int amount) {
      return new CTAspectStack(this.internal, amount);
   }

   @ZenGetter("internal")
   public CTAspect getInternal() {
      return this.internal;
   }

   @ZenGetter("amount")
   public int getAmount() {
      return this.amount;
   }
}
