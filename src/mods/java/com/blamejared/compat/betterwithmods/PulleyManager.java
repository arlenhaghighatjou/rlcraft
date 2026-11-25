package com.blamejared.compat.betterwithmods;

import betterwithmods.common.registry.PulleyStructureManager;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterwithmods.PulleyManager")
@ModOnly("betterwithmods")
@ZenRegister
public class PulleyManager {
   public PulleyManager() {
   }

   @ZenMethod
   public static void addPulleyBlock(IBlockState state) {
      CraftTweakerAPI.apply(new PulleyManager.AddPulleyBlock(CraftTweakerMC.getBlockState(state)));
   }

   public static class AddPulleyBlock extends BaseAction {
      private net.minecraft.block.state.IBlockState state;

      AddPulleyBlock(net.minecraft.block.state.IBlockState state) {
         super("Add Pulley Block");
         this.state = state;
      }

      public void apply() {
         PulleyStructureManager.registerPulleyBlock(this.state);
      }
   }
}
