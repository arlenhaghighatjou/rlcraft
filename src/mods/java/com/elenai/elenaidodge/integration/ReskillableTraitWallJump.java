package com.elenai.elenaidodge.integration;

import codersafterdark.reskillable.api.unlockable.Trait;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ReskillableTraitWallJump extends Trait {
   public ReskillableTraitWallJump() {
      super(new ResourceLocation("elenaidodge", "walljump"), 4, 2, new ResourceLocation("reskillable", "agility"), 0, new String[]{""});
      if (FMLCommonHandler.instance().getSide().isClient()) {
         MinecraftForge.EVENT_BUS.register(this);
      }

   }
}
