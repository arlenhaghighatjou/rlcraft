package slimeknights.mantle.configurate.commented;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import slimeknights.mantle.configurate.ConfigurationNode;

public interface CommentedConfigurationNode extends ConfigurationNode {
   Optional<String> getComment();

   CommentedConfigurationNode setComment(String var1);

   CommentedConfigurationNode getParent();

   List<? extends CommentedConfigurationNode> getChildrenList();

   Map<Object, ? extends CommentedConfigurationNode> getChildrenMap();

   CommentedConfigurationNode setValue(Object var1);

   CommentedConfigurationNode mergeValuesFrom(ConfigurationNode var1);

   CommentedConfigurationNode getAppendedNode();

   CommentedConfigurationNode getNode(Object... var1);
}
