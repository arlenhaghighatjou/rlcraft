package vazkii.quark.world.feature;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration.Type;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.module.Feature;

public class BuriedTreasure extends Feature {
   public static final String TAG_TREASURE_MAP = "Quark:TreasureMap";
   public static final String TAG_TREASURE_MAP_DELEGATE = "Quark:TreasureMapDelegate";
   public static final ImmutableSet<ResourceLocation> tablesToEdit = ImmutableSet.of(LootTableList.CHESTS_DESERT_PYRAMID, LootTableList.CHESTS_JUNGLE_TEMPLE, LootTableList.CHESTS_STRONGHOLD_CORRIDOR);
   public static final Map<ResourceLocation, String> customPools = new HashMap();
   public static int rarity;
   public static int quality;

   public BuriedTreasure() {
   }

   public void setupConfig() {
      rarity = this.loadPropInt("Treasure map Rarity", "", 10);
      quality = this.loadPropInt("Treasure map item quality", "This is used for the luck attribute in loot tables. It doesn't affect the loot you get from the map itself.", 2);
   }

   public void preInit(FMLPreInitializationEvent event) {
      LootFunctionManager.registerFunction(new BuriedTreasure.SetAsTreasureFunction.Serializer());
      customPools.put(PirateShips.PIRATE_CHEST_LOOT_TABLE, "quark:pirate_ship");
   }

   @SubscribeEvent
   public void onLootTableLoad(LootTableLoadEvent event) {
      ResourceLocation res = event.getName();
      if (tablesToEdit.contains(res)) {
         if (customPools.containsKey(res)) {
            customPools.get(res);
         }

         event.getTable().getPool("main").addEntry(new LootEntryItem(Items.FILLED_MAP, rarity, quality, new LootFunction[]{new BuriedTreasure.SetAsTreasureFunction()}, new LootCondition[0], "quark:treasure_map"));
      }

   }

   @SubscribeEvent
   public void onUpdate(LivingUpdateEvent event) {
      if (event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();

         for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.hasTagCompound() && ItemNBTHelper.getBoolean(stack, "Quark:TreasureMapDelegate", false)) {
               this.makeMap(stack, player.getEntityWorld(), player.getPosition());
            }
         }
      }

   }

   public ItemStack makeMap(ItemStack itemstack, World world, BlockPos sourcePos) {
      Random r = world.rand;
      boolean validPos = false;
      int tries = 0;

      while(tries <= 100) {
         int distance = 400 + r.nextInt(200);
         double angle = (double)r.nextFloat() * 6.283185307179586D;
         int x = (int)((double)sourcePos.getX() + Math.cos(angle) * (double)distance);
         int z = (int)((double)sourcePos.getZ() + Math.sin(angle) * (double)distance);
         BlockPos treasurePos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 255, z)).add(0, -4, 0);
         IBlockState state = world.getBlockState(treasurePos);
         if (state.getBlock() == Blocks.DIRT) {
            validPos = true;
         }

         ++tries;
         if (validPos) {
            String s = "map_" + itemstack.getMetadata();
            MapData mapdata = new MapData(s);
            world.setData(s, mapdata);
            mapdata.scale = 1;
            mapdata.calculateMapCenter((double)(treasurePos.getX() + (int)((Math.random() - 0.5D) * 64.0D)), (double)(treasurePos.getZ() + (int)((Math.random() - 0.5D) * 64.0D)), mapdata.scale);
            mapdata.dimension = 0;
            mapdata.trackingPosition = true;
            mapdata.unlimitedTracking = true;
            ItemMap.renderBiomePreviewMap(world, itemstack);
            MapData.addTargetDecoration(itemstack, treasurePos, "x", Type.TARGET_X);
            mapdata.markDirty();
            world.setBlockState(treasurePos, Blocks.CHEST.getDefaultState());
            TileEntityChest chest = (TileEntityChest)world.getTileEntity(treasurePos);
            if (chest != null) {
               chest.setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, r.nextLong());
            }

            ItemNBTHelper.setBoolean(itemstack, "Quark:TreasureMap", true);
            ItemNBTHelper.setBoolean(itemstack, "Quark:TreasureMapDelegate", false);
            return itemstack;
         }
      }

      return null;
   }

   public int xy(int x, int y) {
      return x + y * 128;
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public static class SetAsTreasureFunction extends LootFunction {
      protected SetAsTreasureFunction() {
         super(new LootCondition[0]);
      }

      @Nonnull
      public ItemStack apply(@Nonnull ItemStack stack, @Nonnull Random rand, @Nonnull LootContext context) {
         int id = context.getWorld().getUniqueDataId("map");
         stack.setItemDamage(id);
         stack.setTranslatableName("quarkmisc.buried_chest_map");
         NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "display", false);
         cmp.setInteger("MapColor", 9178638);
         ItemNBTHelper.setCompound(stack, "display", cmp);
         ItemNBTHelper.setBoolean(stack, "Quark:TreasureMapDelegate", true);
         return stack;
      }

      public static class Serializer extends net.minecraft.world.storage.loot.functions.LootFunction.Serializer<BuriedTreasure.SetAsTreasureFunction> {
         protected Serializer() {
            super(new ResourceLocation("quark", "set_treasure"), BuriedTreasure.SetAsTreasureFunction.class);
         }

         public void serialize(@Nonnull JsonObject object, @Nonnull BuriedTreasure.SetAsTreasureFunction functionClazz, @Nonnull JsonSerializationContext serializationContext) {
         }

         @Nonnull
         public BuriedTreasure.SetAsTreasureFunction deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext, @Nonnull LootCondition[] conditionsIn) {
            return new BuriedTreasure.SetAsTreasureFunction();
         }
      }
   }
}
