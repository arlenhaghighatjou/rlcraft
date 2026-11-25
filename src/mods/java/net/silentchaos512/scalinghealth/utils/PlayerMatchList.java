package net.silentchaos512.scalinghealth.utils;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

/** @deprecated */
@Deprecated
public class PlayerMatchList {
   private final List<String> list = new ArrayList();

   public PlayerMatchList() {
   }

   public void add(String name) {
      this.list.add(name);
   }

   public void clear() {
      this.list.clear();
   }

   public boolean contains(EntityPlayer player) {
      if (player == null) {
         return false;
      } else {
         for(String name : this.list) {
            if (name.equalsIgnoreCase(player.getName())) {
               return true;
            }
         }

         return false;
      }
   }
}
