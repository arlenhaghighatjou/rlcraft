package levelup2.skills.combat;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class SwordCritBonus extends BaseSkill {
   public SwordCritBonus() {
   }

   public String getSkillName() {
      return "levelup:swordcrit";
   }

   public byte getSkillType() {
      return 2;
   }

   public boolean hasSubscription() {
      return true;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.IRON_SWORD);
   }

   public void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, this::onHurting);
   }

   public void onHurting(LivingHurtEvent evt) {
      if (this.isActive()) {
         DamageSource source = evt.getSource();
         float amount = evt.getAmount();
         if (source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)source.getTrueSource();
            int skill = SkillRegistry.getSkillLevel(player, this.getSkillName());
            if (skill > 0 && !(source instanceof EntityDamageSourceIndirect) && !player.getHeldItemMainhand().isEmpty() && player.getRNG().nextDouble() <= (double)skill / 20.0D) {
               evt.setAmount(amount * 2.0F);
            }
         }

      }
   }
}
