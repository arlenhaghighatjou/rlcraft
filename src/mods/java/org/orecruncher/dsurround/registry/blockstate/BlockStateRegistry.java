package org.orecruncher.dsurround.registry.blockstate;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.client.fx.BlockEffect;
import org.orecruncher.dsurround.client.fx.BlockEffectType;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.registry.Registry;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.config.BlockConfig;
import org.orecruncher.dsurround.registry.config.EffectConfig;
import org.orecruncher.dsurround.registry.config.ModConfiguration;
import org.orecruncher.dsurround.registry.config.SoundConfig;
import org.orecruncher.dsurround.registry.sound.SoundRegistry;

@SideOnly(Side.CLIENT)
public final class BlockStateRegistry extends Registry {
   private Map<BlockStateMatcher, BlockStateProfile> registry;
   private int blockStates;

   public BlockStateRegistry() {
      super("BlockState Registry");
   }

   protected void preInit() {
      this.registry = new Object2ObjectOpenHashMap();
      this.getBlockStates().forEach((state) -> {
         BlockStateUtil.setStateData(state, (BlockStateData)null);
      });
      BlockStateUtil.setStateData(Blocks.AIR.getDefaultState(), BlockStateData.DEFAULT);
   }

   protected void init(@Nonnull ModConfiguration cfg) {
      cfg.blocks.forEach(this::register);
   }

   protected void postInit() {
      this.blockStates = (int)this.getBlockStates().map(this::get).count();
   }

   protected void complete() {
      ModBase.log().info("[%s] %d block states processed, %d registry entries", new Object[]{this.getName(), this.blockStates, this.registry.size()});
      this.registry = null;
   }

   private Stream<IBlockState> getBlockStates() {
      return StreamSupport.stream(ForgeRegistries.BLOCKS.spliterator(), false).map((block) -> {
         return block.getBlockState().getValidStates();
      }).flatMap(Collection::stream);
   }

   @Nonnull
   private BlockStateData get(@Nonnull IBlockState state) {
      BlockStateData profile = BlockStateUtil.getStateDataRaw(state);
      if (profile == null) {
         if (this.registry == null) {
            ModBase.log().warn("Unknown blockstate encountered '%s'", new Object[]{state.toString()});
         } else {
            profile = (BlockStateData)this.registry.get(BlockStateMatcher.create(state));
            if (profile == null) {
               profile = (BlockStateData)this.registry.get(BlockStateMatcher.asGeneric(state));
            }
         }

         if (profile == null) {
            profile = BlockStateData.DEFAULT;
         }

         BlockStateUtil.setStateData(state, profile);
      }

      return profile;
   }

   @Nullable
   private BlockStateProfile getOrCreateProfile(@Nonnull BlockStateMatcher info) {
      if (info.getBlock() == Blocks.AIR) {
         return null;
      } else {
         BlockStateProfile profile = (BlockStateProfile)this.registry.get(info);
         if (profile == null) {
            profile = new BlockStateProfile();
            this.registry.put(info, profile);
         }

         return profile;
      }
   }

   private void register(@Nonnull BlockConfig entry) {
      if (!entry.blocks.isEmpty()) {
         SoundRegistry soundRegistry = RegistryManager.SOUND;

         for(String blockName : entry.blocks) {
            BlockStateMatcher blockInfo = BlockStateMatcher.create(blockName);
            if (blockInfo == null) {
               ModBase.log().warn("Unknown block [%s] in block config file", new Object[]{blockName});
            } else {
               BlockStateProfile blockData = this.getOrCreateProfile(blockInfo);
               if (blockData == null) {
                  ModBase.log().warn("Unknown block [%s] in block config file", new Object[]{blockName});
               } else {
                  if (entry.soundReset != null && entry.soundReset.booleanValue()) {
                     blockData.clearSounds();
                  }

                  if (entry.effectReset != null && entry.effectReset.booleanValue()) {
                     blockData.clearEffects();
                  }

                  if (entry.chance != null) {
                     blockData.setChance(entry.chance.intValue());
                  }

                  for(SoundConfig sr : entry.sounds) {
                     if (sr.sound != null && !soundRegistry.isSoundBlocked(new ResourceLocation(sr.sound))) {
                        SoundEffect.Builder b = new SoundEffect.Builder(sr);
                        if (sr.soundCategory == null) {
                           b.setSoundCategory(SoundCategory.BLOCKS);
                        }

                        SoundEffect eff = b.build();
                        blockData.addSound(eff);
                     }
                  }

                  for(EffectConfig e : entry.effects) {
                     if (!StringUtils.isEmpty(e.effect)) {
                        BlockEffectType type = BlockEffectType.get(e.effect);
                        if (type == BlockEffectType.UNKNOWN) {
                           ModBase.log().warn("Unknown block effect type in configuration: [%s]", new Object[]{e.effect});
                        } else if (type.isEnabled()) {
                           int chance = e.chance != null ? e.chance.intValue() : 100;
                           BlockEffect blockEffect = type.getInstance(chance);
                           if (blockEffect != null) {
                              if (e.conditions != null) {
                                 blockEffect.setConditions(e.conditions);
                              }

                              blockData.addEffect(blockEffect);
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }
}
