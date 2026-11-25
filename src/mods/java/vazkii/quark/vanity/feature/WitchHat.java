package vazkii.quark.vanity.feature;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.vanity.item.ItemWitchHat;

public class WitchHat extends Feature {
   public static Item witch_hat;
   public static boolean halveDamage;
   public static boolean witchesIgnoreYou;
   public static double dropRate;
   public static double lootingBoost;
   public static boolean verifyTruePlayer;

   public WitchHat() {
   }

   public void setupConfig() {
      halveDamage = this.loadPropBool("Halve witch damage", "", true);
      witchesIgnoreYou = this.loadPropBool("Make witches ignore players with witch hats", "", true);
      dropRate = this.loadPropDouble("Drop Chance from witches", "", 0.025D);
      lootingBoost = this.loadPropDouble("Drop Chance boost per looting level", "", 0.01D);
      verifyTruePlayer = this.loadPropBool("Only Drop on Player Kills", "", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      witch_hat = new ItemWitchHat();
   }

   @SubscribeEvent
   public void onDrops(LivingDropsEvent event) {
      if (event.getEntityLiving() instanceof EntityWitch && (!verifyTruePlayer || event.getSource().getTrueSource() instanceof EntityPlayer) && Math.random() < dropRate + lootingBoost * (double)event.getLootingLevel()) {
         event.getDrops().add(new EntityItem(event.getEntity().getEntityWorld(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, new ItemStack(witch_hat)));
      }

   }

   @SubscribeEvent
   public void onDamage(LivingHurtEvent event) {
      if (halveDamage && event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityWitch) {
         ItemStack hat = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD);
         if (!hat.isEmpty() && hat.getItem() == witch_hat) {
            event.setAmount(event.getAmount() / 2.0F);
         }
      }

   }

   public static boolean hasWitchHat(EntityLiving attacker, @Nullable EntityLivingBase target) {
      if (witchesIgnoreYou && attacker instanceof EntityWitch && target != null) {
         ItemStack head = target.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
         return !head.isEmpty() && head.getItem() == witch_hat;
      } else {
         return false;
      }
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
