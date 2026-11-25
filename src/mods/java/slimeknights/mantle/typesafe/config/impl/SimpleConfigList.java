package slimeknights.mantle.typesafe.config.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import slimeknights.mantle.typesafe.config.ConfigException;
import slimeknights.mantle.typesafe.config.ConfigList;
import slimeknights.mantle.typesafe.config.ConfigOrigin;
import slimeknights.mantle.typesafe.config.ConfigRenderOptions;
import slimeknights.mantle.typesafe.config.ConfigValue;
import slimeknights.mantle.typesafe.config.ConfigValueType;

final class SimpleConfigList extends AbstractConfigValue implements ConfigList, Container, Serializable {
   private static final long serialVersionUID = 2L;
   private final List<AbstractConfigValue> value;
   private final boolean resolved;

   SimpleConfigList(ConfigOrigin origin, List<AbstractConfigValue> value) {
      this(origin, value, ResolveStatus.fromValues(value));
   }

   SimpleConfigList(ConfigOrigin origin, List<AbstractConfigValue> value, ResolveStatus status) {
      super(origin);
      this.value = value;
      this.resolved = status == ResolveStatus.RESOLVED;
      if (status != ResolveStatus.fromValues(value)) {
         throw new ConfigException.BugOrBroken("SimpleConfigList created with wrong resolve status: " + this);
      }
   }

   public ConfigValueType valueType() {
      return ConfigValueType.LIST;
   }

   public List<Object> unwrapped() {
      List<Object> list = new ArrayList();

      for(AbstractConfigValue v : this.value) {
         list.add(v.unwrapped());
      }

      return list;
   }

   ResolveStatus resolveStatus() {
      return ResolveStatus.fromBoolean(this.resolved);
   }

   public SimpleConfigList replaceChild(AbstractConfigValue child, AbstractConfigValue replacement) {
      List<AbstractConfigValue> newList = replaceChildInList(this.value, child, replacement);
      return newList == null ? null : new SimpleConfigList(this.origin(), newList);
   }

   public boolean hasDescendant(AbstractConfigValue descendant) {
      return hasDescendantInList(this.value, descendant);
   }

   private SimpleConfigList modify(AbstractConfigValue.NoExceptionsModifier modifier, ResolveStatus newResolveStatus) {
      try {
         return this.modifyMayThrow(modifier, newResolveStatus);
      } catch (RuntimeException var4) {
         throw var4;
      } catch (Exception var5) {
         throw new ConfigException.BugOrBroken("unexpected checked exception", var5);
      }
   }

   private SimpleConfigList modifyMayThrow(AbstractConfigValue.Modifier modifier, ResolveStatus newResolveStatus) throws Exception {
      List<AbstractConfigValue> changed = null;
      int i = 0;

      for(AbstractConfigValue v : this.value) {
         AbstractConfigValue modified = modifier.modifyChildMayThrow((String)null, v);
         if (changed == null && modified != v) {
            changed = new ArrayList();

            for(int j = 0; j < i; ++j) {
               changed.add(this.value.get(j));
            }
         }

         if (changed != null && modified != null) {
            changed.add(modified);
         }

         ++i;
      }

      if (changed != null) {
         if (newResolveStatus != null) {
            return new SimpleConfigList(this.origin(), changed, newResolveStatus);
         } else {
            return new SimpleConfigList(this.origin(), changed);
         }
      } else {
         return this;
      }
   }

   ResolveResult<? extends SimpleConfigList> resolveSubstitutions(ResolveContext context, ResolveSource source) throws AbstractConfigValue.NotPossibleToResolve {
      if (this.resolved) {
         return ResolveResult.<SimpleConfigList>make(context, this);
      } else if (context.isRestrictedToChild()) {
         return ResolveResult.<SimpleConfigList>make(context, this);
      } else {
         try {
            SimpleConfigList.ResolveModifier modifier = new SimpleConfigList.ResolveModifier(context, source.pushParent(this));
            SimpleConfigList value = this.modifyMayThrow(modifier, context.options().getAllowUnresolved() ? null : ResolveStatus.RESOLVED);
            return ResolveResult.<SimpleConfigList>make(modifier.context, value);
         } catch (AbstractConfigValue.NotPossibleToResolve var5) {
            throw var5;
         } catch (RuntimeException var6) {
            throw var6;
         } catch (Exception var7) {
            throw new ConfigException.BugOrBroken("unexpected checked exception", var7);
         }
      }
   }

   SimpleConfigList relativized(final Path prefix) {
      return this.modify(new AbstractConfigValue.NoExceptionsModifier() {
         public AbstractConfigValue modifyChild(String key, AbstractConfigValue v) {
            return v.relativized(prefix);
         }
      }, this.resolveStatus());
   }

   protected boolean canEqual(Object other) {
      return other instanceof SimpleConfigList;
   }

   public boolean equals(Object other) {
      if (!(other instanceof SimpleConfigList)) {
         return false;
      } else {
         return this.canEqual(other) && (this.value == ((SimpleConfigList)other).value || this.value.equals(((SimpleConfigList)other).value));
      }
   }

   public int hashCode() {
      return this.value.hashCode();
   }

   protected void render(StringBuilder sb, int indent, boolean atRoot, ConfigRenderOptions options) {
      if (this.value.isEmpty()) {
         sb.append("[]");
      } else {
         sb.append("[");
         if (options.getFormatted()) {
            sb.append('\n');
         }

         for(AbstractConfigValue v : this.value) {
            if (options.getOriginComments()) {
               String[] lines = v.origin().description().split("\n");

               for(String l : lines) {
                  indent(sb, indent + 1, options);
                  sb.append('#');
                  if (!l.isEmpty()) {
                     sb.append(' ');
                  }

                  sb.append(l);
                  sb.append("\n");
               }
            }

            if (options.getComments()) {
               for(String comment : v.origin().comments()) {
                  indent(sb, indent + 1, options);
                  sb.append("# ");
                  sb.append(comment);
                  sb.append("\n");
               }
            }

            indent(sb, indent + 1, options);
            v.render(sb, indent + 1, atRoot, options);
            sb.append(",");
            if (options.getFormatted()) {
               sb.append('\n');
            }
         }

         sb.setLength(sb.length() - 1);
         if (options.getFormatted()) {
            sb.setLength(sb.length() - 1);
            sb.append('\n');
            indent(sb, indent, options);
         }

         sb.append("]");
      }

   }

   public boolean contains(Object o) {
      return this.value.contains(o);
   }

   public boolean containsAll(Collection<?> c) {
      return this.value.containsAll(c);
   }

   public AbstractConfigValue get(int index) {
      return (AbstractConfigValue)this.value.get(index);
   }

   public int indexOf(Object o) {
      return this.value.indexOf(o);
   }

   public boolean isEmpty() {
      return this.value.isEmpty();
   }

   public Iterator<ConfigValue> iterator() {
      final Iterator<AbstractConfigValue> i = this.value.iterator();
      return new Iterator<ConfigValue>() {
         public boolean hasNext() {
            return i.hasNext();
         }

         public ConfigValue next() {
            return (ConfigValue)i.next();
         }

         public void remove() {
            throw SimpleConfigList.weAreImmutable("iterator().remove");
         }
      };
   }

   public int lastIndexOf(Object o) {
      return this.value.lastIndexOf(o);
   }

   private static ListIterator<ConfigValue> wrapListIterator(final ListIterator<AbstractConfigValue> i) {
      return new ListIterator<ConfigValue>() {
         public boolean hasNext() {
            return i.hasNext();
         }

         public ConfigValue next() {
            return (ConfigValue)i.next();
         }

         public void remove() {
            throw SimpleConfigList.weAreImmutable("listIterator().remove");
         }

         public void add(ConfigValue arg0) {
            throw SimpleConfigList.weAreImmutable("listIterator().add");
         }

         public boolean hasPrevious() {
            return i.hasPrevious();
         }

         public int nextIndex() {
            return i.nextIndex();
         }

         public ConfigValue previous() {
            return (ConfigValue)i.previous();
         }

         public int previousIndex() {
            return i.previousIndex();
         }

         public void set(ConfigValue arg0) {
            throw SimpleConfigList.weAreImmutable("listIterator().set");
         }
      };
   }

   public ListIterator<ConfigValue> listIterator() {
      return wrapListIterator(this.value.listIterator());
   }

   public ListIterator<ConfigValue> listIterator(int index) {
      return wrapListIterator(this.value.listIterator(index));
   }

   public int size() {
      return this.value.size();
   }

   public List<ConfigValue> subList(int fromIndex, int toIndex) {
      List<ConfigValue> list = new ArrayList();

      for(AbstractConfigValue v : this.value.subList(fromIndex, toIndex)) {
         list.add(v);
      }

      return list;
   }

   public Object[] toArray() {
      return this.value.toArray();
   }

   public <T> T[] toArray(T[] a) {
      return (T[])this.value.toArray(a);
   }

   private static UnsupportedOperationException weAreImmutable(String method) {
      return new UnsupportedOperationException("ConfigList is immutable, you can't call List.'" + method + "'");
   }

   public boolean add(ConfigValue e) {
      throw weAreImmutable("add");
   }

   public void add(int index, ConfigValue element) {
      throw weAreImmutable("add");
   }

   public boolean addAll(Collection<? extends ConfigValue> c) {
      throw weAreImmutable("addAll");
   }

   public boolean addAll(int index, Collection<? extends ConfigValue> c) {
      throw weAreImmutable("addAll");
   }

   public void clear() {
      throw weAreImmutable("clear");
   }

   public boolean remove(Object o) {
      throw weAreImmutable("remove");
   }

   public ConfigValue remove(int index) {
      throw weAreImmutable("remove");
   }

   public boolean removeAll(Collection<?> c) {
      throw weAreImmutable("removeAll");
   }

   public boolean retainAll(Collection<?> c) {
      throw weAreImmutable("retainAll");
   }

   public ConfigValue set(int index, ConfigValue element) {
      throw weAreImmutable("set");
   }

   protected SimpleConfigList newCopy(ConfigOrigin newOrigin) {
      return new SimpleConfigList(newOrigin, this.value);
   }

   final SimpleConfigList concatenate(SimpleConfigList other) {
      ConfigOrigin combinedOrigin = SimpleConfigOrigin.mergeOrigins(this.origin(), other.origin());
      List<AbstractConfigValue> combined = new ArrayList(this.value.size() + other.value.size());
      combined.addAll(this.value);
      combined.addAll(other.value);
      return new SimpleConfigList(combinedOrigin, combined);
   }

   private Object writeReplace() throws ObjectStreamException {
      return new SerializedConfigValue(this);
   }

   public SimpleConfigList withOrigin(ConfigOrigin origin) {
      return (SimpleConfigList)super.withOrigin(origin);
   }

   private static class ResolveModifier implements AbstractConfigValue.Modifier {
      ResolveContext context;
      final ResolveSource source;

      ResolveModifier(ResolveContext context, ResolveSource source) {
         this.context = context;
         this.source = source;
      }

      public AbstractConfigValue modifyChildMayThrow(String key, AbstractConfigValue v) throws AbstractConfigValue.NotPossibleToResolve {
         ResolveResult<? extends AbstractConfigValue> result = this.context.resolve(v, this.source);
         this.context = result.context;
         return result.value;
      }
   }
}
