/**
 * Copyright (C) 2008 Robbie Vanbrabant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.garbagecollected.util;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


/** 
 * Dollar sign based syntax for constructing JDK {@link Map} implementations,
 * best used with Java 5's static import feature.
 * <p>
 * Examples:
 * <pre>
 * Map&lt;Integer, String&gt; map = $(1,"one").$(2,"two").$(3,"three").asHashMap();
 * 
 * // $ objects are iterable      
 * for(Entry&lt;Integer, String&gt; e : $(1,"one").$(2,"two")) {
 *     System.out.println(String.format("%s => %s", e.getKey(), e.getValue()));
 * }
 * 
 * // start with $$ for easier iteration
 * for(String[] s : $$("1","one").$("2", "two").asEasy()) {
 *     System.out.println(String.format("%s => %s", s[0], s[1]));
 * }
 * </pre>
 * @author <a href="http://garbagecollected.org">Robbie Vanbrabant</a>
 */
public class DollarMaps {
    private DollarMaps() {}
    
    /**
     * Start constructing a map implementation with the given key and 
     * value. The returned {@link $} instance can be used to further construct 
     * the desired map using {@link $#$(Object, Object)}, or complete 
     * construction by invoking on of the <code>asXXX()</code> where 
     * <code>XXX</code> is the desired map implementation name.
     * <p>
     * The returned {@link $} instance also implements the {@link Iterable}
     * interface, and thus can be used in a Java 5 style for loop.
     * <p>
     * Tip: statically import this method for improved readability.
     * 
     * @param <K> the map's key type
     * @param <V> the map's value type
     * @param key the key for the entry
     * @param value the value for the entry
     * @return a {@link $} instance initialized with the given key and value
     */
    public static <K, V> $<K, V> $(K key, V value) {
        return new $<K, V>(key, value);
    }
    
    public static <K, V> $<K, V> $(Map<K,V> initializer) {
        return new $<K, V>(initializer);
    }
    
    /**
     * Basically the same as {@link #$}, but enforces that the key and the
     * value are of the same type. This enables easier map iteration.
     * 
     * @param <T> the type for both the keys and the values
     * @param key the key for the entry
     * @param value the value for the entry
     * @return a {@link $$} instance initialized with the given key and value
     */
    public static <T> $$<T> $$(T key, T value) {
        return new $$<T>(key, value);
    }
    public static <T> $$<T> $$(Map<T,T> initializer) {
        return new $$<T>(initializer);
    }
    
    public static class $<K, V> implements Iterable<Map.Entry<K,V>> {
        // Predictable iteration order
        private final Map<K, V> m = new LinkedHashMap<K, V>();

        $(K key, V value) {
            this.$(key, value);
        }
        $(Map<K,V> initializer) {
            m.putAll(initializer);
        }
        
        @SuppressWarnings("all") // remove "uses constructor name" warning
        public $<K, V> $(K key, V value) {
            m.put(key, value);
            return this;
        }
        public Map<K, V> asHashMap() {
            return new HashMap<K, V>(m);
        }
        public Map<K, V> asTreeMap() {
            return new TreeMap<K, V>(m);
        }
        public Map<K, V> asConcurrentHashMap() {
            return new ConcurrentHashMap<K, V>(m);
        }
        public Map<K,V> asLinkedHashMap() {
            return new LinkedHashMap<K, V>(m); // defensive copy
        }
        public Map<K,V> asWeakHashMap() {
            return new WeakHashMap<K, V>(m);
        }
        public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K,V>>() {
                private Iterator<Entry<K,V>> iter = m.entrySet().iterator();
                public boolean hasNext() { return iter.hasNext(); }
                public Entry<K, V> next() { return iter.next(); }
                public void remove() {
                    // avoid modifying the internal map
                    throw new UnsupportedOperationException();
                }
            };
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((m == null) ? 0 : m.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final $<?,?> other = ($<?,?>) obj;
            if (m == null) {
                if (other.m != null)
                    return false;
            } else if (!m.equals(other.m))
                return false;
            return true;
        }
        @Override
        public String toString() {
            return m.toString();
        }
    }
    
    // The same as $, except that the key and value need to be of the same type.
    // This enables easier, fail-fast (compile time) key/value iteration.
    public static class $$<T> extends $<T,T> {
        private boolean hasNullKeys;
        
        $$(T key, T value) {
            super(key, value);
            recordIfNull(key);
        }
        @SuppressWarnings("unchecked")
        $$(Map<T,T> initializer) {
            // record nulls, so call super with nothing
            super((Map<T, T>) Collections.emptyMap());
            for (Entry<T,T> e : initializer.entrySet()) {
                this.$(e.getKey(), e.getValue());
            }
        }
        @SuppressWarnings("all") // remove "uses constructor name" warning
        public $$<T> $(T key, T value) {
            super.$(key, value);
            recordIfNull(key);
            return this;
        }
        public Easy<T> asEasy(Class<T> clazz) {
            return new Easy<T>(this, clazz);
        }
        /** 
         * Attempt an Easy conversion using a random key's type.
         * Only use this method when there is no <code>null</code> key
         * in the map. If there is, use {@link #asEasy(Class)} instead.
         * @throws NullPointerException if the map contains a null key
         */
        @SuppressWarnings("unchecked")
        public Easy<T> asEasy() {
            if (hasNullKeys) throw new NullPointerException();
            return new Easy<T>(this, (Class<T>)iterator().next().getClass());
        }
        
        private void recordIfNull(T key) {
            if (!hasNullKeys) if (key == null) hasNullKeys = true;
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + (hasNullKeys ? 1231 : 1237);
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            final $$<?> other = ($$<?>) obj;
            if (hasNullKeys != other.hasNullKeys)
                return false;
            return true;
        }
        @Override
        public String toString() {
            return super.toString();
        }
    }
    
    public static class Easy<T> implements Iterable<T[]> {
        private final Iterable<Entry<T, T>> m;
        private final Class<T> clazz;

        Easy(Iterable<Entry<T, T>> m, Class<T> clazz) {
            this.m = m;
            this.clazz = clazz;
        }
        public Iterator<T[]> iterator() {
            final List<T[]> list = new LinkedList<T[]>();
            for(Entry<T,T> e : m) {
                list.add(toArray(e));
            }
            return new Iterator<T[]>() {
                private Iterator<T[]> iter = list.iterator();
                public boolean hasNext() { return iter.hasNext(); }
                public T[] next() { return iter.next(); }
                public void remove() {
                    // Using this is useless in this context
                    throw new UnsupportedOperationException();
                }
            };
        }
        @SuppressWarnings("unchecked")
        private T[] toArray(Entry<T,T> e) {
           T[] a = (T[])Array.newInstance(this.clazz, 2);
           a[0] = e.getKey(); a[1] = e.getValue();
           return a;
        }
    }
}
