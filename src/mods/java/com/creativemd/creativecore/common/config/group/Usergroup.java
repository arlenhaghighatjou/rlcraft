package com.creativemd.creativecore.common.config.group;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.utils.player.PlayerSelector;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class Usergroup {
   @CreativeConfig
   public List<PlayerSelector> filters = new ArrayList();

   public Usergroup() {
   }

   public Usergroup(PlayerSelector... selectors) {
      for(PlayerSelector selector : selectors) {
         this.filters.add(selector);
      }

   }

   public boolean is(EntityPlayer player) {
      for(PlayerSelector selector : this.filters) {
         if (!selector.is(player)) {
            return false;
         }
      }

      return true;
   }
}
