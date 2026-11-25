package codersafterdark.reskillable.api.unlockable;

import codersafterdark.reskillable.Reskillable;
import codersafterdark.reskillable.api.ReskillableAPI;
import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.api.skill.Skill;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import org.apache.logging.log4j.Level;

public abstract class Unlockable extends Impl<Unlockable> implements Comparable<Unlockable> {
   private final String name;
   protected UnlockableConfig unlockableConfig;
   private ResourceLocation icon;
   private Skill parentSkill;

   public Unlockable(ResourceLocation name, int x, int y, ResourceLocation skillName, int cost, String... defaultRequirements) {
      this.name = name.toString().replace(":", ".");
      this.setRegistryName(name);
      this.setIcon(new ResourceLocation(name.getResourceDomain(), "textures/unlockables/" + name.getResourcePath() + ".png"));
      this.unlockableConfig = ReskillableAPI.getInstance().getTraitConfig(name, x, y, cost, defaultRequirements);
      this.setParentSkill(skillName);
   }

   @Nonnull
   public Skill getParentSkill() {
      return this.parentSkill;
   }

   protected void setParentSkill(ResourceLocation skillName) {
      if (this.parentSkill != null) {
         if (skillName != null && skillName.equals(this.parentSkill.getRegistryName())) {
            return;
         }

         this.parentSkill.getUnlockables().remove(this);
      }

      this.parentSkill = (Skill)Objects.requireNonNull(ReskillableRegistries.SKILLS.getValue(skillName));
      if (this.isEnabled()) {
         if (this.parentSkill.isEnabled()) {
            this.parentSkill.addUnlockable(this);
         } else {
            Reskillable.logger.log(Level.ERROR, this.getName() + " is enabled but the parent skill: " + this.parentSkill.getName() + " is disabled. Disabling: " + this.getName());
            this.unlockableConfig.setEnabled(false);
         }
      }

   }

   public RequirementHolder getRequirements() {
      return this.unlockableConfig.getRequirementHolder();
   }

   public String getKey() {
      return this.name;
   }

   public String getName() {
      return (new TextComponentTranslation("reskillable.unlock." + this.getKey(), new Object[0])).getUnformattedComponentText();
   }

   public String getDescription() {
      return (new TextComponentTranslation("reskillable.unlock." + this.getKey() + ".desc", new Object[0])).getUnformattedComponentText();
   }

   public ResourceLocation getIcon() {
      return this.icon;
   }

   protected void setIcon(ResourceLocation newIcon) {
      this.icon = newIcon;
   }

   public void onUnlock(EntityPlayer player) {
   }

   public void onLock(EntityPlayer player) {
   }

   public boolean hasSpikes() {
      return false;
   }

   public boolean isEnabled() {
      return this.unlockableConfig.isEnabled();
   }

   public int compareTo(@Nonnull Unlockable o) {
      int skillCmp = this.getParentSkill().compareTo(o.getParentSkill());
      return skillCmp == 0 ? this.getName().compareTo(o.getName()) : skillCmp;
   }

   public int getCost() {
      return this.unlockableConfig.getCost();
   }

   public int getX() {
      return this.unlockableConfig.getX();
   }

   public int getY() {
      return this.unlockableConfig.getY();
   }

   public final UnlockableConfig getUnlockableConfig() {
      return this.unlockableConfig;
   }
}
