package ma.oralCare.service.common;

import ma.oralCare.common.consoleLog.ConsoleLogger;
import ma.oralCare.conf.SessionFactory;

import java.sql.Connection;

public final class Transaction {


    private Transaction(){}


    @FunctionalInterface
    public interface TransactionBlocExecuter<T> {

        T run(Connection c) throws Exception;
    }

    public static <T> T initTransaction(TransactionBlocExecuter<T> blocTransactionnelAExecuter) {

        try (Connection connection = SessionFactory.getInstance().getConnection()) {

            ConsoleLogger.info(" ouverture de la connexion JDBC " + connection.getMetaData().getURL());
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                T result = blocTransactionnelAExecuter.run(connection);
                connection.commit();
                return result;
            }
            catch (Exception ex) {
                connection.rollback();
                throw new RuntimeException(ex);
            }
            finally {
                connection.setAutoCommit(oldAutoCommit);

                if(connection.isClosed()){

                    ConsoleLogger.info("Closing JDBC connection " + connection.getMetaData().getURL());
                }
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
