package com.bloodnbonesgaming.lib;

import com.bloodnbonesgaming.lib.network.MessageConsumer;
import com.bloodnbonesgaming.lib.network.NetworkManager;
import com.bloodnbonesgaming.lib.util.LogHelper;
import com.bloodnbonesgaming.lib.util.ModManager;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.Mod.CustomProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public abstract class BNBGamingMod {
   protected LogHelper log;
   protected ModContainer mod;

   public BNBGamingMod() {
      Class<? extends BNBGamingMod> clazz = this.getClass();
      if (!clazz.isAnnotationPresent(Mod.class)) {
         FMLLog.log("bnbgaminglib", Level.ERROR, "Subclass of BNBGamingMod does not have @Mod annotation! Things aren't going to work!", new Object[0]);
      } else {
         Mod ann = (Mod)clazz.getAnnotation(Mod.class);
         this.log = new LogHelper(ann.modid());
         this.mod = (ModContainer)Loader.instance().getIndexedModList().get(ann.modid());
         if (this.mod == null) {
            this.log.warn("Unable to retrieve ModContainer from FML! Things aren't going to work!");
         }

         if (!ModManager.registerMod(ann.modid(), this)) {
            this.log.warn("Failed to register with ModManager! Is there an ID conflict?");
         }

         CustomProperty[] meta = ann.customProperties();
         if (meta != null) {
            for(CustomProperty prop : meta) {
               String var8 = prop.k().toLowerCase();
               byte var9 = -1;
               switch(var8.hashCode()) {
               case -1936093350:
                  if (var8.equals("servermessageconsumer")) {
                     var9 = 2;
                  }
                  break;
               case 166890749:
                  if (var8.equals("messageconsumer")) {
                     var9 = 0;
                  }
                  break;
               case 1759914450:
                  if (var8.equals("clientmessageconsumer")) {
                     var9 = 1;
                  }
               }

               switch(var9) {
               case 0:
                  try {
                     Class<? extends MessageConsumer> consumerClass = Class.forName(prop.v());
                     MessageConsumer consumer = (MessageConsumer)consumerClass.newInstance();
                     NetworkManager.INSTANCE.registerConsumer(Side.SERVER, consumer);
                     NetworkManager.INSTANCE.registerConsumer(Side.CLIENT, consumer);
                  } catch (Exception var14) {
                     BNBGamingLib.libInstance.log.error(String.format("Failed to read messageConsumer property of submod %s!", ann.modid()));
                  }
                  break;
               case 1:
                  try {
                     Class<? extends MessageConsumer> consumerClass = Class.forName(prop.v());
                     MessageConsumer consumer = (MessageConsumer)consumerClass.newInstance();
                     NetworkManager.INSTANCE.registerConsumer(Side.CLIENT, consumer);
                  } catch (Exception var13) {
                     BNBGamingLib.libInstance.log.error(String.format("Failed to read clientMessageConsumer property of submod %s!", ann.modid()));
                  }
                  break;
               case 2:
                  try {
                     Class<? extends MessageConsumer> consumerClass = Class.forName(prop.v());
                     MessageConsumer consumer = (MessageConsumer)consumerClass.newInstance();
                     NetworkManager.INSTANCE.registerConsumer(Side.SERVER, consumer);
                  } catch (Exception var12) {
                     BNBGamingLib.libInstance.log.error(String.format("Failed to read serverMessageConsumer property of submod %s!", ann.modid()));
                  }
               }
            }
         }

      }
   }

   public LogHelper getLog() {
      return this.log;
   }

   public ModContainer getMod() {
      return this.mod;
   }

   public void setLog(LogHelper log) {
      this.log = log;
   }

   public void setMod(ModContainer mod) {
      this.mod = mod;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BNBGamingMod)) {
         return false;
      } else {
         BNBGamingMod other = (BNBGamingMod)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$log = this.getLog();
            Object other$log = other.getLog();
            if (this$log == null) {
               if (other$log != null) {
                  return false;
               }
            } else if (!this$log.equals(other$log)) {
               return false;
            }

            Object this$mod = this.getMod();
            Object other$mod = other.getMod();
            if (this$mod == null) {
               if (other$mod != null) {
                  return false;
               }
            } else if (!this$mod.equals(other$mod)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof BNBGamingMod;
   }

   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $log = this.getLog();
      result = result * 59 + ($log == null ? 43 : $log.hashCode());
      Object $mod = this.getMod();
      result = result * 59 + ($mod == null ? 43 : $mod.hashCode());
      return result;
   }

   public String toString() {
      return "BNBGamingMod(log=" + this.getLog() + ", mod=" + this.getMod() + ")";
   }
}
