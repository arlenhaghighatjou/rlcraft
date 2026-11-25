package org.orecruncher.dsurround.mixins;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.lang.reflect.Field;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({SoundCategory.class})
public abstract class MixinSoundCategory {
   public MixinSoundCategory() {
   }

   @Nonnull
   private static Field findField() throws NoSuchFieldException, SecurityException {
      Preconditions.checkNotNull(SoundCategory.class);
      Preconditions.checkArgument(StringUtils.isNotEmpty("SOUND_CATEGORIES"), "Field name cannot be empty");
      String nameToFind = FMLLaunchHandler.isDeobfuscatedEnvironment() ? "SOUND_CATEGORIES" : (String)MoreObjects.firstNonNull("SOUND_CATEGORIES", "SOUND_CATEGORIES");
      Field f = SoundCategory.class.getDeclaredField(nameToFind);
      f.setAccessible(true);
      return f;
   }

   static {
      if (SoundCategory.getByName("ds_footsteps") == null) {
         Class<?>[] parms = new Class[]{String.class};
         SoundCategory fs = (SoundCategory)EnumHelper.addEnum(SoundCategory.class, "DS_FOOTSTEPS", parms, new Object[]{"ds_footsteps"});
         SoundCategory b = (SoundCategory)EnumHelper.addEnum(SoundCategory.class, "DS_BIOME", parms, new Object[]{"ds_biome"});

         try {
            Field f = findField();
            Map<String, SoundCategory> theMap = (Map)f.get((Object)null);
            theMap.put(fs.getName(), fs);
            theMap.put(b.getName(), b);
         } catch (Throwable var5) {
            ;
         }
      }

   }
}
