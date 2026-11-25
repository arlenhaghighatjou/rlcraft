package codersafterdark.compatskills.utils;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

public class CompatSkillsConfig {
   public CompatSkillsConfig() {
   }

   public static void postInit(FMLPostInitializationEvent event) {
   }

   @Config(
      modid = "compatskills",
      type = Type.INSTANCE,
      name = "compatskills"
   )
   public static class Configs {
      public static CompatSkillsConfig.Configs.Modules modules;
      public static CompatSkillsConfig.Configs.Hwyla hwyla;
      public static CompatSkillsConfig.Configs.TOP top;
      public static CompatSkillsConfig.Configs.Minecraft minecraft;

      public Configs() {
      }

      public static class Hwyla {
         @Comment({"Should Hwyla Requirements Support require Shifting?"})
         public static boolean HwylaShifting;

         public Hwyla() {
         }
      }

      public static class Minecraft {
         @Comment({"Should locked Block Drops post their requirements?"})
         public static boolean BlockDropsError;

         public Minecraft() {
         }
      }

      public static class Modules {
         @Comment({"A list of all mods that CompatSkills has integrated compatability with.", "Setting any of these to false disables the respective compat:"})
         public static Map<String, Boolean> compat = Maps.newHashMap(Maps.toMap(CompatModuleBase.moduleClasses.keySet(), (k) -> {
            return Boolean.TRUE;
         }));

         public Modules() {
         }
      }

      public static class TOP {
         @Comment({"Should TOP Requirements Support require Shifting?"})
         public static boolean TOPShifting;

         public TOP() {
         }
      }
   }
}
