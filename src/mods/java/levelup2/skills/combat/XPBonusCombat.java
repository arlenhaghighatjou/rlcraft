package levelup2.skills.combat;

import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XPBonusCombat extends BaseSkill {
   public XPBonusCombat() {
   }

   public boolean hasSubscription() {
      return true;
   }

   public String getSkillName() {
      return "levelup:combat_bonus";
   }

   public int getLevelCost(int currentLevel) {
      return -1;
   }

   public byte getSkillType() {
      return 2;
   }

   public String[] getPrerequisites() {
      return new String[0];
   }

   @SubscribeEvent
   public void getCombatBonus(LivingDeathEvent evt) {
      if (evt.getEntityLiving() instanceof EntityMob && evt.getSource().getTrueSource() instanceof EntityPlayer && SkillRegistry.getSkillLevel((EntityPlayer)evt.getSource().getTrueSource(), this.getSkillName()) > 0) {
         int deathXP = (int)evt.getEntityLiving().getMaxHealth();
         SkillRegistry.addExperience((EntityPlayer)evt.getSource().getTrueSource(), deathXP / 10);
      }

   }

   public int getSkillRow() {
      return 0;
   }

   public int getSkillColumn() {
      return 0;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.DIAMOND_SWORD);
   }

   public boolean isMaxLevel(int level) {
      return true;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean isEnabled() {
      return true;
   }

   public boolean hasExternalJson() {
      return false;
   }

   public boolean isActive() {
      return true;
   }
}
