package com.fantasticsource.setbonus.server;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.setbonus.common.Bonus;
import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.common.bonuselements.ABonusElement;
import com.fantasticsource.setbonus.common.bonusrequirements.ABonusRequirement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class ServerBonus extends Bonus {
   public static boolean changed;
   private LinkedHashMap<EntityPlayer, ServerBonus.BonusInstance> instances = new LinkedHashMap();

   public ServerBonus() {
   }

   public static ServerBonus getInstance(String parsableBonus) {
      return (ServerBonus)Bonus.getInstance(parsableBonus, Side.SERVER);
   }

   public static void saveDiscoveries(EntityPlayerMP player) {
      World world = player.world;

      try {
         String string = MCTools.getDataDir(world.getMinecraftServer()) + "setbonus" + File.separator;
         File file = new File(string);
         if (!file.exists()) {
            file.mkdir();
         }

         string = string + "discoveries" + File.separator;
         file = new File(string);
         if (!file.exists()) {
            file.mkdir();
         }

         string = string + player.getCachedUniqueIdString() + ".txt";
         file = new File(string);
         BufferedWriter writer = new BufferedWriter(new FileWriter(file));

         for(Entry<String, ServerBonus> entry : ServerData.bonuses.entrySet()) {
            ServerBonus.BonusInstance data = (ServerBonus.BonusInstance)((ServerBonus)entry.getValue()).instances.get(player);
            if (data != null && data.discovered) {
               writer.write((String)entry.getKey() + "\r\n");
            }
         }

         writer.close();
      } catch (IOException var8) {
         MCTools.crash(var8, 901, false);
      }

   }

   public static void loadDiscoveries(EntityPlayerMP player) {
      World world = player.world;

      try {
         String string = MCTools.getDataDir(world.getMinecraftServer()) + "setbonus" + File.separator;
         File file = new File(string);
         if (!file.exists()) {
            return;
         }

         string = string + "discoveries" + File.separator;
         file = new File(string);
         if (!file.exists()) {
            return;
         }

         string = string + player.getCachedUniqueIdString() + ".txt";
         file = new File(string);
         if (!file.exists()) {
            return;
         }

         BufferedReader reader = new BufferedReader(new FileReader(file));

         for(String var9 = reader.readLine(); var9 != null && !var9.equals(""); var9 = reader.readLine()) {
            ServerBonus bonus = (ServerBonus)ServerData.bonuses.get(var9);
            if (bonus != null && bonus.discoveryMode != MODE_GLOBALLY_HIDDEN) {
               bonus.getBonusInstance(player).discovered = true;
            }
         }

         reader.close();
      } catch (IOException var6) {
         MCTools.crash(var6, 901, false);
      }

      saveDiscoveries(player);
   }

   public static void dropAll() {
      for(ServerBonus bonus : ServerData.bonuses.values()) {
         for(ServerBonus.BonusInstance data : bonus.instances.values()) {
            data.update(false);
         }
      }

      ServerData.bonuses.clear();
   }

   public static void clearMem(EntityPlayer player) {
      for(ServerBonus bonus : ServerData.bonuses.values()) {
         ServerBonus.BonusInstance data = (ServerBonus.BonusInstance)bonus.instances.get(player);
         if (data != null) {
            data.update(false);
            bonus.instances.remove(player);
         }
      }

   }

   public static void updateBonuses(EntityPlayerMP player) {
      updateBonuses(player, false);
   }

   public static void updateBonuses(EntityPlayerMP player, boolean forceNew) {
      changed = false;

      for(ServerBonus bonus : ServerData.bonuses.values()) {
         bonus.update(player, forceNew);
      }

      while(changed) {
         changed = false;

         for(ServerBonus bonus : ServerData.bonuses.values()) {
            bonus.update(player);
         }
      }

   }

   @Nonnull
   public ServerBonus.BonusInstance getBonusInstance(EntityPlayerMP player) {
      return (ServerBonus.BonusInstance)this.instances.computeIfAbsent(player, (k) -> {
         return new ServerBonus.BonusInstance(player, this);
      });
   }

   public void update(EntityPlayerMP player) {
      this.update(player, false);
   }

   public void update(EntityPlayerMP player, boolean forceNew) {
      if (forceNew) {
         ServerBonus.BonusInstance instance = new ServerBonus.BonusInstance(player, this);
         this.instances.put(player, instance);
         instance.update();
      } else {
         ((ServerBonus.BonusInstance)this.instances.computeIfAbsent(player, (k) -> {
            return new ServerBonus.BonusInstance(player, this);
         })).update();
      }

   }

   public class BonusInstance {
      public boolean active;
      public boolean discovered;
      private EntityPlayerMP player;
      private ServerBonus bonus;

      private BonusInstance(EntityPlayerMP player, ServerBonus bonus) {
         this.player = player;
         this.bonus = bonus;
      }

      public ServerBonus.BonusInstance update() {
         for(ABonusRequirement requirement : ServerBonus.this.bonusRequirements) {
            if (requirement.active(this.player) < requirement.required()) {
               this.update(false);
               return this;
            }
         }

         this.update(true);
         return this;
      }

      private void update(boolean activate) {
         if (activate) {
            if (!this.active) {
               ServerBonus.changed = true;
               this.active = true;
               if (this.bonus.discoveryMode != Bonus.MODE_GLOBALLY_HIDDEN && !this.discovered) {
                  this.discovered = true;
                  Network.WRAPPER.sendTo(new Network.DiscoverBonusPacket(this.bonus), this.player);
                  ServerBonus.saveDiscoveries(this.player);
               }

               for(ABonusElement element : ServerBonus.this.bonusElements) {
                  element.activate(this.player);
               }
            } else {
               for(ABonusElement element : ServerBonus.this.bonusElements) {
                  element.updateActive(this.player);
               }
            }
         } else if (this.active) {
            ServerBonus.changed = true;
            this.active = false;

            for(ABonusElement element : ServerBonus.this.bonusElements) {
               element.deactivate(this.player);
            }
         }

      }
   }
}
