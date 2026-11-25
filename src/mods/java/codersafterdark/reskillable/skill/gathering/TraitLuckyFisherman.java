package codersafterdark.reskillable.skill.gathering;

import codersafterdark.reskillable.api.unlockable.Trait;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class TraitLuckyFisherman extends Trait {
   public TraitLuckyFisherman() {
      super(new ResourceLocation("reskillable", "lucky_fisherman"), 3, 2, new ResourceLocation("reskillable", "gathering"), 6, "reskillable:gathering|12", "reskillable:magic|4");
   }

   public void onPlayerTick(PlayerTickEvent event) {
      if (event.player.fishEntity != null) {
         event.player.addPotionEffect(new PotionEffect(MobEffects.LUCK, 10, 0, true, true));
      }

   }
}
