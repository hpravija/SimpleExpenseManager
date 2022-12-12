package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.db.DBHelper;

public class PersistentExpenseManager extends ExpenseManager {
    private final Context context;

    public PersistentExpenseManager(Context context) {
        this.context = context;
        setup();
    }

    @Override
    public void setup() {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(sqLiteDatabase);
        setTransactionsDAO(persistentTransactionDAO);

        AccountDAO persistentAccountDAO = new PersistentAccountDAO(sqLiteDatabase);
        setAccountsDAO(persistentAccountDAO);
    }
}
