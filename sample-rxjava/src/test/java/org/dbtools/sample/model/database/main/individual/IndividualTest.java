package org.dbtools.sample.model.database.main.individual;

import org.dbtools.android.domain.config.TestDatabaseConfig;
import org.dbtools.android.domain.database.JdbcSqliteDatabaseWrapper;
import org.dbtools.query.sql.SQLQueryBuilder;
import org.dbtools.sample.model.database.DatabaseManager;
import org.dbtools.sample.model.database.TestMainDatabaseConfig;
import org.dbtools.sample.model.database.main.MainDatabaseManagers;
import org.dbtools.sample.model.database.main.individualdata.IndividualData;
import org.dbtools.sample.model.database.main.individualdata.IndividualDataManager;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class IndividualTest {
    @Before
    public void setUp() throws Exception {
        TestDatabaseConfig databaseConfig = new TestMainDatabaseConfig("java-test-individual.db");
        databaseConfig.deleteAllDatabaseFiles();
        DatabaseManager databaseManager = new DatabaseManager(databaseConfig);
        MainDatabaseManagers.init(databaseManager);
    }

    @Test
    public void testIndividual() throws Exception {
        // === CREATE / INSERT ===
        IndividualManager individualManager = MainDatabaseManagers.getIndividualManager();
        Individual individual = new Individual();
        individual.setFirstName("Jeff");
        individual.setLastName("Campbell");
        individual.setSampleDateTime(new GregorianCalendar(1970,1,1).getTime());
        individual.setBirthDate(new GregorianCalendar(1970,1,2).getTime());
        individual.setLastModified(new GregorianCalendar(1970,1,3).getTime());
        individual.setNumber(1234);
        individual.setPhone("555-555-1234");
        individual.setEmail("test@test.com");
        individual.setAmount1(19.95F);
        individual.setAmount2(1000000000.25D);
        individual.setEnabled(true);

        individualManager.save(individual);

        // test save
        assertEquals(1, individualManager.findCount());

        // read save... verify values
        Individual testIndividual = individualManager.findByRowId(individual.getId());
        if (testIndividual == null) {
            throw new IllegalStateException("Could not find test individual");
        }
        assertEquals("First", individual.getFirstName(), testIndividual.getFirstName());
        assertEquals("Last", individual.getLastName(), testIndividual.getLastName());
        assertEquals("Sample Date", individual.getSampleDateTime().getTime(), testIndividual.getSampleDateTime().getTime());
        assertEquals("Sample Date", individual.getBirthDate().getTime(), testIndividual.getBirthDate().getTime());
        assertEquals("Sample Date", individual.getLastModified().getTime(), testIndividual.getLastModified().getTime());
        assertEquals("Number", individual.getNumber(), testIndividual.getNumber());
        assertEquals("Phone", individual.getPhone(), testIndividual.getPhone());
        assertEquals("Email", individual.getEmail(), testIndividual.getEmail());
        assertEquals("Amount1", individual.getAmount1(), testIndividual.getAmount1());
        assertEquals("Amount2", individual.getAmount2(), testIndividual.getAmount2());
        assertEquals("Enabled", individual.isEnabled(), testIndividual.isEnabled());


        // === UPDATE ===
        individual.setFirstName("Jeffery");
        individualManager.save(individual);

        String dbFirstName = individualManager.findValueBySelection(String.class, IndividualConst.C_FIRST_NAME, IndividualConst.C_ID + " = " + individual.getId(), null, "");
        assertEquals("Jeffery", dbFirstName);

        // === DELETE ===
        individualManager.delete(individual);
        assertEquals(0, individualManager.findCount());
    }

    @Test
    public void testFindBy() throws Exception {
        TestDatabaseConfig databaseConfig = new TestMainDatabaseConfig("java-test-individual.db");
        databaseConfig.deleteAllDatabaseFiles();
        DatabaseManager databaseManager = new DatabaseManager(databaseConfig);
        MainDatabaseManagers.init(databaseManager);
        IndividualManager individualManager = MainDatabaseManagers.getIndividualManager();

        Individual ind1 = addIndividual("Darth", "Vader", 1, 1.1d, true);
        Individual ind2 = addIndividual("Boba", "Fett", 2, 2.2d, false);
        Individual ind3 = addIndividual("Luke", "Skywalker", 3, 3.3d, true);
        Individual ind4 = addIndividual("Leia", "Organa", 4, 4.4d, true);
        Individual ind5 = addIndividual("Han", "Solo", 5, 5.5d, false);

        assertEqualsRx("findCountRx", 5L, individualManager.findCountRx());
        assertEqualsRx("findCountByRawQueryRx", 5L, individualManager.findCountByRawQueryRx("SELECT count(1) FROM " + IndividualConst.TABLE));
        assertEqualsRx("findCountBySelectionRx", 3L, individualManager.findCountBySelectionRx(IndividualConst.C_ENABLED + " = ?", SQLQueryBuilder.toSelectionArgs(1)));
        assertEqualsIndividualRx("findByRawQueryRx", ind5, individualManager.findByRawQueryRx("SELECT * FROM " + IndividualConst.TABLE + " WHERE " + IndividualConst.C_ID + " = ?", SQLQueryBuilder.toSelectionArgs(ind5.getId())));
        assertEqualsIndividualListRx("findAllByRawQueryRx", Arrays.asList(ind1, ind3, ind4), individualManager.findAllByRawQueryRx("SELECT * FROM " + IndividualConst.TABLE + " WHERE " + IndividualConst.C_ENABLED + " = ?", SQLQueryBuilder.toSelectionArgs(1)));
        assertEqualsRx("findValueByRawQueryRx", "Darth", individualManager.findValueByRawQueryRx(String.class, "SELECT " + IndividualConst.C_FIRST_NAME + " FROM " + IndividualConst.TABLE + " WHERE " + IndividualConst.C_ID + " = ?", SQLQueryBuilder.toSelectionArgs(1), ""));
        assertEqualsIndividualListRx("findAllRx", Arrays.asList(ind1, ind2, ind3, ind4, ind5), individualManager.findAllRx());
        assertEqualsIndividualRx("findByRowIdRx", ind1, individualManager.findByRowIdRx(ind1.getId()));
        assertEqualsIndividualRx("findBySelectionRx", ind1, individualManager.findBySelectionRx(IndividualConst.C_ENABLED + " = ?", SQLQueryBuilder.toSelectionArgs(1), null));
        assertEqualsIndividualListRx("findAllByRowIdsRx", Arrays.asList(ind1, ind2), individualManager.findAllByRowIdsRx(new long[]{ind1.getId(), ind2.getId(), 99999}));
        assertEqualsIndividualListRx("findAllBySelectionRx", Arrays.asList(ind1, ind3, ind4), individualManager.findAllBySelectionRx(IndividualConst.C_ENABLED + " = ?", SQLQueryBuilder.toSelectionArgs(1)));
        assertEqualsRx("findAllValuesBySelectionRx", Arrays.asList(1, 3, 4), individualManager.findAllValuesBySelectionRx(Integer.class, IndividualConst.C_NUMBER, IndividualConst.C_ENABLED + " = ?", SQLQueryBuilder.toSelectionArgs(1)));
        assertEqualsRx("findValueBySelectionRx", ind4.getNumber(), individualManager.findValueBySelectionRx(Integer.class, IndividualConst.C_NUMBER, ind4.getId(), 99));
        assertEqualsIndividualListRx("findAllOrderByRx", Arrays.asList(ind2, ind4, ind3, ind5, ind1), individualManager.findAllOrderByRx(IndividualConst.C_LAST_NAME));
        assertEqualsRx("tableExistsRx", true, individualManager.tableExistsRx(IndividualConst.TABLE));
        assertEqualsRx("tableExistsRx false", false, individualManager.tableExistsRx("BadTableName"));
    }

    private <I> void assertEqualsRx(final String message, final I expected, Observable<I> observable) {
        TestSubscriber<I> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        testSubscriber.assertReceivedOnNext(Arrays.asList(expected));
    }

    private void assertEqualsIndividualRx(final String message, final Individual expected, Observable<Individual> observable) {
        TestSubscriber<Individual> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        List<Individual> onNextEvents = testSubscriber.getOnNextEvents();
        Individual individual = onNextEvents.get(0);
        assertEquals(message, expected.getId(), individual.getId());
    }

    private void assertEqualsIndividualListRx(final String message, final List<Individual> expected, Observable<List<Individual>> observable) {
        TestSubscriber<List<Individual>> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);
        List<List<Individual>> onNextEvents = testSubscriber.getOnNextEvents();
        List<Individual> individuals = onNextEvents.get(0);

        assertEquals(message, expected.size(), individuals.size());

        for (int i = 0; i < individuals.size(); i++) {
            assertEquals(message, expected.get(i).getId(), individuals.get(i).getId());
        }
    }

    private Individual addIndividual(String first, String last, int number, double amount, boolean enabled) {
        IndividualManager individualManager = MainDatabaseManagers.getIndividualManager();
        Individual individual = new Individual();
        individual.setFirstName(first);
        individual.setLastName(last);
//        individual.setSampleDateTime(new GregorianCalendar(1970,1,1).getTime());
//        individual.setBirthDate(new GregorianCalendar(1970,1,2).getTime());
//        individual.setLastModified(new GregorianCalendar(1970,1,3).getTime());
        individual.setNumber(number);
//        individual.setPhone("555-555-1234");
//        individual.setEmail("test@test.com");
//        individual.setAmount1(amount);
        individual.setAmount2(amount);
        individual.setEnabled(enabled);

        individualManager.save(individual);

        return individual;
    }

    @Test
    public void testNoPrimaryKeyAndUnique() {
        IndividualDataManager individualDataManager = MainDatabaseManagers.getIndividualDataManager();

        IndividualData data = new IndividualData();
        data.setExternalId(555L);
        data.setTypeId(1);
        data.setName("Foo");

        // save
        individualDataManager.save(data);
        assertEquals(1, individualDataManager.findAll().size());

        // save again... unique constraint should replace the existing conflict
        individualDataManager.save(data);
        assertEquals(1, individualDataManager.findAll().size());
    }


    @Test
    public void testNullColumns() {
        JdbcSqliteDatabaseWrapper.setEnableLogging(true);

        IndividualManager individualManager = MainDatabaseManagers.getIndividualManager();

        Individual individual1 = new Individual();
        individual1.setFirstName("Bob");
        individual1.setPhone("555");

        Individual individual2 = new Individual();
        individual2.setFirstName("Sam");

        // Make sure all null fields save
        individualManager.save(individual1);
        individualManager.save(individual2);

        // Make sure all null fields load
        Individual individual1a = individualManager.findByRowId(individual1.getId());
        Individual individual2a = individualManager.findByRowId(individual2.getId());

        assertNotNull(individual1a);
        assertNotNull(individual2a);

        assertEquals(individual1a.getFirstName(), "Bob");
        assertEquals(individual1a.getLastName(), ""); // not null column
        assertEquals(individual1a.getPhone(), "555"); // null column
        assertNull(individual1a.getNumber());

        assertEquals(individual2a.getFirstName(), "Sam");
        assertEquals(individual2a.getLastName(), ""); // not null column
        assertNull(individual2a.getPhone()); // null column.. Make sure statement does NOT carry over individual1 data
        assertNull(individual2a.getNumber());
    }
}