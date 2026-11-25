package slimeknights.mantle.configurate.transformation;

import slimeknights.mantle.configurate.ConfigurationNode;

@FunctionalInterface
public interface TransformAction {
   Object[] visitPath(ConfigurationTransformation.NodePath var1, ConfigurationNode var2);
}
