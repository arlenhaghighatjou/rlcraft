package team.creative.enhancedvisuals.common.packet;

import com.creativemd.creativecore.common.packet.CreativeCorePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import team.creative.enhancedvisuals.common.handler.VisualHandlers;

public class DamagePacket extends CreativeCorePacket {
   public String attackerClass;
   public ItemStack stack;
   public float damage;
   public float distance;
   public boolean fire;
   public String source;

   public DamagePacket(LivingDamageEvent event) {
      this.damage = event.getAmount();
      Entity attacker = event.getSource().getImmediateSource();
      this.fire = event.getSource().isFireDamage();
      if (!(attacker instanceof EntityLiving) && !(attacker instanceof EntityArrow)) {
         this.source = event.getSource().damageType;
      } else {
         this.attackerClass = attacker.getClass().getName().toLowerCase();
         this.source = "attacker";
         if (attacker instanceof EntityLiving && ((EntityLiving)attacker).getHeldItemMainhand() != null) {
            this.stack = ((EntityLiving)attacker).getHeldItemMainhand();
         }
      }

   }

   public DamagePacket() {
   }

   public void writeBytes(ByteBuf buf) {
      if (this.attackerClass != null) {
         buf.writeBoolean(true);
         writeString(buf, this.attackerClass);
      } else {
         buf.writeBoolean(false);
      }

      if (this.stack != null) {
         buf.writeBoolean(true);
         writeItemStack(buf, this.stack);
      } else {
         buf.writeBoolean(false);
      }

      buf.writeFloat(this.damage);
      buf.writeFloat(this.distance);
      writeString(buf, this.source);
      buf.writeBoolean(this.fire);
   }

   public void readBytes(ByteBuf buf) {
      if (buf.readBoolean()) {
         this.attackerClass = readString(buf);
      }

      if (buf.readBoolean()) {
         this.stack = readItemStack(buf);
      }

      this.damage = buf.readFloat();
      this.distance = buf.readFloat();
      this.source = readString(buf);
      this.fire = buf.readBoolean();
   }

   public void executeClient(EntityPlayer player) {
      if (VisualHandlers.DAMAGE.isEnabled(player)) {
         VisualHandlers.DAMAGE.playerDamaged(player, this);
      }

   }

   public void executeServer(EntityPlayer player) {
   }
}
