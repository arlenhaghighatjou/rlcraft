package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDDamageSources;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class PotionHypothermia extends PotionThermia {
   private final ResourceLocation texture;

   public PotionHypothermia() {
      super(true, 6089727);
      this.xOffset = 0;
      this.yOffset = 0;
      this.texture = this.formatTexture("hypothermia");
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public void attackPlayer(EntityPlayer player, float damage) {
      player.attackEntityFrom(SDDamageSources.HYPOTHERMIA, damage);
   }
}
