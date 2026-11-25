package com.bloodnbonesgaming.loadingscreens;

import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.client.Minecraft;

public class GamestageHandler {
   public GamestageHandler() {
   }

   public static boolean hasGamestage(Minecraft minecraft, String stage) {
      return minecraft.player != null ? GameStageHelper.clientHasStage(minecraft.player, stage) : false;
   }
}
