package levelup2.skills.combat;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.skills.BaseSkill;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;

public class StealthBonus extends BaseSkill {
   public StealthBonus() {
   }

   public String getSkillName() {
      return "levelup:stealth";
   }

   public byte getSkillType() {
      return 2;
   }

   public boolean hasSubscription() {
      return true;
   }

   public ItemStack getRepresentativeStack() {
      ItemStack stack = new ItemStack(Items.POTIONITEM);
      PotionUtils.addPotionToItemStack(stack, PotionType.getPotionTypeForName("invisibility"));
      return stack;
   }

   public void register(EventBus bus) {
      bus.register(LivingSetAttackTargetEvent.class, this::onTargetSet);
   }

   public void onTargetSet(LivingSetAttackTargetEvent evt) {
      if (this.isActive()) {
         if (evt.getTarget() instanceof EntityPlayer && evt.getEntityLiving() instanceof EntityMob && evt.getTarget().isSneaking() && !StealthLib.entityHasVisionOf(evt.getEntityLiving(), (EntityPlayer)evt.getTarget()) && evt.getEntityLiving().getRevengeTimer() != ((EntityMob)evt.getEntityLiving()).ticksExisted) {
            ((EntityMob)evt.getEntityLiving()).setAttackTarget((EntityLivingBase)null);
         }

      }
   }
}
