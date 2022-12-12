package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final Map<String, Account> accounts;
    private final SQLiteDatabase sqLiteDatabase;

    public PersistentAccountDAO(SQLiteDatabase sqLiteDatabase) {
        this.accounts = new HashMap<>();
        this.sqLiteDatabase = sqLiteDatabase;
        loadAccountData();
    }

    @Override
    public List<String> getAccountNumbersList() {
        return new ArrayList<>(accounts.keySet());
    }

    @Override
    public List<Account> getAccountsList() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if (accounts.containsKey(accountNo)) {
            return accounts.get(accountNo);
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        accounts.put(account.getAccountNo(), account);

        // adding the new account to the database.
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo",account.getAccountNo());
        contentValues.put("bankName",account.getBankName());
        contentValues.put("accountHolderName",account.getAccountHolderName());
        contentValues.put("balance",account.getBalance());
        sqLiteDatabase.insert("user_account",null,contentValues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM user_account WHERE accountNo = ?",new String[]{accountNo});
        if(cursor.getCount()>0) {
            sqLiteDatabase.delete("user_account", "accountNo=?", new String[]{accountNo});
            accounts.remove(accountNo);
        }else {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        cursor.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = accounts.get(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        accounts.put(accountNo, account);
    }

    public void loadAccountData(){
        // loads all account data in the database to the hashmap.
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM user_account",null);
        while (cursor.moveToNext()){
            Account account = new Account(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3)
            );
            accounts.put(account.getAccountNo(), account);
        }
        cursor.close();
    }
}
