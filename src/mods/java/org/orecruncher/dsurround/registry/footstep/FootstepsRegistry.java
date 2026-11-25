package org.orecruncher.dsurround.registry.footstep;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.footsteps.Generator;
import org.orecruncher.dsurround.client.footsteps.GeneratorQP;
import org.orecruncher.dsurround.registry.Registry;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.acoustics.AcousticRegistry;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.dsurround.registry.acoustics.RainSplashAcoustic;
import org.orecruncher.dsurround.registry.blockstate.BlockStateMatcher;
import org.orecruncher.dsurround.registry.config.ModConfiguration;
import org.orecruncher.dsurround.registry.config.VariatorConfig;
import org.orecruncher.dsurround.registry.effect.EntityEffectInfo;
import org.orecruncher.lib.ItemStackUtil;
import org.orecruncher.lib.MCHelper;

@SideOnly(Side.CLIENT)
public final class FootstepsRegistry extends Registry {
   private static final List<String> FOOTPRINT_SOUND_PROFILE = Arrays.asList("minecraft:block.sand.step", "minecraft:block.gravel.step", "minecraft:block.snow.step");
   private BlockMap blockMap;
   private Set<Material> FOOTPRINT_MATERIAL;
   private Set<IBlockState> FOOTPRINT_STATES;
   private Map<String, Variator> variators;
   private Variator defaultVariator;
   private Variator childVariator;
   private Variator playerVariator;
   private Variator playerQuadrupedVariator;
   private Set<IBlockState> missingAcoustics;
   public IAcoustic[] SWIM;
   public IAcoustic[] JUMP;
   public IAcoustic[] SPLASH;

   public FootstepsRegistry() {
      super("Footsteps Registry");
   }

   protected void preInit() {
      AcousticRegistry acoustics = RegistryManager.ACOUSTICS;
      this.blockMap = new BlockMap(acoustics);
      this.FOOTPRINT_MATERIAL = new ReferenceOpenHashSet();
      this.FOOTPRINT_STATES = new ReferenceOpenHashSet();
      this.variators = new Object2ObjectOpenHashMap();
      this.FOOTPRINT_MATERIAL.add(Material.CLAY);
      this.FOOTPRINT_MATERIAL.add(Material.GRASS);
      this.FOOTPRINT_MATERIAL.add(Material.GROUND);
      this.FOOTPRINT_MATERIAL.add(Material.ICE);
      this.FOOTPRINT_MATERIAL.add(Material.PACKED_ICE);
      this.FOOTPRINT_MATERIAL.add(Material.SAND);
      this.FOOTPRINT_MATERIAL.add(Material.CRAFTED_SNOW);
      this.FOOTPRINT_MATERIAL.add(Material.SNOW);
      this.SWIM = null;
      this.JUMP = null;
      this.SPLASH = null;
      this.seedMap();
   }

   protected void init(@Nonnull ModConfiguration cfg) {
      for(ModConfiguration.ForgeEntry entry : cfg.forgeMappings) {
         for(String name : entry.dictionaryEntries) {
            this.registerForgeEntries(entry.acousticProfile, name);
         }
      }

      cfg.footsteps.forEach((k, v) -> {
         this.registerBlocks(v, k);
      });
      cfg.footprints.forEach((xva$0) -> {
         rec$.registerFootprint(xva$0);
      });
      this.variators.putAll((Map)cfg.variators.entrySet().stream().collect(Collectors.toMap(Entry::getKey, (e) -> {
         return new Variator((VariatorConfig)e.getValue());
      })));
   }

   protected void postInit() {
      this.SWIM = RegistryManager.ACOUSTICS.compileAcoustics("_SWIM");
      this.JUMP = RegistryManager.ACOUSTICS.compileAcoustics("_JUMP");
      this.SPLASH = new IAcoustic[]{new RainSplashAcoustic(RegistryManager.ACOUSTICS.compileAcoustics("waterfine"))};
      this.defaultVariator = this.getVariator("default");
      this.childVariator = this.getVariator("child");
      this.playerVariator = this.getVariator(ModOptions.sound.firstPersonFootstepCadence ? "playerSlow" : "player");
      this.playerQuadrupedVariator = this.getVariator(ModOptions.sound.firstPersonFootstepCadence ? "quadrupedSlow" : "quadruped");
      Set<IBlockState> blockStates = (Set)StreamSupport.stream(ForgeRegistries.BLOCKS.spliterator(), false).map((block) -> {
         return block.getBlockState().getValidStates();
      }).flatMap(Collection::stream).collect(Collectors.toSet());
      this.missingAcoustics = (Set)blockStates.stream().filter((bs) -> {
         return !this.getBlockMap().hasAcoustics(bs);
      }).collect(Collectors.toSet());
      blockStates.stream().filter((bs) -> {
         return bs.getMaterial().blocksMovement() && !this.hasFootprint(bs);
      }).filter((bs) -> {
         SoundType sound = MCHelper.getSoundType(bs);
         if (sound != null) {
            SoundEvent event = sound.getStepSound();
            ResourceLocation resource = event.getSoundName();
            String soundName = resource.toString();
            return FOOTPRINT_SOUND_PROFILE.contains(soundName);
         } else {
            return false;
         }
      }).forEach((bs) -> {
         this.FOOTPRINT_STATES.add(bs);
      });
   }

   protected void complete() {
      if (ModOptions.logging.enableDebugLogging && this.missingAcoustics.size() > 0) {
         ModBase.log().info("          >>>> MISSING ACOUSTIC ENTRIES <<<< ", new Object[0]);
         ModBase.log().info("Sounds for these states will default to their step sound", new Object[0]);
         ModBase.log().info("========================================================", new Object[0]);
         this.missingAcoustics.stream().map(Object::toString).sorted().forEach((x$0) -> {
            rec$.info(x$0, new Object[0]);
         });
      }

      this.missingAcoustics = null;
   }

   private void seedMap() {
      for(Block block : Block.REGISTRY) {
         String blockName = MCHelper.nameOf(block);
         if (blockName != null) {
            if (block instanceof BlockCrops) {
               BlockCrops crop = (BlockCrops)block;
               if (crop.getMaxAge() == 3) {
                  this.registerBlocks("#beets", blockName);
               } else if (blockName.equals("minecraft:wheat")) {
                  this.registerBlocks("#wheat", blockName);
               } else if (crop.getMaxAge() == 7) {
                  this.registerBlocks("#crop", blockName);
               }
            } else if (block instanceof BlockSapling) {
               this.registerBlocks("#sapling", blockName);
            } else if (block instanceof BlockReed) {
               this.registerBlocks("#reed", blockName);
            } else if (block instanceof BlockFence) {
               this.registerBlocks("#fence", blockName);
            } else if (block instanceof BlockVine) {
               this.registerBlocks("#vine", blockName);
            } else if (!(block instanceof BlockFlower) && !(block instanceof BlockMushroom)) {
               if (block instanceof BlockPlanks) {
                  this.registerBlocks("wood", blockName);
               } else if (block instanceof BlockLog) {
                  this.registerBlocks("log", blockName);
               } else if (block instanceof BlockDoor) {
                  this.registerBlocks("bluntwood", blockName);
               } else if (block instanceof BlockLeaves) {
                  this.registerBlocks("leaves", blockName);
               } else if (block instanceof BlockOre) {
                  this.registerBlocks("ore", blockName);
               } else if (block instanceof BlockIce) {
                  this.registerBlocks("ice", blockName);
               } else if (block instanceof BlockChest) {
                  this.registerBlocks("squeakywood", blockName);
               } else if (block instanceof BlockGlass) {
                  this.registerBlocks("glass", blockName);
               }
            } else {
               this.registerBlocks("NOT_EMITTER", blockName);
            }
         }
      }

   }

   public Generator createGenerator(@Nonnull EntityLivingBase entity) {
      EntityEffectInfo info = RegistryManager.EFFECTS.getEffects(entity);
      Variator var;
      if (entity.isChild()) {
         var = this.childVariator;
      } else if (entity instanceof EntityPlayer) {
         var = ModOptions.sound.foostepsQuadruped ? this.playerQuadrupedVariator : this.playerVariator;
      } else {
         var = this.getVariator(info.variator);
      }

      return (Generator)(var.QUADRUPED ? new GeneratorQP(var) : new Generator(var));
   }

   @Nonnull
   private Variator getVariator(@Nonnull String varName) {
      return (Variator)this.variators.getOrDefault(varName, this.defaultVariator);
   }

   @Nonnull
   public BlockMap getBlockMap() {
      return this.blockMap;
   }

   @Nonnull
   public IAcoustic[] resolve(@Nonnull IBlockState state) {
      return RegistryManager.ACOUSTICS.resolvePrimitive(state);
   }

   public boolean hasFootprint(@Nonnull IBlockState state) {
      return this.FOOTPRINT_MATERIAL.contains(state.getMaterial()) || this.FOOTPRINT_STATES.contains(state);
   }

   private static Block resolveToBlock(@Nonnull ItemStack stack) {
      if (!ItemStackUtil.isValidItemStack(stack)) {
         return null;
      } else {
         Item item = stack.getItem();
         if (item instanceof ItemBlock) {
            return ((ItemBlock)item).getBlock();
         } else {
            return item instanceof ItemBlockSpecial ? ((ItemBlockSpecial)item).getBlock() : null;
         }
      }
   }

   private void registerFootprint(@Nonnull String... blocks) {
      for(String b : blocks) {
         boolean materialMatch = false;
         if (b.startsWith("@")) {
            materialMatch = true;
            b = b.substring(1);
         }

         BlockStateMatcher bi = BlockStateMatcher.create(b);
         if (bi != null) {
            if (materialMatch) {
               IBlockState state = bi.getBlock().getDefaultState();
               this.FOOTPRINT_MATERIAL.add(state.getMaterial());
            } else {
               this.FOOTPRINT_STATES.addAll(bi.asBlockStates());
            }
         } else {
            ModBase.log().warn("Unable to create matcher for '%s'", new Object[]{b});
         }
      }

   }

   private void registerForgeEntries(@Nonnull String blockClass, @Nonnull String... entries) {
      for(String dictionaryName : entries) {
         for(ItemStack stack : OreDictionary.getOres(dictionaryName, false)) {
            Block block = resolveToBlock(stack);
            if (block != null) {
               String blockName = null;
               if (stack.getHasSubtypes() && stack.getItemDamage() != 32767) {
                  try {
                     int meta = stack.getItem().getMetadata(stack);
                     IBlockState state = block.getStateFromMeta(meta);
                     blockName = state.toString();
                  } catch (Throwable var14) {
                     ModBase.log().warn("Unable to resolve blockstate for [%s]", new Object[]{ItemStackUtil.getItemName(stack)});
                  }
               } else {
                  blockName = MCHelper.nameOf(block);
               }

               if (blockName != null) {
                  this.getBlockMap().register(blockName, blockClass);
               } else {
                  ModBase.log().warn("Unable to obtain block name for ItemStack [%s]", new Object[]{stack.toString()});
               }
            }
         }
      }

   }

   private void registerBlocks(@Nonnull String blockClass, @Nonnull String... blocks) {
      for(String s : blocks) {
         this.getBlockMap().register(s, blockClass);
      }

   }
}
