package codersafterdark.compatskills.common.compats.minecraft.item;

import codersafterdark.reskillable.api.data.GenericNBTLockKey;
import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntPredicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictRequirement extends Requirement {
   private final NBTTagCompound tag;
   private final int oreEntry;

   public OreDictRequirement(String oreDictEntry, NBTTagCompound tag) {
      this.oreEntry = OreDictionary.getOreID(oreDictEntry);
      this.tag = tag;
      String name = oreDictEntry;
      if (tag != null) {
         name = oreDictEntry + " With NBT Tag: " + tag;
      }

      this.tooltip = TextFormatting.GRAY + " - " + (new TextComponentTranslation("compatskills.requirements.format.ore_dict", new Object[]{"%s", name})).getUnformattedComponentText();
   }

   public static OreDictRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No Ore dictionary entry given.");
      } else {
         String[] inputInfo = input.split("\\|");
         String ore = inputInfo[0];
         if (!OreDictionary.doesOreNameExist(ore)) {
            throw new RequirementException("Ore dictionary entry '" + ore + "' not found.");
         } else {
            NBTTagCompound nbt = null;
            if (inputInfo.length > 1) {
               String nbtString = input.substring(ore.length() + 1).trim();

               try {
                  nbt = JsonToNBT.getTagFromJson(nbtString);
               } catch (NBTException var6) {
                  throw new RequirementException("Invalid NBT JSON '" + nbtString + "'.");
               }
            }

            return new OreDictRequirement(ore, nbt);
         }
      }
   }

   public boolean achievedByPlayer(EntityPlayer player) {
      return this.hasOreDict(player.getHeldItemMainhand()) || this.hasOreDict(player.getHeldItemOffhand());
   }

   private boolean hasOreDict(ItemStack stack) {
      return !stack.isEmpty() && !Arrays.stream(OreDictionary.getOreIDs(stack)).noneMatch((oreID) -> {
         return oreID == this.oreEntry;
      }) ? (new GenericNBTLockKey(stack.getTagCompound())).fuzzyEquals(new GenericNBTLockKey(this.tag)) : false;
   }

   public RequirementComparision matches(Requirement o) {
      if (o instanceof OreDictRequirement) {
         OreDictRequirement other = (OreDictRequirement)o;
         if (this.oreEntry == other.oreEntry) {
            GenericNBTLockKey key = new GenericNBTLockKey(this.tag);
            GenericNBTLockKey otherKey = new GenericNBTLockKey(other.tag);
            if (key.fuzzyEquals(otherKey)) {
               return RequirementComparision.GREATER_THAN;
            }

            if (otherKey.fuzzyEquals(key)) {
               return RequirementComparision.LESS_THAN;
            }
         }
      }

      return RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof OreDictRequirement)) {
         return false;
      } else {
         OreDictRequirement oreReq = (OreDictRequirement)o;
         if (this.tag == null) {
            return this.oreEntry == oreReq.oreEntry && oreReq.tag == null;
         } else {
            return this.oreEntry == oreReq.oreEntry && this.tag.equals(oreReq.tag);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.oreEntry, this.tag});
   }
}
