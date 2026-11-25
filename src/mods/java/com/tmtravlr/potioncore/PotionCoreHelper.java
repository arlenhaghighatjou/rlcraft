package com.tmtravlr.potioncore;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

public class PotionCoreHelper {
   public static ArrayList<EntityLivingBase> invulResetEntities = new ArrayList();
   public static final String HEAL_TAG = "Potion Core - Health Fix";
   public static final String HEAL_DELAY_TAG = "Potion Core - Health Fix Delay";
   public static ArrayList<EntityPlayerMP> playersToHeal = new ArrayList();
   public static final String JUMP_BOOST_JUMPING = "Potion Core - Jump Boost Jumping";
   public static final String JUMP_BOOST_HEIGHT_TAG = "Potion Core - Jump Boost Height";
   public static final String JUMP_BOOST_BASE_TAG = "Potion Core - Jump Boost Base";
   public static final String JUMP_BOOST_UPDATED_TAG = "Potion Core - Jump Boost Updated";
   public static float vanillaJumpBoostModifier = 0.75F;
   public static final String INVUL_PROJECTILE_TAG = "Potion Core - Projectile Invulnerability";
   public static final String INVUL_MAGIC_TAG = "Potion Core - Magic Invulnerability";
   public static final UUID TEMP_UUID = UUID.fromString("a6684e4c-f9da-4c3d-9ec1-a07ff26da4ef");
   public static boolean hasServer = false;
   public static int nextId = 1;

   public PotionCoreHelper() {
   }

   public static void addPotionEffectPositive(EntityLivingBase entity) {
      int r = entity.getRNG().nextInt(PotionCoreEffects.GOOD_EFFECTS.size());
      Potion potion = (Potion)PotionCoreEffects.GOOD_EFFECTS.get(r);
      if (potion.isInstant()) {
         entity.addPotionEffect(new PotionEffect(potion, 1));
      } else {
         entity.addPotionEffect(new PotionEffect(potion, 1200));
      }

   }

   public static void addPotionEffectNegative(EntityLivingBase entity) {
      int r = entity.getRNG().nextInt(PotionCoreEffects.BAD_EFFECTS.size());
      Potion potion = (Potion)PotionCoreEffects.BAD_EFFECTS.get(r);
      if (potion.isInstant()) {
         entity.addPotionEffect(new PotionEffect(potion, 1));
      } else {
         entity.addPotionEffect(new PotionEffect(potion, 1200));
      }

   }

   public static int getNextId() {
      while(Potion.getPotionById(nextId) != null) {
         ++nextId;
      }

      return nextId;
   }

   public static List<PotionEffect> getEffectsFromStack(ItemStack stack) {
      List<PotionEffect> effectList = Lists.newArrayList();
      if (!stack.isEmpty()) {
         PotionType potiontype = PotionUtils.getPotionFromItem(stack);
         if (potiontype != null) {
            effectList.addAll(potiontype.getEffects());
         }

         if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
            NBTTagList nbttaglist = stack.getTagCompound().getTagList("CustomPotionEffects", 10);

            for(int i = 0; i < nbttaglist.tagCount(); ++i) {
               NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
               PotionEffect potioneffect = readPotionEffectFromTag(nbttagcompound);
               if (potioneffect != null) {
                  effectList.add(potioneffect);
               }
            }
         }
      }

      return !effectList.isEmpty() ? effectList : PotionUtils.getEffectsFromStack(stack);
   }

   public static NBTTagCompound writePotionToTag(PotionEffect effect) {
      NBTTagCompound tag = new NBTTagCompound();
      if (effect != null && Potion.REGISTRY.getNameForObject(effect.getPotion()) != null) {
         tag.setString("Id", ((ResourceLocation)Potion.REGISTRY.getNameForObject(effect.getPotion())).toString());
         tag.setInteger("Amplifier", effect.getAmplifier());
         tag.setInteger("Duration", effect.getDuration());
         tag.setBoolean("Ambient", effect.getIsAmbient());
         tag.setBoolean("ShowParticles", effect.doesShowParticles());
         return tag;
      } else {
         return tag;
      }
   }

   public static PotionEffect readPotionEffectFromTag(NBTTagCompound tag) {
      Potion potion = null;
      int id = -1;
      if (tag.hasKey("Id", 8)) {
         String idString = tag.getString("Id");
         potion = Potion.getPotionFromResourceLocation(idString);
      } else {
         id = tag.getByte("Id") & 255;
         potion = Potion.getPotionById(id);
      }

      if (potion != null) {
         int j = tag.getByte("Amplifier");
         int k = tag.getInteger("Duration");
         boolean ambient = tag.getBoolean("Ambient");
         boolean showParticles = true;
         if (tag.hasKey("ShowParticles", 1)) {
            showParticles = tag.getBoolean("ShowParticles");
         }

         return new PotionEffect(potion, k, j, ambient, showParticles);
      } else {
         return null;
      }
   }
}
