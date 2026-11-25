package org.orecruncher.lib;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;

public final class BlockNameUtil {
   private static final Pattern pattern = Pattern.compile("([\\w\\-]+:[\\w\\.\\-/]+)\\[?((?:\\w+=\\w+)?(?:,\\w+=\\w+)*)\\]?\\+?(\\w+)?");

   private BlockNameUtil() {
   }

   @Nullable
   public static BlockNameUtil.NameResult parseBlockName(@Nonnull String blockName) {
      try {
         Matcher matcher = pattern.matcher(blockName);
         return matcher.matches() ? new BlockNameUtil.NameResult(matcher) : null;
      } catch (Exception var2) {
         LibLog.log().error(String.format("Unable to parse '%s'", blockName), var2);
         return null;
      }
   }

   public static final class NameResult {
      protected String blockName;
      protected Block block;
      protected Map<String, String> properties;
      protected String extras;

      NameResult() {
      }

      NameResult(Matcher matcher) throws NBTException {
         this.blockName = matcher.group(1);
         Block proposed = (Block)Block.REGISTRY.getObject(new ResourceLocation(this.blockName));
         this.block = proposed == Blocks.AIR ? null : proposed;
         String temp = matcher.group(2);
         if (!StringUtils.isEmpty(temp)) {
            this.properties = (Map)Arrays.stream(temp.split(",")).map((elem) -> {
               return elem.split("=");
            }).collect(Collectors.toMap((e) -> {
               return e[0];
            }, (e) -> {
               return e[1];
            }));
         }

         this.extras = matcher.group(3);
      }

      @Nonnull
      public String getBlockName() {
         return this.blockName;
      }

      @Nullable
      public Block getBlock() {
         return this.block;
      }

      public boolean hasProperties() {
         return this.properties != null;
      }

      @Nullable
      public Map<String, String> getProperties() {
         return this.properties;
      }

      public boolean hasExtras() {
         return this.extras != null;
      }

      @Nullable
      public String getExtras() {
         return this.extras;
      }

      @Nonnull
      public String toString() {
         StringBuilder builder = new StringBuilder();
         if (this.getBlock() == null) {
            builder.append("*INVALID* ");
         }

         builder.append(this.getBlockName());
         if (this.hasProperties()) {
            builder.append('[');
            String props = (String)this.properties.entrySet().stream().map((e) -> {
               return (String)e.getKey() + '=' + (String)e.getValue();
            }).collect(Collectors.joining(","));
            builder.append(props);
            builder.append(']');
         }

         return builder.toString();
      }
   }
}
