package com.creativemd.creativecore;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.group.Usergroup;
import com.creativemd.creativecore.common.config.premade.NamedList;
import com.creativemd.creativecore.common.config.sync.ConfigSynchronization;
import com.creativemd.creativecore.common.utils.player.PlayerSelector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;

public class CreativeCoreConfig {
   @CreativeConfig(
      name = "use-stencil",
      type = ConfigSynchronization.CLIENT
   )
   public boolean useStencil = true;
   @CreativeConfig(
      name = "use-optifine-compat",
      type = ConfigSynchronization.CLIENT
   )
   public boolean useOptifineCompat = true;
   @CreativeConfig(
      type = ConfigSynchronization.CLIENT
   )
   public List<CreativeCoreConfig.ColorPalette> palette = Arrays.asList(new CreativeCoreConfig.ColorPalette("basic", new int[]{-1, -16777216, -65536, -16711936, -16776961}));
   @CreativeConfig
   public NamedList<Usergroup> usergroups = new NamedList<Usergroup>();

   public CreativeCoreConfig() {
      this.usergroups.put("creative", new Usergroup(new PlayerSelector[]{new PlayerSelector.PlayerSelectorGamemode(GameType.CREATIVE)}));
      this.usergroups.put("survival", new Usergroup(new PlayerSelector[]{new PlayerSelector.PlayerSelectorGamemode(GameType.SURVIVAL)}));
      this.usergroups.put("adventure", new Usergroup(new PlayerSelector[]{new PlayerSelector.PlayerSelectorGamemode(GameType.ADVENTURE)}));
   }

   public boolean is(EntityPlayer player, String usergroup) {
      Usergroup group = (Usergroup)this.usergroups.get(usergroup);
      return group != null ? group.is(player) : false;
   }

   public static class ColorPalette {
      @CreativeConfig
      public String name = "";
      @CreativeConfig
      public List<Integer> colors = new ArrayList();

      public ColorPalette() {
      }

      public ColorPalette(String name, int... colors) {
         this.name = name;

         for(int i = 0; i < colors.length; ++i) {
            this.colors.add(Integer.valueOf(colors[i]));
         }

      }
   }
}
