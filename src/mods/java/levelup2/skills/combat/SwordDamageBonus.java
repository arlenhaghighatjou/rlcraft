package levelup2.skills.combat;

import levelup2.config.LevelUpConfig;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SwordDamageBonus extends BaseSkill {
   public SwordDamageBonus() {
   }

   public String getSkillName() {
      return "levelup:sworddamage";
   }

   public byte getSkillType() {
      return 2;
   }

   public boolean hasSubscription() {
      return true;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.GOLDEN_SWORD);
   }

   @SubscribeEvent
   public void onHurting(LivingHurtEvent evt) {
      if (this.isActive()) {
         DamageSource source = evt.getSource();
         float amount = evt.getAmount();
         if (source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)source.getTrueSource();
            int skill = SkillRegistry.getSkillLevel(player, this.getSkillName());
            if (skill > 0 && !(source instanceof EntityDamageSourceIndirect)) {
               if (!player.getHeldItemMainhand().isEmpty()) {
                  amount = amount * (1.0F + (float)skill / 20.0F);
                  if (LevelUpConfig.damageScaling && !(evt.getEntityLiving() instanceof EntityPlayer) && evt.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue() > 20.0D) {
                     double health = evt.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
                     float skillOutput = (float)skill / 40.0F;
                     amount = (float)((double)amount + Math.min(health * (double)skillOutput, health * 0.375D));
                  }

                  evt.setAmount(amount);
               }
            } else {
               skill = SkillRegistry.getSkillLevel(player, "levelup:arrowspeed");
               String src = source.getDamageType();
               if (skill > 0 && src.equals("arrow")) {
                  amount = amount * (1.0F + (float)skill / 20.0F);
                  if (LevelUpConfig.damageScaling && !(evt.getEntityLiving() instanceof EntityPlayer) && evt.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue() > 20.0D) {
                     double health = evt.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
                     float skillOutput = (float)skill / 40.0F;
                     amount = (float)((double)amount + Math.min(health * (double)skillOutput, health * 0.375D));
                     evt.setAmount(amount);
                  }
               }
            }
         }

      }
   }
}
