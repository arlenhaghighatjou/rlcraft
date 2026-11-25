package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.core.ModVillagers;
import java.lang.reflect.Field;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EntitySnowVillager extends EntityVillager {
   private String professionName;
   private VillagerProfession prof;

   public EntitySnowVillager(World worldIn) {
      super(worldIn);
   }

   public EntitySnowVillager(World worldIn, int profession) {
      super(worldIn, profession);
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
      return properties != null && properties.isStone ? false : super.processInteract(player, hand);
   }

   public void setProfession(int professionId) {
      if (professionId > 2) {
         professionId = 2;
      }

      this.dataManager.set(this.PROFFESSION(), Integer.valueOf(professionId));
   }

   private DataParameter<Boolean> BABY() {
      Field field = ReflectionHelper.findField(EntityAgeable.class, ObfuscationReflectionHelper.remapFieldNames(EntityAgeable.class.getName(), new String[]{"BABY", "BABY"}));

      try {
         Field modifier = Field.class.getDeclaredField("modifiers");
         modifier.setAccessible(true);
         modifier.setInt(field, field.getModifiers() & -17);
         DataParameter<Boolean> bay = (DataParameter)field.get(this);
         return bay;
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   private DataParameter<Integer> PROFFESSION() {
      Field field = ReflectionHelper.findField(EntityVillager.class, ObfuscationReflectionHelper.remapFieldNames(EntityVillager.class.getName(), new String[]{"PROFESSION", "PROFESSION"}));

      try {
         Field modifier = Field.class.getDeclaredField("modifiers");
         modifier.setAccessible(true);
         modifier.setInt(field, field.getModifiers() & -17);
         DataParameter<Integer> prof = (DataParameter)field.get(this);
         return prof;
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (HAND_STATES.equals(key) && this.world.isRemote) {
         if (this.isHandActive() && this.activeItemStack.isEmpty()) {
            this.activeItemStack = this.getHeldItem(this.getActiveHand());
            if (!this.activeItemStack.isEmpty()) {
               this.activeItemStackUseCount = this.activeItemStack.getMaxItemUseDuration();
            }
         } else if (!this.isHandActive() && !this.activeItemStack.isEmpty()) {
            this.activeItemStack = ItemStack.EMPTY;
            this.activeItemStackUseCount = 0;
         }
      }

      if (this.BABY().equals(key)) {
         this.setScaleForAge(this.isChild());
      }

   }

   public void onDeath(DamageSource cause) {
      if (cause.getTrueSource() == null || !(cause.getTrueSource() instanceof EntityZombie) || this.world.getDifficulty() != EnumDifficulty.NORMAL && this.world.getDifficulty() != EnumDifficulty.HARD) {
         super.onDeath(cause);
      }
   }

   public void setProfession(VillagerProfession prof) {
      if (ModVillagers.INSTANCE.professions.containsValue(prof)) {
         this.setProfession(VillagerRegistry.getId(prof));
      } else {
         ModVillagers.INSTANCE.setRandomProfession(this, this.world.rand);
      }

   }

   public EntityVillager createChild(EntityAgeable ageable) {
      EntitySnowVillager entityvillager = new EntitySnowVillager(this.world);
      entityvillager.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityvillager)), (IEntityLivingData)null);
      return entityvillager;
   }

   public VillagerProfession getProfessionForge() {
      if (this.prof == null) {
         String p = this.getEntityData().getString("ProfessionName");
         if (p.isEmpty()) {
            this.prof = (VillagerProfession)ModVillagers.INSTANCE.professions.get(Integer.valueOf(this.getRNG().nextInt(3)));
         } else {
            this.prof = (VillagerProfession)ModVillagers.INSTANCE.professions.get(Integer.valueOf(this.intFromProfesion(p)));
         }

         try {
            ReflectionHelper.findField(EntityVillager.class, new String[]{"careerId", "careerId"}).set(this, Integer.valueOf(1));
         } catch (IllegalAccessException var3) {
            var3.printStackTrace();
         }
      }

      return this.prof;
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setProfession(compound.getInteger("Profession"));
      if (compound.hasKey("ProfessionName")) {
         VillagerProfession p = (VillagerProfession)ModVillagers.INSTANCE.professions.get(Integer.valueOf(this.intFromProfesion(compound.getString("ProfessionName"))));
         if (p == null) {
            p = (VillagerProfession)ModVillagers.INSTANCE.professions.get(Integer.valueOf(0));
         }

         this.setProfession(p);
      }

   }

   public IEntityLivingData finalizeMobSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData data, boolean forgeCheck) {
      this.prof = (VillagerProfession)ModVillagers.INSTANCE.professions.get(Integer.valueOf(this.getRNG().nextInt(3)));
      return data;
   }

   private int intFromProfesion(String prof) {
      if (prof.contains("fisherman")) {
         return 0;
      } else if (prof.contains("craftsman")) {
         return 1;
      } else {
         return prof.contains("shaman") ? 2 : 0;
      }
   }
}
