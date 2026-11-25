package vazkii.quark.world.feature;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;

public class DefaultWorldOptions extends Feature {
   private static final Map<String, Integer> intProps = new HashMap();
   private static final Map<String, Double> doubleProps = new HashMap();
   private static final Map<String, Boolean> boolProps = new HashMap();
   public static String config;

   public DefaultWorldOptions() {
   }

   public void setupConfig() {
      StringBuilder config = new StringBuilder();
      config.append('{');

      for(String s : intProps.keySet()) {
         int i = this.loadPropInt(s, "", ((Integer)intProps.get(s)).intValue());
         config.append('"').append(s).append("\":").append(i).append(',');
      }

      for(String s : doubleProps.keySet()) {
         double d = this.loadPropDouble(s, "", ((Double)doubleProps.get(s)).doubleValue());
         config.append('"').append(s).append("\":").append(d).append(',');
      }

      for(String s : boolProps.keySet()) {
         boolean b = this.loadPropBool(s, "", ((Boolean)boolProps.get(s)).booleanValue());
         config.append('"').append(s).append("\":").append(b).append(',');
      }

      config = config.toString().replaceAll(",$", "}");
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void guiOpenEvent(Post event) {
      if (event.getGui() instanceof GuiCreateWorld) {
         GuiCreateWorld create = (GuiCreateWorld)event.getGui();
         if (create.chunkProviderSettingsJson == null || create.chunkProviderSettingsJson.isEmpty()) {
            create.chunkProviderSettingsJson = config;
         }
      }

   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   static {
      intProps.put("seaLevel", Integer.valueOf(63));
      intProps.put("dungeonChance", Integer.valueOf(8));
      intProps.put("waterLakeChance", Integer.valueOf(4));
      intProps.put("lavaLakeChance", Integer.valueOf(80));
      intProps.put("fixedBiome", Integer.valueOf(-1));
      intProps.put("biomeSize", Integer.valueOf(4));
      intProps.put("riverSize", Integer.valueOf(4));
      intProps.put("dirtSize", Integer.valueOf(33));
      intProps.put("dirtCount", Integer.valueOf(10));
      intProps.put("dirtMinHeight", Integer.valueOf(0));
      intProps.put("dirtMaxHeight", Integer.valueOf(256));
      intProps.put("gravelSize", Integer.valueOf(33));
      intProps.put("gravelCount", Integer.valueOf(8));
      intProps.put("gravelMinHeight", Integer.valueOf(0));
      intProps.put("gravelMaxHeight", Integer.valueOf(256));
      intProps.put("graniteSize", Integer.valueOf(33));
      intProps.put("graniteCount", Integer.valueOf(10));
      intProps.put("graniteMinHeight", Integer.valueOf(0));
      intProps.put("graniteMaxHeight", Integer.valueOf(80));
      intProps.put("dioriteSize", Integer.valueOf(33));
      intProps.put("dioriteCount", Integer.valueOf(10));
      intProps.put("dioriteMinHeight", Integer.valueOf(0));
      intProps.put("dioriteMaxHeight", Integer.valueOf(80));
      intProps.put("andesiteSize", Integer.valueOf(33));
      intProps.put("andesiteCount", Integer.valueOf(10));
      intProps.put("andesiteMinHeight", Integer.valueOf(0));
      intProps.put("andesiteMaxHeight", Integer.valueOf(80));
      intProps.put("coalSize", Integer.valueOf(17));
      intProps.put("coalCount", Integer.valueOf(20));
      intProps.put("coalMinHeight", Integer.valueOf(0));
      intProps.put("coalMaxHeight", Integer.valueOf(128));
      intProps.put("ironSize", Integer.valueOf(9));
      intProps.put("ironCount", Integer.valueOf(20));
      intProps.put("ironMinHeight", Integer.valueOf(0));
      intProps.put("ironMaxHeight", Integer.valueOf(64));
      intProps.put("goldSize", Integer.valueOf(9));
      intProps.put("goldCount", Integer.valueOf(2));
      intProps.put("goldMinHeight", Integer.valueOf(0));
      intProps.put("goldMaxHeight", Integer.valueOf(32));
      intProps.put("redstoneSize", Integer.valueOf(8));
      intProps.put("redstoneCount", Integer.valueOf(8));
      intProps.put("redstoneMinHeight", Integer.valueOf(0));
      intProps.put("redstoneMaxHeight", Integer.valueOf(16));
      intProps.put("diamondSize", Integer.valueOf(8));
      intProps.put("diamondCount", Integer.valueOf(1));
      intProps.put("diamondMinHeight", Integer.valueOf(0));
      intProps.put("diamondMaxHeight", Integer.valueOf(16));
      intProps.put("lapisSize", Integer.valueOf(7));
      intProps.put("lapisCount", Integer.valueOf(1));
      intProps.put("lapisCenterHeight", Integer.valueOf(16));
      intProps.put("lapisSpread", Integer.valueOf(16));
      doubleProps.put("coordinateScale", Double.valueOf(684.412D));
      doubleProps.put("heightScale", Double.valueOf(684.412D));
      doubleProps.put("lowerLimitScale", Double.valueOf(512.0D));
      doubleProps.put("upperLimitScale", Double.valueOf(512.0D));
      doubleProps.put("depthNoiseScaleX", Double.valueOf(200.0D));
      doubleProps.put("depthNoiseScaleZ", Double.valueOf(200.0D));
      doubleProps.put("depthNoiseScaleExponent", Double.valueOf(0.5D));
      doubleProps.put("mainNoiseScaleX", Double.valueOf(80.0D));
      doubleProps.put("mainNoiseScaleY", Double.valueOf(160.0D));
      doubleProps.put("mainNoiseScaleZ", Double.valueOf(80.0D));
      doubleProps.put("baseSize", Double.valueOf(8.5D));
      doubleProps.put("stretchY", Double.valueOf(12.0D));
      doubleProps.put("biomeDepthWeight", Double.valueOf(1.0D));
      doubleProps.put("biomeDepthOffset", Double.valueOf(0.0D));
      doubleProps.put("biomeScaleWeight", Double.valueOf(1.0D));
      doubleProps.put("biomeScaleOffset", Double.valueOf(0.0D));
      boolProps.put("useCaves", Boolean.valueOf(true));
      boolProps.put("useDungeons", Boolean.valueOf(true));
      boolProps.put("useStrongholds", Boolean.valueOf(true));
      boolProps.put("useVillages", Boolean.valueOf(true));
      boolProps.put("useMineShafts", Boolean.valueOf(true));
      boolProps.put("useTemples", Boolean.valueOf(true));
      boolProps.put("useMonuments", Boolean.valueOf(true));
      boolProps.put("useRavines", Boolean.valueOf(true));
      boolProps.put("useWaterLakes", Boolean.valueOf(true));
      boolProps.put("useLavaLakes", Boolean.valueOf(true));
      boolProps.put("useLavaOceans", Boolean.valueOf(false));
   }
}
