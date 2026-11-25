package slimeknights.mantle.configurate.transformation;

import java.util.Arrays;
import slimeknights.mantle.configurate.ConfigurationNode;

public class ChainedConfigurationTransformation extends ConfigurationTransformation {
   private final ConfigurationTransformation[] transformations;

   public ChainedConfigurationTransformation(ConfigurationTransformation[] transformations) {
      this.transformations = (ConfigurationTransformation[])Arrays.copyOf(transformations, transformations.length);
   }

   public void apply(ConfigurationNode node) {
      for(ConfigurationTransformation transformation : this.transformations) {
         transformation.apply(node);
      }

   }
}
