package xzeroair.trinkets.util.compat.elenaidodge;

import com.elenai.elenaidodge.api.DodgeEvent.ServerDodgeEvent;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.events.EventBaseHandler;
import xzeroair.trinkets.traits.abilities.AbilityDodge;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.TrinketsConfig;

public class ElenaiDodgeCompat extends EventBaseHandler {
   public ElenaiDodgeCompat() {
   }

   @SubscribeEvent
   public void DodgeEvent(ServerDodgeEvent event) {
      if (TrinketsConfig.compat.elenaiDodge) {
         if (event.getCooldown() <= 0) {
            EntityPlayer player = event.getPlayer();
            Capabilities.getEntityProperties(player, (prop) -> {
               try {
                  IAbilityInterface ability = prop.getAbilityHandler().getAbility("xat:dodging");
                  if (ability instanceof AbilityDodge) {
                     Capabilities.getMagicStats(player, (magic) -> {
                        float cost = TrinketsConfig.SERVER.Items.ARCING_ORB.dodgeCost;
                        if (magic.spendMana(cost)) {
                           AbilityDodge dodge = (AbilityDodge)ability;
                           dodge.dodge(player);
                        }

                     });
                  }
               } catch (Exception var3) {
                  var3.printStackTrace();
               }

            });
         }
      }
   }
}
