package org.orecruncher.dsurround.client.handlers;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.fx.particle.system.ParticleSystem;
import org.orecruncher.lib.BlockPosHelper;

@SideOnly(Side.CLIENT)
public class ParticleSystemHandler extends EffectHandlerBase {
   private static ParticleSystemHandler _instance = null;
   private final Object2ObjectOpenHashMap<BlockPos, ParticleSystem> systems = new Object2ObjectOpenHashMap();

   public ParticleSystemHandler() {
      super("Particle Systems");
   }

   public boolean doTick(int tick) {
      return !this.systems.isEmpty();
   }

   public void process(@Nonnull EntityPlayer player) {
      double range = (double)ModOptions.effects.specialEffectRange;
      BlockPos min = EnvironStateHandler.EnvironState.getPlayerPosition().add(-range, -range, -range);
      BlockPos max = EnvironStateHandler.EnvironState.getPlayerPosition().add(range, range, range);
      this.systems.object2ObjectEntrySet().removeIf((entry) -> {
         ParticleSystem system = (ParticleSystem)entry.getValue();
         if (BlockPosHelper.notContains(system.getPos(), min, max)) {
            system.setExpired();
         } else {
            system.onUpdate();
         }

         return !system.isAlive();
      });
   }

   public void onConnect() {
      _instance = this;
      this.systems.clear();
   }

   public void onDisconnect() {
      this.systems.clear();
      _instance = null;
   }

   public static boolean okToSpawn(@Nonnull BlockPos pos) {
      return !_instance.systems.containsKey(pos);
   }

   public static void addSystem(@Nonnull ParticleSystem system) {
      _instance.systems.put(system.getPos().toImmutable(), system);
   }
}
