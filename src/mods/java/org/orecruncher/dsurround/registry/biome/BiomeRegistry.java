package org.orecruncher.dsurround.registry.biome;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.dimension.IDimensionInfo;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.registry.Registry;
import org.orecruncher.dsurround.registry.config.BiomeConfig;
import org.orecruncher.dsurround.registry.config.ModConfiguration;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public final class BiomeRegistry extends Registry {
   private static final int INSIDE_Y_ADJUST = 3;
   public static final FakeBiome UNDERGROUND = new FakeBiome("Underground");
   public static final FakeBiome PLAYER = new FakeBiome("Player");
   public static final FakeBiome UNDERWATER = new FakeBiome("Underwater");
   public static final FakeBiome UNDEROCEAN = new FakeBiome("UnderOCN");
   public static final FakeBiome UNDERDEEPOCEAN = new FakeBiome("UnderDOCN");
   public static final FakeBiome UNDERRIVER = new FakeBiome("UnderRVR");
   public static final FakeBiome OUTERSPACE = new FakeBiome("OuterSpace");
   public static final FakeBiome CLOUDS = new FakeBiome("Clouds");
   public static final FakeBiome VILLAGE = new FakeBiome("Village");
   public static final FakeBiome BATTLE_MUSIC = new FakeBiome("BattleMusic");
   public BiomeInfo UNDERGROUND_INFO;
   public BiomeInfo PLAYER_INFO;
   public BiomeInfo UNDERRIVER_INFO;
   public BiomeInfo UNDEROCEAN_INFO;
   public BiomeInfo UNDERDEEPOCEAN_INFO;
   public BiomeInfo UNDERWATER_INFO;
   public BiomeInfo VILLAGE_INFO;
   public BiomeInfo CLOUDS_INFO;
   public BiomeInfo OUTERSPACE_INFO;
   public BiomeInfo BATTLE_MUSIC_INFO;
   public BiomeInfo WTF_INFO;
   private static final FakeBiome WTF = new WTFFakeBiome();
   private final Map<String, String> biomeAliases = new Object2ObjectOpenHashMap();
   private final ObjectOpenHashSet<FakeBiome> theFakes = new ObjectOpenHashSet();

   public BiomeRegistry() {
      super("Biome Registry");
   }

   protected void preInit() {
      this.biomeAliases.clear();
      this.theFakes.clear();

      for(String entry : ModOptions.biomes.biomeAliases) {
         String[] parts = StringUtils.split(entry, "=");
         if (parts.length == 2) {
            this.biomeAliases.put(parts[0], parts[1]);
         }
      }

      ForgeRegistries.BIOMES.getValuesCollection().forEach(this::register);
      this.register(UNDERWATER);
      this.register(UNDEROCEAN);
      this.register(UNDERDEEPOCEAN);
      this.register(UNDERRIVER);
      this.register(PLAYER);
      this.register(VILLAGE);
      this.register(UNDERGROUND);
      this.register(CLOUDS);
      this.register(OUTERSPACE);
      this.register(BATTLE_MUSIC);
      this.UNDERGROUND_INFO = this.resolve(UNDERGROUND);
      this.PLAYER_INFO = this.resolve(PLAYER);
      this.UNDERRIVER_INFO = this.resolve(UNDERRIVER);
      this.UNDEROCEAN_INFO = this.resolve(UNDEROCEAN);
      this.UNDERDEEPOCEAN_INFO = this.resolve(UNDERDEEPOCEAN);
      this.UNDERWATER_INFO = this.resolve(UNDERWATER);
      this.VILLAGE_INFO = this.resolve(VILLAGE);
      this.CLOUDS_INFO = this.resolve(CLOUDS);
      this.OUTERSPACE_INFO = this.resolve(OUTERSPACE);
      this.BATTLE_MUSIC_INFO = this.resolve(BATTLE_MUSIC);
      this.register(WTF);
      this.WTF_INFO = this.resolve(WTF);
   }

   protected void init(@Nonnull ModConfiguration cfg) {
      cfg.biomeAlias.forEach(this::registerBiomeAlias);
      List<BiomeInfo> infoList = this.getCombinedStream();

      for(BiomeConfig c : cfg.biomes) {
         BiomeMatcher matcher = BiomeMatcher.getMatcher(c);

         for(BiomeInfo bi : infoList) {
            if (matcher.match(bi)) {
               bi.update(c);
            }
         }
      }

      ResourceLocation plainsLoc = new ResourceLocation("plains");
      Biome plains = (Biome)ForgeRegistries.BIOMES.getValue(plainsLoc);
      if (plains != null) {
         BiomeInfo info = BiomeUtil.getBiomeData(plains);
         BiomeUtil.setBiomeData(Biomes.PLAINS, info);
      } else {
         ModBase.log().warn("Biome PLAINS cannot be found in Forge registry!", new Object[0]);
      }

   }

   protected void complete() {
      if (ModOptions.logging.enableDebugLogging) {
         ModBase.log().info("*** BIOME REGISTRY ***", new Object[0]);
         this.getCombinedStream().stream().sorted().map(Object::toString).forEach((x$0) -> {
            rec$.info(x$0, new Object[0]);
         });
      }

      this.biomeAliases.clear();
   }

   private void register(@Nonnull Biome biome) {
      BiomeHandler handler = new BiomeHandler(biome);
      BiomeInfo info = new BiomeInfo(handler);
      BiomeUtil.setBiomeData(biome, info);
   }

   private void register(@Nonnull IBiome biome) {
      if (biome.isFake()) {
         FakeBiome fb = (FakeBiome)biome;
         BiomeInfo info = new BiomeInfo(fb);
         fb.setBiomeData(info);
         this.theFakes.add(fb);
      }

   }

   @Nullable
   private BiomeInfo resolve(@Nonnull IBiome biome) {
      if (biome.isFake()) {
         FakeBiome fb = (FakeBiome)biome;
         return fb.getBiomeData();
      } else {
         return null;
      }
   }

   @Nonnull
   public BiomeInfo getPlayerBiome(@Nonnull EntityPlayer player, boolean getTrue) {
      Biome biome = player.getEntityWorld().getBiome(new BlockPos(player.posX, 0.0D, player.posZ));
      BiomeInfo info = BiomeUtil.getBiomeData(biome);
      if (!getTrue) {
         if (player.isInsideOfMaterial(Material.WATER)) {
            if (info.isRiver()) {
               info = this.UNDERRIVER_INFO;
            } else if (info.isDeepOcean()) {
               info = this.UNDERDEEPOCEAN_INFO;
            } else if (info.isOcean()) {
               info = this.UNDEROCEAN_INFO;
            } else {
               info = this.UNDERWATER_INFO;
            }
         } else {
            IDimensionInfo dimInfo = EnvironStateHandler.EnvironState.getDimensionInfo();
            int theY = MathStuff.floor(player.posY);
            if (theY + 3 <= dimInfo.getSeaLevel()) {
               info = this.UNDERGROUND_INFO;
            } else if (theY >= dimInfo.getSpaceHeight()) {
               info = this.OUTERSPACE_INFO;
            } else if (theY >= dimInfo.getCloudHeight()) {
               info = this.CLOUDS_INFO;
            }
         }
      }

      return info;
   }

   private void registerBiomeAlias(@Nonnull String alias, @Nonnull String biome) {
      this.biomeAliases.put(alias, biome);
   }

   private List<BiomeInfo> getCombinedStream() {
      ArrayList<BiomeInfo> infos = new ArrayList();

      for(Biome b : ForgeRegistries.BIOMES.getValuesCollection()) {
         BiomeInfo info = BiomeUtil.getBiomeData(b);
         infos.add(info);
      }

      ObjectIterator var5 = this.theFakes.iterator();

      while(var5.hasNext()) {
         FakeBiome b = (FakeBiome)var5.next();
         BiomeInfo info = b.getBiomeData();
         if (info != null) {
            infos.add(info);
         }
      }

      return infos;
   }
}
