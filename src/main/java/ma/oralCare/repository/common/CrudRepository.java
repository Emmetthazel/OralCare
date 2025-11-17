package ma.oralCare.repository.common;

import java.util.List;

public interface CrudRepository<T, ID> {

    // CREATE
    void create(T newElement);            // INSERT

    // READ
    T findById(ID id);
    List<T> findAll();

    // UPDATE
    void update(T newValuesElement);

    // DELETE
    void delete(T element);
    void deleteById(ID id);
}
