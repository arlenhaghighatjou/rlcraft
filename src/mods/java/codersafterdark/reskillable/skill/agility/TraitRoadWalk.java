package codersafterdark.reskillable.skill.agility;

import codersafterdark.reskillable.api.unlockable.Trait;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class TraitRoadWalk extends Trait {
   public TraitRoadWalk() {
      super(new ResourceLocation("reskillable", "roadwalk"), 1, 1, new ResourceLocation("reskillable", "agility"), 6, "reskillable:agility|12", "reskillable:building|8");
   }

   public void onPlayerTick(PlayerTickEvent event) {
      if (event.player.world.getBlockState(event.player.getPosition().down()).getBlock() == Blocks.GRASS_PATH && event.player.onGround && event.player.moveForward > 0.0F) {
         event.player.moveRelative(0.0F, 0.0F, 1.0F, 0.05F);
      }

   }
}
