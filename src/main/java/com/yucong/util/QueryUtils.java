
package com.yucong.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.annotations.QueryHints;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * jap sql鏌ヨ
 *
 * @author meiguiyang
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public final class QueryUtils {
    private QueryUtils() {}

    /**
     * 鍒嗛〉鏌ヨ
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鍙傛暟map
     * @param pageable Pageable
     * @return Page
     */
    public static Page<Object[]> queryForList(EntityManager em, String sql, Map<String, Object> paramMap, Pageable pageable) {
        if (pageable == null) {
            return new PageImpl<>(queryForList(em, sql, paramMap));
        }

        StringBuffer countSql = new StringBuffer("select count(*) from (");
        countSql.append(sql).append(") ca");
        Long total = Long.valueOf(getSingleResult(em, countSql.toString(), paramMap).toString());
        // 澶т簬0
        if (total > 0L) {
            Query query = buildQuery(em, sql, paramMap, pageable);
            return new PageImpl<>(query.getResultList(), pageable, total);
        }
        return new PageImpl<>(new ArrayList<Object[]>(), pageable, total);
    }

    /**
     * sql鏌ヨ
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap map鏉′欢
     * @return List
     */
    public static List<Object[]> queryForList(EntityManager em, String sql, Map<String, Object> paramMap) {
        Query query = buildQuery(em, sql, paramMap, null);
        return query.getResultList();
    }

    /**
     * SQL鍒嗛〉鏌ヨ杩斿洖 Map,key涓烘煡璇㈢殑瀛楁鍒楀悕
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鏉′欢mao
     * @param pageable Pageable
     * @return Page
     */
    public static Page<Map<String, Object>> queryForMap(EntityManager em, String sql, Map<String, Object> paramMap, Pageable pageable) {
        if (pageable == null) {
            return new PageImpl<>(queryForMap(em, sql, paramMap));
        }
        StringBuffer countSql = new StringBuffer("select count(*) from (");
        countSql.append(sql).append(") ca");
        Long total = Long.valueOf(getSingleResult(em, countSql.toString(), paramMap).toString());
        // 澶т簬0
        if (total > 0L) {
            Query query = buildQuery(em, sql, paramMap, pageable);
            query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            return new PageImpl<>(query.getResultList(), pageable, total);
        }
        return new PageImpl<>(new ArrayList<Map<String, Object>>(), pageable, total);
    }

    /**
     * SQL 杩斿洖listmap
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鏉′欢map
     * @return List
     */
    public static List<Map<String, Object>> queryForMap(EntityManager em, String sql, Map<String, Object> paramMap) {
        Query query = buildQuery(em, sql, paramMap, null);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.getResultList();
    }

    /**
     * 鍙互鎸囧畾琛�,鍒楄繑鍥炵被鍨嬪垎椤垫煡璇�
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鏉′欢
     * @param pageable Pageable
     * @param tableResultType 琛ㄥ悕鎸囧畾绫诲瀷杩斿洖
     * @param columnResultType 鍒楁寚瀹氱被鍨嬭繑鍥�
     * @return Page
     */
    public static Page<Map<String, Object>> queryForMapExt(EntityManager em, String sql, Map<String, Object> paramMap, Pageable pageable,
            Map<String, Class<? extends Object>> tableResultType, Map<String, Type> columnResultType) {
        if (pageable == null) {
            return new PageImpl<>(queryForMapExt(em, sql, paramMap, tableResultType, columnResultType));
        }
        StringBuffer countSql = new StringBuffer("select count(*) from (");
        countSql.append(sql).append(") ca");
        Long total = Long.valueOf(getSingleResult(em, countSql.toString(), paramMap).toString());
        // 澶т簬0
        if (total > 0L) {
            Query query = buildQuery(em, sql, paramMap, pageable);
            SQLQuery unwrap = query.unwrap(SQLQuery.class);
            unwrap.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            if (tableResultType != null) {
                for (Entry<String, Class<? extends Object>> entry : tableResultType.entrySet()) {
                    unwrap.addEntity(entry.getKey(), entry.getValue());
                }
            }
            if (columnResultType != null) {
                for (Entry<String, Type> entry : columnResultType.entrySet()) {
                    unwrap.addScalar(entry.getKey(), entry.getValue());
                }
            }
            return new PageImpl<>(query.getResultList(), pageable, total);
        }
        return new PageImpl<>(new ArrayList<Map<String, Object>>(), pageable, total);
    }

    /**
     * 鍙互鎸囧畾琛�,鍒楄繑鍥炵被鍨嬫煡璇�
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鏉′欢
     * @param tableResultType 琛ㄥ悕瀛楀埗瀹氱被鍨嬭繑鍥�
     * @param columnResultType 鎸囧畾绫诲瀷杩斿洖
     * @return List
     */
    public static List<Map<String, Object>> queryForMapExt(EntityManager em, String sql, Map<String, Object> paramMap,
            Map<String, Class<? extends Object>> tableResultType, Map<String, Type> columnResultType) {
        Query query = buildQuery(em, sql, paramMap, null);
        SQLQuery unwrap = query.unwrap(SQLQuery.class);
        unwrap.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        if (tableResultType != null) {
            for (Entry<String, Class<? extends Object>> entry : tableResultType.entrySet()) {
                unwrap.addEntity(entry.getKey(), entry.getValue());
            }
        }
        if (columnResultType != null) {
            for (Entry<String, Type> entry : columnResultType.entrySet()) {
                unwrap.addScalar(entry.getKey(), entry.getValue());
            }
        }
        // 鍒跺畾杩斿洖map绫诲瀷
        return query.getResultList();
    }

    /**
     * 鏌ヨ瀵硅薄鍒嗛〉
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鏉′欢
     * @param pageable Pageable
     * @param t Class
     * @return Page
     */
    public static <T> Page<T> queryForDto(EntityManager em, String sql, Map<String, Object> paramMap, Pageable pageable, Class<T> t) {
        if (pageable == null) {
            return new PageImpl<>(queryForDto(em, sql, paramMap, t));
        }
        StringBuffer countSql = new StringBuffer("select count(*) from (");
        countSql.append(sql).append(") ca");
        Long total = Long.valueOf(getSingleResult(em, countSql.toString(), paramMap).toString());
        // 澶т簬0
        if (total > 0L) {
            Query query = buildQuery(em, sql, paramMap, pageable, t);
            return new PageImpl<>(query.getResultList(), pageable, total);
        }
        return new PageImpl<>(new ArrayList<T>(), pageable, total);
    }

    /**
     * 鏌ヨ瀵硅薄
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鏉′欢
     * @param t Class
     * @return List
     */
    public static <T> List<T> queryForDto(EntityManager em, String sql, Map<String, Object> paramMap, Class<T> t) {
        Query query = buildQuery(em, sql, paramMap, null, t);
        return query.getResultList();
    }

    /**
     * 鏌ヨ鍗曚釜瀵硅薄
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鏉′欢
     * @param t Class
     * @return T
     */
    public static <T> T querySingleForDto(EntityManager em, String sql, Map<String, Object> paramMap, Class<T> t) {
        Query query = buildQuery(em, sql, paramMap, null, t);
        return (T) query.getSingleResult();
    }

    /**
     * 鍒嗛〉鏌ヨ鍗曟潯璁板綍闆嗗悎,濡俵ist绛�
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鍙傛暟
     * @param pageable Pageable
     * @param c Class
     * @return Page
     */
    public static <K> Page<K> queryForObject(EntityManager em, String sql, Map<String, Object> paramMap, Pageable pageable, Class<K> c) {
        if (pageable == null) {
            return new PageImpl<>(queryForObject(em, sql, paramMap, c));
        }
        StringBuffer countSql = new StringBuffer("select count(*) from (");
        countSql.append(sql).append(") ca");
        Long total = Long.valueOf(getSingleResult(em, countSql.toString(), paramMap).toString());
        // 澶т簬0
        if (total > 0L) {
            List<K> list = new ArrayList<>();
            Query query = buildQuery(em, sql, paramMap, pageable);
            List<?> resultList = query.getResultList();
            for (Object obj : resultList) {
                if (obj.getClass().isArray()) {
                    list.add((K) ((Object[]) obj)[0]);
                } else {
                    list.add((K) obj);
                }
            }
            return new PageImpl<>(list, pageable, total);
        }
        return new PageImpl<>(new ArrayList<K>(), pageable, total);
    }

    /**
     * 鏌ヨ鍗曟潯璁板綍,濡俵ist绛�
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鍙傛暟
     * @param c Class
     * @return List
     */
    public static <K> List<K> queryForObject(EntityManager em, String sql, Map<String, Object> paramMap, Class<K> c) {
        List<K> list = new ArrayList<>();
        Query query = buildQuery(em, sql, paramMap, null);
        List<?> resultList = query.getResultList();
        for (Object obj : resultList) {
            if (obj.getClass().isArray()) {
                list.add((K) ((Object[]) obj)[0]);
            } else {
                list.add((K) obj);
            }
        }
        return list;
    }

    private static Query buildQuery(EntityManager em, String sql, Map<String, Object> paramMap, Pageable pageable) {
        Query query = em.createNativeQuery(sql);
        query.setHint(QueryHints.CACHEABLE, true);
        if (paramMap != null) {
            for (Entry<String, Object> entry : paramMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        return query;
    }

    private static Query buildQuery(EntityManager em, String sql, Map<String, Object> paramMap, Pageable pageable, Class<?> c) {
        Entity entity = c.getAnnotation(Entity.class);
        // 鍒ゆ柇鏄惁涓哄疄浣�
        Query query = null;
        if (entity != null) {
            // 瀹炰綋
            query = em.createNativeQuery(sql, c);
        } else {
            query = em.createNativeQuery(sql);
            SQLQuery unwrap = query.unwrap(SQLQuery.class);
            unwrap.setResultTransformer(Transformers.aliasToBean(c));
        }
        query.setHint(QueryHints.CACHEABLE, true);
        if (paramMap != null) {
            for (Entry<String, Object> entry : paramMap.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        if (pageable != null) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        return query;
    }

    /**
     * 鏌ヨ鍗曟潯璁板綍
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鏉′欢map
     * @return Object
     */
    public static Object getSingleResult(EntityManager em, String sql, Map<String, Object> paramMap) {
        Query query = buildQuery(em, sql, paramMap, null);
        return query.getSingleResult();
    }

    /**
     * 鏌ヨ鍗曟潯璁板綍
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鏉′欢map
     * @return K
     */
    public static <K> K getSingleResult(EntityManager em, String sql, Map<String, Object> paramMap, Class<K> c) {
        Object obj = getSingleResult(em, sql, paramMap);
        return obj == null ? null : (K) obj.toString();
    }

    /**
     * sql 杩斿洖map
     * 
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鏉′欢map
     * @return Map
     */
    public static Map<String, Object> querySingleForMap(EntityManager em, String sql, Map<String, Object> paramMap) {
        Query query = buildQuery(em, sql, paramMap, null);
        query.unwrap(SQLQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return (Map<String, Object>) query.getSingleResult();
    }

    /**
     * 鎵цsql
     *
     * @param em EntityManager
     * @param sql sql
     * @param paramMap 鏉′欢
     * @return 杩斿洖褰卞搷鐨勮鏁�
     */
    public static int executeUpdate(EntityManager em, String sql, Map<String, Object> paramMap) {
        Query query = buildQuery(em, sql, paramMap, null);
        return query.executeUpdate();
    }
}
