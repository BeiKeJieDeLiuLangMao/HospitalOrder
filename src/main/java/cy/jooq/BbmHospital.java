/*
 * This file is generated by jOOQ.
 */
package cy.jooq;


import cy.jooq.tables.UserInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BbmHospital extends SchemaImpl {

    private static final long serialVersionUID = 1078334653;

    /**
     * The reference instance of <code>bbm_hospital</code>
     */
    public static final BbmHospital BBM_HOSPITAL = new BbmHospital();

    /**
     * The table <code>bbm_hospital.user_info</code>.
     */
    public final UserInfo USER_INFO = cy.jooq.tables.UserInfo.USER_INFO;

    /**
     * No further instances allowed
     */
    private BbmHospital() {
        super("bbm_hospital", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            UserInfo.USER_INFO);
    }
}
