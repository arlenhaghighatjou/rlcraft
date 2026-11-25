package com.oblivioussp.spartanshields.network;

import com.oblivioussp.spartanshields.item.ItemShieldBase;
import com.oblivioussp.spartanshields.util.ConfigHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketShieldBash extends PacketBase<PacketShieldBash> {
   protected int entityId;
   protected boolean attackEntity = false;
   protected EnumHand hand;

   public PacketShieldBash() {
   }

   public PacketShieldBash(EnumHand enumHand, int entId, boolean atkEntity) {
      this.hand = enumHand;
      this.entityId = entId;
      this.attackEntity = atkEntity;
   }

   public void fromBytes(ByteBuf buf) {
      this.hand = EnumHand.values()[ByteBufUtils.readVarInt(buf, 1)];
      this.entityId = ByteBufUtils.readVarInt(buf, 4);
      this.attackEntity = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeVarInt(buf, this.hand.ordinal(), 1);
      ByteBufUtils.writeVarInt(buf, this.entityId, 4);
      buf.writeBoolean(this.attackEntity);
   }

   public void handleClientSide(PacketShieldBash message, EntityPlayer player) {
   }

   public void handleServerSide(PacketShieldBash message, EntityPlayerMP player) {
      boolean attackEntity = false;
      if (message != null && player != null) {
         EnumHand shieldHand = message.hand;
         int entId = message.entityId;
         attackEntity = message.attackEntity;
         Entity victim = player.world.getEntityByID(entId);
         if (player.isActiveItemStackBlocking()) {
            ItemStack shield = player.getHeldItem(shieldHand);
            if (!shield.isEmpty() && !player.getCooldownTracker().hasCooldown(shield.getItem()) && shield.getItem() instanceof ItemShieldBase) {
               if (attackEntity && victim != null && victim instanceof EntityLivingBase) {
                  int knockLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, shield);
                  victim.hurtResistantTime = 0;
                  ((EntityLivingBase)victim).knockBack(player, 1.0F + (float)knockLvl, (double)MathHelper.sin(player.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                  victim.attackEntityFrom(DamageSource.causePlayerDamage(player), 1.0F);
                  shield.damageItem(5, player);
                  player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_SHIELD_BLOCK, player.getSoundCategory(), 1.0F, 1.0F);
                  player.onCriticalHit(victim);
               } else {
                  player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 0.5F, 0.01F);
               }

               player.stopActiveHand();
               player.swingArm(shieldHand);
               player.getCooldownTracker().setCooldown(shield.getItem(), ConfigHandler.cooldownShieldBash);
            }
         }

      }
   }
}
