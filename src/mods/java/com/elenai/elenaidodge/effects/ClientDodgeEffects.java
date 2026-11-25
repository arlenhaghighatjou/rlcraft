package com.elenai.elenaidodge.effects;

import com.elenai.elenaidodge.gui.DodgeStep;
import com.elenai.elenaidodge.util.ClientStorage;
import net.minecraft.client.Minecraft;

public class ClientDodgeEffects {
   public ClientDodgeEffects() {
   }

   public static void run() {
      if (!Minecraft.getMinecraft().player.isCreative() && !Minecraft.getMinecraft().player.isSpectator()) {
         ClientStorage.dodgesDouble = 27.0D;
      }

      if (ClientStorage.tutorialDodges < 1.0D) {
         ClientStorage.tutorialDodges += 0.25D;
         DodgeStep.moveToast.setProgress((float)ClientStorage.tutorialDodges);
      }

   }
}
