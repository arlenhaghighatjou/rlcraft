package org.orecruncher.dsurround.registry.acoustics;

import com.google.common.base.MoreObjects;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.client.footsteps.DelayedAcoustic;
import org.orecruncher.dsurround.registry.Registry;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.config.ModConfiguration;
import org.orecruncher.lib.MCHelper;

@SideOnly(Side.CLIENT)
public class AcousticRegistry extends Registry {
   private static final float default_volMin = 0.9F;
   private static final float default_volMax = 1.0F;
   private static final float default_pitchMin = 0.95F;
   private static final float default_pitchMax = 1.05F;
   private static final float DIVIDE = 100.0F;
   private static final Map<Material, String> materialProfiles = new Reference2ObjectOpenHashMap();
   private final Map<String, IAcoustic> acoustics = new Object2ObjectAVLTreeMap(String.CASE_INSENSITIVE_ORDER);
   private final Map<String, IAcoustic[]> compiled = new Object2ObjectAVLTreeMap(String.CASE_INSENSITIVE_ORDER);
   public static final IAcoustic[] EMPTY = new IAcoustic[0];
   public static final IAcoustic[] NOT_EMITTER = new IAcoustic[]{new NullAcoustic("NOT_EMITTER")};
   public static final IAcoustic[] MESSY_GROUND = new IAcoustic[]{new NullAcoustic("MESSY_GROUND")};
   private int hits;
   private int primitives;
   private int material;

   public AcousticRegistry() {
      super("Acoustic Registry");
   }

   protected void preInit() {
      this.hits = 0;
      this.primitives = 0;
      this.material = 0;
      this.acoustics.clear();
      this.compiled.clear();
      this.compiled.put("EMPTY", EMPTY);
      this.compiled.put("NOT_EMITTER", NOT_EMITTER);
      this.compiled.put("MESSY_GROUND", MESSY_GROUND);
   }

   protected void init(@Nonnull ModConfiguration cfg) {
      for(Entry<String, JsonElement> preAcoustics : cfg.acoustics.entrySet()) {
         String acousticsName = (String)preAcoustics.getKey();

         try {
            JsonObject acousticsDefinition = ((JsonElement)preAcoustics.getValue()).getAsJsonObject();
            EventSelectorAcoustics selector = new EventSelectorAcoustics(acousticsName);
            this.parseSelector(selector, acousticsDefinition);
            this.addAcoustic(selector);
         } catch (Throwable var7) {
            String msg = String.format("Unable to parse Json entry [%s]!", acousticsName);
            ModBase.log().error(msg, var7);
         }
      }

      for(Entry<String, String> prims : cfg.primitiveAcoustics.entrySet()) {
         this.compiled.put(prims.getKey(), this.compileAcoustics((String)prims.getValue()));
      }

   }

   protected void complete() {
      ModBase.log().info("[%s] %d cache hits during initialization", new Object[]{this.getName(), this.hits});
      ModBase.log().info("[%s] %d primitives by material generated", new Object[]{this.getName(), this.material});
      ModBase.log().info("[%s] %d primitives by sound generated", new Object[]{this.getName(), this.primitives});
   }

   @Nonnull
   public IAcoustic[] resolvePrimitive(@Nonnull IBlockState state) {
      if (state == Blocks.AIR.getDefaultState()) {
         return NOT_EMITTER;
      } else {
         SoundType type = MCHelper.getSoundType(state);
         String soundName;
         if (type != null && type.getStepSound() != null && !type.getStepSound().getSoundName().getResourceDomain().isEmpty()) {
            soundName = type.getStepSound().getSoundName().toString();
         } else {
            soundName = null;
         }

         IAcoustic[] acoustics = null;
         if (soundName == null || soundName.startsWith("minecraft")) {
            acoustics = this.resolveByMaterial(state);
         }

         if (acoustics != null) {
            ++this.material;
         }

         if (acoustics == null && StringUtils.isNotEmpty(soundName)) {
            acoustics = this.getPrimitive(soundName);
         }

         return (IAcoustic[])MoreObjects.firstNonNull(acoustics, EMPTY);
      }
   }

   @Nonnull
   private IAcoustic[] getPrimitive(@Nonnull String primitive) {
      IAcoustic[] result = (IAcoustic[])this.compiled.get(primitive);
      if (result == null) {
         IAcoustic a = this.generateAcoustic(primitive);
         this.compiled.put(primitive, result = new IAcoustic[]{a});
         ++this.primitives;
      }

      return result;
   }

   @Nullable
   private IAcoustic[] resolveByMaterial(@Nonnull IBlockState state) {
      IAcoustic[] result = null;
      String profile = (String)materialProfiles.get(state.getMaterial());
      if (StringUtils.isNotEmpty(profile)) {
         result = this.compileAcoustics(profile);
      }

      return result == EMPTY ? null : result;
   }

   private void addAcoustic(@Nonnull IAcoustic acoustic) {
      this.acoustics.put(acoustic.getName(), acoustic);
   }

   @Nullable
   public IAcoustic getAcoustic(@Nonnull String name) {
      return (IAcoustic)this.acoustics.get(name);
   }

   @Nonnull
   public IAcoustic[] compileAcoustics(@Nonnull String acousticName) {
      IAcoustic[] result = (IAcoustic[])this.compiled.get(acousticName);
      if (result == null) {
         result = (IAcoustic[])Arrays.stream(acousticName.split(",")).map(this::generateAcoustic).toArray((x$0) -> {
            return new IAcoustic[x$0];
         });
         if (result.length == 0) {
            result = EMPTY;
         }

         this.compiled.put(acousticName, result);
      } else {
         ++this.hits;
      }

      return result;
   }

   @Nonnull
   private IAcoustic generateAcoustic(@Nonnull String name) {
      IAcoustic a = (IAcoustic)this.acoustics.get(name);
      if (a == null) {
         ResourceLocation loc = new ResourceLocation(name);
         SoundEvent evt = RegistryManager.SOUND.getSound(loc);
         a = this.generateAcoustic(evt);
      }

      return a;
   }

   @Nonnull
   private IAcoustic generateAcoustic(@Nonnull SoundEvent evt) {
      IAcoustic result = (IAcoustic)this.acoustics.get(evt.getSoundName().toString());
      if (result == null) {
         result = new SimpleAcoustic(evt);
         this.acoustics.put(result.getName(), result);
      }

      return result;
   }

   private void parseSelector(EventSelectorAcoustics selector, JsonObject acousticsDefinition) {
      for(EventType i : EventType.values()) {
         String eventName = i.jsonName();
         if (acousticsDefinition.has(eventName)) {
            JsonElement unsolved = acousticsDefinition.get(eventName);
            IAcoustic acoustic = this.solveAcoustic(unsolved);
            selector.setAcousticPair(i, acoustic);
         }
      }

   }

   private IAcoustic solveAcoustic(JsonElement unsolved) {
      IAcoustic ret = null;
      if (unsolved.isJsonObject()) {
         ret = this.solveAcousticsCompound(unsolved.getAsJsonObject());
      } else if (unsolved.isJsonPrimitive() && unsolved.getAsJsonPrimitive().isString()) {
         SimpleAcoustic a = new SimpleAcoustic();
         this.prepareDefaults(a);
         this.setupSoundName(a, unsolved.getAsString());
         ret = a;
      }

      if (ret == null) {
         throw new IllegalStateException("Unresolved Json element: \r\n" + unsolved.toString());
      } else {
         return ret;
      }
   }

   private IAcoustic solveAcousticsCompound(JsonObject unsolved) {
      IAcoustic ret = null;
      if (unsolved.has("type") && !unsolved.get("type").getAsString().equals("basic")) {
         String type = unsolved.get("type").getAsString();
         byte var5 = -1;
         switch(type.hashCode()) {
         case -1290561483:
            if (type.equals("probability")) {
               var5 = 2;
            }
            break;
         case -1002697093:
            if (type.equals("simultaneous")) {
               var5 = 0;
            }
            break;
         case 1550348642:
            if (type.equals("delayed")) {
               var5 = 1;
            }
         }

         switch(var5) {
         case 0:
            List<IAcoustic> acoustics = new ArrayList();

            for(JsonElement subElement : unsolved.getAsJsonArray("array")) {
               acoustics.add(this.solveAcoustic(subElement));
            }

            ret = new SimultaneousAcoustic(acoustics);
            break;
         case 1:
            DelayedAcoustic a = new DelayedAcoustic();
            this.prepareDefaults(a);
            this.setupClassics(a, unsolved);
            if (unsolved.has("delay")) {
               a.setDelayMin((long)unsolved.get("delay").getAsInt());
               a.setDelayMax((long)unsolved.get("delay").getAsInt());
            } else {
               a.setDelayMin((long)unsolved.get("delay_min").getAsInt());
               a.setDelayMax((long)unsolved.get("delay_max").getAsInt());
            }

            ret = a;
            break;
         case 2:
            List<Integer> weights = new ArrayList();
            List<IAcoustic> acoustics = new ArrayList();
            JsonArray sim = unsolved.getAsJsonArray("array");
            Iterator<JsonElement> iter = sim.iterator();

            while(iter.hasNext()) {
               JsonElement subElement = (JsonElement)iter.next();
               weights.add(Integer.valueOf(subElement.getAsInt()));
               if (!iter.hasNext()) {
                  throw new IllegalStateException("Probability has odd number of children!");
               }

               subElement = (JsonElement)iter.next();
               acoustics.add(this.solveAcoustic(subElement));
            }

            ret = new ProbabilityWeightsAcoustic(acoustics, weights);
         }
      } else {
         SimpleAcoustic a = new SimpleAcoustic();
         this.prepareDefaults(a);
         this.setupClassics(a, unsolved);
         ret = a;
      }

      return ret;
   }

   private void prepareDefaults(@Nonnull SimpleAcoustic a) {
      a.setVolMin(0.9F);
      a.setVolMax(1.0F);
      a.setPitchMin(0.95F);
      a.setPitchMax(1.05F);
   }

   private void setupSoundName(@Nonnull SimpleAcoustic a, @Nonnull String soundName) {
      try {
         ResourceLocation res;
         if ("@".equals(soundName)) {
            res = null;
         } else if (soundName.contains(":")) {
            res = new ResourceLocation(soundName);
         } else if (soundName.charAt(0) != '@') {
            res = new ResourceLocation("dsurround", soundName);
         } else {
            res = new ResourceLocation("minecraft", soundName.substring(1));
         }

         if (res == null) {
            a.setSound((SoundEvent)null);
         } else {
            a.setSound(RegistryManager.SOUND.getSound(res));
         }
      } catch (Throwable var4) {
         ModBase.log().warn("Unable to locate sound [%s]", new Object[]{soundName});
         a.setSound((SoundEvent)null);
      }

   }

   private void setupClassics(SimpleAcoustic a, JsonObject solved) {
      this.setupSoundName(a, solved.get("name").getAsString());
      if (solved.has("vol_min")) {
         a.setVolMin(this.processPitchOrVolume(solved, "vol_min"));
      }

      if (solved.has("vol_max")) {
         a.setVolMax(this.processPitchOrVolume(solved, "vol_max"));
      }

      if (solved.has("pitch_min")) {
         a.setPitchMin(this.processPitchOrVolume(solved, "pitch_min"));
      }

      if (solved.has("pitch_max")) {
         a.setPitchMax(this.processPitchOrVolume(solved, "pitch_max"));
      }

   }

   private float processPitchOrVolume(@Nonnull JsonObject object, @Nonnull String param) {
      return object.get(param).getAsFloat() / 100.0F;
   }

   static {
      materialProfiles.put(Material.AIR, "NOT_EMITTER");
      materialProfiles.put(Material.GRASS, "grass");
      materialProfiles.put(Material.GROUND, "dirt");
      materialProfiles.put(Material.WOOD, "wood");
      materialProfiles.put(Material.ROCK, "stone");
      materialProfiles.put(Material.IRON, "hardmetal");
      materialProfiles.put(Material.ANVIL, "metalcompressed,hardmetal");
      materialProfiles.put(Material.WATER, "NOT_EMITTER");
      materialProfiles.put(Material.LAVA, "NOT_EMITTER");
      materialProfiles.put(Material.LEAVES, "leaves");
      materialProfiles.put(Material.PLANTS, "brush");
      materialProfiles.put(Material.VINE, "leaves");
      materialProfiles.put(Material.SPONGE, "organic_dry");
      materialProfiles.put(Material.CLOTH, "rug");
      materialProfiles.put(Material.FIRE, "NOT_EMITTER");
      materialProfiles.put(Material.SAND, "sand");
      materialProfiles.put(Material.CIRCUITS, "stoneutility");
      materialProfiles.put(Material.CARPET, "rug");
      materialProfiles.put(Material.GLASS, "glass");
      materialProfiles.put(Material.REDSTONE_LIGHT, "NOT_EMITTER");
      materialProfiles.put(Material.TNT, "equipment");
      materialProfiles.put(Material.CORAL, "NOT_EMITTER");
      materialProfiles.put(Material.ICE, "ice");
      materialProfiles.put(Material.PACKED_ICE, "ice");
      materialProfiles.put(Material.SNOW, "snow");
      materialProfiles.put(Material.CRAFTED_SNOW, "snow");
      materialProfiles.put(Material.CACTUS, "grass");
      materialProfiles.put(Material.CLAY, "dirt");
      materialProfiles.put(Material.GOURD, "organic_dry");
      materialProfiles.put(Material.DRAGON_EGG, "obsidian");
      materialProfiles.put(Material.PORTAL, "NOT_EMITTER");
      materialProfiles.put(Material.CAKE, "organic");
      materialProfiles.put(Material.WEB, "NOT_EMITTER");
      materialProfiles.put(Material.PISTON, "stonemachine");
      materialProfiles.put(Material.BARRIER, "glass");
      materialProfiles.put(Material.STRUCTURE_VOID, "NOT_EMITTER");
   }
}
