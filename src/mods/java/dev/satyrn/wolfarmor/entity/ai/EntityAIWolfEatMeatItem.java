package dev.satyrn.wolfarmor.entity.ai;

import dev.satyrn.wolfarmor.WolfArmorMod;
import dev.satyrn.wolfarmor.api.entity.passive.IArmoredWolf;
import dev.satyrn.wolfarmor.config.WolfArmorConfig;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityWolf;

public class EntityAIWolfEatMeatItem extends EntityAIBase {
   private final EntityWolf entity;
   private final IArmoredWolf armoredWolf;
   private final WolfArmorConfig config;

   public EntityAIWolfEatMeatItem(EntityWolf entity) {
      this.entity = entity;
      this.armoredWolf = (IArmoredWolf)entity;
      this.config = WolfArmorMod.getConfig();
   }

   public boolean shouldExecute() {
      return !this.entity.getIsInvulnerable() && this.entity.hurtTime == 0 && this.config.getChestEnabled() && this.config.getAutoHealEnabled() && this.armoredWolf.getHasChest();
   }

   public boolean shouldContinueExecuting() {
      return false;
   }
}
