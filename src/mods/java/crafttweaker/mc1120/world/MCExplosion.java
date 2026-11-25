package crafttweaker.mc1120.world;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.util.Position3f;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IExplosion;
import crafttweaker.api.world.IVector3d;
import crafttweaker.api.world.IWorld;
import crafttweaker.mc1120.entity.MCEntityLivingBase;
import crafttweaker.mc1120.player.MCPlayer;
import crafttweaker.mc1120.util.MCPosition3f;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.ForgeEventFactory;

public class MCExplosion implements IExplosion {
   private Explosion explosion;

   public MCExplosion(Explosion explosion) {
      this.explosion = explosion;
   }

   public IWorld getWorld() {
      return CraftTweakerMC.getIWorld(this.explosion.world);
   }

   public IEntityLivingBase getExplosivePlacedBy() {
      return new MCEntityLivingBase(this.explosion.getExplosivePlacedBy());
   }

   public Position3f getPosition() {
      return new MCPosition3f((float)this.explosion.getPosition().x, (float)this.explosion.getPosition().y, (float)this.explosion.getPosition().z);
   }

   public List<IBlockPos> getAffectedBlockPositions() {
      return (List)this.explosion.getAffectedBlockPositions().stream().map(MCBlockPos::new).collect(Collectors.toList());
   }

   public void setAffectedBlockPositions(List<IBlockPos> affectedBlockPositions) {
      this.explosion.affectedBlockPositions = (List)affectedBlockPositions.stream().filter((x) -> {
         return (void)(x instanceof BlockPos);
      }).map((x) -> {
         return (void)((BlockPos)x.getInternal());
      }).collect(Collectors.toList());
   }

   public void clearAffectedBlockPositions() {
      this.explosion.clearAffectedBlockPositions();
   }

   public Map<IPlayer, IVector3d> getPlayerKnockbackMap() {
      return (Map)this.explosion.getPlayerKnockbackMap().entrySet().stream().map((e) -> {
         return new SimpleEntry(new MCPlayer((EntityPlayer)e.getKey()), new MCVector3d((Vec3d)e.getValue()));
      }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
   }

   public boolean onExplosionStart(IWorld world) {
      return ForgeEventFactory.onExplosionStart(CraftTweakerMC.getWorld(world), this.explosion);
   }

   public void doExplosionA() {
      this.explosion.doExplosionA();
   }

   public void doExplosionB(boolean spawnParticles) {
      this.explosion.doExplosionB(spawnParticles);
   }

   public Object getInternal() {
      return this.explosion;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCExplosion that = (MCExplosion)o;
         return Objects.equals(this.explosion, that.explosion);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.explosion});
   }
}
