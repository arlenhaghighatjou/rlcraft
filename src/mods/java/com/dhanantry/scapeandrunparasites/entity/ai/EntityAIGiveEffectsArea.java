package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.List;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIGiveEffectsArea extends EntityAIBase {
   EntityParasiteBase parent;
   private int attackTimer = 0;
   private int bRange;
   private int bCooldown;
   private String[] effects;

   public EntityAIGiveEffectsArea(EntityParasiteBase parent, int cooldown, int range, String[] in) {
      this.parent = parent;
      this.bRange = range;
      this.bCooldown = cooldown;
      this.effects = in;
   }

   public boolean shouldExecute() {
      return true;
   }

   public void updateTask() {
      ++this.attackTimer;
      if (this.attackTimer >= 60) {
         this.BuffParasites();
         this.attackTimer -= this.bCooldown;
      }

   }

   protected void BuffParasites() {
      String[] here = new String[3];
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parent.getPosition())).grow((double)this.bRange);
      List<EntityParasiteBase> moblist = this.parent.world.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb);

      for(String i : this.effects) {
         here = i.split(";");
         Potion potionE = Potion.getPotionFromResourceLocation(here[2]);
         if (potionE != null) {
            int duration = Integer.parseInt(here[0]) * 20;
            int amp = Integer.parseInt(here[1]);

            for(EntityParasiteBase mob : moblist) {
               if (mob != this.parent) {
                  mob.addPotionEffect(new PotionEffect(potionE, duration, amp, false, false));
               }
            }
         }
      }

   }
}
