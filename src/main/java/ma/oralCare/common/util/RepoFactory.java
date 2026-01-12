package ma.oralCare.common.util;

import java.sql.Connection;

@FunctionalInterface
public interface RepoFactory<T> {
    T create(Connection c);
}
