package codersafterdark.reskillable.api.skill;

import codersafterdark.reskillable.api.ReskillableAPI;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public abstract class Skill extends Impl<Skill> implements Comparable<Skill> {
   private final Map<Integer, ResourceLocation> customSprites = new HashMap();
   private final List<Unlockable> unlockables = new ArrayList();
   private final ResourceLocation spriteLocation;
   private final String name;
   protected ResourceLocation background;
   protected SkillConfig skillConfig;
   private boolean hidden;

   public Skill(ResourceLocation name, ResourceLocation background) {
      this.name = name.toString().replace(":", ".");
      this.background = background;
      this.spriteLocation = new ResourceLocation(name.getResourceDomain(), "textures/skills/" + name.getResourcePath() + ".png");
      this.setRegistryName(name);
      this.skillConfig = ReskillableAPI.getInstance().getSkillConfig(name);
   }

   public void addUnlockable(Unlockable unlockable) {
      this.unlockables.add(unlockable);
   }

   public List<Unlockable> getUnlockables() {
      return this.unlockables;
   }

   public String getKey() {
      return this.name;
   }

   public String getName() {
      return (new TextComponentTranslation("reskillable.skill." + this.getKey(), new Object[0])).getUnformattedComponentText();
   }

   public ResourceLocation getBackground() {
      return this.background;
   }

   public void setBackground(ResourceLocation resourceLocation) {
      this.background = resourceLocation;
   }

   public int getCap() {
      return this.skillConfig.getLevelCap();
   }

   public boolean isEnabled() {
      return this.skillConfig.isEnabled();
   }

   public boolean hasLevelButton() {
      return this.skillConfig.hasLevelButton();
   }

   public ResourceLocation getSpriteLocation() {
      return this.spriteLocation;
   }

   public Pair<Integer, Integer> getSpriteFromRank(int rank) {
      return new MutablePair(Math.min(rank / 2, 3) * 16, Integer.valueOf(0));
   }

   public void setCustomSprite(int rank, ResourceLocation location) {
      this.customSprites.put(Integer.valueOf(rank), location);
   }

   public void removeCustomSprite(int rank) {
      this.customSprites.remove(Integer.valueOf(rank));
   }

   public ResourceLocation getSpriteLocation(int rank) {
      if (this.customSprites.containsKey(Integer.valueOf(rank))) {
         return (ResourceLocation)this.customSprites.get(Integer.valueOf(rank));
      } else {
         for(int i = rank - 1; i >= 0; --i) {
            if (this.customSprites.containsKey(Integer.valueOf(i))) {
               return (ResourceLocation)this.customSprites.get(Integer.valueOf(i));
            }
         }

         return null;
      }
   }

   public boolean hasCustomSprites() {
      return !this.customSprites.isEmpty();
   }

   public int compareTo(@Nonnull Skill o) {
      return o.getName().compareTo(this.getName());
   }

   public int getSkillPointInterval() {
      return this.skillConfig.getSkillPointInterval();
   }

   public int getLevelUpCost(int level) {
      int cost = this.skillConfig.getLevelStaggering().entrySet().stream().filter((entry) -> {
         return ((Integer)entry.getKey()).intValue() < level + 1;
      }).mapToInt(Entry::getValue).sum() + this.skillConfig.getBaseLevelCost();
      return cost < 0 ? 0 : cost;
   }

   public final SkillConfig getSkillConfig() {
      return this.skillConfig;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void setHidden(boolean hidden) {
      this.hidden = hidden;
   }

   public int getRank(int level) {
      return 8 * level / this.getCap();
   }
}
