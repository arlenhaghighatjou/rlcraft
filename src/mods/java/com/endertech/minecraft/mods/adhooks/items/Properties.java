package com.endertech.minecraft.mods.adhooks.items;

import com.endertech.minecraft.forge.messages.ForgeNetMsg;
import com.endertech.minecraft.forge.messages.ForgeNetMsgHandler;
import com.endertech.minecraft.mods.adhooks.init.HookType;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class Properties extends ForgeNetMsg {
   public HookType hookType;
   public int durability;
   public float reelingSpeed;
   public float boostStrength = 1.0F;
   public float hookStrength;
   public float hookDamage;
   public int ropeMaxLength;
   public float ropeElasticity;
   public float ropeWidth;
   public int ropeColorARGB;

   public Properties() {
   }

   public Properties launcher(int durability, float reelingSpeed, float boostStrength) {
      this.durability = durability;
      this.reelingSpeed = reelingSpeed;
      this.boostStrength = boostStrength;
      return this;
   }

   public Properties hook(float strength, float damage) {
      this.hookStrength = strength;
      this.hookDamage = damage;
      return this;
   }

   public Properties rope(int maxLength, float elasticity, float width, int colorARGB) {
      this.ropeMaxLength = maxLength;
      this.ropeElasticity = elasticity;
      this.ropeWidth = width;
      this.ropeColorARGB = colorARGB;
      return this;
   }

   public Properties readValues() {
      this.durability = this.hookType.launcher.getMaxDamage();
      this.reelingSpeed = this.hookType.launcher.getReelingSpeed();
      this.boostStrength = this.hookType.launcher.getBoostStrength();
      this.hookDamage = this.hookType.hook.getDamage();
      this.hookStrength = this.hookType.hook.getStrength();
      this.ropeMaxLength = this.hookType.rope.getMaxLength();
      this.ropeElasticity = this.hookType.rope.getElasticity();
      return this;
   }

   protected void writeValues() {
      this.hookType.launcher.setMaxDamage(this.durability);
      this.hookType.launcher.reelingSpeed = this.reelingSpeed;
      this.hookType.launcher.boostStrength = this.boostStrength;
      this.hookType.hook.damage = this.hookDamage;
      this.hookType.hook.strength = this.hookStrength;
      this.hookType.rope.maxLength = this.ropeMaxLength;
      this.hookType.rope.elasticity = this.ropeElasticity;
   }

   public static class Handler extends ForgeNetMsgHandler<Properties> {
      public Handler() {
      }

      public IMessage onMessage(Properties message, MessageContext ctx) {
         this.addScheduledTask(ctx, () -> {
            message.writeValues();
         });
         return null;
      }
   }
}
