package com.github.alexthe666.iceandfire;

import com.github.alexthe666.iceandfire.block.BlockJar;
import com.github.alexthe666.iceandfire.block.BlockMyrmexResin;
import com.github.alexthe666.iceandfire.block.BlockPixieHouse;
import com.github.alexthe666.iceandfire.block.BlockPodium;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
import com.github.alexthe666.iceandfire.entity.EntityAmphithereArrow;
import com.github.alexthe666.iceandfire.entity.EntityCockatrice;
import com.github.alexthe666.iceandfire.entity.EntityCockatriceEgg;
import com.github.alexthe666.iceandfire.entity.EntityCyclops;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityDeathWormEgg;
import com.github.alexthe666.iceandfire.entity.EntityDragonArrow;
import com.github.alexthe666.iceandfire.entity.EntityDragonEgg;
import com.github.alexthe666.iceandfire.entity.EntityDragonFire;
import com.github.alexthe666.iceandfire.entity.EntityDragonFireCharge;
import com.github.alexthe666.iceandfire.entity.EntityDragonIceCharge;
import com.github.alexthe666.iceandfire.entity.EntityDragonIceProjectile;
import com.github.alexthe666.iceandfire.entity.EntityDragonSkull;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import com.github.alexthe666.iceandfire.entity.EntityHippogryphEgg;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexEgg;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexQueen;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexRoyal;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexSentinel;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexSoldier;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexWorker;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import com.github.alexthe666.iceandfire.entity.EntitySeaSerpentArrow;
import com.github.alexthe666.iceandfire.entity.EntitySeaSerpentBubbles;
import com.github.alexthe666.iceandfire.entity.EntitySiren;
import com.github.alexthe666.iceandfire.entity.EntitySnowVillager;
import com.github.alexthe666.iceandfire.entity.EntityStoneStatue;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianArrow;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianBird;
import com.github.alexthe666.iceandfire.entity.EntityStymphalianFeather;
import com.github.alexthe666.iceandfire.entity.EntityTroll;
import com.github.alexthe666.iceandfire.enums.EnumDragonArmor;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;
import com.github.alexthe666.iceandfire.enums.EnumTroll;
import com.github.alexthe666.iceandfire.item.block.ItemBlockMyrmexResin;
import com.github.alexthe666.iceandfire.item.block.ItemBlockPodium;
import com.github.alexthe666.iceandfire.world.BiomeGlacier;
import java.lang.reflect.Field;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

@EventBusSubscriber
public class CommonProxy {
   public CommonProxy() {
   }

   @SubscribeEvent
   public static void registerSoundEvents(Register<SoundEvent> event) {
      try {
         for(Field f : ModSounds.class.getDeclaredFields()) {
            Object obj = f.get((Object)null);
            if (obj instanceof SoundEvent) {
               event.getRegistry().register((SoundEvent)obj);
            } else if (obj instanceof SoundEvent[]) {
               for(SoundEvent soundEvent : (SoundEvent[])obj) {
                  event.getRegistry().register(soundEvent);
               }
            }
         }

      } catch (IllegalAccessException var10) {
         throw new RuntimeException(var10);
      }
   }

   @SubscribeEvent
   public static void registerBlocks(Register<Block> event) {
      try {
         for(Field f : ModBlocks.class.getDeclaredFields()) {
            Object obj = f.get((Object)null);
            if (obj instanceof Block) {
               event.getRegistry().register((Block)obj);
            } else if (obj instanceof Block[]) {
               for(Block block : (Block[])obj) {
                  event.getRegistry().register(block);
               }
            }
         }

      } catch (IllegalAccessException var10) {
         throw new RuntimeException(var10);
      }
   }

   @SubscribeEvent
   public static void registerEntities(Register<EntityEntry> event) {
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityDragonEgg.class, "dragonegg", 1);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityDragonArrow.class, "dragonarrow", 2);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityDragonSkull.class, "dragonskull", 3);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityDragonFire.class, "dragonfire", 4);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityFireDragon.class, "firedragon", 5, 3407872, 10823977);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityDragonIceProjectile.class, "dragonice", 6);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityIceDragon.class, "icedragon", 7, 11918843, 8305392);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityDragonFireCharge.class, "dragonfirecharge", 8);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityDragonIceCharge.class, "dragonicecharge", 9);
      registerSpawnable(EntityEntryBuilder.create(), event, EntitySnowVillager.class, "snowvillager", 10, 3942947, 7385551);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityHippogryphEgg.class, "hippogryphegg", 11);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityHippogryph.class, "hippogryph", 12, 14211288, 13743453);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityStoneStatue.class, "stonestatue", 13);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityGorgon.class, "gorgon", 14, 13687199, 6833456);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityPixie.class, "if_pixie", 15, 16744329, 14863586);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityCyclops.class, "cyclops", 17, 11567726, 3809039);
      registerSpawnable(EntityEntryBuilder.create(), event, EntitySiren.class, "siren", 18, 9365194, 15917000);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityHippocampus.class, "hippocampus", 19, 4493767, 5227883);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityDeathWorm.class, "deathworm", 20, 13749667, 4340282);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityDeathWormEgg.class, "deathwormegg", 21);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityCockatrice.class, "if_cockatrice", 22, 9392133, 5200419);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityCockatriceEgg.class, "if_cockatriceegg", 23);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityStymphalianBird.class, "stymphalianbird", 24, 7622455, 10382411);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityStymphalianFeather.class, "stymphalianfeather", 25);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityStymphalianArrow.class, "stymphalianarrow", 26);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityTroll.class, "if_troll", 27, 4014397, 5784378);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityMyrmexWorker.class, "myrmex_worker", 28, 10575910, 5850400);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityMyrmexSoldier.class, "myrmex_soldier", 29, 10575910, 8217133);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityMyrmexSentinel.class, "myrmex_sentinel", 30, 10575910, 10649402);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityMyrmexRoyal.class, "myrmex_royal", 31, 10575910, 13081416);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityMyrmexQueen.class, "myrmex_queen", 32, 10575910, 15513685);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityMyrmexEgg.class, "myrmex_egg", 33);
      registerSpawnable(EntityEntryBuilder.create(), event, EntityAmphithere.class, "amphithere", 34, 5862709, 43672);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntityAmphithereArrow.class, "amphitherearrow", 35);
      registerSpawnable(EntityEntryBuilder.create(), event, EntitySeaSerpent.class, "seaserpent", 36, 33433, 12969703);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntitySeaSerpentBubbles.class, "seaserpentbubble", 37);
      registerUnspawnable(EntityEntryBuilder.create(), event, EntitySeaSerpentArrow.class, "seaserpentarrow", 38);
   }

   public static void registerSpawnable(EntityEntryBuilder builder, Register<EntityEntry> event, Class<? extends Entity> entityClass, String name, int id, int mainColor, int subColor) {
      id = id + 900;
      builder.entity(entityClass);
      builder.id(new ResourceLocation("iceandfire", name), id);
      builder.name(name);
      builder.egg(mainColor, subColor);
      builder.tracker(64, 1, true);
      event.getRegistry().register(builder.build());
   }

   public static void registerUnspawnable(EntityEntryBuilder builder, Register<EntityEntry> event, Class<? extends Entity> entityClass, String name, int id) {
      id = id + 900;
      builder.entity(entityClass);
      builder.id(new ResourceLocation("iceandfire", name), id);
      builder.name(name);
      builder.tracker(64, 1, true);
      event.getRegistry().register(builder.build());
   }

   @SubscribeEvent
   public static void registerItemBlocks(Register<Item> event) {
      try {
         for(Field f : ModBlocks.class.getDeclaredFields()) {
            Object obj = f.get((Object)null);
            if (obj instanceof Block) {
               ItemBlock itemBlock;
               if (obj == ModBlocks.jar_pixie) {
                  BlockJar var10002 = (BlockJar)obj;
                  ((BlockJar)obj).getClass();
                  itemBlock = var10002.new ItemBlockJar((Block)obj);
               } else if (obj instanceof BlockPixieHouse) {
                  BlockPixieHouse var13 = (BlockPixieHouse)obj;
                  ((BlockPixieHouse)obj).getClass();
                  itemBlock = var13.new ItemBlockPixieHouse((Block)obj);
               } else if (obj instanceof BlockPodium) {
                  itemBlock = new ItemBlockPodium((Block)obj);
               } else if (obj instanceof BlockMyrmexResin) {
                  itemBlock = new ItemBlockMyrmexResin((Block)obj);
               } else {
                  itemBlock = new ItemBlock((Block)obj);
               }

               itemBlock.setRegistryName(((Block)obj).getRegistryName());
               event.getRegistry().register(itemBlock);
            } else if (obj instanceof Block[]) {
               for(Block block : (Block[])obj) {
                  ItemBlock itemBlock = new ItemBlock(block);
                  itemBlock.setRegistryName(block.getRegistryName());
                  event.getRegistry().register(itemBlock);
               }
            }
         }

      } catch (IllegalAccessException var11) {
         throw new RuntimeException(var11);
      }
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> event) {
      try {
         for(Field f : ModItems.class.getDeclaredFields()) {
            Object obj = f.get((Object)null);
            if (obj instanceof Item) {
               event.getRegistry().register((Item)obj);
            } else if (obj instanceof Item[]) {
               for(Item item : (Item[])obj) {
                  event.getRegistry().register(item);
               }
            }
         }
      } catch (IllegalAccessException var10) {
         throw new RuntimeException(var10);
      }

      for(EnumDragonArmor armor : EnumDragonArmor.values()) {
         event.getRegistry().register(armor.helmet);
         event.getRegistry().register(armor.chestplate);
         event.getRegistry().register(armor.leggings);
         event.getRegistry().register(armor.boots);
      }

      for(EnumSeaSerpent armor : EnumSeaSerpent.values()) {
         event.getRegistry().register(armor.scale);
         event.getRegistry().register(armor.helmet);
         event.getRegistry().register(armor.chestplate);
         event.getRegistry().register(armor.leggings);
         event.getRegistry().register(armor.boots);
      }

      for(EnumTroll.Weapon weapon : EnumTroll.Weapon.values()) {
         event.getRegistry().register(weapon.item);
      }

      for(EnumTroll troll : EnumTroll.values()) {
         event.getRegistry().register(troll.leather);
         event.getRegistry().register(troll.helmet);
         event.getRegistry().register(troll.chestplate);
         event.getRegistry().register(troll.leggings);
         event.getRegistry().register(troll.boots);
      }

   }

   @SubscribeEvent
   public static void registerBiomes(Register<Biome> event) {
      IceAndFire.GLACIER = (Biome)(new BiomeGlacier()).setRegistryName("iceandfire", "Glacier");
      event.getRegistry().register(IceAndFire.GLACIER);
      BiomeDictionary.addTypes(IceAndFire.GLACIER, new Type[]{Type.SNOWY, Type.COLD, Type.SPARSE, Type.DEAD, Type.WASTELAND});
      if (IceAndFire.CONFIG.spawnGlaciers) {
         BiomeManager.addSpawnBiome(IceAndFire.GLACIER);
         BiomeManager.addBiome(BiomeType.COOL, new BiomeEntry(IceAndFire.GLACIER, IceAndFire.CONFIG.glacierSpawnChance));
      }

   }

   public void preRender() {
   }

   public void render() {
   }

   public void postRender() {
   }

   public void spawnParticle(String name, World world, double x, double y, double z, double motX, double motY, double motZ) {
   }

   public void openBestiaryGui(ItemStack book) {
   }

   public void openMyrmexStaffGui(ItemStack staff) {
   }

   public Object getArmorModel(int armorId) {
      return null;
   }

   public Object getFontRenderer() {
      return null;
   }

   @SubscribeEvent
   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equalsIgnoreCase("iceandfire")) {
         IceAndFire.syncConfig();
      }

   }

   public int getDragon3rdPersonView() {
      return 0;
   }

   public void setDragon3rdPersonView(int view) {
   }

   public void openMyrmexAddRoomGui(ItemStack staff, BlockPos pos, EnumFacing facing) {
   }
}
