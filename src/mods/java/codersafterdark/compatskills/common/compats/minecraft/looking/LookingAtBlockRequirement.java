package codersafterdark.compatskills.common.compats.minecraft.looking;

import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class LookingAtBlockRequirement extends Requirement {
   private final List<IProperty<?>> watchedProperties;
   private final IBlockState state;
   private final IBlockState defaultState;

   public LookingAtBlockRequirement(IBlockState state, List<IProperty<?>> watchedProperties) {
      this.state = state;
      this.defaultState = state.getBlock().getDefaultState();
      this.watchedProperties = watchedProperties;
      StringBuilder displayInfo = new StringBuilder(state.getBlock().getLocalizedName());
      int size = watchedProperties.size();
      if (size > 0) {
         displayInfo.append(" {");
      }

      for(int i = 0; i < size; ++i) {
         IProperty<?> property = (IProperty)watchedProperties.get(i);
         displayInfo.append(property.getName()).append(": ").append(state.getValue(property));
         displayInfo.append(i + 1 == size ? "}" : ", ");
      }

      this.tooltip = TextFormatting.GRAY + " - " + TextFormatting.DARK_GREEN + (new TextComponentTranslation("compatskills.requirements.format.looking_at", new Object[]{"%s", displayInfo.toString()})).getUnformattedComponentText();
   }

   public static LookingAtBlockRequirement fromString(String input) throws RequirementException {
      if (input.isEmpty()) {
         throw new RequirementException("No Block given.");
      } else {
         String[] inputInfo = input.split("\\|");
         String blockName = inputInfo[0];
         Block block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockName));
         if (block == null) {
            throw new RequirementException("No Block found matching: '" + blockName + "'.");
         } else {
            IBlockState blockState = block.getDefaultState();
            List<IProperty<?>> watchedProperties = new ArrayList();
            if (inputInfo.length > 1) {
               String props = input.substring(blockName.length() + 1);

               JsonObject properties;
               try {
                  properties = (JsonObject)(new JsonParser()).parse(props);
               } catch (JsonParseException var12) {
                  throw new RequirementException("Invalid properties json: '" + props + "'.");
               }

               BlockStateContainer blockStateContainer = block.getBlockState();

               for(Entry<String, JsonElement> entry : properties.entrySet()) {
                  IProperty<?> property = blockStateContainer.getProperty((String)entry.getKey());
                  if (property == null) {
                     throw new RequirementException("Failed to find property: '" + (String)entry.getKey() + "'.");
                  }

                  blockState = setValueHelper(blockState, property, ((JsonElement)entry.getValue()).getAsString());
                  watchedProperties.add(property);
               }
            }

            return new LookingAtBlockRequirement(blockState, watchedProperties);
         }
      }
   }

   private static <T extends Comparable<T>> IBlockState setValueHelper(IBlockState blockState, IProperty<T> property, String stringValue) throws RequirementException {
      return (IBlockState)property.parseValue(stringValue).toJavaUtil().map((propertyValue) -> {
         return blockState.withProperty(property, propertyValue);
      }).orElseThrow(() -> {
         return new RequirementException("Failed to find value " + stringValue + " for property " + property.getName());
      });
   }

   public boolean achievedByPlayer(EntityPlayer player) {
      RayTraceResult rayTrace = Utils.getLookingAt(player);
      if (rayTrace != null && rayTrace.typeOfHit == Type.BLOCK) {
         IBlockState lookingAt = player.getEntityWorld().getBlockState(rayTrace.getBlockPos());
         if (this.state == lookingAt) {
            return true;
         } else {
            return this.defaultState != lookingAt.getBlock().getDefaultState() ? false : this.watchedProperties.stream().allMatch((property) -> {
               return this.state.getValue(property).equals(lookingAt.getValue(property));
            });
         }
      } else {
         return false;
      }
   }

   public RequirementComparision matches(Requirement o) {
      return o instanceof LookingAtBlockRequirement && this.state == ((LookingAtBlockRequirement)o).state ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object obj) {
      return obj == this || obj instanceof LookingAtBlockRequirement && this.state == ((LookingAtBlockRequirement)obj).state;
   }

   public int hashCode() {
      return this.state.hashCode();
   }

   public boolean isCacheable() {
      return false;
   }
}
