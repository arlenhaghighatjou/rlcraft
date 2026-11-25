package levelup2.capability;

import levelup2.skills.SkillRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityBrewingStand;

public class CapabilityBrewingStand extends PlayerCapability.CapabilityProcessorDefault {
   public CapabilityBrewingStand(TileEntityBrewingStand stand) {
      super(stand);
   }

   public void extraProcessing(EntityPlayer player) {
      if (SkillRegistry.getSkillFromName("levelup:brewingspeed").isActive() && SkillRegistry.getSkillFromName("levelup:brewingspeed").isEnabled()) {
         if (this.tile != null && this.tile instanceof TileEntityBrewingStand) {
            TileEntityBrewingStand stand = (TileEntityBrewingStand)this.tile;
            if (stand.getField(0) > 0) {
               int bonus = SkillRegistry.getSkillLevel(player, "levelup:brewingspeed");
               if (bonus > 0) {
                  int time = player.getRNG().nextInt(bonus + 1);
                  if (time > 0 && stand.getField(0) - time > 0) {
                     stand.setField(0, stand.getField(0) - time);
                  }
               }
            }
         }

      }
   }
}
