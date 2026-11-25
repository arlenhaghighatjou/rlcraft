package com.mcwbridges.kikoz.init;

import com.mcwbridges.kikoz.objects.blocks.BambooBridge;
import com.mcwbridges.kikoz.objects.blocks.BridgeBlock;
import com.mcwbridges.kikoz.objects.blocks.BridgeLog;
import com.mcwbridges.kikoz.objects.blocks.BridgeRope;
import com.mcwbridges.kikoz.objects.blocks.BridgeStone;
import com.mcwbridges.kikoz.objects.blocks.RailBridge;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockInit {
   public static final List<Block> BLOCKS = new ArrayList();
   public static final Block MOST1 = new BridgeBlock("most1", Material.IRON);
   public static final Block MOST2 = new BridgeBlock("most2", Material.IRON);
   public static final Block OAK_LOG_BRIDGE_MIDDLE = new BridgeLog("oak_log_bridge_middle", Material.WOOD);
   public static final Block OAK_LOG_BRIDGE_END = new BridgeLog("oak_log_bridge_end", Material.WOOD);
   public static final Block BIRCH_LOG_BRIDGE_END = new BridgeLog("birch_log_bridge_end", Material.WOOD);
   public static final Block BIRCH_LOG_BRIDGE_MIDDLE = new BridgeLog("birch_log_bridge_middle", Material.WOOD);
   public static final Block ACACIA_LOG_BRIDGE_END = new BridgeLog("acacia_log_bridge_end", Material.WOOD);
   public static final Block ACACIA_LOG_BRIDGE_MIDDLE = new BridgeLog("acacia_log_bridge_middle", Material.WOOD);
   public static final Block SPRUCE_LOG_BRIDGE_END = new BridgeLog("spruce_log_bridge_end", Material.WOOD);
   public static final Block SPRUCE_LOG_BRIDGE_MIDDLE = new BridgeLog("spruce_log_bridge_middle", Material.WOOD);
   public static final Block JUNGLE_LOG_BRIDGE_END = new BridgeLog("jungle_log_bridge_end", Material.WOOD);
   public static final Block JUNGLE_LOG_BRIDGE_MIDDLE = new BridgeLog("jungle_log_bridge_middle", Material.WOOD);
   public static final Block DARK_OAK_LOG_BRIDGE_END = new BridgeLog("dark_oak_log_bridge_end", Material.WOOD);
   public static final Block DARK_OAK_LOG_BRIDGE_MIDDLE = new BridgeLog("dark_oak_log_bridge_middle", Material.WOOD);
   public static final Block ROPE_OAK_BRIDGE = new BridgeRope("rope_oak_bridge", Material.WOOD);
   public static final Block ROPE_OAK_BRIDGE_END = new BridgeRope("rope_oak_bridge_end", Material.WOOD);
   public static final Block ROPE_BIRCH_BRIDGE = new BridgeRope("rope_birch_bridge", Material.WOOD);
   public static final Block ROPE_BIRCH_BRIDGE_END = new BridgeRope("rope_birch_bridge_end", Material.WOOD);
   public static final Block ROPE_SPRUCE_BRIDGE = new BridgeRope("rope_spruce_bridge", Material.WOOD);
   public static final Block ROPE_SPRUCE_BRIDGE_END = new BridgeRope("rope_spruce_bridge_end", Material.WOOD);
   public static final Block ROPE_JUNGLE_BRIDGE = new BridgeRope("rope_jungle_bridge", Material.WOOD);
   public static final Block ROPE_JUNGLE_BRIDGE_END = new BridgeRope("rope_jungle_bridge_end", Material.WOOD);
   public static final Block ROPE_ACACIA_BRIDGE = new BridgeRope("rope_acacia_bridge", Material.WOOD);
   public static final Block ROPE_ACACIA_BRIDGE_END = new BridgeRope("rope_acacia_bridge_end", Material.WOOD);
   public static final Block ROPE_DARK_OAK_BRIDGE = new BridgeRope("rope_dark_oak_bridge", Material.WOOD);
   public static final Block ROPE_DARK_OAK_BRIDGE_END = new BridgeRope("rope_dark_oak_bridge_end", Material.WOOD);
   public static final Block STONE_BRICK_BRIDGE = new BridgeStone("stone_brick_bridge", Material.ROCK);
   public static final Block STONE_BRICK_BRIDGE_END = new BridgeStone("stone_brick_bridge_end", Material.ROCK);
   public static final Block BRICK_BRIDGE = new BridgeStone("brick_bridge", Material.ROCK);
   public static final Block BRICK_BRIDGE_END = new BridgeStone("brick_bridge_end", Material.ROCK);
   public static final Block SANDSTONE_BRIDGE = new BridgeStone("sandstone_bridge", Material.ROCK);
   public static final Block SANDSTONE_BRIDGE_END = new BridgeStone("sandstone_bridge_end", Material.ROCK);
   public static final Block ORANGE_SANDSTONE_BRIDGE = new BridgeStone("orange_sandstone_bridge", Material.ROCK);
   public static final Block ORANGE_SANDSTONE_BRIDGE_END = new BridgeStone("orange_sandstone_bridge_end", Material.ROCK);
   public static final Block MOSSY_STONE_BRICK_BRIDGE = new BridgeStone("mossy_stone_brick_bridge", Material.ROCK);
   public static final Block MOSSY_STONE_BRICK_BRIDGE_END = new BridgeStone("mossy_stone_brick_bridge_end", Material.ROCK);
   public static final Block OAK_RAIL_BRIDGE = new RailBridge("oak_rail_bridge", Material.WOOD);
   public static final Block OAK_RAIL_BRIDGE_END = new RailBridge("oak_rail_bridge_end", Material.WOOD);
   public static final Block SPRUCE_RAIL_BRIDGE = new RailBridge("spruce_rail_bridge", Material.WOOD);
   public static final Block SPRUCE_RAIL_BRIDGE_END = new RailBridge("spruce_rail_bridge_end", Material.WOOD);
   public static final Block BIRCH_RAIL_BRIDGE = new RailBridge("birch_rail_bridge", Material.WOOD);
   public static final Block BIRCH_RAIL_BRIDGE_END = new RailBridge("birch_rail_bridge_end", Material.WOOD);
   public static final Block JUNGLE_RAIL_BRIDGE = new RailBridge("jungle_rail_bridge", Material.WOOD);
   public static final Block JUNGLE_RAIL_BRIDGE_END = new RailBridge("jungle_rail_bridge_end", Material.WOOD);
   public static final Block ACACIA_RAIL_BRIDGE = new RailBridge("acacia_rail_bridge", Material.WOOD);
   public static final Block ACACIA_RAIL_BRIDGE_END = new RailBridge("acacia_rail_bridge_end", Material.WOOD);
   public static final Block DARK_OAK_RAIL_BRIDGE = new RailBridge("dark_oak_rail_bridge", Material.WOOD);
   public static final Block DARK_OAK_RAIL_BRIDGE_END = new RailBridge("dark_oak_rail_bridge_end", Material.WOOD);
   public static final Block BAMBOO_BRIDGE = new BambooBridge("bamboo_bridge", Material.WOOD);
   public static final Block BAMBOO_BRIDGE_END = new BambooBridge("bamboo_bridge_end", Material.WOOD);
   public static final Block DRY_BAMBOO_BRIDGE = new BambooBridge("dry_bamboo_bridge", Material.WOOD);
   public static final Block DRY_BAMBOO_BRIDGE_END = new BambooBridge("dry_bamboo_bridge_end", Material.WOOD);

   public BlockInit() {
   }
}
