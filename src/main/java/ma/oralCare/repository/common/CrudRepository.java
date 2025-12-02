package ma.oralCare.repository.common;

import java.util.List;

public interface CrudRepository<T, ID> {

    T save(T entity);

    T update(T entity);

    T findById(ID id);

    List<T> findAll();

    boolean deleteById(ID id);
}
