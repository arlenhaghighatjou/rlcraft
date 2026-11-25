package org.orecruncher.dsurround.client.footsteps.accents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilitySeasonInfo;
import org.orecruncher.dsurround.capabilities.season.ISeasonInfo;
import org.orecruncher.dsurround.capabilities.season.PrecipitationType;
import org.orecruncher.dsurround.client.footsteps.IFootstepAccentProvider;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.dsurround.registry.biome.BiomeInfo;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public class RainSplashAccent implements IFootstepAccentProvider {
   protected final MutableBlockPos mutable = new MutableBlockPos();

   public RainSplashAccent() {
   }

   @Nonnull
   public String getName() {
      return "Rain Splash Accent";
   }

   public void provide(@Nonnull EntityLivingBase entity, @Nullable BlockPos blockPos, @Nonnull ObjectArray<IAcoustic> in) {
      if (ModOptions.sound.enablePuddleSound && RegistryManager.FOOTSTEPS.SPLASH != null && Weather.isRaining() && EnvironStateHandler.EnvironState.isPlayer(entity)) {
         if (blockPos != null) {
            this.mutable.setPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ());
         } else {
            this.mutable.setPos(entity);
         }

         World world = entity.getEntityWorld();
         ISeasonInfo season = CapabilitySeasonInfo.getCapability(world);
         if (season != null) {
            int precipHeight = season.getPrecipitationHeight(this.mutable).getY();
            if (precipHeight == this.mutable.getY()) {
               PrecipitationType pt = season.getPrecipitationType(this.mutable, (BiomeInfo)null);
               if (pt == PrecipitationType.RAIN) {
                  in.addAll(RegistryManager.FOOTSTEPS.SPLASH);
               }
            }
         }
      }

   }
}
