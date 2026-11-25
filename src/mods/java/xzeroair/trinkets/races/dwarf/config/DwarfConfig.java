package xzeroair.trinkets.races.dwarf.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;

public class DwarfConfig {
   private final String name = "dwarf";
   private final String PREFIX = "xat.config.races.dwarf";
   @Comment({"Dwarves Special Fortune Like Effect. Set to False to Disable. Default True"})
   @Name("01. Fortune Effect")
   @LangKey("xat.config.races.dwarf.fortune")
   public boolean fortune = true;
   @Comment({"Should the Dwarves Fortune Effect stack with the Fortune Enchantment?. Set to False to Disable. Default True"})
   @Name("02. Fortune Stacking")
   @LangKey("xat.config.races.dwarf.fortune.stacking")
   public boolean fortune_mix = true;
   @Comment({"Should the Dwarves lower the mining level requirement for pickaxes. IE. an Iron Pickaxe will be able to break Obsidian. Set to False to Disable. Default True"})
   @Name("03. Skilled Mining Ability")
   @LangKey("xat.config.races.dwarf.skilledminer")
   public boolean skilled_miner = true;
   @Comment({"Mining Speed is static at (Block Hardness * 4), Not Including other Modifiers.  Set to False to Disable. Default True"})
   @Name("04. Static Mining")
   @LangKey("xat.config.races.dwarf.static_mining")
   public boolean static_mining = true;
   @Name("Block Settings")
   @LangKey("xat.config.races.dwarf.blocks")
   public DwarfConfig.Blocks BLOCKS = new DwarfConfig.Blocks();
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   private DwarfConfig.Compatability compat = new DwarfConfig.Compatability();
   @Comment({"For More Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:-0.3, Operation:1", "Name:generic.knockbackResistance; Amount:0.2; Operation:1", "Name:generic.movementSpeed, Amount:-0.25, Operation:1", "Name:generic.attackDamage, Amount:0.25, Operation:1", "Name:generic.attackSpeed, Amount:-0.25, Operation:2", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0.25, Operation:2", "Name:generic.luck, Amount:0, Operation:0", "Name:generic.reachDistance, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public DwarfConfig() {
   }

   public class Blocks {
      @Name("01. Ore Blocks that Fortune Effect works on")
      @LangKey("xat.config.races.dwarf.fortune.blocks.list")
      public String[] Blocks = new String[]{"minecraft:coal_ore", "minecraft:lapis_ore", "minecraft:diamond_ore", "minecraft:redstone_ore", "minecraft:lit_redstone_ore", "minecraft:emerald_ore", "minecraft:quartz_ore"};
      @Comment({"Should Blocks give Bonus XP"})
      @Name("02. Bonus XP")
      @LangKey("xat.config.races.dwarf.blocks.xp")
      public boolean bonus_exp = true;
      @Name("03. Bonus XP Max")
      @LangKey("xat.config.races.dwarf.blocks.xp.max")
      public int bonus_exp_max = 2;
      @Name("04. Bonus XP Min")
      @LangKey("xat.config.races.dwarf.blocks.xp.min")
      public int bonus_exp_min = 0;
      @Comment({"Blocks in this List will give Bonus XP Randomly between Bonus_XP_Min and Bonus_XP_Max"})
      @Name("05. Blocks that give Bonus XP")
      @LangKey("xat.config.races.dwarf.blocks.xp.list")
      public String[] xPBlocks = new String[]{"minecraft:coal_ore", "minecraft:iron_ore", "minecraft:gold_ore", "minecraft:lapis_ore", "minecraft:redstone_ore", "minecraft:diamond_ore", "minecraft:emerald_ore", "minecraft:quartz_ore"};
      @Comment({"Should Blocks give at least 1 XP"})
      @Name("06. Minimum XP Blocks")
      @LangKey("xat.config.races.dwarf.blocks.minimum.xp")
      public boolean minXpBlocks = true;
      @Comment({"Blocks in this List will always give 1 xp when broken"})
      @Name("07. Blocks that give a Minimum XP")
      @LangKey("xat.config.races.dwarf.blocks.minimum.xp.list")
      public String[] MinBlocks = new String[]{"minecraft:stone", "minecraft:end_stone"};

      public Blocks() {
      }
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      private DwarfConfig.Compatability.TANCompat tan = new DwarfConfig.Compatability.TANCompat();

      public Compatability() {
      }

      public class TANCompat {
         public TANCompat() {
         }
      }
   }
}
