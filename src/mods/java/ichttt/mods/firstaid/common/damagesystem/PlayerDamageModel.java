package ichttt.mods.firstaid.common.damagesystem;

import com.creativemd.playerrevive.api.IRevival;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import ichttt.mods.firstaid.api.FirstAidRegistry;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.damagesystem.AbstractPlayerDamageModel;
import ichttt.mods.firstaid.api.debuff.IDebuff;
import ichttt.mods.firstaid.api.enums.EnumDebuffSlot;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import ichttt.mods.firstaid.client.util.HealthRenderUtils;
import ichttt.mods.firstaid.common.CapProvider;
import ichttt.mods.firstaid.common.DataManagerWrapper;
import ichttt.mods.firstaid.common.EventHandler;
import ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl;
import ichttt.mods.firstaid.common.damagesystem.debuff.SharedDebuff;
import ichttt.mods.firstaid.common.network.MessageSyncDamageModel;
import ichttt.mods.firstaid.common.util.CommonUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PlayerDamageModel extends AbstractPlayerDamageModel {
   private final Set<SharedDebuff> sharedDebuffs = new HashSet();
   private int morphineTicksLeft = 0;
   private int sleepBlockTicks = 0;
   private float prevHealthCurrent = -1.0F;
   private float prevScaleFactor;
   private boolean waitingForHelp = false;
   private final boolean noCritical;
   private boolean needsMorphineUpdate = false;
   private int resyncTimer = -1;

   public static PlayerDamageModel create() {
      FirstAidRegistry registry = FirstAidRegistryImpl.INSTANCE;
      IDebuff[] headDebuffs = registry.getDebuffs(EnumDebuffSlot.HEAD);
      IDebuff[] bodyDebuffs = registry.getDebuffs(EnumDebuffSlot.BODY);
      IDebuff[] armsDebuffs = registry.getDebuffs(EnumDebuffSlot.ARMS);
      IDebuff[] legFootDebuffs = registry.getDebuffs(EnumDebuffSlot.LEGS_AND_FEET);
      return new PlayerDamageModel(headDebuffs, bodyDebuffs, armsDebuffs, legFootDebuffs);
   }

   protected PlayerDamageModel(IDebuff[] headDebuffs, IDebuff[] bodyDebuffs, IDebuff[] armDebuffs, IDebuff[] legFootDebuffs) {
      super(new DamageablePart(FirstAidConfig.damageSystem.maxHealthHead, FirstAidConfig.damageSystem.causeDeathHead, EnumPlayerPart.HEAD, headDebuffs), new DamageablePart(FirstAidConfig.damageSystem.maxHealthLeftArm, false, EnumPlayerPart.LEFT_ARM, armDebuffs), new DamageablePart(FirstAidConfig.damageSystem.maxHealthLeftLeg, false, EnumPlayerPart.LEFT_LEG, legFootDebuffs), new DamageablePart(FirstAidConfig.damageSystem.maxHealthLeftFoot, false, EnumPlayerPart.LEFT_FOOT, legFootDebuffs), new DamageablePart(FirstAidConfig.damageSystem.maxHealthBody, FirstAidConfig.damageSystem.causeDeathBody, EnumPlayerPart.BODY, bodyDebuffs), new DamageablePart(FirstAidConfig.damageSystem.maxHealthRightArm, false, EnumPlayerPart.RIGHT_ARM, armDebuffs), new DamageablePart(FirstAidConfig.damageSystem.maxHealthRightLeg, false, EnumPlayerPart.RIGHT_LEG, legFootDebuffs), new DamageablePart(FirstAidConfig.damageSystem.maxHealthRightFoot, false, EnumPlayerPart.RIGHT_FOOT, legFootDebuffs));

      for(IDebuff debuff : armDebuffs) {
         this.sharedDebuffs.add((SharedDebuff)debuff);
      }

      for(IDebuff debuff : legFootDebuffs) {
         this.sharedDebuffs.add((SharedDebuff)debuff);
      }

      this.noCritical = !FirstAidConfig.damageSystem.causeDeathBody && !FirstAidConfig.damageSystem.causeDeathHead;
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound tagCompound = new NBTTagCompound();
      tagCompound.setTag("head", this.HEAD.serializeNBT());
      tagCompound.setTag("leftArm", this.LEFT_ARM.serializeNBT());
      tagCompound.setTag("leftLeg", this.LEFT_LEG.serializeNBT());
      tagCompound.setTag("leftFoot", this.LEFT_FOOT.serializeNBT());
      tagCompound.setTag("body", this.BODY.serializeNBT());
      tagCompound.setTag("rightArm", this.RIGHT_ARM.serializeNBT());
      tagCompound.setTag("rightLeg", this.RIGHT_LEG.serializeNBT());
      tagCompound.setTag("rightFoot", this.RIGHT_FOOT.serializeNBT());
      tagCompound.setBoolean("hasTutorial", this.hasTutorial);
      return tagCompound;
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      this.HEAD.deserializeNBT((NBTTagCompound)nbt.getTag("head"));
      this.LEFT_ARM.deserializeNBT((NBTTagCompound)nbt.getTag("leftArm"));
      this.LEFT_LEG.deserializeNBT((NBTTagCompound)nbt.getTag("leftLeg"));
      this.LEFT_FOOT.deserializeNBT((NBTTagCompound)nbt.getTag("leftFoot"));
      this.BODY.deserializeNBT((NBTTagCompound)nbt.getTag("body"));
      this.RIGHT_ARM.deserializeNBT((NBTTagCompound)nbt.getTag("rightArm"));
      this.RIGHT_LEG.deserializeNBT((NBTTagCompound)nbt.getTag("rightLeg"));
      this.RIGHT_FOOT.deserializeNBT((NBTTagCompound)nbt.getTag("rightFoot"));
      if (nbt.hasKey("morphineTicks")) {
         this.morphineTicksLeft = nbt.getInteger("morphineTicks");
         this.needsMorphineUpdate = true;
      }

      if (nbt.hasKey("hasTutorial")) {
         this.hasTutorial = nbt.getBoolean("hasTutorial");
      }

   }

   public void tick(World world, EntityPlayer player) {
      if (!this.isDead(player)) {
         world.profiler.startSection("FirstAidPlayerModel");
         if (this.sleepBlockTicks > 0) {
            --this.sleepBlockTicks;
         } else if (this.sleepBlockTicks < 0) {
            throw new RuntimeException("Negative sleepBlockTicks " + this.sleepBlockTicks);
         }

         float newCurrentHealth = this.calculateNewCurrentHealth(player);
         if (Float.isNaN(newCurrentHealth)) {
            FirstAid.LOGGER.warn("New current health is not a number, setting it to 0!");
            newCurrentHealth = 0.0F;
         }

         if (newCurrentHealth <= 0.0F) {
            FirstAid.LOGGER.error("Got {} health left, but isn't marked as dead!", Float.valueOf(newCurrentHealth));
            world.profiler.endSection();
         } else {
            if (!world.isRemote && this.resyncTimer != -1) {
               --this.resyncTimer;
               if (this.resyncTimer == 0) {
                  this.resyncTimer = -1;
                  FirstAid.NETWORKING.sendTo(new MessageSyncDamageModel(this, true), (EntityPlayerMP)player);
               }
            }

            if (Float.isInfinite(newCurrentHealth)) {
               FirstAid.LOGGER.error("Error calculating current health: Value was infinite");
            } else {
               if (newCurrentHealth != this.prevHealthCurrent) {
                  ((DataManagerWrapper)player.dataManager).set_impl(EntityPlayer.HEALTH, Float.valueOf(newCurrentHealth));
               }

               this.prevHealthCurrent = newCurrentHealth;
            }

            if (!this.hasTutorial) {
               this.hasTutorial = CapProvider.tutorialDone.contains(player.getName());
            }

            this.runScaleLogic(player);
            if (this.needsMorphineUpdate) {
               player.addPotionEffect(new PotionEffect(EventHandler.MORPHINE, this.morphineTicksLeft, 0, false, false));
            }

            PotionEffect morphine = player.getActivePotionEffect(EventHandler.MORPHINE);
            if (!this.needsMorphineUpdate) {
               this.morphineTicksLeft = morphine == null ? 0 : morphine.getDuration();
            }

            this.needsMorphineUpdate = false;
            world.profiler.startSection("PartDebuffs");
            this.forEach((part) -> {
               part.tick(world, player, morphine == null);
            });
            if (morphine == null) {
               this.sharedDebuffs.forEach((sharedDebuff) -> {
                  sharedDebuff.tick(player);
               });
            }

            world.profiler.endSection();
            world.profiler.endSection();
         }
      }
   }

   public static int getRandMorphineDuration() {
      return EventHandler.rand.nextInt(5) * 20 * 15 + 4200;
   }

   /** @deprecated */
   @Deprecated
   public void applyMorphine() {
      this.morphineTicksLeft = getRandMorphineDuration();
      this.needsMorphineUpdate = true;
   }

   public void applyMorphine(EntityPlayer player) {
      player.addPotionEffect(new PotionEffect(EventHandler.MORPHINE, getRandMorphineDuration(), 0, false, false));
   }

   /** @deprecated */
   @Deprecated
   public int getMorphineTicks() {
      return this.morphineTicksLeft;
   }

   @Nonnull
   public Iterator<AbstractDamageablePart> iterator() {
      return new Iterator<AbstractDamageablePart>() {
         byte count = 1;

         public boolean hasNext() {
            return this.count <= 8;
         }

         public AbstractDamageablePart next() {
            if (this.count > 8) {
               throw new NoSuchElementException();
            } else {
               AbstractDamageablePart part = PlayerDamageModel.this.getFromEnum(EnumPlayerPart.fromID(this.count));
               ++this.count;
               return part;
            }
         }
      };
   }

   /** @deprecated */
   @Deprecated
   public float getCurrentHealth() {
      float currentHealth = 0.0F;

      for(AbstractDamageablePart part : this) {
         currentHealth += part.currentHealth;
      }

      return currentHealth;
   }

   private float calculateNewCurrentHealth(EntityPlayer player) {
      float currentHealth = 0.0F;
      FirstAidConfig.VanillaHealthCalculationMode mode = FirstAidConfig.vanillaHealthCalculation;
      if (this.noCritical) {
         mode = FirstAidConfig.VanillaHealthCalculationMode.AVERAGE_ALL;
      }

      switch(mode) {
      case AVERAGE_CRITICAL:
         int maxHealth = 0;

         for(AbstractDamageablePart part : this) {
            if (part.canCauseDeath) {
               currentHealth += part.currentHealth;
               maxHealth += part.getMaxHealth();
            }
         }

         currentHealth = currentHealth / (float)maxHealth;
         break;
      case MIN_CRITICAL:
         AbstractDamageablePart minimal = null;
         float lowest = Float.MAX_VALUE;

         for(AbstractDamageablePart part : this) {
            if (part.canCauseDeath) {
               float partCurrentHealth = part.currentHealth;
               if (partCurrentHealth < lowest) {
                  minimal = part;
                  lowest = partCurrentHealth;
               }
            }
         }

         Objects.requireNonNull(minimal);
         currentHealth = minimal.currentHealth / (float)minimal.getMaxHealth();
         break;
      case AVERAGE_ALL:
         for(AbstractDamageablePart part : this) {
            currentHealth += part.currentHealth;
         }

         currentHealth = currentHealth / (float)this.getCurrentMaxHealth();
         break;
      case CRITICAL_50_PERCENT_OTHER_50_PERCENT:
         float currentNormal = 0.0F;
         int maxNormal = 0;
         float currentCritical = 0.0F;
         int maxCritical = 0;

         for(AbstractDamageablePart part : this) {
            if (!part.canCauseDeath) {
               currentNormal += part.currentHealth;
               maxNormal += part.getMaxHealth();
            } else {
               currentCritical += part.currentHealth;
               maxCritical += part.getMaxHealth();
            }
         }

         float avgNormal = currentNormal / (float)maxNormal;
         float avgCritical = currentCritical / (float)maxCritical;
         currentHealth = (avgCritical + avgNormal) / 2.0F;
         break;
      default:
         throw new RuntimeException("Unknown constant " + mode);
      }

      return currentHealth * player.getMaxHealth();
   }

   public boolean isDead(@Nullable EntityPlayer player) {
      IRevival revival = CommonUtils.getRevivalIfPossible(player);
      if (revival != null) {
         if (!revival.isHealty() && !revival.isDead()) {
            if (FirstAidConfig.debug && !this.waitingForHelp) {
               FirstAid.LOGGER.info("Player start waiting for help");
            }

            this.waitingForHelp = true;
            return true;
         }

         if (this.waitingForHelp) {
            return true;
         }
      }

      if (player == null || !player.isDead && player.getHealth() > 0.0F) {
         if (!this.noCritical) {
            for(AbstractDamageablePart part : this) {
               if (part.canCauseDeath && part.currentHealth <= 0.0F) {
                  return true;
               }
            }

            return false;
         } else {
            boolean dead = true;

            for(AbstractDamageablePart part : this) {
               if (part.currentHealth > 0.0F) {
                  dead = false;
                  break;
               }
            }

            return dead;
         }
      } else {
         return true;
      }
   }

   public Float getAbsorption() {
      float value = 0.0F;

      for(AbstractDamageablePart part : this) {
         value += part.getAbsorption();
      }

      return value;
   }

   public void setAbsorption(float absorption) {
      float newAbsorption = absorption / 8.0F;
      this.forEach((damageablePart) -> {
         damageablePart.setAbsorption(newAbsorption);
      });
   }

   @SideOnly(Side.CLIENT)
   public int getMaxRenderSize() {
      int max = 0;

      for(AbstractDamageablePart part : this) {
         int newMax;
         if (FirstAidConfig.overlay.overlayMode == FirstAidConfig.Overlay.OverlayMode.NUMBERS) {
            newMax = Minecraft.getMinecraft().fontRenderer.getStringWidth(HealthRenderUtils.TEXT_FORMAT.format((double)part.currentHealth) + "/" + part.getMaxHealth()) + 1;
         } else {
            newMax = (int)((float)((int)((float)part.getMaxHealth() + part.getAbsorption() + 0.9999F) + 1) / 2.0F * 9.0F);
         }

         max = Math.max(max, newMax);
      }

      return max;
   }

   public void sleepHeal(EntityPlayer player) {
      if (this.sleepBlockTicks <= 0) {
         CommonUtils.healPlayerByPercentage(FirstAidConfig.externalHealing.sleepHealPercentage, this, player);
         this.sleepBlockTicks = 20;
      }
   }

   public int getCurrentMaxHealth() {
      int maxHealth = 0;

      for(AbstractDamageablePart part : this) {
         maxHealth += part.getMaxHealth();
      }

      return maxHealth;
   }

   public void stopWaitingForHelp(EntityPlayer player) {
      if (FirstAidConfig.debug) {
         FirstAid.LOGGER.info("Help waiting done!");
      }

      if (!this.waitingForHelp) {
         FirstAid.LOGGER.warn("Player {} not waiting for help!", player.getName());
      }

      this.waitingForHelp = false;
   }

   public boolean isWaitingForHelp() {
      return this.waitingForHelp;
   }

   /** @deprecated */
   @Deprecated
   public void onNotHelped(EntityPlayer player) {
      this.stopWaitingForHelp(player);
   }

   /** @deprecated */
   @Deprecated
   public void onHelpedUp(EntityPlayer player) {
      this.stopWaitingForHelp(player);
      this.revivePlayer(player);
   }

   public void revivePlayer(EntityPlayer player) {
      if (FirstAidConfig.debug) {
         CommonUtils.debugLogStacktrace("Reviving player");
      }

      player.isDead = false;

      for(AbstractDamageablePart part : this) {
         if ((part.canCauseDeath || this.noCritical) && part.currentHealth <= 0.0F) {
            part.currentHealth = 1.0F;
         }
      }

      if (!player.world.isRemote && player instanceof EntityPlayerMP) {
         FirstAid.NETWORKING.sendTo(new MessageSyncDamageModel(this, true), (EntityPlayerMP)player);
      }

   }

   public void runScaleLogic(EntityPlayer player) {
      if (FirstAidConfig.scaleMaxHealth) {
         player.world.profiler.startSection("healthscaling");
         float globalFactor = player.getMaxHealth() / 20.0F;
         if (this.prevScaleFactor != globalFactor) {
            if (FirstAidConfig.debug) {
               FirstAid.LOGGER.info("Starting health scaling factor {} -> {} (max health {})", Float.valueOf(this.prevScaleFactor), Float.valueOf(globalFactor), Float.valueOf(player.getMaxHealth()));
            }

            player.world.profiler.startSection("distribution");
            int reduced = 0;
            int added = 0;
            float expectedNewMaxHealth = 0.0F;
            int newMaxHealth = 0;

            for(AbstractDamageablePart part : this) {
               float floatResult = (float)part.initialMaxHealth * globalFactor;
               expectedNewMaxHealth += floatResult;
               int result = (int)floatResult;
               if (result % 2 == 1) {
                  int partMaxHealth = part.getMaxHealth();
                  if (part.currentHealth < (float)partMaxHealth && reduced < 4) {
                     --result;
                     ++reduced;
                  } else if (part.currentHealth > (float)partMaxHealth && added < 4) {
                     ++result;
                     ++added;
                  } else if (reduced > added) {
                     ++result;
                     ++added;
                  } else {
                     --result;
                     ++reduced;
                  }
               }

               newMaxHealth += result;
               if (FirstAidConfig.debug) {
                  FirstAid.LOGGER.info("Part {} max health: {} initial; {} old; {} new", part.part.name(), Integer.valueOf(part.initialMaxHealth), Integer.valueOf(part.getMaxHealth()), Integer.valueOf(result));
               }

               part.setMaxHealth(result);
            }

            player.world.profiler.endStartSection("correcting");
            if (Math.abs(expectedNewMaxHealth - (float)newMaxHealth) >= 2.0F) {
               if (FirstAidConfig.debug) {
                  FirstAid.LOGGER.info("Entering second stage - diff {}", Float.valueOf(Math.abs(expectedNewMaxHealth - (float)newMaxHealth)));
               }

               List<AbstractDamageablePart> prioList = new ArrayList();

               for(AbstractDamageablePart part : this) {
                  prioList.add(part);
               }

               prioList.sort(Comparator.comparingInt(AbstractDamageablePart::getMaxHealth));

               for(AbstractDamageablePart part : prioList) {
                  int maxHealth = part.getMaxHealth();
                  if (FirstAidConfig.debug) {
                     FirstAid.LOGGER.info("Part {}: Second stage with total diff {}", part.part.name(), Float.valueOf(Math.abs(expectedNewMaxHealth - (float)newMaxHealth)));
                  }

                  if (expectedNewMaxHealth > (float)newMaxHealth) {
                     part.setMaxHealth(maxHealth + 2);
                     newMaxHealth += part.getMaxHealth() - maxHealth;
                  } else if (expectedNewMaxHealth < (float)newMaxHealth) {
                     part.setMaxHealth(maxHealth - 2);
                     newMaxHealth -= maxHealth - part.getMaxHealth();
                  }

                  if (Math.abs(expectedNewMaxHealth - (float)newMaxHealth) < 2.0F) {
                     break;
                  }
               }
            }

            player.world.profiler.endSection();
         }

         this.prevScaleFactor = globalFactor;
         player.world.profiler.endSection();
      }

   }

   public void scheduleResync() {
      if (this.resyncTimer == -1) {
         this.resyncTimer = 3;
      } else {
         FirstAid.LOGGER.warn("resync already scheduled!");
      }

   }

   public boolean hasNoCritical() {
      return this.noCritical;
   }
}
