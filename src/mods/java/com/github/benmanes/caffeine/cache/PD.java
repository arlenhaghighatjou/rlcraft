package com.github.benmanes.caffeine.cache;

import com.github.benmanes.caffeine.base.UnsafeAccess;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

class PD<K, V> extends Node<K, V> implements NodeFactory<K, V> {
   protected static final long KEY_OFFSET = UnsafeAccess.objectFieldOffset(PD.class, "key");
   protected static final long VALUE_OFFSET = UnsafeAccess.objectFieldOffset(PD.class, "value");
   volatile K key;
   volatile References.SoftValueReference<V> value;

   PD() {
   }

   PD(K key, ReferenceQueue<K> keyReferenceQueue, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
      this(key, value, valueReferenceQueue, weight, now);
   }

   PD(Object keyReference, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
      UnsafeAccess.UNSAFE.putObject(this, KEY_OFFSET, keyReference);
      UnsafeAccess.UNSAFE.putObject(this, VALUE_OFFSET, new References.SoftValueReference(keyReference, value, valueReferenceQueue));
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
      UnsafeAccess.UNSAFE.putObject(this, VALUE_OFFSET, new References.SoftValueReference(this.getKeyReference(), value, referenceQueue));
   }

   public final boolean containsValue(Object value) {
      return this.getValue() == value;
   }

   public Node<K, V> newNode(K key, ReferenceQueue<K> keyReferenceQueue, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
      return new PD<K, V>(key, keyReferenceQueue, value, valueReferenceQueue, weight, now);
   }

   public Node<K, V> newNode(Object keyReference, V value, ReferenceQueue<V> valueReferenceQueue, int weight, long now) {
      return new PD<K, V>(keyReference, value, valueReferenceQueue, weight, now);
   }

   public boolean softValues() {
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
