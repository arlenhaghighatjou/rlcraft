package ichttt.mods.firstaid.common.damagesystem.debuff;

import it.unimi.dsi.fastutil.floats.Float2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;

public class ConstantDebuff extends AbstractDebuff {
   private int ticks = 0;
   private int activeMultiplier = 0;

   public ConstantDebuff(@Nonnull String potionName, @Nonnull Float2IntLinkedOpenHashMap map, @Nonnull BooleanSupplier isEnabled) {
      super(potionName, map, isEnabled);
   }

   private void syncMultiplier(float healthPerMax) {
      if (this.isEnabled.getAsBoolean()) {
         boolean found = false;
         ObjectBidirectionalIterator var3 = this.map.float2IntEntrySet().iterator();

         while(var3.hasNext()) {
            Entry entry = (Entry)var3.next();
            if (healthPerMax < entry.getFloatKey()) {
               this.ticks = 0;
               this.activeMultiplier = entry.getIntValue();
               found = true;
               break;
            }
         }

         if (!found) {
            this.activeMultiplier = 0;
         }

      }
   }

   public void handleDamageTaken(float damage, float healthPerMax, EntityPlayerMP player) {
      this.syncMultiplier(healthPerMax);
   }

   public void handleHealing(float healingDone, float healthPerMax, EntityPlayerMP player) {
      this.syncMultiplier(healthPerMax);
   }

   public void update(EntityPlayer player) {
      this.update(player, -1.0F);
   }

   public void update(EntityPlayer player, float healthPerMax) {
      if (this.isEnabled.getAsBoolean()) {
         if (this.activeMultiplier == 0) {
            this.ticks = 0;
         } else {
            if (this.ticks == 0) {
               if (healthPerMax != -1.0F) {
                  this.syncMultiplier(healthPerMax);
               }

               if (this.activeMultiplier != 0) {
                  player.addPotionEffect(new PotionEffect(this.effect, 169, this.activeMultiplier - 1, false, false));
               }
            }

            ++this.ticks;
            if (this.ticks >= 79) {
               this.ticks = 0;
            }
         }

      }
   }
}
