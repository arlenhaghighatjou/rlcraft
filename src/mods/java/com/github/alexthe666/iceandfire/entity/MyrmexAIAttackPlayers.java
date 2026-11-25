package com.github.alexthe666.iceandfire.entity;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;

public class MyrmexAIAttackPlayers extends EntityAINearestAttackableTarget {
   public MyrmexAIAttackPlayers(final EntityMyrmexBase myrmex) {
      super(myrmex, EntityPlayer.class, 10, true, true, new Predicate<EntityPlayer>() {
         public boolean apply(@Nullable EntityPlayer entity) {
            return entity != null && (myrmex.getHive() == null || myrmex.getHive().isPlayerReputationTooLowToFight(entity.getUniqueID()));
         }
      });
   }
}
