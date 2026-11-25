package svenhjol.charm.world.decorator.inner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces.House4Garden;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmDecoratorItems;
import svenhjol.charm.crafting.block.BlockBookshelfChest;
import svenhjol.charm.crafting.feature.BookshelfChest;
import svenhjol.charm.world.compat.QuarkDecoratorItems;
import svenhjol.charm.world.decorator.theme.VillageButcherTheme;
import svenhjol.charm.world.decorator.theme.VillageCarpenterTheme;
import svenhjol.charm.world.decorator.theme.VillageFishermanTheme;
import svenhjol.charm.world.decorator.theme.VillageLibrarianTheme;
import svenhjol.charm.world.decorator.theme.VillagePriestTheme;
import svenhjol.charm.world.decorator.theme.VillageShepherdTheme;
import svenhjol.charm.world.decorator.theme.VillageSmithTheme;
import svenhjol.charm.world.feature.MoreVillageBiomes;
import svenhjol.charm.world.feature.VillageDecorations;
import svenhjol.meson.Meson;
import svenhjol.meson.decorator.MesonDecoratorTheme;
import svenhjol.meson.decorator.MesonInnerDecorator;
import svenhjol.meson.helper.ForgeHelper;
import svenhjol.meson.helper.LootHelper;
import svenhjol.meson.helper.ObfuscationHelper;
import svenhjol.meson.helper.WorldHelper;

public abstract class VillageInnerDecorator extends MesonInnerDecorator {
   protected CharmDecoratorItems items = new CharmDecoratorItems(this);
   public BlockPos pos = this.getPos();
   public Random villageRand = this.getVillageRand();
   public Biome biome = this.getBiome();
   public QuarkDecoratorItems quarkItems;
   public int floor = 1;
   public int roof = 5;

   public VillageInnerDecorator(Village structure, World world, StructureBoundingBox box) {
      super(structure, world, box);

      try {
         if (ForgeHelper.areModsLoaded("quark")) {
            this.quarkItems = (QuarkDecoratorItems)QuarkDecoratorItems.class.getConstructor(MesonInnerDecorator.class).newInstance(this);
         }
      } catch (Exception var5) {
         Meson.runtimeException("Error loading QuarkDecoratorItems");
      }

   }

   protected Class<? extends MesonDecoratorTheme> getThemeClass() {
      List<Class<? extends MesonDecoratorTheme>> availableThemes = new ArrayList<Class<? extends MesonDecoratorTheme>>() {
         {
            this.add(VillageSmithTheme.class);
            this.add(VillagePriestTheme.class);
            this.add(VillageLibrarianTheme.class);
            this.add(VillageButcherTheme.class);
            this.add(VillageCarpenterTheme.class);
            this.add(VillageShepherdTheme.class);
            this.add(VillageFishermanTheme.class);
         }
      };
      return (Class)availableThemes.get(this.rand.nextInt(availableThemes.size()));
   }

   protected boolean common() {
      return this.chance(VillageDecorations.common);
   }

   protected boolean uncommon() {
      return this.chance(VillageDecorations.uncommon);
   }

   protected boolean valuable() {
      return this.chance(VillageDecorations.valuable);
   }

   protected boolean rare() {
      return this.chance(VillageDecorations.rare);
   }

   public long getSeed() {
      return WorldHelper.getNearestVillageSeed(this.world, this.pos);
   }

   public BlockPos getPos() {
      StructureBoundingBox bb = this.structure.getBoundingBox();
      return new BlockPos(bb.minX, bb.minY, bb.minZ);
   }

   public ChunkPos getChunkPos() {
      return WorldHelper.getChunkPos(this.getPos());
   }

   public Random getVillageRand() {
      long seed = this.getSeed();
      Random rand = new Random();
      rand.setSeed(seed);
      return rand;
   }

   public boolean isZombieInfested() {
      return ((Boolean)ReflectionHelper.getPrivateValue(Village.class, (Village)this.structure, ObfuscationHelper.Fields.IS_ZOMBIE_INFESTED)).booleanValue();
   }

   public Biome getBiome() {
      BlockPos nearest = WorldHelper.getNearestVillage(this.world, this.pos);
      return nearest != null ? this.world.getBiome(nearest) : Biomes.PLAINS;
   }

   public static class Church extends VillageInnerDecorator {
      public Church(Village structure, World world, StructureBoundingBox box) {
         super(structure, world, box);
      }

      protected Class<? extends MesonDecoratorTheme> getThemeClass() {
         return VillagePriestTheme.class;
      }

      public void generate() {
         if (VillageDecorations.functionalBlocks && this.valuable()) {
            this.add(Blocks.ENCHANTING_TABLE, 2, 2, 7);
         }

         if (VillageDecorations.torches) {
            if (this.common()) {
               this.items.addLantern(2, this.floor + 3, 6, true);
            }

            this.items.addTorch(3, 7, 1, EnumFacing.WEST);
            this.items.addTorch(2, 10, 2, (EnumFacing)null);
         }

         if (VillageDecorations.decorativeBlocks) {
            if (this.common()) {
               this.addRow(this.items.getBed(), 3, 5, 2, EnumFacing.NORTH, EnumFacing.NORTH);
            }

            if (this.common()) {
               this.addRow(this.items.getBed(), 2, 5, 2, EnumFacing.NORTH, EnumFacing.NORTH);
            }
         }

         if (VillageDecorations.storage && this.common()) {
            this.items.addStorageBlock(3, 10, 1, EnumFacing.NORTH);
         }

      }
   }

   public static class Field1 extends VillageInnerDecorator {
      public Field1(Village structure, World world, StructureBoundingBox box) {
         super(structure, world, box);
      }

      public void generate() {
         if (VillageDecorations.torches) {
            this.items.addTorch(3, 1, 0, (EnumFacing)null);
            this.items.addTorch(3, 1, 8, (EnumFacing)null);
         }

      }
   }

   public static class Field2 extends VillageInnerDecorator {
      public Field2(Village structure, World world, StructureBoundingBox box) {
         super(structure, world, box);
      }

      public void generate() {
         if (VillageDecorations.torches) {
            this.items.addTorch(3, 1, 0, (EnumFacing)null);
            this.items.addTorch(3, 1, 8, (EnumFacing)null);
            this.items.addTorch(9, 1, 0, (EnumFacing)null);
            this.items.addTorch(9, 1, 8, (EnumFacing)null);
         }

      }
   }

   public static class Hall extends VillageInnerDecorator {
      public Hall(Village structure, World world, StructureBoundingBox box) {
         super(structure, world, box);
      }

      protected Class<? extends MesonDecoratorTheme> getThemeClass() {
         return VillageButcherTheme.class;
      }

      public void generate() {
         if (VillageDecorations.storage && this.uncommon()) {
            this.items.addStorageBlock(3, this.floor, 4, EnumFacing.SOUTH);
         }

         if (VillageDecorations.functionalBlocks && this.common()) {
            if (this.chance(0.5F)) {
               this.items.addFunctionalBlock(4, this.floor, 4, EnumFacing.SOUTH);
            } else {
               this.items.addStand(4, this.floor, 4, EnumFacing.SOUTH, (IBlockState)null);
            }
         }

         if (VillageDecorations.itemFrames) {
            if (this.common()) {
               this.items.addFramedItem(5, this.floor + 2, 4, EnumFacing.SOUTH);
            }

            if (this.common()) {
               this.items.addFramedItem(7, this.floor + 2, 1, EnumFacing.WEST);
            }

            if (this.common()) {
               this.items.addFramedItem(3, this.floor + 2, 1, EnumFacing.NORTH);
            }
         }

         if (VillageDecorations.armorStands && this.uncommon()) {
            this.items.addArmorStand(7, this.floor, 4, 215.0F);
         }

         if (VillageDecorations.decorativeBlocks) {
            this.fill(Blocks.FARMLAND.getDefaultState(), 3, 0, 6, 7, 0, 9, EnumFacing.UP, true);

            for(int xx = 3; xx <= 7; ++xx) {
               for(int zz = 6; zz <= 9; ++zz) {
                  if (this.rand.nextFloat() < 0.4F) {
                     this.add(((BlockCrops)Blocks.WHEAT).withAge(6 + this.rand.nextInt(1)), xx, this.floor, zz);
                  }
               }
            }

            this.add(Blocks.WATER.getDefaultState(), 5, 0, 8);
            this.add(Blocks.AIR.getDefaultState(), 5, 1, 8);
         }

         if (VillageDecorations.torches) {
            this.items.addTorch(2, this.floor + 1, 10, (EnumFacing)null);
            this.items.addTorch(8, this.floor + 1, 10, (EnumFacing)null);
         }

      }
   }

   public static class House1 extends VillageInnerDecorator {
      public House1(Village structure, World world, StructureBoundingBox box) {
         super(structure, world, box);
      }

      protected Class<? extends MesonDecoratorTheme> getThemeClass() {
         return VillageLibrarianTheme.class;
      }

      public void generate() {
         if (VillageDecorations.storage) {
            if (this.common()) {
               this.items.addStorageBlock(7, 1, 2, EnumFacing.WEST);
            }

            if (Charm.hasFeature(BookshelfChest.class)) {
               IBlockState type = BookshelfChest.bookshelfChest.getDefaultState();

               for(int i = 0; i < 7; ++i) {
                  if (this.uncommon()) {
                     int numberOfBooks = this.rand.nextInt(4) + 1;
                     type.withProperty(BlockBookshelfChest.SLOTS, Integer.valueOf(numberOfBooks));
                     this.items.addStorageBlock(type, i + 1, 3, 4, EnumFacing.SOUTH, LootHelper.getRandomLootTable(LootHelper.RARITY.COMMON, LootHelper.TYPE.BOOK), numberOfBooks);
                  }
               }
            }
         }

         if (VillageDecorations.torches) {
            this.items.addTorch(1, 4, 2, EnumFacing.EAST);
            this.items.addTorch(1, 4, 3, EnumFacing.EAST);
            this.items.addTorch(7, 4, 2, EnumFacing.WEST);
            this.items.addTorch(7, 4, 3, EnumFacing.WEST);
         }

         if (VillageDecorations.itemFrames) {
            if (this.common()) {
               this.items.addFramedItem(7, 2, 1, EnumFacing.WEST);
            }

            if (this.common()) {
               this.items.addFramedItem(1, 3, 1, EnumFacing.NORTH);
            }
         }

      }
   }

   public static class House2 extends VillageInnerDecorator {
      List<ItemStack> anvilItems = new ArrayList<ItemStack>() {
         {
            this.add(new ItemStack(Items.IRON_SWORD));
            this.add(new ItemStack(Items.IRON_AXE));
            this.add(new ItemStack(Items.IRON_SHOVEL));
            this.add(new ItemStack(Items.IRON_PICKAXE));
         }
      };

      public House2(Village structure, World world, StructureBoundingBox box) {
         super(structure, world, box);
      }

      protected Class<? extends MesonDecoratorTheme> getThemeClass() {
         return VillageSmithTheme.class;
      }

      public void generate() {
         if (VillageDecorations.functionalBlocks) {
            this.items.addAnvil(8, this.floor, 1, this.chance(0.5F) ? 1 : 0, EnumFacing.EAST);
            if (this.quarkItems != null && this.common()) {
               this.quarkItems.addGlassFramedItem(8, this.floor + 1, 1, EnumFacing.UP, (ItemStack)this.anvilItems.get(this.rand.nextInt(this.anvilItems.size())));
            }
         }

         if (VillageDecorations.decorativeBlocks && this.common()) {
            this.fill(Blocks.STONE_SLAB, 6, 5, 4, 8, 5, 6, true);
            this.add(Blocks.AIR, 7, 4, 5);
            this.add(Blocks.AIR, 8, 4, 5);
            this.add(Blocks.AIR, 7, 5, 5);
            this.add(Blocks.AIR, 8, 5, 5);
            this.add(Blocks.MAGMA, 7, 3, 5);
            this.add(Blocks.MAGMA, 8, 3, 5);
         }

         if (VillageDecorations.torches) {
            this.items.addTorch(1, 3, 3, EnumFacing.EAST);
            this.items.addTorch(3, 3, 5, EnumFacing.SOUTH);
         }

         if (VillageDecorations.armorStands && this.uncommon()) {
            this.items.addArmorStand(1, this.floor, 1, 40.0F);
         }

      }
   }

   public static class House3 extends VillageInnerDecorator {
      public House3(Village structure, World world, StructureBoundingBox box) {
         super(structure, world, box);
      }

      public void generate() {
         float r = this.rand.nextFloat();
         if (r <= 0.25F) {
            if (VillageDecorations.beds && VillageDecorations.decorativeBlocks) {
               this.addRow(this.items.getBed(), 4, this.floor, 9, EnumFacing.NORTH, EnumFacing.NORTH);
               this.addRow(this.items.getBed(), 6, this.floor, 9, EnumFacing.NORTH, EnumFacing.NORTH);
               this.add(Blocks.CRAFTING_TABLE.getDefaultState(), 5, this.floor, 9);
               this.add(Blocks.BOOKSHELF.getDefaultState(), 3, this.floor, 9);
               this.add(Blocks.BOOKSHELF.getDefaultState(), 7, this.floor, 9);
            }
         } else if (r <= 0.5F) {
            if (VillageDecorations.beds && VillageDecorations.decorativeBlocks) {
               this.addRow(this.items.getBed(), 4, this.floor, 9, EnumFacing.WEST, EnumFacing.EAST);
               this.add(Blocks.BOOKSHELF.getDefaultState(), 5, this.floor, 9);
               this.addRow(this.items.getBed(), 6, this.floor, 9, EnumFacing.EAST, EnumFacing.WEST);
               if (this.common()) {
                  this.items.addRandomBlock(6, this.floor, 1, EnumFacing.SOUTH);
               }

               if (this.common()) {
                  this.items.addRandomBlock(7, this.floor, 1, EnumFacing.SOUTH);
               }
            }
         } else if (r <= 0.75F) {
            if (VillageDecorations.functionalBlocks && VillageDecorations.decorativeBlocks) {
               if (this.common()) {
                  this.items.addFunctionalBlock(3, this.floor, 9, EnumFacing.SOUTH);
               }

               if (this.common()) {
                  this.items.addFunctionalBlock(4, this.floor, 9, EnumFacing.SOUTH);
               }

               if (this.uncommon()) {
                  this.items.addStorageBlock(5, this.floor, 9, EnumFacing.SOUTH);
               }

               if (this.common()) {
                  this.items.addFunctionalBlock(6, this.floor, 9, EnumFacing.SOUTH);
               }

               if (this.common()) {
                  this.items.addFunctionalBlock(7, this.floor, 9, EnumFacing.SOUTH);
               }
            }
         } else if (VillageDecorations.storage) {
            if (this.common()) {
               this.items.addStorageBlock(5, this.floor, 9, EnumFacing.SOUTH);
            }

            if (this.common()) {
               this.items.addStorageBlock(6, this.floor, 9, EnumFacing.SOUTH);
            }
         }

         if (VillageDecorations.decorativeBlocks && VillageDecorations.functionalBlocks) {
            if (this.common()) {
               this.items.addStand(7, this.floor, 5, EnumFacing.WEST, (IBlockState)null);
            }

            if (this.common()) {
               this.items.addRandomBlock(7, this.floor, 6, EnumFacing.WEST);
            }

            if (this.common()) {
               this.items.addStand(4, this.floor, 1, EnumFacing.NORTH, (IBlockState)null);
            }

            if (this.common()) {
               this.items.addRandomBlock(5, this.floor, 1, EnumFacing.NORTH);
            }
         }

         if (VillageDecorations.storage) {
            if (this.uncommon()) {
               this.items.addStorageBlock(1, this.floor, 2, EnumFacing.EAST);
            }

            if (this.uncommon()) {
               this.items.addStorageBlock(1, this.floor, 3, EnumFacing.EAST);
            }
         }

         if (VillageDecorations.itemFrames) {
            if (this.uncommon()) {
               this.items.addFramedItem(6, this.floor + 1, 1, EnumFacing.NORTH);
            }

            if (this.uncommon()) {
               this.items.addFramedItem(4, this.floor + 1, 9, EnumFacing.SOUTH);
            }

            if (this.uncommon()) {
               this.items.addFramedItem(6, this.floor + 1, 9, EnumFacing.SOUTH);
            }
         }

         if (VillageDecorations.functionalBlocks) {
            if (this.common()) {
               this.items.addFunctionalBlock(7, this.floor, 2, EnumFacing.WEST);
            }

            if (this.common()) {
               this.items.addFunctionalBlock(7, this.floor, 3, EnumFacing.WEST);
            }
         }

         if (VillageDecorations.carpet) {
            this.items.addRug(this.floor, 3, 3, 6, 6, (Random)null);
         }

         if (VillageDecorations.torches) {
            this.items.addTorch(5, 3, 9, EnumFacing.SOUTH);
            this.items.addTorch(7, 3, 5, EnumFacing.WEST);
            this.items.addTorch(3, 3, 5, EnumFacing.EAST);
            if (this.uncommon()) {
               this.items.addLantern(5, 5, 5, true);
            }
         }

      }
   }

   public static class House4 extends VillageInnerDecorator {
      public House4(Village structure, World world, StructureBoundingBox box) {
         super(structure, world, box);
      }

      public void generate() {
         if (this.chance(0.5F)) {
            if (VillageDecorations.beds) {
               this.addRow(this.items.getBed(), 2, 1, 3, EnumFacing.WEST, EnumFacing.EAST);
            }

            if (VillageDecorations.carpet) {
               this.items.addRug(1, 1, 2, 2, 2, (Random)null);
            }
         } else if (VillageDecorations.storage) {
            if (this.uncommon()) {
               this.items.addStorageBlock(1, 1, 3, EnumFacing.SOUTH);
            }

            if (this.uncommon()) {
               this.items.addStorageBlock(2, 1, 3, EnumFacing.SOUTH);
            }
         }

         if (this.common() && Charm.hasFeature(MoreVillageBiomes.class)) {
            EnumType wood = MoreVillageBiomes.getWoodForBiome(this.biome);
            this.items.addDoor(2, 1, 0, EnumFacing.NORTH, MoreVillageBiomes.getDoorForWood(wood));
         }

         if (this.isRoofAccessible()) {
            if (VillageDecorations.storage) {
               if (this.uncommon()) {
                  this.items.addStorageBlock(1, 5, 3, EnumFacing.EAST);
               }

               if (this.uncommon()) {
                  this.items.addStorageBlock(3, 5, 1, EnumFacing.NORTH);
               }
            }
         } else if (VillageDecorations.itemFrames && this.common()) {
            this.items.addFramedItem(3, 3, 3, EnumFacing.SOUTH);
         }

         if (VillageDecorations.torches) {
            this.items.addTorch(0, 6, 0, (EnumFacing)null);
            this.items.addTorch(4, 6, 4, (EnumFacing)null);
            if (this.uncommon()) {
               this.items.addLantern(2, this.floor + 2, 2, true);
            }
         }

         if (VillageDecorations.itemFrames) {
            if (this.common()) {
               this.items.addFramedItem(1, 3, 3, EnumFacing.SOUTH);
            }

            if (this.common()) {
               this.items.addFramedItem(1, 3, 1, EnumFacing.NORTH);
            }

            if (this.common()) {
               this.items.addFramedItem(3, 3, 1, EnumFacing.NORTH);
            }
         }

         if (VillageDecorations.armorStands && this.valuable()) {
            this.items.addArmorStand(3, this.floor, 1, 135.0F);
         }

      }

      public boolean isRoofAccessible() {
         return ((Boolean)ReflectionHelper.getPrivateValue(House4Garden.class, (House4Garden)this.structure, ObfuscationHelper.Fields.IS_ROOF_ACCESSIBLE)).booleanValue();
      }
   }

   public static class Well extends VillageInnerDecorator {
      public Well(Village structure, World world, StructureBoundingBox box) {
         super(structure, world, box);
      }

      public void generate() {
         if (VillageDecorations.carpet) {
            int roof = 16;
            this.items.addRug(roof, 1, 1, 4, 4, this.getVillageRand());
         }

         if (this.common()) {
            IBlockState ore = null;
            switch(this.getRand().nextInt(4)) {
            case 0:
               ore = Blocks.IRON_ORE.getDefaultState();
               break;
            case 1:
               ore = Blocks.GOLD_ORE.getDefaultState();
               break;
            case 2:
               ore = Blocks.EMERALD_ORE.getDefaultState();
               break;
            case 3:
               ore = Blocks.DIAMOND_ORE.getDefaultState();
            }

            if (ore != null) {
               this.add(ore, 2, 0, 2, EnumFacing.NORTH);
            }
         }

      }
   }

   public static class WoodHut extends VillageInnerDecorator {
      public WoodHut(Village structure, World world, StructureBoundingBox box) {
         super(structure, world, box);
         this.roof = 5;
      }

      public void generate() {
         if (this.common()) {
            this.fill(Blocks.COBBLESTONE, 1, 0, 1, 2, 0, 3, true);
         }

         if (this.chance(0.5F)) {
            if (VillageDecorations.beds) {
               this.addRow(this.items.getBed(), 2, 1, 3, EnumFacing.WEST, EnumFacing.EAST);
               this.add(Blocks.AIR, 1, 2, 3);
               this.add(Blocks.AIR, 2, 2, 3);
            }

            if (VillageDecorations.carpet) {
               this.items.addRug(1, 1, 2, 2, 2, (Random)null);
            }
         } else if (VillageDecorations.storage && this.uncommon()) {
            this.items.addStorageBlock(1, 1, 3, EnumFacing.SOUTH);
            this.add(Blocks.AIR, 1, 2, 3);
         }

         if (this.chance(0.5F)) {
            if (VillageDecorations.functionalBlocks && this.common()) {
               if (this.chance(0.5F)) {
                  this.items.addFunctionalBlock(2, 1, 1, EnumFacing.EAST);
               } else {
                  this.items.addStand(2, 1, 1, EnumFacing.EAST, (IBlockState)null);
               }
            }
         } else if (VillageDecorations.armorStands && this.valuable()) {
            this.items.addArmorStand(2, this.floor, 1, 135.0F);
         }

         if (VillageDecorations.itemFrames && this.common()) {
            this.items.addFramedItem(1, this.floor + 2, 3, EnumFacing.SOUTH);
         }

         if (VillageDecorations.torches) {
            this.items.addTorch(2, this.floor + 2, 3, EnumFacing.SOUTH);
         }

      }
   }
}
