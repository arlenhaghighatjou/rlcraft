package team.creative.enhancedvisuals.common.handler;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.event.SelectEndermanEvent;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.api.type.VisualTypeOverlay;
import team.creative.enhancedvisuals.client.VisualManager;

public class SlenderHandler extends VisualHandler {
   @CreativeConfig
   public double defaultIntensity = 0.0D;
   @CreativeConfig
   public double maxIntensity = 0.3D;
   @CreativeConfig
   public double distanceFactor = 0.25D;
   @CreativeConfig
   public VisualType slender = new VisualTypeOverlay("slender", 50);
   public Visual slenderVisual;
   public Class mutantEnderman = this.loadMutantEnderman();
   public Class infectedEnderman = this.loadInfectedEnderman();

   public SlenderHandler() {
   }

   private Class loadMutantEnderman() {
      try {
         return Class.forName("chumbanotz.mutantbeasts.entity.mutant.MutantEndermanEntity");
      } catch (Exception var2) {
         return null;
      }
   }

   private Class loadInfectedEnderman() {
      try {
         return Class.forName("com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman");
      } catch (Exception var2) {
         return null;
      }
   }

   public void tick(@Nullable EntityPlayer player) {
      if (this.slenderVisual == null) {
         this.slenderVisual = new Visual(this.slender, this, 0);
         VisualManager.add(this.slenderVisual);
      }

      float intensity = (float)this.defaultIntensity;
      if (player != null) {
         float modifier = 0.0F;
         double d0 = player.posX;
         double d1 = player.posY;
         double d2 = player.posZ;
         AxisAlignedBB box = player.getEntityBoundingBox();
         box = box.grow(16.0D, 16.0D, 16.0D);
         SelectEndermanEvent event = new SelectEndermanEvent((x) -> {
            return true;
         });
         MinecraftForge.EVENT_BUS.post(event);
         if (!event.isCanceled()) {
            Double distance = null;

            for(EntityEnderman mob : player.world.getEntitiesWithinAABB(EntityEnderman.class, box, (com.google.common.base.Predicate)null)) {
               if (mob != null && event.predicate.test(mob)) {
                  if (distance == null) {
                     distance = Math.sqrt(Math.pow(d0 - mob.posX, 2.0D) + Math.pow(d1 - mob.posY, 2.0D) + Math.pow(d2 - mob.posZ, 2.0D));
                  } else {
                     distance = Math.min(distance.doubleValue(), Math.sqrt(Math.pow(d0 - mob.posX, 2.0D) + Math.pow(d1 - mob.posY, 2.0D) + Math.pow(d2 - mob.posZ, 2.0D)));
                  }
               }
            }

            if (this.mutantEnderman != null) {
               for(Entity mob : player.world.getEntitiesWithinAABB(this.mutantEnderman, box, (com.google.common.base.Predicate)null)) {
                  if (mob != null) {
                     if (distance == null) {
                        distance = Math.sqrt(Math.pow(d0 - mob.posX, 2.0D) + Math.pow(d1 - mob.posY, 2.0D) + Math.pow(d2 - mob.posZ, 2.0D));
                     } else {
                        distance = Math.min(distance.doubleValue(), Math.sqrt(Math.pow(d0 - mob.posX, 2.0D) + Math.pow(d1 - mob.posY, 2.0D) + Math.pow(d2 - mob.posZ, 2.0D)));
                     }
                  }
               }
            }

            if (this.infectedEnderman != null) {
               for(Entity mob : player.world.getEntitiesWithinAABB(this.infectedEnderman, box, (com.google.common.base.Predicate)null)) {
                  if (mob != null) {
                     if (distance == null) {
                        distance = Math.sqrt(Math.pow(d0 - mob.posX, 2.0D) + Math.pow(d1 - mob.posY, 2.0D) + Math.pow(d2 - mob.posZ, 2.0D));
                     } else {
                        distance = Math.min(distance.doubleValue(), Math.sqrt(Math.pow(d0 - mob.posX, 2.0D) + Math.pow(d1 - mob.posY, 2.0D) + Math.pow(d2 - mob.posZ, 2.0D)));
                     }
                  }
               }
            }

            if (distance != null) {
               float distModifier = (float)Math.pow(1.0D / distance.doubleValue() / 3.0D, 2.0D);
               if (distModifier > modifier) {
                  modifier = distModifier;
                  if (distModifier > 3.5F) {
                     modifier = 3.5F;
                  }
               }

               intensity = (float)Math.max(this.defaultIntensity, Math.min(this.maxIntensity, this.distanceFactor * (double)modifier));
            }
         }
      }

      this.slenderVisual.setOpacityInternal(intensity);
   }
}
