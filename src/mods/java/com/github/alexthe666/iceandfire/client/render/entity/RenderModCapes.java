package com.github.alexthe666.iceandfire.client.render.entity;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderModCapes {
   public ResourceLocation redTex = new ResourceLocation("iceandfire", "textures/models/misc/cape_fire.png");
   public ResourceLocation redElytraTex = new ResourceLocation("iceandfire", "textures/models/misc/elytra_fire.png");
   public ResourceLocation blueTex = new ResourceLocation("iceandfire", "textures/models/misc/cape_ice.png");
   public ResourceLocation blueElytraTex = new ResourceLocation("iceandfire", "textures/models/misc/elytra_ice.png");
   public ResourceLocation betaTex = new ResourceLocation("iceandfire", "textures/models/misc/cape_beta.png");
   public ResourceLocation betaElytraTex = new ResourceLocation("iceandfire", "textures/models/misc/elytra_beta.png");
   public EaglercraftUUID[] redcapes = new EaglercraftUUID[]{EaglercraftUUID.fromString("59efccaf-902d-45da-928a-5a549b9fd5e0"), EaglercraftUUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c")};
   public EaglercraftUUID[] bluecapes = new EaglercraftUUID[]{EaglercraftUUID.fromString("0ed918c8-d612-4360-b711-cd415671356f"), EaglercraftUUID.fromString("5d43896a-06a0-49fb-95c5-38485c63667f")};
   public EaglercraftUUID[] betatesters = new EaglercraftUUID[0];

   public RenderModCapes() {
   }

   @SubscribeEvent
   public void playerRender(Pre event) {
      if (event.getEntityPlayer() instanceof AbstractClientPlayer) {
         NetworkPlayerInfo info = null;

         try {
            info = (NetworkPlayerInfo)ReflectionHelper.findField(AbstractClientPlayer.class, new String[]{"playerInfo", "playerInfo"}).get(event.getEntityPlayer());
         } catch (IllegalArgumentException var7) {
            var7.printStackTrace();
         } catch (IllegalAccessException var8) {
            var8.printStackTrace();
         }

         if (info != null) {
            Map<Type, ResourceLocation> textureMap = null;

            try {
               textureMap = (Map)ReflectionHelper.findField(NetworkPlayerInfo.class, new String[]{"playerTextures", "playerTextures"}).get(info);
            } catch (IllegalArgumentException var5) {
               var5.printStackTrace();
            } catch (IllegalAccessException var6) {
               var6.printStackTrace();
            }

            if (textureMap != null) {
               if (this.hasBetaCape(event.getEntityPlayer().getUniqueID())) {
                  textureMap.put(Type.CAPE, this.betaTex);
                  textureMap.put(Type.ELYTRA, this.betaElytraTex);
               }

               if (this.hasRedCape(event.getEntityPlayer().getUniqueID())) {
                  textureMap.put(Type.CAPE, this.redTex);
                  textureMap.put(Type.ELYTRA, this.redElytraTex);
               }

               if (this.hasBlueCape(event.getEntityPlayer().getUniqueID())) {
                  textureMap.put(Type.CAPE, this.blueTex);
                  textureMap.put(Type.ELYTRA, this.blueElytraTex);
               }
            }
         }
      }

   }

   private boolean hasRedCape(EaglercraftUUID uniqueID) {
      for(EaglercraftUUID uuid1 : this.redcapes) {
         if (uniqueID.equals(uuid1)) {
            return true;
         }
      }

      return false;
   }

   private boolean hasBlueCape(EaglercraftUUID uniqueID) {
      for(EaglercraftUUID uuid1 : this.bluecapes) {
         if (uniqueID.equals(uuid1)) {
            return true;
         }
      }

      return false;
   }

   private boolean hasBetaCape(EaglercraftUUID uniqueID) {
      for(EaglercraftUUID uuid1 : this.betatesters) {
         if (uniqueID.equals(uuid1)) {
            return true;
         }
      }

      return false;
   }
}
