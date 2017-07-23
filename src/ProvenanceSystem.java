import java.util.concurrent.atomic.AtomicReference;

public class ProvenanceSystem {
    private static AtomicReference<ProvenanceSystem> _instance = new AtomicReference<>(new ProvenanceSystem());

    /**
     * System details. Define dbms and system/data database connection strings.
     */
    private static final String SQL_CLASS = "org.sqlite.JDBC";
    public static final String SYSTEM_DB_CONNECTION_STRING = "jdbc:sqlite:system.db";
    public static final String DATA_DB_CONNECTON_STRING = "jdbc:sqlite:data.db";

    private final DbManager _dbmanager;
    private final ProvenanceManager _provenancemanager;

    private ProvenanceSystem() {
        this._dbmanager = new DbManager(ProvenanceSystem.SQL_CLASS,
                ProvenanceSystem.SYSTEM_DB_CONNECTION_STRING,
                ProvenanceSystem.DATA_DB_CONNECTON_STRING);
        this._provenancemanager = new ProvenanceManager();
    }

    public static void initialize() {
        _instance.get()._dbmanager.initialize();
        _instance.get()._provenancemanager.initialize();
    }

    public static DbManager getDbManager() {
        return _instance.get()._dbmanager;
    }

    public static ProvenanceManager getProvenanceManager() {
        return _instance.get()._provenancemanager;
    }
}
