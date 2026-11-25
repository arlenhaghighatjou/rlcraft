package codersafterdark.compatskills.common.compats.minecraft.item;

import codersafterdark.reskillable.api.data.GenericNBTLockKey;
import codersafterdark.reskillable.api.data.ItemInfo;
import codersafterdark.reskillable.api.data.LockKey;
import codersafterdark.reskillable.api.data.ModLockKey;
import codersafterdark.reskillable.api.data.NBTLockKey;
import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ItemRequirement extends Requirement {
   private final NBTLockKey key;

   public ItemRequirement(NBTLockKey key) {
      this.key = key;
      String displayName = "";
      if (key instanceof ItemInfo) {
         ItemInfo info = (ItemInfo)key;
         ItemStack stack = new ItemStack(info.getItem(), 1, info.getMetadata());
         stack.setTagCompound(key.getTag());
         displayName = displayName + stack.getDisplayName();
      } else if (key instanceof ModLockKey) {
         displayName = displayName + "From Mod: " + ((ModLockKey)key).getModName();
      }

      if (key.getTag() != null) {
         displayName = displayName + " With NBT Tag: " + key.getTag().toString();
      }

      this.tooltip = TextFormatting.GRAY + " - " + (new TextComponentTranslation("compatskills.requirements.format.item", new Object[]{"%s", displayName.trim()})).getUnformattedComponentText();
   }

   public static ItemRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No Item given.");
      } else {
         String[] inputInfo = input.split("\\|");
         String type = inputInfo[0];
         NBTTagCompound nbt = null;
         if (inputInfo.length > 1) {
            String nbtString = input.substring(type.length() + 1).trim();

            try {
               nbt = JsonToNBT.getTagFromJson(nbtString);
            } catch (NBTException var10) {
               throw new RequirementException("Invalid NBT JSON '" + nbtString + "'.");
            }
         }

         type = type.trim();
         NBTLockKey key;
         if (type.isEmpty()) {
            if (nbt == null) {
               throw new RequirementException("Invalid Item Requirement format. No input data found.");
            }

            key = new GenericNBTLockKey(nbt);
         } else {
            String[] itemParts = type.split(":");
            if (itemParts.length == 1) {
               key = new ModLockKey(type, nbt);
            } else {
               int metadata = 0;
               if (itemParts.length > 2) {
                  String meta = itemParts[2];

                  try {
                     if (meta.equals("*")) {
                        metadata = 32767;
                     } else {
                        metadata = Integer.parseInt(meta);
                     }

                     type = itemParts[0] + ':' + itemParts[1];
                  } catch (NumberFormatException var9) {
                     ;
                  }
               }

               Item item = Item.getByNameOrId(type);
               if (item == null) {
                  throw new RequirementException("No Item found matching: '" + type + "'.");
               }

               key = new ItemInfo(item, metadata, nbt);
            }
         }

         return new ItemRequirement(key);
      }
   }

   public boolean achievedByPlayer(EntityPlayer player) {
      return this.itemMatches(player.getHeldItemMainhand()) || this.itemMatches(player.getHeldItemOffhand());
   }

   public RequirementComparision matches(Requirement o) {
      if (o instanceof ItemRequirement) {
         ItemRequirement other = (ItemRequirement)o;
         if (this.key.equals(other.key)) {
            return RequirementComparision.EQUAL_TO;
         }

         if (this.key instanceof ItemInfo) {
            if (other.key instanceof ItemInfo) {
               if (this.key.getNotFuzzy().equals(other.key.getNotFuzzy())) {
                  if (this.key.fuzzyEquals(other.key)) {
                     return RequirementComparision.GREATER_THAN;
                  }

                  if (other.key.fuzzyEquals(this.key)) {
                     return RequirementComparision.LESS_THAN;
                  }
               }
            } else if (other.key instanceof ModLockKey) {
               ResourceLocation registryName = ((ItemInfo)this.key).getItem().getRegistryName();
               if (registryName != null && registryName.getResourceDomain().equals(((ModLockKey)other.key).getModName()) && this.key.fuzzyEquals(other.key)) {
                  return RequirementComparision.GREATER_THAN;
               }
            } else if (other.key instanceof GenericNBTLockKey && this.key.fuzzyEquals(other.key)) {
               return RequirementComparision.GREATER_THAN;
            }
         } else if (this.key instanceof ModLockKey) {
            if (other.key instanceof ItemInfo) {
               ResourceLocation registryName = ((ItemInfo)other.key).getItem().getRegistryName();
               if (registryName != null && ((ModLockKey)this.key).getModName().equals(registryName.getResourceDomain()) && this.key.fuzzyEquals(other.key)) {
                  return RequirementComparision.LESS_THAN;
               }
            } else if (other.key instanceof ModLockKey) {
               if (this.key.getNotFuzzy().equals(other.key.getNotFuzzy())) {
                  if (this.key.fuzzyEquals(other.key)) {
                     return RequirementComparision.GREATER_THAN;
                  }

                  if (other.key.fuzzyEquals(this.key)) {
                     return RequirementComparision.LESS_THAN;
                  }
               }
            } else if (other.key instanceof GenericNBTLockKey && this.key.fuzzyEquals(other.key)) {
               return RequirementComparision.GREATER_THAN;
            }
         } else if (this.key instanceof GenericNBTLockKey) {
            if (other.key instanceof GenericNBTLockKey && this.key.fuzzyEquals(other.key)) {
               return RequirementComparision.GREATER_THAN;
            }

            if (other.key.fuzzyEquals(this.key)) {
               return RequirementComparision.LESS_THAN;
            }
         }
      }

      return RequirementComparision.NOT_EQUAL;
   }

   private boolean itemMatches(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         NBTLockKey fullKey = null;
         if (this.key instanceof ItemInfo) {
            fullKey = new ItemInfo(stack);
         } else if (this.key instanceof ModLockKey) {
            fullKey = new ModLockKey(stack);
         } else if (this.key instanceof GenericNBTLockKey) {
            fullKey = new GenericNBTLockKey(stack);
         }

         if (fullKey == null) {
            return false;
         } else {
            LockKey notFuzzy = this.key.getNotFuzzy();
            LockKey fullNotFuzzy = fullKey.getNotFuzzy();
            if (notFuzzy == null) {
               return fullNotFuzzy == null && fullKey.fuzzyEquals(this.key);
            } else {
               return notFuzzy.equals(fullNotFuzzy) && fullKey.fuzzyEquals(this.key);
            }
         }
      }
   }

   public boolean equals(Object o) {
      return o == this || o instanceof ItemRequirement && this.key.equals(((ItemRequirement)o).key);
   }

   public int hashCode() {
      return this.key.hashCode();
   }
}
