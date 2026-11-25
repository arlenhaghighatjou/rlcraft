package vazkii.quark.tweaks.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;

public class LookDownLadders extends Feature {
   public boolean sneakWhileInGui;

   public LookDownLadders() {
   }

   public void setupConfig() {
      this.sneakWhileInGui = this.loadPropBool("Sneak While In Gui", "Should your character automatically sneak on ladders in GUIs?", true);
   }

   @SubscribeEvent
   public void onPlayerTick(LivingUpdateEvent event) {
      if (event.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntityLiving();
         if (player.isOnLadder() && !player.isSneaking() && player.moveForward == 0.0F && player.rotationPitch > 70.0F) {
            player.move(MoverType.SELF, 0.0D, -0.2D, 0.0D);
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onInput(InputUpdateEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      if (this.sneakWhileInGui && player.isOnLadder() && Minecraft.getMinecraft().currentScreen != null && (player.moveForward != 0.0F || player.rotationPitch <= 70.0F)) {
         MovementInput input = event.getMovementInput();
         if (input != null) {
            input.sneak = true;
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Look Down Ladders";
   }
}
