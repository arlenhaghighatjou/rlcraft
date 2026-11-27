package com.Shultrea.Rin.Ench0_4_5;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentSweepingEdge;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentMastery extends EnchantmentBase {
   public static String nbtFlag = "smeFlag";

   public EnchantmentMastery() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("SwordMastery");
      this.setRegistryName("SwordMastery");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.AncientSwordMastery;
   }

   public int getMaxLevel() {
      return ModConfig.level.AncientSwordMastery;
   }

   public int getMinEnchantability(int par1) {
      return 80 + 80 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + par1 * 80;
   }

   public boolean canApplyTogether(Enchantment e) {
      return super.canApplyTogether(e) && !(e instanceof EnchantmentSweepingEdge);
   }

   public void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, EventPriority.LOWEST, true, this::HandleEnchant);
   }

   public void HandleEnchant(LivingHurtEvent e) {
      if (e.getSource().damageType == "player" || e.getSource().damageType == "mob") {
         if (e.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)e.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack stack = ((EntityLivingBase)e.getSource().getTrueSource()).getHeldItemMainhand();
               if (!stack.isEmpty() && stack != null) {
                  EntityLivingBase victim = e.getEntityLiving();
                  if (victim != null) {
                     int level = EnchantmentHelper.getEnchantmentLevel(this, stack);
                     if (EnchantmentHelper.getEnchantmentLevel(this, stack) > 0) {
                        if (!isOffensivePetDisallowed(e.getSource().getImmediateSource(), e.getSource().getTrueSource())) {
                           if (!isOffensivePetDisallowed(e.getSource().getImmediateSource(), e.getSource().getTrueSource())) {
                              if (!stack.hasTagCompound()) {
                                 stack.setTagCompound(new NBTTagCompound());
                              }

                              if (stack.getTagCompound().getBoolean(nbtFlag)) {
                                 stack.getTagCompound().setBoolean(nbtFlag, false);
                              } else {
                                 stack.getTagCompound().setBoolean(nbtFlag, true);
                                 if ((level >= 3 || EnchantmentsUtility.RANDOM.nextInt(3) < level - 1) && stack.getItem() instanceof ItemSword && attacker instanceof EntityPlayer) {
                                    ItemSword sword = (ItemSword)stack.getItem();
                                    sword.onLeftClickEntity(stack, (EntityPlayer)attacker, victim);
                                    sword.hitEntity(stack, victim, attacker);
                                    NBTTagList nbt = stack.getEnchantmentTagList();

                                    for(int x = 0; x < nbt.tagCount(); ++x) {
                                       NBTTagCompound tag = nbt.getCompoundTagAt(x);
                                       int enchId = tag.getShort("id");
                                       int currEnchLevel = tag.getShort("lvl");
                                       Enchantment enchantment = Enchantment.getEnchantmentByID(enchId);
                                       if (enchantment != null) {
                                          enchantment.onEntityDamaged(attacker, victim, currEnchLevel);
                                       }
                                    }
                                 }

                                 if (level >= 3) {
                                    float damage = ForgeHooks.onLivingHurt(e.getEntityLiving(), e.getSource(), 1.0F);
                                    damage = damage + ForgeHooks.onLivingDamage(e.getEntityLiving(), e.getSource(), 1.0F);
                                    damage = (damage - 2.0F) * (0.4F + (float)level * 0.1F);
                                    damage = damage + e.getAmount();
                                    e.setAmount(damage);
                                 }

                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public String getPrefix() {
      return TextFormatting.YELLOW.toString();
   }
}
