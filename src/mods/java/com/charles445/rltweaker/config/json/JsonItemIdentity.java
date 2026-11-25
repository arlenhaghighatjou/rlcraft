package com.charles445.rltweaker.config.json;

import com.charles445.rltweaker.RLTweaker;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class JsonItemIdentity {
   public String registryName;
   public int metadata;
   @Nullable
   public String nbt;
   @Nullable
   private NBTTagCompound nbtCompound;

   public JsonItemIdentity(String registryName, int metadata) {
      this(registryName, metadata, (String)null);
   }

   public JsonItemIdentity(String registryName, int metadata, String nbt) {
      this.metadata = metadata;
      this.nbt = nbt;
      this.tryPopulateCompound();
   }

   public void tryPopulateCompound() {
      if (this.nbtCompound == null) {
         if (this.nbt == null) {
            this.nbtCompound = null;
         } else {
            try {
               this.nbtCompound = JsonToNBT.getTagFromJson(this.nbt);
               if (this.nbtCompound == null) {
                  throw new Exception();
               }
            } catch (Exception var2) {
               RLTweaker.logger.error("Configuration failed with NBT string: " + this.nbt);
               this.nbtCompound = null;
               this.nbt = null;
            }
         }
      }

   }

   public boolean matches(ItemStack stack) {
      return stack.hasTagCompound() ? this.matches(stack.getItem().getRegistryName().toString(), stack.getMetadata(), stack.getTagCompound()) : this.matches(stack.getItem().getRegistryName().toString(), stack.getMetadata(), (NBTTagCompound)null);
   }

   public boolean matches(String registryName, int stackMetadata) {
      return this.matches(registryName, stackMetadata, (NBTTagCompound)null);
   }

   public boolean matches(JsonItemIdentity sentIdentity) {
      return this.matches(sentIdentity.registryName, sentIdentity.metadata, sentIdentity.nbtCompound);
   }

   public boolean matches(String registryName, int stackMetadata, @Nullable NBTTagCompound stackCompound) {
      if (!this.registryName.equals(registryName)) {
         return false;
      } else if (this.metadata != -1 && this.metadata != 32767 && this.metadata != stackMetadata) {
         return false;
      } else if (this.nbt != null && !this.nbt.isEmpty()) {
         this.tryPopulateCompound();
         return this.checkNestedCompound(this.nbtCompound, stackCompound);
      } else {
         return true;
      }
   }

   private boolean checkNestedCompound(NBTTagCompound selfCompound, NBTTagCompound stackCompound) {
      if (stackCompound == null) {
         return false;
      } else {
         Iterator var3 = selfCompound.getKeySet().iterator();

         while(true) {
            if (!var3.hasNext()) {
               return true;
            }

            String key = (String)var3.next();
            NBTBase a = selfCompound.getTag(key);
            NBTBase b = stackCompound.getTag(key);
            if (a != null) {
               if (a instanceof NBTTagCompound && b instanceof NBTTagCompound) {
                  if (!this.checkNestedCompound((NBTTagCompound)a, (NBTTagCompound)b)) {
                     break;
                  }
               } else if (!a.equals(b)) {
                  return false;
               }
            } else if (b != null) {
               return false;
            }
         }

         return false;
      }
   }
}
