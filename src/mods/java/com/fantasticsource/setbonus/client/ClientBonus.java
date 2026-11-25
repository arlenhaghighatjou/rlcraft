package com.fantasticsource.setbonus.client;

import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import java.util.LinkedHashMap;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class ClientBonus extends Bonus {
   private LinkedHashMap<EntityPlayer, ClientBonus.BonusInstance> instances = new LinkedHashMap();

   public ClientBonus() {
   }

   public static ClientBonus getInstance(String parsableBonus) {
      return (ClientBonus)Bonus.getInstance(parsableBonus, Side.CLIENT);
   }

   public static void dropAll() {
      for(ClientBonus bonus : ClientData.bonuses.values()) {
         for(ClientBonus.BonusInstance data : bonus.instances.values()) {
            data.update(false);
         }
      }

      ClientData.bonuses.clear();
   }

   public static void updateBonuses(EntityPlayer player) {
      for(ClientBonus bonus : ClientData.bonuses.values()) {
         bonus.update(player);
      }

   }

   @Nonnull
   public ClientBonus.BonusInstance getBonusInstance(EntityPlayer player) {
      return (ClientBonus.BonusInstance)this.instances.computeIfAbsent(player, (k) -> {
         return new ClientBonus.BonusInstance();
      });
   }

   public void update(EntityPlayer player) {
      ((ClientBonus.BonusInstance)this.instances.computeIfAbsent(player, (k) -> {
         return new ClientBonus.BonusInstance();
      })).update();
   }

   public class BonusInstance {
      public boolean active;

      private BonusInstance() {
         this.update();
      }

      public void update() {
         for(ABonusRequirement requirement : ClientBonus.this.bonusRequirements) {
            if (requirement.active(Minecraft.getMinecraft().player) < requirement.required()) {
               this.update(false);
               return;
            }
         }

         this.update(true);
      }

      private void update(boolean activate) {
         if (activate) {
            if (!this.active) {
               this.active = true;
            }
         } else if (this.active) {
            this.active = false;
         }

      }
   }
}
