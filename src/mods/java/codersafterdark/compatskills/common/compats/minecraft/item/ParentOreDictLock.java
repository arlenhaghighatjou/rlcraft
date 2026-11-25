package codersafterdark.compatskills.common.compats.minecraft.item;

import codersafterdark.reskillable.api.data.ParentLockKey;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class ParentOreDictLock implements ParentLockKey {
   private final NBTTagCompound tag;
   private final int[] oreIDs;

   public ParentOreDictLock(ItemStack stack) {
      this(OreDictionary.getOreIDs(stack), stack.getTagCompound());
   }

   public ParentOreDictLock(int[] oreIDs) {
      this(oreIDs, (NBTTagCompound)null);
   }

   public ParentOreDictLock(int[] oreIDs, NBTTagCompound tag) {
      this.oreIDs = oreIDs;
      this.tag = tag;
   }

   public RequirementHolder getSubRequirements() {
      List<RequirementHolder> holders = new ArrayList();

      for(int id : this.oreIDs) {
         RequirementHolder holder = LevelLockHandler.getLockByFuzzyKey(new OreDictLock(OreDictionary.getOreName(id), this.tag));
         if (!holder.equals(LevelLockHandler.EMPTY_LOCK)) {
            holders.add(holder);
         }
      }

      return holders.isEmpty() ? LevelLockHandler.EMPTY_LOCK : new RequirementHolder((RequirementHolder[])holders.toArray(new RequirementHolder[0]));
   }
}
