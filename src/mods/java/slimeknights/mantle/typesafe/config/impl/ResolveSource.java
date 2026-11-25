package slimeknights.mantle.typesafe.config.impl;

import slimeknights.mantle.typesafe.config.ConfigException;

final class ResolveSource {
   final AbstractConfigObject root;
   final ResolveSource.Node<Container> pathFromRoot;

   ResolveSource(AbstractConfigObject root, ResolveSource.Node<Container> pathFromRoot) {
      this.root = root;
      this.pathFromRoot = pathFromRoot;
   }

   ResolveSource(AbstractConfigObject root) {
      this.root = root;
      this.pathFromRoot = null;
   }

   private AbstractConfigObject rootMustBeObj(Container value) {
      return (AbstractConfigObject)(value instanceof AbstractConfigObject ? (AbstractConfigObject)value : SimpleConfigObject.empty());
   }

   private static ResolveSource.ResultWithPath findInObject(AbstractConfigObject obj, ResolveContext context, Path path) throws AbstractConfigValue.NotPossibleToResolve {
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace("*** finding '" + path + "' in " + obj);
      }

      Path restriction = context.restrictToChild();
      ResolveResult<? extends AbstractConfigValue> partiallyResolved = context.restrict(path).resolve(obj, new ResolveSource(obj));
      ResolveContext newContext = partiallyResolved.context.restrict(restriction);
      if (partiallyResolved.value instanceof AbstractConfigObject) {
         ResolveSource.ValueWithPath pair = findInObject((AbstractConfigObject)partiallyResolved.value, path);
         return new ResolveSource.ResultWithPath(ResolveResult.make(newContext, pair.value), pair.pathFromRoot);
      } else {
         throw new ConfigException.BugOrBroken("resolved object to non-object " + obj + " to " + partiallyResolved);
      }
   }

   private static ResolveSource.ValueWithPath findInObject(AbstractConfigObject obj, Path path) {
      try {
         return findInObject(obj, path, (ResolveSource.Node)null);
      } catch (ConfigException.NotResolved var3) {
         throw ConfigImpl.improveNotResolved(path, var3);
      }
   }

   private static ResolveSource.ValueWithPath findInObject(AbstractConfigObject obj, Path path, ResolveSource.Node<Container> parents) {
      String key = path.first();
      Path next = path.remainder();
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace("*** looking up '" + key + "' in " + obj);
      }

      AbstractConfigValue v = obj.attemptPeekWithPartialResolve(key);
      ResolveSource.Node<Container> newParents = parents == null ? new ResolveSource.Node(obj) : parents.prepend(obj);
      if (next == null) {
         return new ResolveSource.ValueWithPath(v, newParents);
      } else {
         return v instanceof AbstractConfigObject ? findInObject((AbstractConfigObject)v, next, newParents) : new ResolveSource.ValueWithPath((AbstractConfigValue)null, newParents);
      }
   }

   ResolveSource.ResultWithPath lookupSubst(ResolveContext context, SubstitutionExpression subst, int prefixLength) throws AbstractConfigValue.NotPossibleToResolve {
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace(context.depth(), "searching for " + subst);
      }

      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace(context.depth(), subst + " - looking up relative to file it occurred in");
      }

      ResolveSource.ResultWithPath result = findInObject(this.root, context, subst.path());
      if (result.result.value == null) {
         Path unprefixed = subst.path().subPath(prefixLength);
         if (prefixLength > 0) {
            if (ConfigImpl.traceSubstitutionsEnabled()) {
               ConfigImpl.trace(result.result.context.depth(), unprefixed + " - looking up relative to parent file");
            }

            result = findInObject(this.root, result.result.context, unprefixed);
         }

         if (result.result.value == null && result.result.context.options().getUseSystemEnvironment()) {
            if (ConfigImpl.traceSubstitutionsEnabled()) {
               ConfigImpl.trace(result.result.context.depth(), unprefixed + " - looking up in system environment");
            }

            result = findInObject(ConfigImpl.envVariablesAsConfigObject(), context, unprefixed);
         }
      }

      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace(result.result.context.depth(), "resolved to " + result);
      }

      return result;
   }

   ResolveSource pushParent(Container parent) {
      if (parent == null) {
         throw new ConfigException.BugOrBroken("can't push null parent");
      } else {
         if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace("pushing parent " + parent + " ==root " + (parent == this.root) + " onto " + this);
         }

         if (this.pathFromRoot == null) {
            if (parent == this.root) {
               return new ResolveSource(this.root, new ResolveSource.Node(parent));
            } else {
               if (ConfigImpl.traceSubstitutionsEnabled() && this.root.hasDescendant((AbstractConfigValue)parent)) {
                  ConfigImpl.trace("***** BUG ***** tried to push parent " + parent + " without having a path to it in " + this);
               }

               return this;
            }
         } else {
            Container parentParent = this.pathFromRoot.head();
            if (ConfigImpl.traceSubstitutionsEnabled() && parentParent != null && !parentParent.hasDescendant((AbstractConfigValue)parent)) {
               ConfigImpl.trace("***** BUG ***** trying to push non-child of " + parentParent + ", non-child was " + parent);
            }

            return new ResolveSource(this.root, this.pathFromRoot.prepend(parent));
         }
      }
   }

   ResolveSource resetParents() {
      return this.pathFromRoot == null ? this : new ResolveSource(this.root);
   }

   private static ResolveSource.Node<Container> replace(ResolveSource.Node<Container> list, Container old, AbstractConfigValue replacement) {
      Container child = list.head();
      if (child != old) {
         throw new ConfigException.BugOrBroken("Can only replace() the top node we're resolving; had " + child + " on top and tried to replace " + old + " overall list was " + list);
      } else {
         Container parent = list.tail() == null ? null : (Container)list.tail().head();
         if (replacement != null && replacement instanceof Container) {
            if (parent == null) {
               return new ResolveSource.Node<Container>((Container)replacement);
            } else {
               AbstractConfigValue newParent = parent.replaceChild((AbstractConfigValue)old, replacement);
               ResolveSource.Node<Container> newTail = replace(list.tail(), parent, newParent);
               return newTail != null ? newTail.prepend((Container)replacement) : new ResolveSource.Node((Container)replacement);
            }
         } else if (parent == null) {
            return null;
         } else {
            AbstractConfigValue newParent = parent.replaceChild((AbstractConfigValue)old, (AbstractConfigValue)null);
            return replace(list.tail(), parent, newParent);
         }
      }
   }

   ResolveSource replaceCurrentParent(Container old, Container replacement) {
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace("replaceCurrentParent old " + old + "@" + System.identityHashCode(old) + " replacement " + replacement + "@" + System.identityHashCode(old) + " in " + this);
      }

      if (old == replacement) {
         return this;
      } else if (this.pathFromRoot != null) {
         ResolveSource.Node<Container> newPath = replace(this.pathFromRoot, old, (AbstractConfigValue)replacement);
         if (ConfigImpl.traceSubstitutionsEnabled()) {
            ConfigImpl.trace("replaced " + old + " with " + replacement + " in " + this);
            ConfigImpl.trace("path was: " + this.pathFromRoot + " is now " + newPath);
         }

         return newPath != null ? new ResolveSource((AbstractConfigObject)newPath.last(), newPath) : new ResolveSource(SimpleConfigObject.empty());
      } else if (old == this.root) {
         return new ResolveSource(this.rootMustBeObj(replacement));
      } else {
         throw new ConfigException.BugOrBroken("attempt to replace root " + this.root + " with " + replacement);
      }
   }

   ResolveSource replaceWithinCurrentParent(AbstractConfigValue old, AbstractConfigValue replacement) {
      if (ConfigImpl.traceSubstitutionsEnabled()) {
         ConfigImpl.trace("replaceWithinCurrentParent old " + old + "@" + System.identityHashCode(old) + " replacement " + replacement + "@" + System.identityHashCode(old) + " in " + this);
      }

      if (old == replacement) {
         return this;
      } else if (this.pathFromRoot != null) {
         Container parent = this.pathFromRoot.head();
         AbstractConfigValue newParent = parent.replaceChild(old, replacement);
         return this.replaceCurrentParent(parent, newParent instanceof Container ? (Container)newParent : null);
      } else if (old == this.root && replacement instanceof Container) {
         return new ResolveSource(this.rootMustBeObj((Container)replacement));
      } else {
         throw new ConfigException.BugOrBroken("replace in parent not possible " + old + " with " + replacement + " in " + this);
      }
   }

   public String toString() {
      return "ResolveSource(root=" + this.root + ", pathFromRoot=" + this.pathFromRoot + ")";
   }

   static final class Node<T> {
      final T value;
      final ResolveSource.Node<T> next;

      Node(T value, ResolveSource.Node<T> next) {
         this.value = value;
         this.next = next;
      }

      Node(T value) {
         this(value, (ResolveSource.Node)null);
      }

      ResolveSource.Node<T> prepend(T value) {
         return new ResolveSource.Node<T>(value, this);
      }

      T head() {
         return this.value;
      }

      ResolveSource.Node<T> tail() {
         return this.next;
      }

      T last() {
         ResolveSource.Node<T> i;
         for(i = this; i.next != null; i = i.next) {
            ;
         }

         return i.value;
      }

      ResolveSource.Node<T> reverse() {
         if (this.next == null) {
            return this;
         } else {
            ResolveSource.Node<T> reversed = new ResolveSource.Node<T>(this.value);

            for(ResolveSource.Node<T> i = this.next; i != null; i = i.next) {
               reversed = reversed.prepend(i.value);
            }

            return reversed;
         }
      }

      public String toString() {
         StringBuffer sb = new StringBuffer();
         sb.append("[");

         for(ResolveSource.Node<T> toAppendValue = this.reverse(); toAppendValue != null; toAppendValue = toAppendValue.next) {
            sb.append(toAppendValue.value.toString());
            if (toAppendValue.next != null) {
               sb.append(" <= ");
            }
         }

         sb.append("]");
         return sb.toString();
      }
   }

   static final class ResultWithPath {
      final ResolveResult<? extends AbstractConfigValue> result;
      final ResolveSource.Node<Container> pathFromRoot;

      ResultWithPath(ResolveResult<? extends AbstractConfigValue> result, ResolveSource.Node<Container> pathFromRoot) {
         this.result = result;
         this.pathFromRoot = pathFromRoot;
      }

      public String toString() {
         return "ResultWithPath(result=" + this.result + ", pathFromRoot=" + this.pathFromRoot + ")";
      }
   }

   static final class ValueWithPath {
      final AbstractConfigValue value;
      final ResolveSource.Node<Container> pathFromRoot;

      ValueWithPath(AbstractConfigValue value, ResolveSource.Node<Container> pathFromRoot) {
         this.value = value;
         this.pathFromRoot = pathFromRoot;
      }

      public String toString() {
         return "ValueWithPath(value=" + this.value + ", pathFromRoot=" + this.pathFromRoot + ")";
      }
   }
}
