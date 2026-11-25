package com.endertech.minecraft.forge.units;

import com.endertech.common.CommonArray;
import com.endertech.common.CommonPath;
import com.endertech.common.CommonString;
import com.endertech.common.KeyValuePair;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.blocks.BlockState;
import com.endertech.minecraft.forge.blocks.ForgeBlock;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.items.ForgeStack;
import com.google.common.collect.UnmodifiableIterator;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class UnitId implements IWithMeta<UnitId> {
   public static final UnitId EMPTY = from("", "");
   public static final String FORMAT_FULL = "<modId:unitName:meta>";
   public static final String DESCRIPTION = "Id is a basic unit (block or item) identifier in <modId:unitName:meta> format.\nModId can be omitted for vanilla items. Meta can be omitted too if it equals 0.\nUnitName must be lowercase, words separated by '_', words order - from private to common (example: black_iron_ore).\nUnit name will be automatically converted to ore dictionary name with reverse word order (example: oreIronBlack).\nUse '*' char as meta value to specify all possible values.\nTo define multiple block states, you can use block properties instead of meta. The format is <modId:blockName:[prop1=value1, prop2=value2]>\nAlso you may use just ore dictionary name as full id.\n";
   public static final String DELIMITER = ":";
   public static final String MOD_ID_MINECRAFT = "minecraft";
   public static final String REG_NAME_WORDS_DELIMITER = "_";
   public static final String UNLOCALIZED_NAME_DELIMITER = ".";
   public static final String INCODE_DELIMITER = "$";
   public static final int META_DEFAULT = 0;
   public static final int META_ALL_INT = 32767;
   public static final String META_ALL_CHAR = "*";
   public static final Pattern PATTERN = Pattern.compile("^([A-Za-z0-9_\\-$]*+)?(?::)?([A-Za-z_][A-Za-z0-9_\\-/.]*)(?::)?([0-9]{1,5}|\\*|\\[.*\\])?");
   private final String modId;
   private final String regName;
   private final String dictName;
   private final String metadata;
   protected final UnitId.Cache cache = new UnitId.Cache();

   public UnitId(String modId, String regName, String metadata) {
      this.modId = modId;
      this.regName = regName;
      this.dictName = regToDictName(regName);
      this.metadata = metadata;
   }

   public static String joinWords(String delimiter, String... words) {
      List<String> wordsList = new ArrayList();

      for(String word : words) {
         word = word.trim();
         if (!word.isEmpty()) {
            wordsList.add(word);
         }
      }

      return String.join(delimiter, wordsList);
   }

   public static String[] capitalizeWords(String... words) {
      String[] capitalizedWords = new String[words.length];

      for(int i = 0; i < capitalizedWords.length; ++i) {
         capitalizedWords[i] = CommonString.capitalizeFirstChar(words[i]);
      }

      return capitalizedWords;
   }

   public static String buildRegName(String... nameWords) {
      return joinWords("_", nameWords).toLowerCase(Locale.ROOT);
   }

   public static String buildDictName(String... nameWords) {
      nameWords = capitalizeWords(nameWords);
      String dictName = String.join("", nameWords);
      dictName = CommonString.decapitalizeFirstChar(dictName);
      return dictName;
   }

   public static String[] splitRegName(String regName, boolean capitalizeWords, boolean invertOrder) {
      String[] nameWords = regName.split("_");
      if (capitalizeWords) {
         nameWords = capitalizeWords(nameWords);
      }

      if (invertOrder) {
         CommonArray.reverse(nameWords);
      }

      return nameWords;
   }

   public static String[] splitDictName(String dictName) {
      List<String> wordsList = new ArrayList();
      int wordStartsAt = 0;

      for(int i = 0; i < dictName.length(); ++i) {
         char ch = dictName.charAt(i);
         if (Character.isUpperCase(ch) && i != 0) {
            String word = dictName.substring(wordStartsAt, i);
            wordsList.add(word);
            wordStartsAt = i;
         }

         if (i == dictName.length() - 1) {
            String lastWord = dictName.substring(wordStartsAt);
            wordsList.add(lastWord);
         }
      }

      return (String[])wordsList.toArray(new String[0]);
   }

   public static String regToDictName(String regName) {
      String[] nameWords = splitRegName(regName, true, true);
      String dictName = buildDictName(nameWords);
      return dictName;
   }

   public static String dictToRegName(String dictName) {
      String[] nameWords = splitDictName(dictName);
      return buildRegName(nameWords);
   }

   public static boolean isNullOrEmpty(UnitId unitId) {
      return unitId == null || unitId.isEmpty();
   }

   public boolean isEmpty() {
      return CommonString.isNullOrEmpty(this.getModId()) || CommonString.isNullOrEmpty(this.getRegName());
   }

   public boolean notEmpty() {
      return !this.isEmpty();
   }

   public static UnitId from(String id) {
      id = id.trim();
      if (!id.isEmpty() && !id.equals(":")) {
         Matcher matcher = PATTERN.matcher(id);
         if (matcher.matches()) {
            String modId = UnitId.Groups.MOD_ID.getFrom(matcher);
            if (modId == null) {
               modId = "minecraft";
            }

            String unitName = UnitId.Groups.UNIT_NAME.getFrom(matcher);
            if (unitName == null) {
               unitName = "!NULL!";
            }

            String metadata = UnitId.Groups.METADATA.getFrom(matcher);
            if (metadata == null) {
               metadata = "";
            }

            return new UnitId(modId, unitName, metadata);
         } else {
            ForgeMain.instance.getLogger().error("Unable to parse '{}'. Invalid id format", id);
            return EMPTY;
         }
      } else {
         return EMPTY;
      }
   }

   public static UnitId from(Enum<?> e, boolean withMetaAllByDefault) {
      String stringId = e instanceof IForgeEnum ? ((IForgeEnum)e).getName() : e.name();
      stringId = stringId.replace("$", ":");
      UnitId unitId = from(stringId);
      return withMetaAllByDefault && !unitId.hasMetaData() ? (UnitId)unitId.withMetaAll() : unitId;
   }

   public static UnitId from(Path path) {
      String stringId = CommonPath.getFileNameOnly(path).replace("$", ":");
      return from(stringId);
   }

   public static UnitId from(String modId, String... nameWords) {
      return from(modId, buildRegName(nameWords));
   }

   public static UnitId from(String modId, String unitName) {
      return new UnitId(modId, unitName, "");
   }

   public static UnitId from(String modId, String unitName, int meta) {
      switch(meta) {
      case 0:
         return new UnitId(modId, unitName, "");
      case 32767:
         return new UnitId(modId, unitName, "*");
      default:
         return new UnitId(modId, unitName, String.valueOf(meta));
      }
   }

   public static UnitId from(ResourceLocation location, int meta) {
      return from(location.getResourceDomain(), location.getResourcePath(), meta);
   }

   public static UnitId from(Item item, int meta) {
      ResourceLocation location = (ResourceLocation)Item.REGISTRY.getNameForObject(item);
      return location != null ? from(location, meta) : EMPTY;
   }

   public static UnitId from(Item item) {
      return from(item, 0);
   }

   public static UnitId from(Block block, int meta) {
      ResourceLocation location = (ResourceLocation)Block.REGISTRY.getNameForObject(block);
      return location != null ? from(location, meta) : EMPTY;
   }

   public static UnitId from(Block block) {
      return from(block, 0);
   }

   public static UnitId from(IBlockState state) {
      return from(state.getBlock(), BlockState.getMeta(state));
   }

   public static UnitId from(BlockState state) {
      return state.exists() ? from(state.getBlock(), state.getMeta()) : EMPTY;
   }

   public static UnitId from(ItemState state) {
      return state.exists() ? from(state.getItem(), state.getMeta()) : EMPTY;
   }

   public static boolean metasAreMatched(int meta1, int meta2) {
      return meta1 == meta2 || meta1 == 32767 || meta2 == 32767;
   }

   public String getMetaData() {
      return this.metadata;
   }

   public boolean metaMatches(int meta) {
      return metasAreMatched(this.getMeta(), meta);
   }

   @Nullable
   protected Block getBlockOrNull() {
      ResourceLocation key = this.toResLoc();
      return Block.REGISTRY.containsKey(key) ? (Block)Block.REGISTRY.getObject(key) : null;
   }

   public BlockState getBlockState() {
      if (this.cache.blockState != null) {
         return this.cache.blockState;
      } else {
         Block block = this.getBlockOrNull();
         int meta = this.getMeta();
         if (block == null) {
            ForgeStack stack = this.getFirstFoundOre();
            if (stack.exists()) {
               block = ForgeBlock.getBlockOrNull(stack.getItem());
            }

            if (block != null) {
               meta = stack.getMeta();
            }
         }

         if (block != null) {
            if (this.hasProperties()) {
               List<BlockState> states = this.getMatchedStates(block, this.cache.props);
               if (!states.isEmpty()) {
                  this.cache.blockState = (BlockState)states.get(0);
               }
            } else {
               this.cache.blockState = BlockState.from(block, meta);
            }
         }

         return this.cache.blockState != null ? this.cache.blockState : BlockState.EMPTY;
      }
   }

   @Nullable
   public IBlockState getIBlockState() {
      if (this.cache.iblockState != null) {
         return this.cache.iblockState;
      } else {
         BlockState state = this.getBlockState();
         if (state.exists()) {
            int meta = state.getMeta();
            if (meta != 0 && meta != 32767) {
               this.cache.iblockState = state.getBlock().getStateFromMeta(meta);
            } else {
               this.cache.iblockState = state.getBlock().getDefaultState();
            }
         }

         return this.cache.iblockState;
      }
   }

   protected List<BlockState> getMatchedStates(Block block, List<KeyValuePair> props) {
      List<BlockState> list = new ArrayList();
      Map<IProperty<?>, Object> properties = new HashMap();
      BlockStateContainer container = block.getBlockState();

      for(KeyValuePair prop : props) {
         IProperty<?> property = container.getProperty(prop.key);
         if (property != null) {
            Object value = property.parseValue(prop.value).orNull();
            if (value != null) {
               properties.put(property, value);
            }
         }
      }

      UnmodifiableIterator var12 = container.getValidStates().iterator();

      while(var12.hasNext()) {
         IBlockState state = (IBlockState)var12.next();
         boolean match = true;

         for(Entry<IProperty<?>, Object> prop : properties.entrySet()) {
            Object value = state.getValue((IProperty)prop.getKey());
            if (!value.equals(prop.getValue())) {
               match = false;
               break;
            }
         }

         if (match) {
            BlockState matchedState = BlockState.from(state);
            if (!list.contains(matchedState)) {
               list.add(matchedState);
            }
         }
      }

      return list;
   }

   @Nullable
   protected Item getItemOrNull() {
      ResourceLocation key = this.toResLoc();
      return Item.REGISTRY.containsKey(key) ? (Item)Item.REGISTRY.getObject(key) : null;
   }

   public ItemState getItemState() {
      if (this.cache.itemState != null) {
         return this.cache.itemState;
      } else {
         Item item = this.getItemOrNull();
         int meta = this.getMeta();
         if (item == null) {
            ForgeStack stack = this.getFirstFoundOre();
            if (stack.exists()) {
               item = stack.getItem();
               meta = stack.getMeta();
            }
         }

         if (item != null) {
            this.cache.itemState = ItemState.from(item, meta);
            return this.cache.itemState;
         } else {
            return ItemState.EMPTY;
         }
      }
   }

   public List<BlockState> getAllBlockStates() {
      if (this.cache.allBlockStates.isEmpty()) {
         Block block = this.getBlockOrNull();
         if (block != null) {
            if (this.hasProperties()) {
               List<BlockState> states = this.getMatchedStates(block, this.cache.props);
               this.cache.allBlockStates.addAll(states);
            } else {
               BlockState state = BlockState.from(block, this.getMeta());
               this.cache.allBlockStates.add(state);
            }
         }

         for(ItemStack stack : this.getAllOres()) {
            block = ForgeBlock.getBlockOrNull(stack.getItem());
            if (block != null) {
               if (this.hasProperties()) {
                  for(BlockState state : this.getMatchedStates(block, this.cache.props)) {
                     if (!this.cache.allBlockStates.contains(state)) {
                        this.cache.allBlockStates.add(state);
                     }
                  }
               } else {
                  BlockState state = BlockState.from(block, stack.getMetadata());
                  if (!this.cache.allBlockStates.contains(state)) {
                     this.cache.allBlockStates.add(state);
                  }
               }
            }
         }
      }

      return Collections.unmodifiableList(this.cache.allBlockStates);
   }

   protected List<ItemStack> getAllOres() {
      if (this.cache.allOres.isEmpty() && this.isOreDictName()) {
         this.cache.allOres = OreDictionary.getOres(this.getDictName(), false);
      }

      return Collections.unmodifiableList(this.cache.allOres);
   }

   protected ForgeStack getFirstFoundOre() {
      List<ItemStack> allOres = this.getAllOres();
      return allOres.isEmpty() ? ForgeStack.EMPTY : ForgeStack.from((ItemStack)allOres.get(0));
   }

   public List<ItemState> getAllItemStates() {
      if (this.cache.allItemStates.isEmpty()) {
         ItemState itemState = this.getItemState();
         if (itemState.exists()) {
            this.cache.allItemStates.add(itemState);
         }

         for(ItemStack stack : this.getAllOres()) {
            itemState = ItemState.from(stack.getItem(), stack.getMetadata());
            if (!this.cache.allItemStates.contains(itemState)) {
               this.cache.allItemStates.add(itemState);
            }
         }
      }

      return Collections.unmodifiableList(this.cache.allItemStates);
   }

   public UnitId withMeta(int meta) {
      return from(this.getModId(), this.getRegName(), meta);
   }

   public UnitId withMetaData(String metadata) {
      return new UnitId(this.getModId(), this.getRegName(), metadata);
   }

   public UnitId withName(String newName) {
      return new UnitId(this.getModId(), newName, this.getMetaData());
   }

   public UnitId withName(String... nameWords) {
      return new UnitId(this.getModId(), buildRegName(nameWords), this.getMetaData());
   }

   public UnitId withNamePrefix(String prefix) {
      return this.withName(prefix, this.getRegName());
   }

   public UnitId withNameSuffix(String suffix) {
      return this.withName(this.getRegName(), suffix);
   }

   public String toString() {
      if (this.cache.string != null) {
         return this.cache.string;
      } else {
         StringJoiner joiner = new StringJoiner(":");
         boolean defaultModId = this.getModId().equals("minecraft");
         if (!defaultModId) {
            joiner.add(this.getModId());
         }

         joiner.add(this.getRegName());
         if (this.hasMetaData()) {
            joiner.add(this.getMetaData());
         }

         this.cache.string = joiner.toString();
         if (this.cache.string.equals(":")) {
            this.cache.string = "";
         }

         return this.cache.string;
      }
   }

   public String toConfigName() {
      return this.toResLoc().toString().replace(":", "$").replaceAll("[\\n\\r\\t\\f`?*\\\\<>|\\\":]", "");
   }

   public ResourceLocation toResLoc() {
      if (this.cache.location == null) {
         this.cache.location = new ResourceLocation(this.getModId(), this.getRegName());
      }

      return this.cache.location;
   }

   public ModelResourceLocation toModelResLoc_Inventory() {
      return this.toModelResLoc("inventory");
   }

   public ModelResourceLocation toModelResLoc_Normal() {
      return this.toModelResLoc("");
   }

   public ModelResourceLocation toModelResLoc(String variant) {
      return new ModelResourceLocation(this.toResLoc(), variant);
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof UnitId)) {
         return super.equals(obj);
      } else {
         UnitId another = (UnitId)obj;
         return this.getModId().equals(another.getModId()) && this.getRegName().equals(another.getRegName()) && this.getMetaData().equals(another.getMetaData());
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getModId(), this.getRegName(), this.getMetaData()});
   }

   protected boolean isOreDictName() {
      return this.getRegName().equals(this.getDictName()) && this.getModId().equals("minecraft") && (!this.hasMetaData() || this.hasProperties());
   }

   public String getRegName() {
      return this.regName;
   }

   public String getDictName() {
      return this.dictName;
   }

   public String getModId() {
      return this.modId;
   }

   public int getMeta() {
      if (this.cache.meta != null) {
         return this.cache.meta.intValue();
      } else {
         if (this.getMetaData().equals("*")) {
            this.cache.meta = Integer.valueOf(32767);
         } else if (CommonString.allCharsAreDigits(this.metadata)) {
            this.cache.meta = Integer.valueOf(this.metadata);
         } else {
            this.cache.meta = Integer.valueOf(0);
         }

         return this.cache.meta.intValue();
      }
   }

   public boolean hasMetaData() {
      return !this.getMetaData().isEmpty();
   }

   public boolean hasProperties() {
      if (this.cache.props == null) {
         this.cache.props = new ArrayList();
         String data = this.getMetaData();
         if (data.startsWith("[") && data.endsWith("]")) {
            data = data.substring(1, data.length() - 1);

            for(String elem : data.split(",")) {
               KeyValuePair pair = KeyValuePair.from(elem);
               if (!pair.key.isEmpty() && !pair.value.isEmpty() && !this.cache.props.contains(pair)) {
                  this.cache.props.add(pair);
               }
            }
         }
      }

      return !this.cache.props.isEmpty();
   }

   public boolean matches(BlockState state) {
      for(BlockState blockstate : this.getAllBlockStates()) {
         if (blockstate.matches(state)) {
            return true;
         }
      }

      return false;
   }

   public boolean matches(ItemStack stack) {
      return this.matches(ItemState.from(stack));
   }

   public boolean matches(ItemState state) {
      for(ItemState itemstate : this.getAllItemStates()) {
         if (itemstate.matches(state)) {
            return true;
         }
      }

      return false;
   }

   protected class Cache {
      public BlockState blockState;
      public IBlockState iblockState;
      public ItemState itemState;
      public List<BlockState> allBlockStates = new ArrayList();
      public List<ItemState> allItemStates = new ArrayList();
      public List<ItemStack> allOres = new ArrayList();
      public List<KeyValuePair> props;
      public ResourceLocation location;
      public String string;
      public Integer meta;

      protected Cache() {
      }
   }

   protected static enum Groups {
      FULL_ID,
      MOD_ID,
      UNIT_NAME,
      METADATA;

      private Groups() {
      }

      @Nullable
      public String getFrom(Matcher matcher) {
         return matcher.group(this.ordinal());
      }
   }
}
