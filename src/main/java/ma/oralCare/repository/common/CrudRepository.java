package ma.oralCare.repository.common;

import java.util.List;

public interface CrudRepository<T, ID> {

    T save(T entity);
    T findById(ID id);
    List<T> findAll();
    void update(T newValuesElement);
    void delete(T element);
    void deleteById(ID id);
    boolean existsById(ID id);
    long count();
    void deleteAll();

}
