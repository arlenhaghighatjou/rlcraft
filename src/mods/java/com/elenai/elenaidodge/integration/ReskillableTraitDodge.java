package com.elenai.elenaidodge.integration;

import codersafterdark.reskillable.api.unlockable.Trait;
import com.elenai.elenaidodge.ModConfig;
import com.elenai.elenaidodge.util.ClientStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ReskillableTraitDodge extends Trait {
   public ReskillableTraitDodge() {
      super(new ResourceLocation("elenaidodge", "dodge"), 2, 0, new ResourceLocation("reskillable", "agility"), 0, new String[]{""});
      if (FMLCommonHandler.instance().getSide().isClient()) {
         MinecraftForge.EVENT_BUS.register(this);
      }

   }

   public void onUnlock(EntityPlayer player) {
      if (ModConfig.client.hud.tutorial) {
         ClientStorage.shownTutorial = false;
         ClientStorage.tutorialDodges = 0.0D;
      }

   }
}
