/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.seata.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The type Collection utils.
 *
 */
public class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Is empty boolean.
     *
     * @param col the col
     * @return the boolean
     */
    public static boolean isEmpty(Collection<?> col) {
        return !isNotEmpty(col);
    }

    /**
     * Is not empty boolean.
     *
     * @param col the col
     * @return the boolean
     */
    public static boolean isNotEmpty(Collection<?> col) {
        return col != null && !col.isEmpty();
    }

    /**
     * Is empty boolean.
     *
     * @param array the array
     * @return the boolean
     */
    public static boolean isEmpty(Object[] array) {
        return !isNotEmpty(array);
    }

    /**
     * Is not empty boolean.
     *
     * @param array the array
     * @return the boolean
     */
    public static boolean isNotEmpty(Object[] array) {
        return array != null && array.length > 0;
    }

    /**
     * Is empty boolean.
     *
     * @param map the map
     * @return the boolean
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return !isNotEmpty(map);
    }

    /**
     * Is not empty boolean.
     *
     * @param map the map
     * @return the boolean
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    /**
     * Collection To string.
     *
     * @param col the col
     * @return the string
     */
    public static String toString(final Collection<?> col) {
        if (col == null) {
            return "null";
        }
        if (col.isEmpty()) {
            return "[]";
        }

        return CycleDependencyHandler.wrap(col, o -> {
            StringBuilder sb = new StringBuilder(32);
            sb.append("[");
            for (Object obj : col) {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                if (obj == col) {
                    sb.append("(this ").append(obj.getClass().getSimpleName()).append(")");
                } else {
                    sb.append(StringUtils.toString(obj));
                }
            }
            sb.append("]");
            return sb.toString();
        });
    }

    /**
     * Map to string.
     *
     * @param map the map
     * @return the string
     */
    public static String toString(final Map<?, ?> map) {
        if (map == null) {
            return "null";
        }
        if (map.isEmpty()) {
            return "{}";
        }

        return CycleDependencyHandler.wrap(map, o -> {
            StringBuilder sb = new StringBuilder(32);
            sb.append("{");
            map.forEach((key, value) -> {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                if (key == map) {
                    sb.append("(this ").append(map.getClass().getSimpleName()).append(")");
                } else {
                    sb.append(StringUtils.toString(key));
                }
                sb.append("->");
                if (value == map) {
                    sb.append("(this ").append(map.getClass().getSimpleName()).append(")");
                } else {
                    sb.append(StringUtils.toString(value));
                }
            });
            sb.append("}");
            return sb.toString();
        });
    }

    /**
     * To string map
     *
     * @param param map
     * @return the string map
     */
    public static Map<String, String> toStringMap(Map<String, Object> param) {
        Map<String, String> covertMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(param)) {
            param.forEach((key, value) -> {
                if (value != null) {
                    if (value instanceof CharSequence || value instanceof Character) {
                        covertMap.put(key, value.toString());
                    } else {
                        covertMap.put(key, StringUtils.toString(value));
                    }
                }
            });
        }
        return covertMap;
    }

    /**
     * Is size equals boolean.
     *
     * @param col0 the col 0
     * @param col1 the col 1
     * @return the boolean
     */
    public static boolean isSizeEquals(Collection<?> col0, Collection<?> col1) {
        if (col0 == null) {
            return col1 == null;
        } else {
            if (col1 == null) {
                return false;
            } else {
                return col0.size() == col1.size();
            }
        }
    }

    public static final String KV_SPLIT = "=";

    public static final String PAIR_SPLIT = "&";

    /**
     * Encode map to string
     *
     * @param map origin map
     * @return String string
     */
    public static String encodeMap(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        if (map.isEmpty()) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(KV_SPLIT).append(entry.getValue()).append(PAIR_SPLIT);
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Decode string to map
     *
     * @param data data
     * @return map map
     */
    public static Map<String, String> decodeMap(String data) {
        if (data == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(data)) {
            return map;
        }
        String[] kvPairs = data.split(PAIR_SPLIT);
        if (kvPairs.length == 0) {
            return map;
        }
        for (String kvPair : kvPairs) {
            if (StringUtils.isNullOrEmpty(kvPair)) {
                continue;
            }
            String[] kvs = kvPair.split(KV_SPLIT);
            if (kvs.length != 2) {
                continue;
            }
            map.put(kvs[0], kvs[1]);
        }
        return map;
    }

    /**
     * Compute if absent.
     * Use this method if you are frequently using the same key,
     * because the get method has no lock.
     *
     * @param map             the map
     * @param key             the key
     * @param mappingFunction the mapping function
     * @param <K>             the type of key
     * @param <V>             the type of value
     * @return the value
     */
    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<? super K, ? extends V> mappingFunction) {
        V value = map.get(key);
        if (value != null) {
            return value;
        }
        return map.computeIfAbsent(key, mappingFunction);
    }

    /**
     * To upper list.
     *
     * @param sourceList the source list
     * @return the list
     */
    public static List<String> toUpperList(List<String> sourceList) {
        if (isEmpty(sourceList)) {
            return sourceList;
        }
        List<String> destList = new ArrayList<>(sourceList.size());
        for (String element : sourceList) {
            if (element != null) {
                destList.add(element.toUpperCase());
            } else {
                destList.add(null);
            }
        }
        return destList;
    }

    /**
     * Get the last item.
     * <p>
     * 'IndexOutOfBoundsException' may be thrown, because the `list.size()` and `list.get(size - 1)` are not atomic.
     * This method can avoid the 'IndexOutOfBoundsException' cause by concurrency.
     * </p>
     *
     * @param list the list
     * @param <T>  the type of item
     * @return the last item
     */
    public static <T> T getLast(List<T> list) {
        if (isEmpty(list)) {
            return null;
        }

        int size;
        while (true) {
            size = list.size();
            if (size == 0) {
                return null;
            }

            try {
                return list.get(size - 1);
            } catch (IndexOutOfBoundsException ex) {
                // catch the exception and continue to retry
            }
        }
    }

    /**
     * Convert a Map<String, Object> into a JSON-formatted string.
     *
     * @param map Map containing key-value pairs
     * @return JSON string representing the Map
     */
    public static String mapToJsonString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        int i = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("\"").append(entry.getKey()).append("\": ");
            if (entry.getValue() instanceof HashMap) {
                HashMap<String, Object> objectHashMap = (HashMap<String, Object>) entry.getValue();
                sb.append(mapToJsonString(objectHashMap));
            } else if (entry.getValue() instanceof String) {
                sb.append("\"");
                sb.append(entry.getValue());
                sb.append("\"");
            } else {
                sb.append(entry.getValue());
            }
            i++;
        }
        sb.append("}");
        return sb.toString();
    }
}
