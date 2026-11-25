package com.tmtravlr.potioncore;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ClientProxy extends CommonProxy {
   EaglercraftRandom random = new EaglercraftRandom();

   public ClientProxy() {
   }

   public EntityPlayer getPlayer() {
      return Minecraft.getMinecraft().player;
   }

   public void replaceEntityRenderer() {
      Minecraft mc = Minecraft.getMinecraft();
      EntityRenderer renderer = new PotionCoreEntityRenderer(mc, mc.getResourceManager());
      ObfuscationReflectionHelper.setPrivateValue(Minecraft.class, mc, renderer, new String[]{"entityRenderer", "entityRenderer"});
   }

   public void loadInverted() {
      PotionCoreEventHandlerClient.loadInverted();
   }
}
