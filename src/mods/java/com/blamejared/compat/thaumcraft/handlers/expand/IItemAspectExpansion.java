package com.blamejared.compat.thaumcraft.handlers.expand;

import com.blamejared.ModTweaker;
import com.blamejared.compat.thaumcraft.handlers.aspects.CTAspectStack;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;

@ZenExpansion("crafttweaker.item.IItemStack")
@ModOnly("thaumcraft")
@ZenRegister
public class IItemAspectExpansion {
   public IItemAspectExpansion() {
   }

   @ZenMethod
   public static void setAspects(final IItemStack stack, final CTAspectStack... aspects) {
      ModTweaker.LATE_ADDITIONS.add(new BaseAction("Aspects") {
         public void apply() {
            AspectList list = new AspectList();

            for(CTAspectStack aspect : aspects) {
               list.add(aspect.getInternal().getInternal(), aspect.getAmount());
            }

            ThaumcraftApi.registerObjectTag(InputHelper.toStack(stack), list);
         }

         public String describe() {
            return "Setting aspects on item: " + LogHelper.getStackDescription(stack) + " to: " + this.getAspects();
         }

         private String getAspects() {
            StringBuilder builder = new StringBuilder();

            for(CTAspectStack aspect : aspects) {
               builder.append(aspect.getInternal().getInternal().getName()).append(", ");
            }

            return builder.reverse().deleteCharAt(0).deleteCharAt(0).reverse().toString();
         }
      });
   }

   @ZenMethod
   public static void removeAspects(final IItemStack stack, final CTAspectStack... aspects) {
      ModTweaker.LATE_REMOVALS.add(new BaseAction("Aspects") {
         public void apply() {
            AspectList list = new AspectList(InputHelper.toStack(stack));

            for(CTAspectStack aspect : aspects) {
               list.remove(aspect.getInternal().getInternal());
            }

            ThaumcraftApi.registerObjectTag(InputHelper.toStack(stack), list);
         }

         public String describe() {
            return "Removing aspects on item: " + LogHelper.getStackDescription(stack) + "," + this.getAspects();
         }

         private String getAspects() {
            StringBuilder builder = new StringBuilder();

            for(CTAspectStack aspect : aspects) {
               builder.append(aspect.getInternal().getInternal().getName()).append(", ");
            }

            return builder.reverse().deleteCharAt(0).deleteCharAt(0).reverse().toString();
         }
      });
   }
}
