package levelup2.skills.mining;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import levelup2.util.Library;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class SprintSpeedBonus extends BaseSkill {
   public SprintSpeedBonus() {
   }

   public String getSkillName() {
      return "levelup:sprintspeed";
   }

   public boolean hasSubscription() {
      return true;
   }

   public byte getSkillType() {
      return 0;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.RABBIT_FOOT);
   }

   public void register(EventBus bus) {
      bus.register(PlayerTickEvent.class, this::onPlayerSprint);
   }

   public void onPlayerSprint(PlayerTickEvent evt) {
      if (this.isActive()) {
         if (evt.phase == Phase.START) {
            int skill = SkillRegistry.getSkillLevel(evt.player, this.getSkillName());
            if (skill > 0) {
               IAttributeInstance attrib = evt.player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
               AttributeModifier mod = new AttributeModifier(Library.speedID, "SprintingSkillSpeed", (double)((float)skill / 20.0F), 2);
               if (evt.player.isSprinting()) {
                  if (attrib.getModifier(Library.speedID) == null) {
                     attrib.applyModifier(mod);
                  }
               } else if (attrib.getModifier(Library.speedID) != null) {
                  attrib.removeModifier(mod);
               }
            }
         }

      }
   }
}
