package rlmixins.handlers.bountifulbaubles;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import cursedflames.bountifulbaubles.baubleeffect.IFireResistance;
import cursedflames.bountifulbaubles.item.ItemShieldObsidian;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FireResistanceBaubleHandler {
   public FireResistanceBaubleHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onLivingHurt(LivingHurtEvent event) {
      if (event.getEntityLiving() instanceof EntityPlayer && event.getSource().isFireDamage() && !event.getEntityLiving().world.isRemote && !event.getSource().equals(DamageSource.LAVA)) {
         EntityPlayer player = (EntityPlayer)event.getEntityLiving();
         float damageMulti = 1.0F;
         IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
         List<EaglercraftUUID> found = new ArrayList();

         for(int i = -2; i < baubles.getSlots(); ++i) {
            ItemStack stack = i >= 0 ? baubles.getStackInSlot(i) : (i == -2 ? player.getHeldItemMainhand() : player.getHeldItemOffhand());
            if (stack.getItem() instanceof IFireResistance && !found.contains(((IFireResistance)stack.getItem()).getFireResistID()) && (i >= 0 || stack.getItem() instanceof ItemShieldObsidian)) {
               IFireResistance fireResist = (IFireResistance)stack.getItem();
               found.add(fireResist.getFireResistID());
               damageMulti = (float)((double)damageMulti * Math.max(1.0D - (double)fireResist.getResistance(), 0.0D));
            }
         }

         event.setAmount(event.getAmount() * damageMulti);
         if (player.isBurning() && damageMulti != 1.0F) {
            player.extinguish();
         }
      }

   }
}
