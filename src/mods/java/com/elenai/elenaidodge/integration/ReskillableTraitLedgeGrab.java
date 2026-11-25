package com.elenai.elenaidodge.integration;

import codersafterdark.reskillable.api.unlockable.Trait;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ReskillableTraitLedgeGrab extends Trait {
   public ReskillableTraitLedgeGrab() {
      super(new ResourceLocation("elenaidodge", "ledgegrab"), 0, 2, new ResourceLocation("reskillable", "agility"), 0, new String[]{""});
      if (FMLCommonHandler.instance().getSide().isClient()) {
         MinecraftForge.EVENT_BUS.register(this);
      }

   }
}
