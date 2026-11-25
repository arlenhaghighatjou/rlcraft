package com.bloodnbonesgaming.lib;

import com.bloodnbonesgaming.lib.util.LogHelper;
import com.bloodnbonesgaming.lib.util.ModManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public abstract class BNBGamingMod {
   protected LogHelper log;
   protected ModContainer mod;

   protected BNBGamingMod(String modId) {
      this.log = new LogHelper(modId);
      this.mod = Loader.instance().getIndexedModList().get(modId);
      if (this.mod == null) {
         throw new IllegalStateException("Missing BNBGaming mod container " + modId);
      }
      if (!ModManager.registerMod(modId, this)) {
         throw new IllegalStateException("Duplicate BNBGaming mod ID " + modId);
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
