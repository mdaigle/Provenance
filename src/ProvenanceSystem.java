import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by mdaigle on 5/10/17.
 */
public class ProvenanceSystem {
    private static AtomicReference<ProvenanceSystem> _instance = new AtomicReference<>(new ProvenanceSystem());

    /**
     * System details. Define dbms and system/data database connection strings.
     */
    private static final String SQL_CLASS = "org.sqlite.JDBC";
    public static final String SYSTEM_DB_CONNECTION_STRING = "jdbc:sqlite:system.db";
    public static final String DATA_DB_CONNECTON_STRING = "jdbc:sqlite:data.db";

    private final ProvenanceManager _provenancemanager;
    private final DbManager _dbmanager;

    private ProvenanceSystem() {
        this._provenancemanager = new ProvenanceManager();
        this._dbmanager = new DbManager(ProvenanceSystem.SQL_CLASS,
                ProvenanceSystem.SYSTEM_DB_CONNECTION_STRING,
                ProvenanceSystem.DATA_DB_CONNECTON_STRING);
    }

    public static void initialize() {
        _instance.get()._provenancemanager.initialize();
        _instance.get()._dbmanager.initialize();
    }

    public static ProvenanceManager getProvenanceManager() {
        return _instance.get()._provenancemanager;
    }

    public static DbManager getDbManager() {
        return _instance.get()._dbmanager;
    }
}
