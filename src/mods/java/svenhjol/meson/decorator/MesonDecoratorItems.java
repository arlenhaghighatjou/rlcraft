package svenhjol.meson.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCake;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import svenhjol.charm.world.compat.ItemHandlerLootTableFiller;
import svenhjol.meson.MesonTileInventory;
import svenhjol.meson.helper.EntityHelper;

public class MesonDecoratorItems {
   protected MesonInnerDecorator generator;
   protected MesonDecoratorTheme theme;
   protected Random rand;
   protected World world;
   protected StructureBoundingBox box;
   protected StructureComponent structure;

   public MesonDecoratorItems(MesonInnerDecorator generator) {
      this.generator = generator;
      this.world = generator.getWorld();
      this.rand = generator.getRand();
      this.theme = generator.getTheme();
      this.box = generator.getBox();
      this.structure = generator.getStructure();
   }

   public void addStorageBlock(int x, int y, int z, EnumFacing facing) {
      IBlockState type = this.theme.getStorageBlock();
      this.addStorageBlock(type, x, y, z, facing);
   }

   public void addStorageBlock(IBlockState type, int x, int y, int z, EnumFacing facing) {
      this.addStorageBlock(type, x, y, z, facing, this.theme.getLootTable(), 0);
   }

   public void addStorageBlock(IBlockState type, int x, int y, int z, EnumFacing facing, ResourceLocation loot, int lootSize) {
      BlockPos place = new BlockPos(x, y, z);
      TileEntity tile = this.generator.addTileEntity(place, type, facing);
      if (tile instanceof MesonTileInventory) {
         ((MesonTileInventory)tile).setLootTable(loot, lootSize);
      } else if (tile instanceof TileEntityLockableLoot) {
         ((TileEntityChest)tile).setLootTable(loot, this.world.rand.nextLong());
      } else if (tile != null) {
         IItemHandler capability = (IItemHandler)tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
         if (capability != null) {
            ItemHandlerLootTableFiller.fillWithLoot(capability, this.world, loot, lootSize);
         }
      }

   }

   public void addFunctionalBlock(int x, int y, int z, EnumFacing facing) {
      this.generator.add(this.theme.getFunctionalBlock(), x, y, z, facing);
   }

   public void addDecorationBlock(int x, int y, int z, EnumFacing facing) {
      this.generator.add(this.theme.getDecorationBlock(), x, y, z, facing);
   }

   public void addRandomBlock(int x, int y, int z, EnumFacing facing) {
      float f = this.rand.nextFloat();
      if (f <= 0.5F) {
         this.addFunctionalBlock(x, y, z, facing);
      } else {
         this.addDecorationBlock(x, y, z, facing);
      }

   }

   public void addArmorStand(int x, int y, int z, float angle) {
      BlockPos place = new BlockPos(x, y, z);
      this.addArmorStand(place, angle, this.theme.getWearables());
   }

   public void addArmorStand(BlockPos pos, float angle, @Nullable Map<EntityEquipmentSlot, ItemStack> wearing) {
      BlockPos place = this.generator.getRelativePos(pos);
      if (!this.generator.isNotInBox(place)) {
         EntityArmorStand stand = new EntityArmorStand(this.world, (double)((float)place.getX() + 0.5F), (double)place.getY(), (double)((float)place.getZ() + 0.5F));
         if (wearing != null) {
            List<EntityEquipmentSlot> slots = new ArrayList<EntityEquipmentSlot>() {
               {
                  this.add(EntityEquipmentSlot.HEAD);
                  this.add(EntityEquipmentSlot.CHEST);
                  this.add(EntityEquipmentSlot.LEGS);
                  this.add(EntityEquipmentSlot.FEET);
                  this.add(EntityEquipmentSlot.MAINHAND);
                  this.add(EntityEquipmentSlot.OFFHAND);
               }
            };
            slots.forEach((slot) -> {
               if (wearing.get(slot) != null) {
                  stand.setItemStackToSlot(slot, (ItemStack)wearing.get(slot));
               }

            });
         }

         this.world.setBlockToAir(place);
         this.world.setBlockToAir(place.up());
         stand.setLocationAndAngles((double)((float)place.getX() + 0.5F), (double)place.getY(), (double)((float)place.getZ() + 0.5F), this.generator.getRotatedAngle(this.structure.getCoordBaseMode(), angle), 0.0F);
         this.world.spawnEntity(stand);
      }
   }

   public void addAnvil(int x, int y, int z, int damage, @Nullable EnumFacing facing) {
      IBlockState anvil = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.DAMAGE, Integer.valueOf(damage));
      this.generator.add(anvil, x, y, z, facing);
   }

   public void addFramedItem(int x, int y, int z, EnumFacing direction) {
      BlockPos place = new BlockPos(x, y, z);
      this.addFramedItem(place, direction, this.theme.getFramedItem());
   }

   public void addFramedItem(BlockPos pos, EnumFacing facing, @Nullable ItemStack displayedItem) {
      BlockPos place = this.generator.getRelativePos(pos);
      if (!this.generator.isNotInBox(place)) {
         EntityItemFrame frame = new EntityItemFrame(this.world, place, this.generator.getRelativeFacing(facing));
         if (displayedItem != null) {
            frame.setDisplayedItem(displayedItem);
         }

         this.world.spawnEntity(frame);
      }
   }

   public void addPainting(int x, int y, int z, EnumFacing direction) {
      BlockPos place = new BlockPos(x, y, z);
      this.addPainting(place, direction);
   }

   public void addPainting(BlockPos pos, EnumFacing facing) {
      BlockPos place = this.generator.getRelativePos(pos);
      if (!this.generator.isNotInBox(place)) {
         EntityPainting painting = new EntityPainting(this.world, place, this.generator.getRelativeFacing(facing));
         this.world.spawnEntity(painting);
      }
   }

   public void addRug(int floor, int x0, int z0, int x1, int z1, @Nullable Random rand) {
      if (rand == null) {
         rand = this.generator.getRand();
      }

      IBlockState carpet1 = Blocks.CARPET.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.byDyeDamage(rand.nextInt(15)));
      IBlockState carpet2 = Blocks.CARPET.getDefaultState().withProperty(BlockCarpet.COLOR, EnumDyeColor.byDyeDamage(rand.nextInt(15)));
      int style = rand.nextInt(3) + 1;
      int i = 0;

      for(int xx = x0; xx <= x1; ++xx) {
         for(int zz = z0; zz <= z1; ++zz) {
            IBlockState carpet;
            if (i % style == 0) {
               carpet = carpet1;
            } else {
               carpet = carpet2;
            }

            ++i;
            this.generator.add(carpet, xx, floor, zz);
         }
      }

   }

   public void addDoor(int x, int y, int z, EnumFacing facing, BlockDoor door) {
      this.generator.addRow(this.getDoor(door), x, y, z, EnumFacing.UP, facing);
   }

   public void addStand(int x, int y, int z, EnumFacing facing, @Nullable IBlockState onTop) {
      if (onTop == null) {
         switch(this.rand.nextInt(2)) {
         case 0:
            onTop = this.getCake();
            break;
         case 1:
            onTop = this.getFlowerPot();
         }
      }

      if (onTop != null) {
         this.generator.add(Blocks.PLANKS.getDefaultState(), x, y, z, facing);
         this.generator.add(onTop, x, y + 1, z, facing);
      } else {
         this.addRandomBlock(x, y, z, facing);
      }

   }

   public void addTorch(int x, int y, int z, @Nullable EnumFacing facing) {
      IBlockState torch = Blocks.TORCH.getDefaultState();
      if (facing != null) {
         torch = torch.withProperty(BlockTorch.FACING, facing);
      }

      this.generator.add(torch, x, y, z);
   }

   public List<IBlockState> getBed() {
      List<IBlockState> bed = new ArrayList();
      bed.add(Blocks.BED.getDefaultState().withProperty(BlockBed.PART, EnumPartType.HEAD));
      bed.add(Blocks.BED.getDefaultState().withProperty(BlockBed.PART, EnumPartType.FOOT));
      return bed;
   }

   public List<IBlockState> getDoor(BlockDoor door) {
      List<IBlockState> d = new ArrayList();
      d.add(door.getDefaultState().withProperty(BlockDoor.HALF, EnumDoorHalf.LOWER));
      d.add(door.getDefaultState().withProperty(BlockDoor.HALF, EnumDoorHalf.UPPER));
      return d;
   }

   public IBlockState getCake() {
      return Blocks.CAKE.getDefaultState().withProperty(BlockCake.BITES, Integer.valueOf(this.generator.getRand().nextInt(2)));
   }

   public IBlockState getFlowerPot() {
      return this.getFlowerPot(this.generator.getRand().nextInt(14) + 1);
   }

   public IBlockState getFlowerPot(int flowerType) {
      return Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.LEGACY_DATA, Integer.valueOf(flowerType));
   }

   public void spawnCat(int x, int y, int z, int skin, boolean tamed, @Nullable EntityPlayer player) {
      BlockPos posForMob = this.generator.getRelativePos(new BlockPos(x, y, z));
      if (!this.generator.isNotInBox(posForMob)) {
         Entity entity = EntityHelper.spawnEntity(new ResourceLocation("minecraft:ocelot"), this.world, posForMob);
         if (entity != null) {
            EntityOcelot cat = (EntityOcelot)entity;
            cat.setTameSkin(skin);
            if (tamed) {
               cat.setTamed(true);
               if (player != null) {
                  cat.setOwnerId(EntityPlayer.getUUID(player.getGameProfile()));
               }
            }
         }

      }
   }
}
