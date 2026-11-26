package levelup2.skills.combat;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class NaturalArmorBonus extends BaseSkill {
   public NaturalArmorBonus() {
   }

   public String getSkillName() {
      return "levelup:naturalarmor";
   }

   public byte getSkillType() {
      return 2;
   }

   public boolean hasSubscription() {
      return true;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.IRON_CHESTPLATE);
   }

   public void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, this::onDamageTaken);
   }

   public void onDamageTaken(LivingHurtEvent evt) {
      if (this.isActive()) {
         if (evt.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)evt.getEntityLiving();
            int skill = SkillRegistry.getSkillLevel(player, this.getSkillName());
            if (skill > 0 && !evt.getSource().isUnblockable()) {
               float amount = evt.getAmount() * (1.0F - (float)skill / 20.0F);
               evt.setAmount(amount);
            }
         }

      }
   }
}
