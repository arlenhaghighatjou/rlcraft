package com.github.benmanes.caffeine.cache;

import com.github.benmanes.caffeine.base.UnsafeAccess;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

class PW<K, V> extends Node<K, V> implements NodeFactory<K, V> {
   protected static final long KEY_OFFSET = UnsafeAccess.objectFieldOffset(PW.class, "key");
   protected static final long VALUE_OFFSET = UnsafeAccess.objectFieldOffset(PW.class, "value");
   volatile K key;
   volatile References.WeakValueReference<V> value;

   PW() {
   }

   PW(K key, ReferenceQueue<K> keyReferenceQueue, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
      this(key, value, valueReferenceQueue, weight, now);
   }

   PW(Object keyReference, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
      UnsafeAccess.UNSAFE.putObject(this, KEY_OFFSET, keyReference);
      UnsafeAccess.UNSAFE.putObject(this, VALUE_OFFSET, new References.WeakValueReference(keyReference, value, valueReferenceQueue));
   }

   public final K getKey() {
      return (K)UnsafeAccess.UNSAFE.getObject(this, KEY_OFFSET);
   }

   public final Object getKeyReference() {
      return UnsafeAccess.UNSAFE.getObject(this, KEY_OFFSET);
   }

   public final V getValue() {
      return (V)((Reference)UnsafeAccess.UNSAFE.getObject(this, VALUE_OFFSET)).get();
   }

   public final Object getValueReference() {
      return UnsafeAccess.UNSAFE.getObject(this, VALUE_OFFSET);
   }

   public final void setValue(V value, ReferenceQueue<V> referenceQueue) {
      ((Reference)this.getValueReference()).clear();
      UnsafeAccess.UNSAFE.putObject(this, VALUE_OFFSET, new References.WeakValueReference(this.getKeyReference(), value, referenceQueue));
   }

   public final boolean containsValue(Object value) {
      return this.getValue() == value;
   }

   public Node<K, V> newNode(K key, ReferenceQueue<K> keyReferenceQueue, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
      return new PW<K, V>(key, keyReferenceQueue, value, valueReferenceQueue, weight, now);
   }

   public Node<K, V> newNode(Object keyReference, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
      return new PW<K, V>(keyReference, value, valueReferenceQueue, weight, now);
   }

   public boolean weakValues() {
      return true;
   }

   public final boolean isAlive() {
      Object key = this.getKeyReference();
      return key != RETIRED_STRONG_KEY && key != DEAD_STRONG_KEY;
   }

   public final boolean isRetired() {
      return this.getKeyReference() == RETIRED_STRONG_KEY;
   }

   public final void retire() {
      ((Reference)this.getValueReference()).clear();
      UnsafeAccess.UNSAFE.putObject(this, KEY_OFFSET, RETIRED_STRONG_KEY);
   }

   public final boolean isDead() {
      return this.getKeyReference() == DEAD_STRONG_KEY;
   }

   public final void die() {
      ((Reference)this.getValueReference()).clear();
      UnsafeAccess.UNSAFE.putObject(this, KEY_OFFSET, DEAD_STRONG_KEY);
   }
}
