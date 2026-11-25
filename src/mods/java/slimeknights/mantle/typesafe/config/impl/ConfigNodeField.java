package slimeknights.mantle.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import slimeknights.mantle.typesafe.config.ConfigException;

final class ConfigNodeField extends AbstractConfigNode {
   private final ArrayList<AbstractConfigNode> children;

   public ConfigNodeField(Collection<AbstractConfigNode> children) {
      this.children = new ArrayList(children);
   }

   protected Collection<Token> tokens() {
      ArrayList<Token> tokens = new ArrayList();

      for(AbstractConfigNode child : this.children) {
         tokens.addAll(child.tokens());
      }

      return tokens;
   }

   public ConfigNodeField replaceValue(AbstractConfigNodeValue newValue) {
      ArrayList<AbstractConfigNode> childrenCopy = new ArrayList(this.children);

      for(int i = 0; i < childrenCopy.size(); ++i) {
         if (childrenCopy.get(i) instanceof AbstractConfigNodeValue) {
            childrenCopy.set(i, newValue);
            return new ConfigNodeField(childrenCopy);
         }
      }

      throw new ConfigException.BugOrBroken("Field node doesn't have a value");
   }

   public AbstractConfigNodeValue value() {
      for(int i = 0; i < this.children.size(); ++i) {
         if (this.children.get(i) instanceof AbstractConfigNodeValue) {
            return (AbstractConfigNodeValue)this.children.get(i);
         }
      }

      throw new ConfigException.BugOrBroken("Field node doesn't have a value");
   }

   public ConfigNodePath path() {
      for(int i = 0; i < this.children.size(); ++i) {
         if (this.children.get(i) instanceof ConfigNodePath) {
            return (ConfigNodePath)this.children.get(i);
         }
      }

      throw new ConfigException.BugOrBroken("Field node doesn't have a path");
   }

   protected Token separator() {
      for(AbstractConfigNode child : this.children) {
         if (child instanceof ConfigNodeSingleToken) {
            Token t = ((ConfigNodeSingleToken)child).token();
            if (t == Tokens.PLUS_EQUALS || t == Tokens.COLON || t == Tokens.EQUALS) {
               return t;
            }
         }
      }

      return null;
   }

   protected List<String> comments() {
      List<String> comments = new ArrayList();

      for(AbstractConfigNode child : this.children) {
         if (child instanceof ConfigNodeComment) {
            comments.add(((ConfigNodeComment)child).commentText());
         }
      }

      return comments;
   }
}
