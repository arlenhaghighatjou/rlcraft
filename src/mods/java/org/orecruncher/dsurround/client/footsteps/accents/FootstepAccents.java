package org.orecruncher.dsurround.client.footsteps.accents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.footsteps.IFootstepAccentProvider;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public class FootstepAccents {
   private static final List<IFootstepAccentProvider> providers = new ArrayList();

   private FootstepAccents() {
   }

   public static void provide(@Nonnull EntityLivingBase entity, @Nullable BlockPos pos, @Nonnull ObjectArray<IAcoustic> in) {
      providers.forEach((provider) -> {
         provider.provide(entity, pos, in);
      });
   }

   static {
      providers.add(new ArmorAccents());
      providers.add(new RainSplashAccent());
   }
}
