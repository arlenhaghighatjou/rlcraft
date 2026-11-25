package c4.champions.common.capability;

import c4.champions.common.rank.Rank;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;

public interface IChampionship {
   Rank getRank();

   void setRank(Rank var1);

   ImmutableSet<String> getAffixes();

   NBTTagCompound getAffixData(String var1);

   void setAffixData(String var1, NBTTagCompound var2);

   void setAffixes(Set<String> var1);

   ImmutableMap<String, NBTTagCompound> getAffixData();

   void setAffixData(Map<String, NBTTagCompound> var1);

   void setName(String var1);

   String getName();
}
