package c4.champions.common.capability;

import c4.champions.common.rank.Rank;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;

public class Championship implements IChampionship {
   private Map<String, NBTTagCompound> affixData = Maps.newHashMap();
   private Rank rank = null;
   private String name;

   public Championship() {
   }

   public Rank getRank() {
      return this.rank;
   }

   public void setRank(Rank rank) {
      this.rank = rank;
   }

   public ImmutableSet<String> getAffixes() {
      return ImmutableSet.copyOf(this.affixData.keySet());
   }

   public void setAffixData(String identifier, NBTTagCompound compound) {
      this.affixData.replace(identifier, compound);
   }

   public void setAffixes(Set<String> affixes) {
      Map<String, NBTTagCompound> newData = Maps.newHashMap();

      for(String s : affixes) {
         newData.put(s, new NBTTagCompound());
      }

      this.affixData = newData;
   }

   public ImmutableMap<String, NBTTagCompound> getAffixData() {
      return ImmutableMap.copyOf(this.affixData);
   }

   public void setAffixData(Map<String, NBTTagCompound> affixes) {
      this.affixData = affixes;
   }

   @Nullable
   public NBTTagCompound getAffixData(String identifier) {
      return (NBTTagCompound)this.affixData.get(identifier);
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }
}
