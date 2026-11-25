package vazkii.quark.base.lib;

import java.util.Arrays;
import java.util.List;
import net.minecraft.util.ResourceLocation;

public final class LibMisc {
   public static final String DEV_ENV_HACK = "[GRADLE.VERSION-GRADLE.BUILD,),";
   public static final String MOD_ID = "quark";
   public static final String MOD_NAME = "quark";
   public static final String BUILD = "179";
   public static final String VERSION = "r1.6-179";
   public static final String DEPENDENCIES = "required-after:forge@[14.23.5.2831,);required-before:autoreglib@[1.3-32,);after:jei@[4.6.0,);";
   public static final String PREFIX_MOD = "quark:";
   public static final String PROXY_COMMON = "vazkii.quark.base.proxy.CommonProxy";
   public static final String PROXY_CLIENT = "vazkii.quark.base.proxy.ClientProxy";
   public static final String GUI_FACTORY = "vazkii.quark.base.client.gui.GuiFactory";
   public static final List<String> OREDICT_DYES = Arrays.asList("dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite");
   public static final String MOD_WEBSITE = "https://quark.vazkii.net";
   public static final ResourceLocation GENERAL_ICONS_RESOURCE = new ResourceLocation("quark", "textures/misc/general_icons.png");

   public LibMisc() {
   }
}
