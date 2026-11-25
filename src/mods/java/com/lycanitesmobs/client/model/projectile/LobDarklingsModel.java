package com.lycanitesmobs.client.model.projectile;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelCreatureObj;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LobDarklingsModel extends ModelCreatureObj {
   public LobDarklingsModel() {
      this(1.0F);
   }

   public LobDarklingsModel(float shadowSize) {
      this.initModel("lobdarklings", LycanitesMobs.modInfo, "projectile/lobdarklings");
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
      this.rotate(0.0F, loop * 8.0F, 0.0F);
   }
}
