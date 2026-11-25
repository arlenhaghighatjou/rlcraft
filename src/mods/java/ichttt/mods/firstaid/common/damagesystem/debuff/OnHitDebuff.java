package ichttt.mods.firstaid.common.damagesystem.debuff;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.common.network.MessagePlayHurtSound;
import it.unimi.dsi.fastutil.floats.Float2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;

public class OnHitDebuff extends AbstractDebuff {
   @Nullable
   private final SoundEvent sound;

   public OnHitDebuff(@Nonnull String potionName, @Nonnull Float2IntLinkedOpenHashMap map, @Nonnull BooleanSupplier isEnabled, @Nullable SoundEvent sound) {
      super(potionName, map, isEnabled);
      this.sound = sound;
   }

   public void handleDamageTaken(float damage, float healthPerMax, EntityPlayerMP player) {
      if (this.isEnabled.getAsBoolean()) {
         int value = -1;
         ObjectBidirectionalIterator var5 = this.map.float2IntEntrySet().iterator();

         while(var5.hasNext()) {
            Entry entry = (Entry)var5.next();
            if (damage >= entry.getFloatKey()) {
               value = Math.max(value, entry.getIntValue());
               player.addPotionEffect(new PotionEffect(this.effect, entry.getIntValue(), 0, false, false));
            }
         }

         if (value != -1 && this.sound != null) {
            FirstAid.NETWORKING.sendTo(new MessagePlayHurtSound(this.sound, value), player);
         }

      }
   }

   public void handleHealing(float healingDone, float healthPerMax, EntityPlayerMP player) {
   }
}
