package ma.oralCare.repository.common;

import java.util.List;
import java.util.Optional;


public interface CrudRepository<T, ID> {


    List<T> findAll();

    Optional<T> findById(ID id);

    void create(T newElement);

    void update(T newValuesElement);

    void delete(T patient);

    void deleteById(ID id);
}



