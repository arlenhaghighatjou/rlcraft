package com.lycanitesmobs.core.mobevent.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.info.AltarInfo;
import com.lycanitesmobs.core.mobevent.MobEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AltarMobEventTrigger extends MobEventTrigger {
   public AltarInfo altar;

   public AltarMobEventTrigger(MobEvent mobEvent) {
      super(mobEvent);
   }

   public void loadFromJSON(JsonObject json) {
      this.altar = AltarInfo.getAltar(json.get("altarName").getAsString());
      if (this.altar != null) {
         this.altar.mobEventTrigger = this;
      }

      super.loadFromJSON(json);
   }

   public boolean onActivate(Entity entity, World world, BlockPos pos, int variant) {
      EntityPlayer player = null;
      if (entity instanceof EntityPlayer) {
         player = (EntityPlayer)entity;
      }

      return !this.canTrigger(world, player) ? false : this.trigger(world, player, pos, 1, variant);
   }

   public void onRemove() {
      if (this.altar != null) {
         this.altar.mobEventTrigger = null;
      }

   }
}
