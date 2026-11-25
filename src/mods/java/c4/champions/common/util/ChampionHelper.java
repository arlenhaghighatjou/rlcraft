package c4.champions.common.util;

import c4.champions.Champions;
import c4.champions.common.affix.AffixRegistry;
import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.filter.AffixFilterManager;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.potion.PotionPlague;
import c4.champions.common.rank.Rank;
import c4.champions.common.rank.RankManager;
import c4.champions.integrations.gamestages.ChampionStages;
import c4.champions.integrations.scalinghealth.ChampionDifficulty;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.silentchaos512.scalinghealth.api.ScalingHealthAPI;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class ChampionHelper {
   public static EaglercraftRandom rand = new EaglercraftRandom();
   private static Set<Integer> dimensions = Sets.newHashSet();
   private static Set<ResourceLocation> mobs = Sets.newHashSet();
   private static Map<Integer, List<ChampionHelper.LootData>> drops = Maps.newHashMap();
   private static Map<ResourceLocation, Tuple<Integer, Integer>> champions = Maps.newHashMap();
   private static final Field IS_COMPLETE = ReflectionHelper.findField(TileEntityBeacon.class, "isComplete", "isComplete");

   public ChampionHelper() {
   }

   public static boolean isValidChampion(Entity entity) {
      return entity instanceof EntityLiving && entity instanceof IMob && isValidEntity(entity);
   }

   public static Rank generateRank(EntityLiving entityLivingIn) {
      ImmutableSortedMap<Integer, Rank> ranks = RankManager.getRanks();
      int finalTier = 0;
      int firstTier = ((Integer)ranks.firstKey()).intValue();
      float chance = ((Rank)ranks.get(Integer.valueOf(firstTier))).getChance();
      if (Champions.isScalingHealthLoaded) {
         double modifier = ChampionDifficulty.getSpawnModifier(firstTier);
         double difficulty = ScalingHealthAPI.getAreaDifficulty(entityLivingIn.world, entityLivingIn.getPosition());
         chance = (float)((double)chance + modifier * difficulty);
      }

      ResourceLocation entityKey = EntityList.getKey(entityLivingIn);
      Tuple<Integer, Integer> curated = (Tuple)champions.get(entityKey);
      if (curated != null) {
         if (((Integer)curated.getFirst()).intValue() > 0) {
            finalTier = ((Integer)curated.getFirst()).intValue();
            if (((Integer)curated.getSecond()).intValue() == 0) {
               return (Rank)ranks.get(Integer.valueOf(finalTier));
            }
         } else {
            finalTier = firstTier;
         }
      } else if (rand.nextFloat() < chance) {
         if (Champions.isGameStagesLoaded && !ChampionStages.isValidTier(((Integer)ranks.firstKey()).intValue(), entityLivingIn) || nearActiveBeacon(entityLivingIn)) {
            return RankManager.getEmptyRank();
         }

         finalTier = firstTier;
      }

      if (finalTier > 0) {
         for(Integer tier : ranks.keySet().tailSet(Integer.valueOf(finalTier), false)) {
            chance = ((Rank)ranks.get(tier)).getChance();
            if (Champions.isScalingHealthLoaded) {
               double modifier = ChampionDifficulty.getSpawnModifier(tier.intValue());
               double difficulty = ScalingHealthAPI.getAreaDifficulty(entityLivingIn.world, entityLivingIn.getPosition());
               chance = (float)((double)chance + modifier * difficulty);
            }

            if (rand.nextFloat() >= chance || Champions.isGameStagesLoaded && !ChampionStages.isValidTier(tier.intValue(), entityLivingIn)) {
               break;
            }

            finalTier = tier.intValue();
         }
      }

      if (finalTier == 0) {
         return RankManager.getEmptyRank();
      } else {
         if (curated != null && ((Integer)curated.getSecond()).intValue() > 0) {
            finalTier = Math.min(finalTier, ((Integer)curated.getSecond()).intValue());
         }

         return (Rank)ranks.get(Integer.valueOf(finalTier));
      }
   }

   public static String generateRandomName() {
      int langSize = 24;
      int randomPrefix = rand.nextInt(langSize + ConfigHandler.championNames.length);
      int randomSuffix = rand.nextInt(langSize + ConfigHandler.championNameSuffixes.length);
      String header = "champions.%s.%d";
      String prefix;
      if (randomPrefix < langSize) {
         prefix = (new TextComponentTranslation(String.format(header, "prefix", randomPrefix), new Object[0])).getFormattedText();
      } else {
         prefix = ConfigHandler.championNames[randomPrefix - langSize];
      }

      String suffix;
      if (randomSuffix < langSize) {
         suffix = (new TextComponentTranslation(String.format(header, "suffix", randomSuffix), new Object[0])).getFormattedText();
      } else {
         String configSuffix = ConfigHandler.championNameSuffixes[randomSuffix - langSize];
         if (!configSuffix.isEmpty()) {
            suffix = configSuffix.charAt(0) == ',' ? configSuffix : " " + configSuffix;
         } else {
            suffix = "";
         }
      }

      return prefix + suffix;
   }

   public static Set<String> generateAffixes(Rank rank, EntityLiving entityLivingIn, String... presets) {
      int size = rank.getAffixes();
      int tier = rank.getTier();
      Set<String> affixList = Sets.newHashSet();
      Map<AffixCategory, Set<String>> categoryMap = (Map)AffixRegistry.getCategoryMap().entrySet().stream().collect(Collectors.toMap(Entry::getKey, (e) -> {
         return Sets.newHashSet((Iterable)e.getValue());
      }));
      Set<String> curatedPresets = Sets.newHashSet(presets);
      curatedPresets.addAll(AffixFilterManager.getPresetAffixesForEntity(entityLivingIn));
      if (curatedPresets.size() > 0) {
         for(String s : curatedPresets) {
            AffixBase aff = AffixRegistry.getAffix(s);
            if (aff != null) {
               AffixCategory cat = aff.getCategory();
               Set<String> availableAffixes = (Set)categoryMap.get(cat);
               if (availableAffixes != null && availableAffixes.contains(s)) {
                  availableAffixes.remove(s);
                  boolean added = false;
                  AffixBase affix = AffixRegistry.getAffix(s);
                  if (affix != null) {
                     boolean flag = true;

                     for(String s1 : affixList) {
                        if (!affix.isCompatibleWith(AffixRegistry.getAffix(s1))) {
                           flag = false;
                           break;
                        }
                     }

                     if (flag) {
                        affixList.add(s);
                        added = true;
                     }
                  }

                  if (added && (availableAffixes.isEmpty() || cat != AffixCategory.OFFENSE)) {
                     categoryMap.remove(cat);
                  }
               }
            }
         }
      }

      while(!categoryMap.isEmpty() && affixList.size() < size) {
         AffixCategory[] categories = (AffixCategory[])categoryMap.keySet().toArray(new AffixCategory[0]);
         AffixCategory randomCategory = categories[rand.nextInt(categories.length)];
         Set<String> affixes = (Set)categoryMap.get(randomCategory);
         if (!affixes.isEmpty()) {
            int element = rand.nextInt(affixes.size());
            Iterator<String> iter = affixes.iterator();

            for(int i = 0; i < element; ++i) {
               iter.next();
            }

            String id = (String)iter.next();
            boolean added = false;
            AffixBase affix = AffixRegistry.getAffix(id);
            if (affix != null && affix.canApply(entityLivingIn) && AffixFilterManager.isValidAffix(affix, entityLivingIn, tier)) {
               boolean flag = true;

               for(String s : affixList) {
                  if (!affix.isCompatibleWith(AffixRegistry.getAffix(s))) {
                     flag = false;
                     break;
                  }
               }

               if (flag) {
                  affixList.add(id);
                  added = true;
               }
            }

            if (added && randomCategory != AffixCategory.OFFENSE) {
               categoryMap.remove(randomCategory);
            } else {
               affixes.remove(id);
               if (affixes.isEmpty()) {
                  categoryMap.remove(randomCategory);
               }
            }
         }
      }

      return affixList;
   }

   public static boolean isElite(Rank rank) {
      return rank != null && rank.getTier() > 0;
   }

   private static boolean nearActiveBeacon(EntityLiving entityLivingIn) {
      int range = ConfigHandler.beaconRange;
      if (range <= 0) {
         return false;
      } else {
         for(TileEntity te : entityLivingIn.world.tickableTileEntities) {
            BlockPos pos = te.getPos();
            if (Math.sqrt(entityLivingIn.getDistanceSq(pos)) <= (double)range && te instanceof TileEntityBeacon) {
               TileEntityBeacon beacon = (TileEntityBeacon)te;
               boolean flag = false;

               try {
                  flag = IS_COMPLETE.getBoolean(beacon);
               } catch (IllegalAccessException var8) {
                  Champions.logger.log(Level.ERROR, "Error reading isComplete from beacon!");
               }

               if (flag) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static boolean isValidEntity(Entity entity) {
      ResourceLocation rl = EntityList.getKey(entity);
      if (rl == null) {
         return false;
      } else if (mobs.isEmpty()) {
         return true;
      } else if (ConfigHandler.mobPermission == ConfigHandler.PermissionMode.BLACKLIST) {
         return !mobs.contains(rl);
      } else {
         return mobs.contains(rl);
      }
   }

   public static boolean isValidDimension(int dim) {
      if (dimensions.isEmpty()) {
         return true;
      } else if (ConfigHandler.dimensionPermission == ConfigHandler.PermissionMode.BLACKLIST) {
         return !dimensions.contains(Integer.valueOf(dim));
      } else {
         return dimensions.contains(Integer.valueOf(dim));
      }
   }

   public static void parseConfigs() {
      Potion potion = Potion.getPotionFromResourceLocation(ConfigHandler.affix.plagued.infectPotion);
      if (potion != null) {
         PotionPlague.setInfectionPotion(potion);
      }

      if (ConfigHandler.dimensionList.length > 0) {
         for(String s : ConfigHandler.dimensionList) {
            try {
               dimensions.add(Integer.valueOf(Integer.parseInt(s)));
            } catch (NumberFormatException var17) {
               Champions.logger.log(Level.ERROR, "Non-integer found in dimension config! " + s);
            }
         }
      }

      if (ConfigHandler.mobList.length > 0) {
         for(String s : ConfigHandler.mobList) {
            ResourceLocation rl = new ResourceLocation(s);
            if (EntityList.getEntityNameList().contains(rl)) {
               mobs.add(rl);
            } else {
               Champions.logger.log(Level.ERROR, "Invalid entity found in mob config! " + s);
            }
         }
      }

      if (ConfigHandler.championsList.length > 0) {
         for(String s : ConfigHandler.championsList) {
            String[] args = s.split(";");
            ResourceLocation rl = new ResourceLocation(args[0]);
            int minTier = args.length > 1 ? Integer.parseInt(args[1]) : 0;
            int maxTier = args.length > 2 ? Integer.parseInt(args[2]) : 0;
            if (EntityList.getEntityNameList().contains(rl)) {
               champions.put(rl, new Tuple(minTier, maxTier));
            } else {
               Champions.logger.log(Level.ERROR, "Invalid entity found in champions list config! " + s);
            }
         }
      }

      if (ConfigHandler.lootDrops.length > 0) {
         for(String s : ConfigHandler.lootDrops) {
            String[] parsed = s.split(";");
            if (parsed.length > 0) {
               int metadata = 0;
               int stackSize = 1;
               boolean enchant = false;
               int weight = 1;
               if (parsed.length < 2) {
                  Champions.logger.log(Level.ERROR, s + " needs at least a tier and an item name");
               } else {
                  int tier;
                  try {
                     tier = Integer.parseInt(parsed[0]);
                  } catch (NumberFormatException var18) {
                     Champions.logger.log(Level.ERROR, parsed[0] + " is not a valid tier");
                     continue;
                  }

                  Item item = Item.getByNameOrId(parsed[1]);
                  if (item == null) {
                     Champions.logger.log(Level.ERROR, "Item not found!" + parsed[1]);
                  } else {
                     if (parsed.length > 2) {
                        try {
                           metadata = Integer.parseInt(parsed[2]);
                        } catch (NumberFormatException var16) {
                           Champions.logger.log(Level.ERROR, parsed[2] + " is not a valid metadata");
                        }

                        if (parsed.length > 3) {
                           try {
                              stackSize = Integer.parseInt(parsed[3]);
                           } catch (NumberFormatException var15) {
                              Champions.logger.log(Level.ERROR, parsed[3] + " is not a valid stack creeperStrength");
                           }

                           if (parsed.length > 4) {
                              if (parsed[4].equalsIgnoreCase("true")) {
                                 enchant = true;
                              }

                              if (parsed.length > 5) {
                                 try {
                                    weight = Integer.parseInt(parsed[5]);
                                 } catch (NumberFormatException var14) {
                                    Champions.logger.log(Level.ERROR, parsed[5] + " is not a valid weight");
                                 }
                              }
                           }
                        }
                     }

                     ItemStack stack = new ItemStack(item, stackSize, metadata);
                     ((List)drops.computeIfAbsent(Integer.valueOf(tier), (list) -> {
                        return Lists.newArrayList();
                     })).add(new ChampionHelper.LootData(stack, enchant, weight));
                  }
               }
            }
         }
      }

   }

   public static List<ItemStack> getLootDrops(int tier) {
      List<ChampionHelper.LootData> data = Lists.newArrayList((Iterable)drops.getOrDefault(Integer.valueOf(tier), Lists.newArrayList()));
      List<ItemStack> drops = new ArrayList();
      if (data.isEmpty()) {
         return drops;
      } else {
         int amount = ConfigHandler.lootScaling ? tier : 1;

         for(int i = 0; i < amount; ++i) {
            double totalWeight = 0.0D;

            for(ChampionHelper.LootData loot : data) {
               totalWeight += (double)loot.weight;
            }

            double random = rand.nextDouble() * totalWeight;
            double countWeight = 0.0D;

            for(ChampionHelper.LootData loot : data) {
               countWeight += (double)loot.weight;
               if (countWeight >= random) {
                  drops.add(loot.getLootStack());
                  break;
               }
            }
         }

         return drops;
      }
   }

   private static class LootData {
      private ItemStack stack;
      private boolean enchant;
      private int weight;

      LootData(ItemStack stack, boolean enchant, int weight) {
         this.stack = stack;
         this.enchant = enchant;
         this.weight = weight;
      }

      public ItemStack getLootStack() {
         ItemStack loot = this.stack.copy();
         if (this.enchant) {
            EnchantmentHelper.addRandomEnchantment(ChampionHelper.rand, loot, 30, true);
         }

         return loot;
      }
   }
}
