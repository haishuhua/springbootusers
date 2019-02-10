package com.techprimers.db.dao;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.techprimers.db.dto.HistoryDto;
import com.techprimers.db.dto.ListItemDto;
import org.hibernate.Session;

public interface BaseDao <T, PK extends Serializable> {

    /**
     * Persist a new object into database .
     *
     * @param  entity New object to persist
     * @return        Returns the Primary Key of the newly created object
     *
     **/
    PK create(T entity);


    /**
     * Persist multiple new objects into database .
     *
     * @param  entityList - List of new objects to persist
     * @return Returns a list of the Primary Keys of the newly created objects
     *
     **/
    List<PK> createBatch(List<T> entityList);


    /**
     * Retrieve an existing object from the database via Primary Key.
     *
     * @param  id  - Primary Key of the Object we want to find
     * @return Returns Object
     *
     **/
    T read(PK id);


    List<T> readBatch(List<PK> idList);

    /**
     * Retrieve all records from DB table.
     *
     * @return Returns List of Objects
     *
     **/
    List<T> readAll();

    List<T> customRead(List<ListItemDto> filterList, List<ListItemDto> sortList, Integer pageSize, Integer pageNum);


    /**
     * Save changes made to an object.
     *
     * @param  entity - Object to update
     *
     **/
    void update(T entity);


    /**
     * Save changes made to a list of objects.
     *
     * @param entityList - List of objects to update
     *
     * */
    void updateBatch(List<T> entityList);


    /**
     * Save changes made to an object.
     *
     * @param  entity - Object to update
     *
     **/
    void saveOrUpdate(T entity);


    /**
     * Save changes made to a list of objects.
     *
     * @param entityList - List of objects to update
     *
     * */
    void saveOrUpdateBatch(List<T> entityList);


    /**
     * Remove an object from the database.
     *
     * @param entity - Object to remove
     *
     **/
    void delete(T entity);


    /**
     * Removes a list of objects from the database.
     *
     * @param entityList - List of objects to remove
     *
     **/
    void deleteBatch(List<T> entityList);


    /**
     * Remove an object from the database via the object's Primary Key.
     *
     * @param id - Primary Key of the object to remove
     *
     */
    void deleteByPk(PK id);


    /**
     *  Remove a list of objects from database via each object's Primary Keys.
     *
     * @param idList - List of Primary Keys of the objects to remove
     *
     **/
    void deleteByPkBatch(List<PK> idList);

    int deleteWithFilters(Map<String, Object> filterMap);

    /**
     * Returns a Session object.
     *
     * @return Returns a Session from the Session factory
     *
     **/
    Session getCurrentSession();

    Class<T> getClassType();

    void bulkInsert(List<T> entityList);

    List<Long> bulkInsertWithIds(List<T> entityList);

    void executeQueryBatch(List<String> queries);

}
