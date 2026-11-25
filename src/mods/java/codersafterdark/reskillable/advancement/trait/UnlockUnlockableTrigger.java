package codersafterdark.reskillable.advancement.trait;

import codersafterdark.reskillable.advancement.CriterionTrigger;
import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.function.Function;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class UnlockUnlockableTrigger extends CriterionTrigger<UnlockUnlockableListeners, UnlockUnlockableCriterionInstance> {
   public UnlockUnlockableTrigger() {
      super(new ResourceLocation("reskillable", "unlockable"), UnlockUnlockableListeners::new);
   }

   public void trigger(EntityPlayerMP entityPlayer, Unlockable unlockable) {
      UnlockUnlockableListeners listeners = (UnlockUnlockableListeners)this.getListeners(entityPlayer.getAdvancements());
      if (listeners != null) {
         listeners.trigger(unlockable);
      }

   }

   public UnlockUnlockableCriterionInstance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      if (json.has("unlockable_name")) {
         String unlockableName = json.get("unlockable_name").getAsString();
         Unlockable unlockable = (Unlockable)ReskillableRegistries.UNLOCKABLES.getValue(new ResourceLocation(unlockableName));
         if (unlockable != null) {
            return new UnlockUnlockableCriterionInstance(unlockable);
         } else {
            throw new JsonParseException("No Unlockable found for name " + unlockableName);
         }
      } else {
         throw new JsonParseException("Field 'unlockable_name' not found");
      }
   }
}
