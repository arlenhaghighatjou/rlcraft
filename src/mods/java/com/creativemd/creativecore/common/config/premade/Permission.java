package com.creativemd.creativecore.common.config.premade;

import com.creativemd.creativecore.CreativeCore;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;

public class Permission<T> extends NamedList<T> {
   private T value;

   public Permission(T defaultValue) {
      this.value = defaultValue;
      this.put("default", this.value);
   }

   public T getDefault() {
      return this.value;
   }

   public Permission<T> add(String usergroup, T value) {
      if (usergroup.equals("default")) {
         this.value = value;
      } else {
         super.put(usergroup, value);
      }

      return this;
   }

   public T get(EntityPlayer player) {
      for(Entry<String, T> pair : this.entrySet()) {
         if (!((String)pair.getKey()).equals("default") && CreativeCore.config.is(player, (String)pair.getKey())) {
            return (T)pair.getValue();
         }
      }

      return this.value;
   }
}
