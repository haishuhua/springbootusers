package com.techprimers.db.dao;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.sql.DataSource;

import com.techprimers.db.constants.GlobalConstants;
import com.techprimers.db.dto.ListItemDto;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import util.ClassUtil;

@Repository("baseDao")
@Scope("prototype")
public class BaseDaoImpl<T, PK extends Serializable> implements BaseDao<T, PK> {

    private static final Logger log = Logger.getLogger(BaseDaoImpl.class);

    private static final String CRUD_U = "UPDATE";
    private static final String CRUD_D = "DELETE";

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private DataSource dataSource;

    private boolean enableHistory = false;

    private Class<T> classType;
    private ClassUtil classUtil;

    public BaseDaoImpl() {
    }

    public BaseDaoImpl(Class<T> classType) {
        this.classType = classType;
        classUtil = new ClassUtil(classType);
    }

    public BaseDaoImpl(Class<T> classType, boolean enableHistory) {
        this.enableHistory = enableHistory;
        this.classType = classType;
        classUtil = new ClassUtil(classType);
    }


    /**
     * @Inheritdoc
     */
    @SuppressWarnings("unchecked")
    public PK create(T entity) {
        log.debug("create()");
        return (PK) getCurrentSession().save(entity);

    }

    /**
     * @Inheritdoc
     */
    @SuppressWarnings("unchecked")
    public List<PK> createBatch(List<T> entityList) {
        log.debug("createBatch()");
        List<PK> result = new ArrayList<>();
        for (int index = 0; index < entityList.size(); index++)
            result.add((PK) getCurrentSession().save(entityList.get(index)));
        return result;
    }

    /**
     * @Inheritdoc
     */
    @SuppressWarnings("unchecked")
    public T read(PK id) {
        log.debug("read()");
        return (T) getCurrentSession().get(classType, id);
    }

    /**
     * @Inheritdoc
     */
    public List<T> readBatch(List<PK> idList) {
        log.debug("readBatch()");
        List<T> entityList = new ArrayList<>();
        for (int index = 0; index < idList.size(); index++)
            entityList.add(read(idList.get(index)));
        return entityList;
    }


    /**
     * @Inheritdoc
     */
    @SuppressWarnings("unchecked")
    public List<T> readAll() {
        log.debug("readAll()");
        return (List<T>) getCurrentSession().createCriteria(classType).list();
    }


    @SuppressWarnings("unchecked")
    public List<T> customRead(List<ListItemDto> filterList, List<ListItemDto> sortList, Integer pageSize, Integer pageNum) {
        log.debug("customRead()");
        Criteria criteria = getCurrentSession().createCriteria(classType);

        if (filterList != null && filterList.size() > 0) {
            String dataType;
            String fieldName;
            Object fieldValue;

            for (int i = 0; i < filterList.size(); i++) {
                fieldName = filterList.get(i).getFieldName();
                fieldValue = filterList.get(i).getFieldValue();
                dataType = classUtil.getFieldDataType(fieldName);
                if (dataType != null && fieldName != null && fieldName.length() > 0) {
                    if (dataType.equalsIgnoreCase("java.lang.Integer"))
                        criteria.add(Restrictions.eq(fieldName, (Integer) fieldValue));
                    else if (dataType.equalsIgnoreCase("java.lang.BigInteger"))
                        criteria.add(Restrictions.eq(fieldName, (BigInteger) fieldValue));
                    else if (dataType.equalsIgnoreCase("java.lang.Double"))
                        criteria.add(Restrictions.eq(fieldName, (Double) fieldValue));
                    else if (dataType.equalsIgnoreCase("java.util.Date"))
                        criteria.add(Restrictions.eq(fieldName, (Date) fieldValue));
                    else if (dataType.equalsIgnoreCase("java.lang.Boolean"))
                        criteria.add(Restrictions.eq(fieldName, (Boolean) fieldValue));
                    else
                        criteria.add(Restrictions.eq(fieldName, (String) fieldValue));
                }
            }
        }


        if (sortList != null && sortList.size() > 0) {
            String fieldName;
            String sortOrder;
            for (int i = 0; i < sortList.size(); i++) {
                fieldName = sortList.get(i).getFieldName();
                sortOrder = (String) sortList.get(i).getFieldValue();
                if (classUtil.hasField(fieldName) && fieldName != null && fieldName.length() > 0) {
                    if (sortOrder.equalsIgnoreCase(GlobalConstants.SQL_SORT_DESC))
                        criteria.addOrder(Order.desc(fieldName));
                    else
                        criteria.addOrder(Order.asc(fieldName));
                }
            }
        }

        return (List<T>) criteria.list();

    }


    /**
     * @Inheritdoc
     */
    public void update(T entity) {
        log.debug("update()");
        getCurrentSession().update(entity);
    }

    /**
     * @Inheritdoc
     */
    public void updateBatch(List<T> entityList) {
        log.debug("updateBatch()");
        for (int index = 0; index < entityList.size(); index++) {
            getCurrentSession().update(entityList.get(index));
        }
    }

    /**
     * @Inheritdoc
     */
    public void saveOrUpdate(T entity) {
        log.debug("saveOrUpdate()");
        getCurrentSession().saveOrUpdate(getCurrentSession().merge(entity));
    }

    /**
     * @Inheritdoc
     */
    public void saveOrUpdateBatch(List<T> entityList) {
        log.debug("saveOrUpdateBatch()");
        for (int index = 0; index < entityList.size(); index++) {
            saveOrUpdate(entityList.get(index));
        }
    }


    /**
     * @Inheritdoc
     */
    public void delete(T entity) {
        log.debug("delete()");
        getCurrentSession().delete(entity);
    }

    /**
     * @Inheritdoc
     */
    public void deleteBatch(List<T> entityList) {
        log.debug("deleteBatch()");
        for (int index = 0; index < entityList.size(); index++) {
            getCurrentSession().delete(entityList.get(index));
        }
    }

    /**
     * @Inheritdoc
     */
    public void deleteByPk(PK id) {
        log.debug("deleteByPk()");
        T entity = read(id);
        if (entity != null)
            getCurrentSession().delete(entity);


    }

    /**
     * @Inheritdoc
     */
    public void deleteByPkBatch(List<PK> idList) {
        log.debug("deleteByPkBatch()");
        for (int index = 0; index < idList.size(); index++) {
            getCurrentSession().delete(sessionFactory.getCurrentSession().get(classType, idList.get(index)));
        }
    }

    public int deleteWithFilters(Map<String, Object> filterMap) {

        log.debug("deleteWithFilters()");

        if (filterMap == null)
            return 0;

        StringBuilder queryString = new StringBuilder("DELETE FROM " + classType.getName() + " WHERE 1 = 1");

        for (Map.Entry<String, Object> entry : filterMap.entrySet())
            queryString.append("  AND " + entry.getKey() + " = :" + entry.getKey());

        Query query = getCurrentSession().createQuery(queryString.toString());

        for (Map.Entry<String, Object> entry : filterMap.entrySet())
            query.setParameter(entry.getKey(), entry.getValue());

        return query.executeUpdate();

    }


    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public Class<T> getClassType() {
        return this.classType;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void bulkInsert(List<T> entityList) {

        if (entityList == null || entityList.isEmpty()) {
            return;
        }

        DataSource dataSource = getDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        String sqlTable = classType.getAnnotation(Table.class).name();
        BeanInfo info = null;

        try {
            info = Introspector.getBeanInfo(classType, Object.class);
        } catch (IntrospectionException e) {
            log.error("Failed to get Bean information from classType " + classType + " sqlTable: " + sqlTable, e);
        }

        final PropertyDescriptor[] props = info.getPropertyDescriptors();

        StringBuilder queryString = new StringBuilder("INSERT INTO " + sqlTable + " ( ");

        Integer valueCounter = 0;
        Map<String, Integer> fieldsToInsert = new HashMap<String, Integer>();

        Annotation columnAnnotation, transientAnnotation, autoIncrementAnnotation;
        for (Field field : classType.getDeclaredFields()) {

            transientAnnotation = field.getAnnotation(Transient.class);
            autoIncrementAnnotation = field.getAnnotation(GeneratedValue.class);

            if (!"serialVersionUID".equalsIgnoreCase(field.getName()) &&
                    transientAnnotation == null &&
                    autoIncrementAnnotation == null) {

                columnAnnotation = field.getAnnotation(Column.class);

                if (valueCounter > 0)
                    queryString.append(" , ");

                if (columnAnnotation != null)
                    queryString.append(field.getAnnotation(Column.class).name());
                else
                    queryString.append(field.getName());

                fieldsToInsert.put(field.getName(), ++valueCounter);
            }

        }

        queryString.append(" ) VALUES ( ");

        for (int i = 0; i < valueCounter; i++) {
            if (i == 0)
                queryString.append("?");
            else
                queryString.append(" , ?");
        }

        queryString.append(" ) ");

        log.info("Number of Entities: " + entityList.size() + " | Insert Signature: " + queryString.toString());
        jdbcTemplate.batchUpdate(queryString.toString(), new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                T entity = entityList.get(i);
                if (props != null) {
                    for (PropertyDescriptor pd : props) {
                        if (fieldsToInsert.containsKey(pd.getName())) {
                            Object object = null;
                            try {

                                object = pd.getReadMethod().invoke(entity);
                                if (object instanceof Integer)
                                    ps.setInt(fieldsToInsert.get(pd.getName()), (Integer) object);
                                else if (object instanceof BigInteger)
                                    ps.setObject(fieldsToInsert.get(pd.getName()), object, Types.BIGINT);
                                else if (object instanceof BigDecimal)
                                    ps.setBigDecimal(fieldsToInsert.get(pd.getName()), (BigDecimal) object);
                                else if (object instanceof String)
                                    ps.setString(fieldsToInsert.get(pd.getName()), (String) object);
                                else if (object instanceof Date)
                                    ps.setTimestamp(fieldsToInsert.get(pd.getName()), new Timestamp(((Date) object).getTime()));
                                else if (object instanceof Boolean)
                                    ps.setBoolean(fieldsToInsert.get(pd.getName()), (Boolean) object);
                                else if (object instanceof UUID)
                                    ps.setObject(fieldsToInsert.get(pd.getName()), ((UUID) object));
                                else
                                    ps.setObject(fieldsToInsert.get(pd.getName()), object);
                            } catch (Exception e) {
                                ps.setObject(fieldsToInsert.get(pd.getName()), object);
                            }
                        }
                    }
                }
            }

            public int getBatchSize() {
                return entityList.size();
            }

        });

    }


    public List<Long> bulkInsertWithIds(List<T> entityList) {

        DataSource dataSource = getDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder holder = new GeneratedKeyHolder();
        List<Long> autoGeneratedIds = new ArrayList<>();

        String sqlTable = classType.getAnnotation(Table.class).name();
        BeanInfo info = null;

        try {
            info = Introspector.getBeanInfo(classType, Object.class);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }

        final PropertyDescriptor[] props = info.getPropertyDescriptors();

        StringBuilder queryString = new StringBuilder("INSERT INTO " + sqlTable + " ( ");

        Integer valueCounter = 0;
        Map<String, Integer> fieldsToInsert = new HashMap<String, Integer>();

        Annotation columnAnnotation, transientAnnotation, autoIncrementAnnotation;
        for (Field field : classType.getDeclaredFields()) {

            transientAnnotation = field.getAnnotation(Transient.class);
            autoIncrementAnnotation = field.getAnnotation(GeneratedValue.class);

            if (!"serialVersionUID".equalsIgnoreCase(field.getName()) &&
                    transientAnnotation == null &&
                    autoIncrementAnnotation == null) {

                columnAnnotation = field.getAnnotation(Column.class);

                if (valueCounter > 0)
                    queryString.append(" , ");

                if (columnAnnotation != null)
                    queryString.append(field.getAnnotation(Column.class).name());
                else
                    queryString.append(field.getName());

                fieldsToInsert.put(field.getName(), ++valueCounter);
            }

        }

        queryString.append(" ) VALUES ( ");

        for (int i = 0; i < valueCounter; i++) {
            if (i == 0)
                queryString.append("?");
            else
                queryString.append(" , ?");
        }

        queryString.append(" ) ");

        log.info("Number of Entities: " + entityList.size() + " | Insert Signature: " + queryString.toString());

        if (props != null) {
            Integer count = 1;
            for (T entity : entityList) {

                count++;
                if (count % 500 == 0)
                    log.info(count + " of " + entityList.size());

                jdbcTemplate.update(new PreparedStatementCreator() {

                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(queryString.toString(), Statement.RETURN_GENERATED_KEYS);
                        for (PropertyDescriptor pd : props) {
                            if (fieldsToInsert.containsKey(pd.getName())) {
                                Object object = null;
                                try {
                                    object = pd.getReadMethod().invoke(entity);
                                    if (object instanceof Integer)
                                        ps.setInt(fieldsToInsert.get(pd.getName()), (Integer) object);
                                    else if (object instanceof BigInteger)
                                        ps.setObject(fieldsToInsert.get(pd.getName()), object, Types.BIGINT);
                                    else if (object instanceof BigDecimal)
                                        ps.setBigDecimal(fieldsToInsert.get(pd.getName()), (BigDecimal) object);
                                    else if (object instanceof String)
                                        ps.setString(fieldsToInsert.get(pd.getName()), (String) object);
                                    else if (object instanceof Date)
                                        ps.setTimestamp(fieldsToInsert.get(pd.getName()), new Timestamp(((Date) object).getTime()));
                                    else if (object instanceof Boolean)
                                        ps.setBoolean(fieldsToInsert.get(pd.getName()), (Boolean) object);
                                    else if (object instanceof UUID)
                                        ps.setObject(fieldsToInsert.get(pd.getName()), ((UUID) object));
                                    else
                                        ps.setObject(fieldsToInsert.get(pd.getName()), object);
                                } catch (Exception e) {
                                    ps.setObject(fieldsToInsert.get(pd.getName()), object);
                                }
                            }
                        }

                        return ps;
                    }
                }, holder);

                autoGeneratedIds.add(holder.getKey().longValue());
            }
        }

        return autoGeneratedIds;

    }

    public void executeQueryBatch(List<String> queries) {
        Work work = new Work() {

            boolean toUpdate = false;

            @Override
            public void execute(Connection connection) throws SQLException {
                Statement statement = connection.createStatement();

                queries.stream().filter(Objects::nonNull).forEach(q -> {
                    try {
                        log.info("Appending query " + q);
                        toUpdate = true;
                        statement.addBatch(q);
                    } catch (Exception e) {
                    }
                });
                if (toUpdate) {
                    statement.executeBatch();
                }
            }
        };
        sessionFactory.getCurrentSession().doWork(work);
    }

    public DataSource getDataSource() {
        return dataSource;
    }


    // HELPER METHODS

    public Boolean checkIfExistsById(PK id) {
        return this.checkIfExistsById(id, false);
    }

    public Boolean checkIfExistsById(PK id, Boolean checkIsActiveFlag) {
        return this.checkIfExistsById(id, checkIsActiveFlag, null);
    }

    public Boolean checkIfExistsById(PK id, Boolean checkIsActiveFlag, Integer companyId) {

        Criteria criteria = getCurrentSession()
                .createCriteria(classType)
                .setProjection(Projections.projectionList().add(Projections.property(GlobalConstants.DB_COLUMN_ID)))
                .add(Restrictions.eq(GlobalConstants.DB_COLUMN_ID, id));

        return ((Object) criteria.uniqueResult()) != null;

    }

    public Boolean checkIfActiveById(PK id) {

        Criteria criteria = getCurrentSession()
                .createCriteria(classType)
                .setProjection(Projections.projectionList().add(Projections.property(GlobalConstants.DB_COLUMN_ID)))
                .add(Restrictions.eq(GlobalConstants.DB_COLUMN_ID, id));

        return ((Object) criteria.uniqueResult()) != null;

    }

    public PK getIdFromField(String fieldName, Object fieldValue) {
        return this.getIdFromField(fieldName, fieldValue, false, true);
    }

    public PK getIdFromField(String fieldName, Object fieldValue, Boolean checkIsActiveFlag, Boolean checkUnique) {
        return this.getIdFromField(fieldName, fieldValue, checkIsActiveFlag, checkUnique, null);
    }

    @SuppressWarnings("unchecked")
    public PK getIdFromField(String fieldName, Object fieldValue, Boolean checkIsActiveFlag, Boolean checkUnique, Integer companyId) {

        Criteria criteria = getCurrentSession()
                .createCriteria(classType)
                .setProjection(Projections.projectionList().add(Projections.property(GlobalConstants.DB_COLUMN_ID)))
                .add(Restrictions.eq(fieldName, fieldValue));

        List<PK> idList = (List<PK>) criteria.setMaxResults(2).list();

        return (idList.isEmpty() || (checkUnique && idList.size() != 1)) ? null : (PK) idList.get(0);
    }


    public List<PK> getIdListFromField(String fieldName, Object fieldValue) {
        return this.getIdListFromField(fieldName, fieldValue, false);
    }

    public List<PK> getIdListFromField(String fieldName, Object fieldValue, Boolean checkIsActiveFlag) {
        return this.getIdListFromField(fieldName, fieldValue, checkIsActiveFlag, null);
    }

    @SuppressWarnings("unchecked")
    public List<PK> getIdListFromField(String fieldName, Object fieldValue, Boolean checkIsActiveFlag, Integer companyId) {

        Criteria criteria = getCurrentSession()
                .createCriteria(classType)
                .setProjection(Projections.projectionList().add(Projections.property(GlobalConstants.DB_COLUMN_ID)))
                .add(Restrictions.eq(fieldName, fieldValue));

        return (List<PK>) criteria.list();

    }

    public T getEntityFromField(String fieldName, Object fieldValue) {
        return this.getEntityFromField(fieldName, fieldValue, false, true);
    }

    public T getEntityFromField(String fieldName, Object fieldValue, Boolean checkIsActiveFlag, Boolean checkUnique) {
        return this.getEntityFromField(fieldName, fieldValue, checkIsActiveFlag, checkUnique, null);
    }

    @SuppressWarnings("unchecked")
    public T getEntityFromField(String fieldName, Object fieldValue, Boolean checkIsActiveFlag, Boolean checkUnique, Integer companyId) {

        Criteria criteria = getCurrentSession()
                .createCriteria(classType)
                .add(Restrictions.eq(fieldName, fieldValue));

        List<T> list = (List<T>) criteria.setMaxResults(2).list();

        return (list.isEmpty() || (checkUnique && list.size() != 1)) ? null : (T) list.get(0);

    }

    public List<T> getEntityListFromField(String fieldName, Object fieldValue) {
        return this.getEntityListFromField(fieldName, fieldValue, false);
    }

    public List<T> getEntityListFromField(String fieldName, Object fieldValue, Boolean checkIsActiveFlag) {
        return this.getEntityListFromField(fieldName, fieldValue, checkIsActiveFlag, null);
    }

    @SuppressWarnings("unchecked")
    public List<T> getEntityListFromField(String fieldName, Object fieldValue, Boolean checkIsActiveFlag, Integer companyId) {

        Criteria criteria = getCurrentSession()
                .createCriteria(classType)
                .add(Restrictions.eq(fieldName, fieldValue));

        return (List<T>) criteria.list();

    }


    @SuppressWarnings("unchecked")
    public List<T> getEntityList(Boolean checkIsActiveFlag, Integer companyId) {

        Criteria criteria = getCurrentSession().createCriteria(classType);


        return (List<T>) criteria.list();

    }

    @SuppressWarnings("unchecked")
    public List<T> getEntityListByIdList(List<PK> idList) {
        return (List<T>) getCurrentSession()
                .createCriteria(classType)
                .add(Restrictions.in(GlobalConstants.DB_COLUMN_ID, idList))
                .list();
    }

    public Object getFieldById(PK id, String fieldName) {
        return this.getFieldById(id, fieldName, false);
    }

    public Object getFieldById(PK id, String fieldName, Boolean checkIsActiveFlag) {
        return this.getFieldById(id, fieldName, checkIsActiveFlag, null);
    }

    public Object getFieldById(PK id, String fieldName, Boolean checkIsActiveFlag, Integer companyId) {

        Criteria criteria = getCurrentSession()
                .createCriteria(classType)
                .setProjection(Projections.projectionList().add(Projections.property(fieldName)))
                .add(Restrictions.eq(GlobalConstants.DB_COLUMN_ID, id));

        return (Object) criteria.uniqueResult();
    }

    public Map<PK, String> getIdToFieldMap(String fieldName) {
        return this.getIdToFieldMap(null, fieldName, false);
    }

    public Map<PK, String> getIdToFieldMap(List<PK> idList, String fieldName) {
        return this.getIdToFieldMap(idList, fieldName, false);
    }

    public Map<PK, String> getIdToFieldMap(List<PK> idList, String fieldName, Boolean checkIsActiveFlag) {
        return this.getIdToFieldMap(idList, fieldName, checkIsActiveFlag, null);
    }

    @SuppressWarnings("unchecked")
    public Map<PK, String> getIdToFieldMap(List<PK> idList, String fieldName, Boolean checkIsActiveFlag, Integer companyId) {

        if (fieldName == null)
            return null;

        Criteria criteria = getCurrentSession()
                .createCriteria(classType)
                .setProjection(Projections.projectionList()
                        .add(Projections.property(GlobalConstants.DB_COLUMN_ID))
                        .add(Projections.property(fieldName))
                );

        if (idList != null && !idList.isEmpty())
            criteria.add(Restrictions.in(GlobalConstants.DB_COLUMN_ID, idList));


        List<Object[]> list = (List<Object[]>) criteria.list();

        Map<PK, String> map = new HashMap<>();
        for (Object[] objArray : list)
            map.put(((PK) objArray[0]), (String) objArray[1]);

        return map;

    }

    public Map<String, PK> getFieldToIdMap(List<PK> idList, String fieldName) {
        return this.getFieldToIdMap(idList, fieldName, false);
    }

    public Map<String, PK> getFieldToIdMap(List<PK> idList, String fieldName, Boolean checkIsActiveFlag) {
        return this.getFieldToIdMap(idList, fieldName, checkIsActiveFlag, null);
    }

    @SuppressWarnings("unchecked")
    public Map<String, PK> getFieldToIdMap(List<PK> idList, String fieldName, Boolean checkIsActiveFlag, Integer companyId) {

        if (fieldName == null)
            return null;

        Criteria criteria = getCurrentSession()
                .createCriteria(classType)
                .setProjection(Projections.projectionList()
                        .add(Projections.property(GlobalConstants.DB_COLUMN_ID))
                        .add(Projections.property(fieldName))
                );

        if (idList != null && !idList.isEmpty())
            criteria.add(Restrictions.in(GlobalConstants.DB_COLUMN_ID, idList));

        List<Object[]> list = (List<Object[]>) criteria.list();

        Map<String, PK> map = new HashMap<>();
        for (Object[] objArray : list)
            map.put(((String) objArray[1]).toLowerCase(), (PK) objArray[0]);

        return map;

    }


    public Map<PK, T> getIdToEntityMap(List<PK> idList) {
        return this.getIdToEntityMap(idList, null);
    }

    @SuppressWarnings("unchecked")
    public Map<PK, T> getIdToEntityMap(List<PK> idList, Integer companyId) {

        if (idList == null || idList.isEmpty())
            return null;

        Criteria criteria = getCurrentSession()
                .createCriteria(classType)
                .add(Restrictions.in(GlobalConstants.DB_COLUMN_ID, idList));
        List<T> list = (List<T>) criteria.list();

        Map<PK, T> map = new HashMap<>();
        Method getIdMethod;

        try {
            getIdMethod = classType.getDeclaredMethod("getId");
            for (T entity : list)
                map.put((PK) getIdMethod.invoke(entity), entity);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        return map;

    }
}