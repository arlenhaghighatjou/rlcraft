package com.endertech.minecraft.mods.adhooks.forces;

import com.endertech.minecraft.forge.entities.ForgeEntity;
import com.endertech.minecraft.forge.math.Vect3d;
import com.endertech.minecraft.mods.adhooks.motion.MotionMsg;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.Entity;

/** @deprecated */
@Deprecated
public class ForceTarget extends ForceNode {
   protected long lastUpdateTime = 0L;
   protected final Map<Entity, ForceSource> forceSources = new ConcurrentHashMap();
   protected Vect3d resultantForce = Vect3d.ZERO;

   public ForceTarget(Entity entity) {
      super(entity);
   }

   public void applyForces() {
      ForgeEntity.setMotion(this.entity, this.getResultantForce());
      if (ForgeEntity.isServerSide(this.entity)) {
         MotionMsg.of(this.entity).sendToAllTracking(this.entity);
      }

   }

   public Vect3d getResultantForce() {
      long currentTime = this.entity.getEntityWorld().getTotalWorldTime();
      if (this.lastUpdateTime != currentTime) {
         this.resultantForce = Vect3d.ZERO;
         Iterator<ForceSource> iterator = this.forceSources.values().iterator();

         while(iterator.hasNext()) {
            ForceSource source = (ForceSource)iterator.next();
            source.tick();
            if (source.exist()) {
               this.resultantForce = this.resultantForce.add(source.getVect());
            } else {
               iterator.remove();
            }
         }

         this.lastUpdateTime = currentTime;
      }

      return this.resultantForce;
   }

   public void addForceFrom(Entity source, Vect3d vect) {
      this.forceSources.put(source, new ForceSource(source, vect, 3));
   }

   public void removeForceFrom(Entity source) {
      this.forceSources.remove(source);
   }

   public boolean exist() {
      return this.entity.isEntityAlive() && this.getResultantForce().notZero();
   }
}
