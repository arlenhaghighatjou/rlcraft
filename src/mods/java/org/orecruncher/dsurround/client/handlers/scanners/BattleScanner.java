package org.orecruncher.dsurround.client.handlers.scanners;

import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilityEntityData;
import org.orecruncher.dsurround.capabilities.entitydata.IEntityData;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;

@SideOnly(Side.CLIENT)
public class BattleScanner {
   private static final String DEBUG_NOT_ENABLED = TextFormatting.RED + "<BATTLE MUSIC NOT ENABLED>";
   private static final int BOSS_RANGE = 65536;
   private static final int MINI_BOSS_RANGE = 16384;
   private static final int MOB_RANGE = 400;
   private static final int BATTLE_TIMER_EXPIRY = 10;
   protected int battleTimer;
   protected boolean inBattle;
   protected boolean isWither;
   protected boolean isDragon;
   protected boolean isBoss;
   protected int hostileCount;

   public BattleScanner() {
   }

   public void reset() {
      this.inBattle = false;
      this.isWither = false;
      this.isDragon = false;
      this.isBoss = false;
      this.hostileCount = 0;
   }

   public boolean inBattle() {
      return this.inBattle;
   }

   public boolean isWither() {
      return this.isWither;
   }

   public boolean isDragon() {
      return this.isDragon;
   }

   public boolean isBoss() {
      return this.isBoss;
   }

   public int hostileCount() {
      return this.hostileCount;
   }

   private boolean isApplicableType(Entity e) {
      if (e instanceof IMob) {
         return true;
      } else if (e instanceof EntityPlayer) {
         return true;
      } else if (e instanceof EntityGolem) {
         return true;
      } else {
         return e instanceof EntityPolarBear;
      }
   }

   public void update(EntityPlayer player) {
      if (!ModOptions.sound.enableBattleMusic) {
         this.reset();
      } else {
         World world = player.getEntityWorld();
         BlockPos playerPos = new BlockPos(player.posX, player.getEntityBoundingBox().minY, player.posZ);
         boolean inBattle = false;
         boolean isBoss = false;
         boolean isDragon = false;
         boolean isWither = false;
         int hostileCount = 0;

         for(Entity e : world.getLoadedEntityList()) {
            if (e != player && this.isApplicableType(e)) {
               double dist = e.getDistanceSq(playerPos);
               if (dist <= 65536.0D && !e.isInvisible() && !e.isOnSameTeam(player)) {
                  if (!e.isNonBoss()) {
                     if (e instanceof EntityWither) {
                        isBoss = true;
                        isWither = true;
                        inBattle = true;
                        isDragon = false;
                     } else if (e instanceof EntityDragon) {
                        if (!isWither) {
                           isBoss = true;
                           isDragon = true;
                           inBattle = true;
                        }
                     } else if (dist <= 16384.0D && !isDragon && !isWither) {
                        isBoss = true;
                        inBattle = true;
                     }

                     ++hostileCount;
                  } else if (dist <= 400.0D) {
                     IEntityData data = (IEntityData)e.getCapability(CapabilityEntityData.ENTITY_DATA, (EnumFacing)null);
                     if (data != null && data.isAttacking()) {
                        EntityLiving living = (EntityLiving)e;
                        if (living.getEntitySenses().canSee(player) || player.canEntityBeSeen(living)) {
                           inBattle = true;
                           ++hostileCount;
                        }
                     }
                  }
               }
            }
         }

         int tickCounter = EnvironStateHandler.EnvironState.getTickCounter();
         if (inBattle) {
            this.inBattle = inBattle;
            this.isBoss = isBoss;
            this.isWither = isWither;
            this.isDragon = isDragon;
            this.hostileCount = hostileCount;
            this.battleTimer = tickCounter + 10;
         } else if (this.inBattle && tickCounter > this.battleTimer) {
            this.reset();
         }

      }
   }

   @Nonnull
   public String toString() {
      if (!ModOptions.sound.enableBattleMusic) {
         return DEBUG_NOT_ENABLED;
      } else {
         StringBuilder builder = new StringBuilder();
         builder.append("BattleScanner inBattle: ").append(this.inBattle).append(';');
         builder.append(" hostiles: ").append(this.hostileCount).append(';');
         builder.append(" isBoss: ").append(this.isBoss).append(';');
         builder.append(" isWither: ").append(this.isWither).append(';');
         builder.append(" isDragon: ").append(this.isDragon).append(';');
         return builder.toString();
      }
   }
}
