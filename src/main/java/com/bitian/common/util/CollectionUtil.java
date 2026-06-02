package com.bitian.common.util;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 集合工具类
 * <p>提供对 Collection、Map、数组等容器类型的常用操作，所有方法均为 null 安全。</p>
 *
 * @author admin
 */
public class CollectionUtil {

    private CollectionUtil() {
        // 工具类禁止实例化
    }

    // ==================== 原有方法（保持向后兼容） ====================

    /**
     * List 转数组
     *
     * @param list 源列表
     * @param <T>  元素类型
     * @return 转换后的数组
     * @deprecated 方法命名不符合 Java 规范，请使用 {@link #listToArray(List)}
     */
    @Deprecated
    public static <T> T[] ListToArray(List<T> list) {
        return listToArray(list);
    }

    /**
     * 数组转 List
     *
     * @param t   源数组
     * @param <T> 元素类型
     * @return 转换后的 List（固定大小）
     * @deprecated 方法命名不符合 Java 规范，请使用 {@link #arrayToList(Object[])}
     */
    @Deprecated
    @SafeVarargs
    public static <T> List<T> ArrayToList(T... t) {
        return arrayToList(t);
    }

    // ==================== 判空 / 非空 ====================

    /**
     * 判断集合是否为空（null 或无元素）
     *
     * @param collection 待检查集合
     * @return true 表示为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否非空
     *
     * @param collection 待检查集合
     * @return true 表示非空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断 Map 是否为空（null 或无键值对）
     *
     * @param map 待检查 Map
     * @return true 表示为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断 Map 是否非空
     *
     * @param map 待检查 Map
     * @return true 表示非空
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 判断数组是否为空（null 或长度为 0）
     *
     * @param array 待检查数组
     * @param <T>   元素类型
     * @return true 表示为空
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 判断数组是否非空
     *
     * @param array 待检查数组
     * @param <T>   元素类型
     * @return true 表示非空
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    // ==================== 转换 ====================

    /**
     * List 转数组
     *
     * @param list 源列表
     * @param <T>  元素类型
     * @return 转换后的数组；如果 list 为 null，返回空 Object 数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] listToArray(List<T> list) {
        if (list == null) {
            return (T[]) new Object[0];
        }
        Object[] t = new Object[list.size()];
        return (T[]) list.toArray(t);
    }

    /**
     * 数组转 List（返回可变 ArrayList）
     *
     * @param t   源数组
     * @param <T> 元素类型
     * @return 转换后的可变 List；如果数组为 null，返回空 ArrayList
     */
    @SafeVarargs
    public static <T> List<T> arrayToList(T... t) {
        if (t == null || t.length == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(t));
    }

    /**
     * 将集合元素通过映射函数转换为新的 List
     *
     * <pre>{@code
     * List<String> names = CollectionUtil.map(users, User::getName);
     * }</pre>
     *
     * @param collection 源集合
     * @param mapper     映射函数
     * @param <T>        源元素类型
     * @param <R>        目标元素类型
     * @return 转换后的 List
     */
    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> mapper) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 将集合元素通过映射函数转换为新的 Set
     *
     * @param collection 源集合
     * @param mapper     映射函数
     * @param <T>        源元素类型
     * @param <R>        目标元素类型
     * @return 转换后的 Set
     */
    public static <T, R> Set<R> mapToSet(Collection<T> collection, Function<T, R> mapper) {
        if (isEmpty(collection)) {
            return new LinkedHashSet<>();
        }
        return collection.stream().map(mapper).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * 将集合转换为 Map，根据指定的 key 提取函数
     * <p>如果存在重复 key，后者覆盖前者。</p>
     *
     * <pre>{@code
     * Map<Long, User> userMap = CollectionUtil.toMap(users, User::getId);
     * }</pre>
     *
     * @param collection  源集合
     * @param keyMapper   key 提取函数
     * @param <T>         元素类型
     * @param <K>         key 类型
     * @return 转换后的 Map
     */
    public static <T, K> Map<K, T> toMap(Collection<T> collection, Function<T, K> keyMapper) {
        if (isEmpty(collection)) {
            return new LinkedHashMap<>();
        }
        return collection.stream()
                .collect(Collectors.toMap(keyMapper, Function.identity(), (a, b) -> b, LinkedHashMap::new));
    }

    /**
     * 将集合转换为 Map，分别指定 key 和 value 提取函数
     * <p>如果存在重复 key，后者覆盖前者。</p>
     *
     * @param collection  源集合
     * @param keyMapper   key 提取函数
     * @param valueMapper value 提取函数
     * @param <T>         元素类型
     * @param <K>         key 类型
     * @param <V>         value 类型
     * @return 转换后的 Map
     */
    public static <T, K, V> Map<K, V> toMap(Collection<T> collection,
                                             Function<T, K> keyMapper,
                                             Function<T, V> valueMapper) {
        if (isEmpty(collection)) {
            return new LinkedHashMap<>();
        }
        return collection.stream()
                .collect(Collectors.toMap(keyMapper, valueMapper, (a, b) -> b, LinkedHashMap::new));
    }

    // ==================== 过滤 ====================

    /**
     * 根据条件过滤集合
     *
     * <pre>{@code
     * List<User> adults = CollectionUtil.filter(users, u -> u.getAge() >= 18);
     * }</pre>
     *
     * @param collection 源集合
     * @param predicate  过滤条件
     * @param <T>        元素类型
     * @return 过滤后的新 List
     */
    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 过滤集合中的 null 元素
     *
     * @param collection 源集合
     * @param <T>        元素类型
     * @return 不含 null 的新 List
     */
    public static <T> List<T> filterNull(Collection<T> collection) {
        return filter(collection, Objects::nonNull);
    }

    /**
     * 查找第一个满足条件的元素
     *
     * @param collection 源集合
     * @param predicate  匹配条件
     * @param <T>        元素类型
     * @return 匹配的元素，如果不存在则返回 null
     */
    public static <T> T findFirst(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return null;
        }
        return collection.stream().filter(predicate).findFirst().orElse(null);
    }

    /**
     * 判断集合中是否存在满足条件的元素
     *
     * @param collection 源集合
     * @param predicate  匹配条件
     * @param <T>        元素类型
     * @return true 表示存在
     */
    public static <T> boolean anyMatch(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return false;
        }
        return collection.stream().anyMatch(predicate);
    }

    /**
     * 判断集合中是否所有元素都满足条件
     *
     * @param collection 源集合
     * @param predicate  匹配条件
     * @param <T>        元素类型
     * @return true 表示全部满足
     */
    public static <T> boolean allMatch(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return true;
        }
        return collection.stream().allMatch(predicate);
    }

    /**
     * 判断集合中是否没有元素满足条件
     *
     * @param collection 源集合
     * @param predicate  匹配条件
     * @param <T>        元素类型
     * @return true 表示没有元素满足
     */
    public static <T> boolean noneMatch(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return true;
        }
        return collection.stream().noneMatch(predicate);
    }

    // ==================== 分组 / 分区 ====================

    /**
     * 按指定 key 对集合进行分组
     *
     * <pre>{@code
     * Map<String, List<User>> byDept = CollectionUtil.groupBy(users, User::getDepartment);
     * }</pre>
     *
     * @param collection 源集合
     * @param classifier 分组 key 提取函数
     * @param <T>        元素类型
     * @param <K>        分组 key 类型
     * @return 分组后的 Map
     */
    public static <T, K> Map<K, List<T>> groupBy(Collection<T> collection, Function<T, K> classifier) {
        if (isEmpty(collection)) {
            return new LinkedHashMap<>();
        }
        return collection.stream()
                .collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.toList()));
    }

    /**
     * 按条件将集合分为两组（满足条件 / 不满足条件）
     *
     * <pre>{@code
     * Map<Boolean, List<Integer>> result = CollectionUtil.partition(numbers, n -> n > 0);
     * List<Integer> positive = result.get(true);
     * List<Integer> nonPositive = result.get(false);
     * }</pre>
     *
     * @param collection 源集合
     * @param predicate  分区条件
     * @param <T>        元素类型
     * @return key 为 true/false 的 Map
     */
    public static <T> Map<Boolean, List<T>> partition(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            Map<Boolean, List<T>> result = new HashMap<>(4);
            result.put(true, new ArrayList<>());
            result.put(false, new ArrayList<>());
            return result;
        }
        return collection.stream().collect(Collectors.partitioningBy(predicate));
    }

    // ==================== 去重 ====================

    /**
     * 对集合进行去重（保留插入顺序）
     *
     * @param collection 源集合
     * @param <T>        元素类型
     * @return 去重后的新 List
     */
    public static <T> List<T> distinct(Collection<T> collection) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 根据指定属性进行去重（保留每组第一个元素）
     *
     * <pre>{@code
     * List<User> unique = CollectionUtil.distinctBy(users, User::getEmail);
     * }</pre>
     *
     * @param collection   源集合
     * @param keyExtractor 属性提取函数
     * @param <T>          元素类型
     * @param <K>          去重 key 类型
     * @return 去重后的新 List
     */
    public static <T, K> List<T> distinctBy(Collection<T> collection, Function<T, K> keyExtractor) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        Set<K> seen = new LinkedHashSet<>();
        List<T> result = new ArrayList<>();
        for (T item : collection) {
            if (seen.add(keyExtractor.apply(item))) {
                result.add(item);
            }
        }
        return result;
    }

    // ==================== 扁平化 ====================

    /**
     * 将嵌套集合扁平化为单层 List
     *
     * <pre>{@code
     * List<List<Integer>> nested = Arrays.asList(Arrays.asList(1,2), Arrays.asList(3,4));
     * List<Integer> flat = CollectionUtil.flatten(nested);
     * // [1, 2, 3, 4]
     * }</pre>
     *
     * @param nestedCollection 嵌套集合
     * @param <T>              元素类型
     * @return 扁平化后的 List
     */
    public static <T> List<T> flatten(Collection<? extends Collection<T>> nestedCollection) {
        if (isEmpty(nestedCollection)) {
            return new ArrayList<>();
        }
        return nestedCollection.stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 先对集合每个元素映射为子集合，再扁平化
     *
     * <pre>{@code
     * List<String> allTags = CollectionUtil.flatMap(articles, Article::getTags);
     * }</pre>
     *
     * @param collection 源集合
     * @param mapper     元素到子集合的映射函数
     * @param <T>        源元素类型
     * @param <R>        目标元素类型
     * @return 扁平化后的 List
     */
    public static <T, R> List<R> flatMap(Collection<T> collection,
                                         Function<T, Collection<R>> mapper) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .map(mapper)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    // ==================== 集合运算 ====================

    /**
     * 求两个集合的交集
     *
     * @param c1  集合 1
     * @param c2  集合 2
     * @param <T> 元素类型
     * @return 交集 List
     */
    public static <T> List<T> intersection(Collection<T> c1, Collection<T> c2) {
        if (isEmpty(c1) || isEmpty(c2)) {
            return new ArrayList<>();
        }
        Set<T> set = new HashSet<>(c2);
        return c1.stream().filter(set::contains).collect(Collectors.toList());
    }

    /**
     * 求两个集合的并集（去重）
     *
     * @param c1  集合 1
     * @param c2  集合 2
     * @param <T> 元素类型
     * @return 并集 List
     */
    public static <T> List<T> union(Collection<T> c1, Collection<T> c2) {
        Set<T> set = new LinkedHashSet<>();
        if (isNotEmpty(c1)) {
            set.addAll(c1);
        }
        if (isNotEmpty(c2)) {
            set.addAll(c2);
        }
        return new ArrayList<>(set);
    }

    /**
     * 求差集：存在于 c1 但不存在于 c2 的元素
     *
     * @param c1  集合 1
     * @param c2  集合 2
     * @param <T> 元素类型
     * @return 差集 List
     */
    public static <T> List<T> subtract(Collection<T> c1, Collection<T> c2) {
        if (isEmpty(c1)) {
            return new ArrayList<>();
        }
        if (isEmpty(c2)) {
            return new ArrayList<>(c1);
        }
        Set<T> set = new HashSet<>(c2);
        return c1.stream().filter(e -> !set.contains(e)).collect(Collectors.toList());
    }

    // ==================== 分批处理 ====================

    /**
     * 将集合按指定大小分批
     *
     * <pre>{@code
     * List<List<User>> batches = CollectionUtil.partition(users, 100);
     * for (List<User> batch : batches) {
     *     batchInsert(batch);
     * }
     * }</pre>
     *
     * @param list      源列表
     * @param batchSize 每批大小，必须大于 0
     * @param <T>       元素类型
     * @return 分批后的列表
     */
    public static <T> List<List<T>> split(List<T> list, int batchSize) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be greater than 0");
        }
        int size = list.size();
        int batchCount = (size + batchSize - 1) / batchSize;
        List<List<T>> result = new ArrayList<>(batchCount);
        for (int i = 0; i < size; i += batchSize) {
            result.add(new ArrayList<>(list.subList(i, Math.min(i + batchSize, size))));
        }
        return result;
    }

    // ==================== 安全访问 ====================

    /**
     * 安全获取 List 中指定下标的元素
     *
     * @param list  源列表
     * @param index 下标（支持负数，-1 表示最后一个元素）
     * @param <T>   元素类型
     * @return 对应元素，越界或 list 为 null 时返回 null
     */
    public static <T> T getOrNull(List<T> list, int index) {
        if (isEmpty(list)) {
            return null;
        }
        int actualIndex = index < 0 ? list.size() + index : index;
        if (actualIndex < 0 || actualIndex >= list.size()) {
            return null;
        }
        return list.get(actualIndex);
    }

    /**
     * 安全获取 List 中指定下标的元素，不存在时返回默认值
     *
     * @param list         源列表
     * @param index        下标（支持负数）
     * @param defaultValue 默认值
     * @param <T>          元素类型
     * @return 对应元素或默认值
     */
    public static <T> T getOrDefault(List<T> list, int index, T defaultValue) {
        T value = getOrNull(list, index);
        return value != null ? value : defaultValue;
    }

    /**
     * 安全获取第一个元素
     *
     * @param collection 源集合
     * @param <T>        元素类型
     * @return 第一个元素，集合为空时返回 null
     */
    public static <T> T first(Collection<T> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        if (collection instanceof List) {
            return ((List<T>) collection).get(0);
        }
        return collection.iterator().next();
    }

    /**
     * 安全获取最后一个元素
     *
     * @param list 源列表
     * @param <T>  元素类型
     * @return 最后一个元素，列表为空时返回 null
     */
    public static <T> T last(List<T> list) {
        return getOrNull(list, -1);
    }

    // ==================== 排序 ====================

    /**
     * 对集合排序并返回新 List（不修改原集合）
     *
     * @param collection 源集合
     * @param comparator 比较器
     * @param <T>        元素类型
     * @return 排序后的新 List
     */
    public static <T> List<T> sort(Collection<T> collection, Comparator<T> comparator) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream().sorted(comparator).collect(Collectors.toList());
    }

    /**
     * 按自然顺序排序
     *
     * @param collection 源集合
     * @param <T>        元素类型（需实现 Comparable）
     * @return 排序后的新 List
     */
    public static <T extends Comparable<? super T>> List<T> sort(Collection<T> collection) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }
        return collection.stream().sorted().collect(Collectors.toList());
    }

    // ==================== 聚合 ====================

    /**
     * 对集合使用逗号拼接为字符串
     *
     * <pre>{@code
     * String ids = CollectionUtil.join(idList, ",");
     * // "1,2,3"
     * }</pre>
     *
     * @param collection 源集合
     * @param delimiter  分隔符
     * @param <T>        元素类型
     * @return 拼接后的字符串；集合为空时返回空字符串
     */
    public static <T> String join(Collection<T> collection, String delimiter) {
        if (isEmpty(collection)) {
            return "";
        }
        return collection.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(delimiter));
    }

    /**
     * 统计集合中各元素出现的次数
     *
     * @param collection 源集合
     * @param <T>        元素类型
     * @return 元素到出现次数的 Map
     */
    public static <T> Map<T, Long> frequency(Collection<T> collection) {
        if (isEmpty(collection)) {
            return new LinkedHashMap<>();
        }
        return collection.stream()
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));
    }

    /**
     * 统计满足条件的元素数量
     *
     * @param collection 源集合
     * @param predicate  匹配条件
     * @param <T>        元素类型
     * @return 满足条件的元素数量
     */
    public static <T> long count(Collection<T> collection, Predicate<T> predicate) {
        if (isEmpty(collection)) {
            return 0L;
        }
        return collection.stream().filter(predicate).count();
    }

    // ==================== 集合创建 ====================

    /**
     * 创建不可变 List
     *
     * @param elements 元素
     * @param <T>      元素类型
     * @return 不可变 List
     */
    @SafeVarargs
    public static <T> List<T> of(T... elements) {
        if (elements == null || elements.length == 0) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(elements));
    }

    /**
     * 创建可变 ArrayList 并填入初始元素
     *
     * @param elements 初始元素
     * @param <T>      元素类型
     * @return 可变 ArrayList
     */
    @SafeVarargs
    public static <T> List<T> newArrayList(T... elements) {
        if (elements == null || elements.length == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(elements));
    }

    /**
     * 创建可变 HashSet 并填入初始元素
     *
     * @param elements 初始元素
     * @param <T>      元素类型
     * @return 可变 HashSet
     */
    @SafeVarargs
    public static <T> Set<T> newHashSet(T... elements) {
        if (elements == null || elements.length == 0) {
            return new HashSet<>();
        }
        Set<T> set = new HashSet<>((int) (elements.length / 0.75f) + 1);
        Collections.addAll(set, elements);
        return set;
    }

    /**
     * 创建可变 LinkedHashSet 并填入初始元素（保持插入顺序）
     *
     * @param elements 初始元素
     * @param <T>      元素类型
     * @return 可变 LinkedHashSet
     */
    @SafeVarargs
    public static <T> Set<T> newLinkedHashSet(T... elements) {
        if (elements == null || elements.length == 0) {
            return new LinkedHashSet<>();
        }
        Set<T> set = new LinkedHashSet<>((int) (elements.length / 0.75f) + 1);
        Collections.addAll(set, elements);
        return set;
    }

    // ==================== 安全操作 ====================

    /**
     * 返回集合本身，如果为 null 则返回空集合
     *
     * @param collection 源集合
     * @param <T>        元素类型
     * @return 非 null 的集合
     */
    public static <T> Collection<T> emptyIfNull(Collection<T> collection) {
        return collection == null ? Collections.emptyList() : collection;
    }

    /**
     * 返回 List 本身，如果为 null 则返回空 List
     *
     * @param list 源列表
     * @param <T>  元素类型
     * @return 非 null 的 List
     */
    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * 返回 Set 本身，如果为 null 则返回空 Set
     *
     * @param set 源集合
     * @param <T> 元素类型
     * @return 非 null 的 Set
     */
    public static <T> Set<T> emptyIfNull(Set<T> set) {
        return set == null ? Collections.emptySet() : set;
    }

    /**
     * 返回 Map 本身，如果为 null 则返回空 Map
     *
     * @param map 源 Map
     * @param <K> key 类型
     * @param <V> value 类型
     * @return 非 null 的 Map
     */
    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    // ==================== 遍历 ====================

    /**
     * 带索引的 forEach 遍历
     *
     * <pre>{@code
     * CollectionUtil.forEachIndexed(list, (index, item) -> {
     *     System.out.println(index + ": " + item);
     * });
     * }</pre>
     *
     * @param collection 源集合
     * @param action     接收索引和元素的 BiConsumer
     * @param <T>        元素类型
     */
    public static <T> void forEachIndexed(Collection<T> collection, BiConsumer<Integer, T> action) {
        if (isEmpty(collection)) {
            return;
        }
        int index = 0;
        for (T item : collection) {
            action.accept(index++, item);
        }
    }

    // ==================== Iterable 支持 ====================

    /**
     * 将 Iterable 转为 List
     *
     * @param iterable 可迭代对象
     * @param <T>      元素类型
     * @return List
     */
    public static <T> List<T> toList(Iterable<T> iterable) {
        if (iterable == null) {
            return new ArrayList<>();
        }
        if (iterable instanceof List) {
            return (List<T>) iterable;
        }
        if (iterable instanceof Collection) {
            return new ArrayList<>((Collection<T>) iterable);
        }
        List<T> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }

    /**
     * 将 Iterator 转为 List
     *
     * @param iterator 迭代器
     * @param <T>      元素类型
     * @return List
     */
    public static <T> List<T> toList(Iterator<T> iterator) {
        if (iterator == null) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>();
        iterator.forEachRemaining(result::add);
        return result;
    }

    /**
     * 将 Enumeration 转为 List
     *
     * @param enumeration 枚举
     * @param <T>         元素类型
     * @return List
     */
    public static <T> List<T> toList(Enumeration<T> enumeration) {
        if (enumeration == null) {
            return new ArrayList<>();
        }
        return Collections.list(enumeration);
    }

    // ==================== 集合大小 ====================

    /**
     * 安全获取集合大小
     *
     * @param collection 集合
     * @return 大小，null 返回 0
     */
    public static int size(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    /**
     * 安全获取 Map 大小
     *
     * @param map Map
     * @return 大小，null 返回 0
     */
    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    /**
     * 安全获取数组大小
     *
     * @param array 数组
     * @param <T>   元素类型
     * @return 大小，null 返回 0
     */
    public static <T> int size(T[] array) {
        return array == null ? 0 : array.length;
    }

    // ==================== 包含判断 ====================

    /**
     * 判断集合是否包含指定元素（null 安全）
     *
     * @param collection 源集合
     * @param element    要查找的元素
     * @param <T>        元素类型
     * @return true 表示包含
     */
    public static <T> boolean contains(Collection<T> collection, T element) {
        if (isEmpty(collection)) {
            return false;
        }
        return collection.contains(element);
    }

    /**
     * 判断集合是否包含任意一个指定元素
     *
     * @param collection 源集合
     * @param elements   要查找的元素
     * @param <T>        元素类型
     * @return true 表示包含至少一个
     */
    @SafeVarargs
    public static <T> boolean containsAny(Collection<T> collection, T... elements) {
        if (isEmpty(collection) || isEmpty(elements)) {
            return false;
        }
        Set<T> set = new HashSet<>(collection);
        for (T element : elements) {
            if (set.contains(element)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断集合是否包含所有指定元素
     *
     * @param collection 源集合
     * @param elements   要查找的元素
     * @param <T>        元素类型
     * @return true 表示全部包含
     */
    @SafeVarargs
    public static <T> boolean containsAll(Collection<T> collection, T... elements) {
        if (isEmpty(elements)) {
            return true;
        }
        if (isEmpty(collection)) {
            return false;
        }
        Set<T> set = new HashSet<>(collection);
        for (T element : elements) {
            if (!set.contains(element)) {
                return false;
            }
        }
        return true;
    }

    // ==================== Zip ====================

    /**
     * 将两个集合按索引配对，生成新的 List
     *
     * <pre>{@code
     * List<String> keys = Arrays.asList("a", "b", "c");
     * List<Integer> values = Arrays.asList(1, 2, 3);
     * Map<String, Integer> map = CollectionUtil.zip(keys, values)
     *     .stream()
     *     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
     * }</pre>
     *
     * @param c1  集合 1
     * @param c2  集合 2
     * @param <A> 集合 1 元素类型
     * @param <B> 集合 2 元素类型
     * @return 配对后的 Entry List，长度取两个集合的较小值
     */
    public static <A, B> List<Map.Entry<A, B>> zip(Collection<A> c1, Collection<B> c2) {
        if (isEmpty(c1) || isEmpty(c2)) {
            return new ArrayList<>();
        }
        Iterator<A> iter1 = c1.iterator();
        Iterator<B> iter2 = c2.iterator();
        List<Map.Entry<A, B>> result = new ArrayList<>(Math.min(c1.size(), c2.size()));
        while (iter1.hasNext() && iter2.hasNext()) {
            result.add(new AbstractMap.SimpleEntry<>(iter1.next(), iter2.next()));
        }
        return result;
    }
}
