package levelup2.skills.combat;

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

public class StealthSpeed extends BaseSkill {
   public StealthSpeed() {
   }

   public String getSkillName() {
      return "levelup:stealthspeed";
   }

   public byte getSkillType() {
      return 2;
   }

   public boolean hasSubscription() {
      return true;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.LEATHER_BOOTS);
   }

   public void register(EventBus bus) {
      bus.register(PlayerTickEvent.class, this::onPlayerSneak);
   }

   public void onPlayerSneak(PlayerTickEvent evt) {
      if (this.isActive()) {
         if (evt.phase == Phase.START) {
            int skill = SkillRegistry.getSkillLevel(evt.player, this.getSkillName());
            if (skill > 0) {
               IAttributeInstance attrib = evt.player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
               AttributeModifier mod = new AttributeModifier(Library.sneakID, "SneakingSkillSpeed", (double)((float)skill / 10.0F), 2);
               if (evt.player.isSneaking()) {
                  if (attrib.getModifier(Library.sneakID) == null) {
                     attrib.applyModifier(mod);
                  }
               } else if (attrib.getModifier(Library.sneakID) != null) {
                  attrib.removeModifier(mod);
               }
            }
         }

      }
   }
}
