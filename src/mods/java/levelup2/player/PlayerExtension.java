package levelup2.player;

import java.util.HashMap;
import java.util.Map;
import levelup2.api.IPlayerSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.nbt.NBTTagCompound;

public final class PlayerExtension implements IPlayerClass {
   private Map<String, Integer> skillMap = new HashMap();

   public PlayerExtension() {
      for(IPlayerSkill skill : SkillRegistry.getSkillRegistry()) {
         this.skillMap.put(skill.getSkillName(), Integer.valueOf(0));
      }

   }

   public NBTTagCompound saveNBTData(NBTTagCompound tag) {
      NBTTagCompound combat = new NBTTagCompound();
      NBTTagCompound crafting = new NBTTagCompound();
      NBTTagCompound mining = new NBTTagCompound();

      for(String skillName : this.skillMap.keySet()) {
         IPlayerSkill skill = this.getSkillFromName(skillName);
         byte skillType = skill.getSkillType();
         switch(skillType) {
         case 1:
            crafting.setInteger(skill.getSkillName(), ((Integer)this.skillMap.get(skillName)).intValue());
            break;
         case 2:
            combat.setInteger(skill.getSkillName(), ((Integer)this.skillMap.get(skillName)).intValue());
            break;
         default:
            mining.setInteger(skill.getSkillName(), ((Integer)this.skillMap.get(skillName)).intValue());
         }
      }

      tag.setTag("Combat", combat);
      tag.setTag("Crafting", crafting);
      tag.setTag("Mining", mining);
      return tag;
   }

   public void loadNBTData(NBTTagCompound tag) {
      for(String skill : this.skillMap.keySet()) {
         IPlayerSkill playerSkill = this.getSkillFromName(skill);
         byte skillType = playerSkill.getSkillType();
         String skillName = skillType == 0 ? "Mining" : (skillType == 1 ? "Crafting" : "Combat");
         NBTTagCompound skillTag = tag.getCompoundTag(skillName);
         this.setSkillLevel(skill, skillTag.getInteger(playerSkill.getSkillName()));
      }

   }

   public IPlayerSkill getSkillFromName(String name) {
      return SkillRegistry.getSkillFromName(name);
   }

   public void addToSkill(String name, int increase) {
      this.setSkillLevel(name, ((Integer)this.skillMap.get(name)).intValue() + increase);
   }

   public int getSkillLevel(String name) {
      return ((Integer)this.skillMap.get(name)).intValue();
   }

   public void setSkillLevel(String name, int level) {
      this.skillMap.put(name, Integer.valueOf(level));
   }

   public void setPlayerData(String[] names, int[] data) {
      for(int i = 0; i < names.length && i < data.length; ++i) {
         this.setSkillLevel(names[i], data[i]);
      }

   }

   public boolean hasClass() {
      return this.getSkillLevel("levelup:mining_bonus") > 0 || this.getSkillLevel("levelup:craft_bonus") > 0 || this.getSkillLevel("levelup:combat_bonus") > 0;
   }

   public byte getSpecialization() {
      if (this.getSkillLevel("levelup:mining_bonus") > 0) {
         return 0;
      } else if (this.getSkillLevel("levelup:craft_bonus") > 0) {
         return 1;
      } else {
         return (byte)(this.getSkillLevel("levelup:combat_bonus") > 0 ? 2 : -1);
      }
   }

   public void setSpecialization(byte spec) {
      if (spec > -1) {
         switch(spec) {
         case 0:
            this.setSkillLevel("levelup:mining_bonus", 1);
            this.setSkillLevel("levelup:craft_bonus", 0);
            this.setSkillLevel("levelup:combat_bonus", 0);
            break;
         case 1:
            this.setSkillLevel("levelup:mining_bonus", 0);
            this.setSkillLevel("levelup:craft_bonus", 1);
            this.setSkillLevel("levelup:combat_bonus", 0);
            break;
         case 2:
            this.setSkillLevel("levelup:mining_bonus", 0);
            this.setSkillLevel("levelup:craft_bonus", 0);
            this.setSkillLevel("levelup:combat_bonus", 1);
         }
      }

   }

   public Map<String, Integer> getSkills() {
      return this.skillMap;
   }
}
