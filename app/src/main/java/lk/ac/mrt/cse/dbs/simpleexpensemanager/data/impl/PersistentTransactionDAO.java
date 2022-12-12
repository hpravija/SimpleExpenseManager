package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final List<Transaction> transactions;
    private final SQLiteDatabase sqLiteDatabase;
    private final SimpleDateFormat simpleDateFormat;

    public PersistentTransactionDAO(SQLiteDatabase sqLiteDatabase) {
        this.transactions = new LinkedList<>();
        this.sqLiteDatabase = sqLiteDatabase;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        loadTransactions();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);

        // adding the new transaction to the database.
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",simpleDateFormat.format(date));
        contentValues.put("accountNo",accountNo);
        contentValues.put("expenseType", String.valueOf(expenseType));
        contentValues.put("amount",amount);
        sqLiteDatabase.insert("transaction_log",null,contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }

    public void loadTransactions(){
        // loads all transaction data in the database to the linkedList.
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM transaction_log",null);

        while (cursor.moveToNext()){
            Date date = null;
            try {
                date = simpleDateFormat.parse(cursor.getString(1));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Transaction transaction = new Transaction(
                    date,
                    cursor.getString(2),
                    Enum.valueOf(ExpenseType.class,cursor.getString(3)),
                    cursor.getDouble(4)
            );
            transactions.add(transaction);
        }
        cursor.close();
    }
}
