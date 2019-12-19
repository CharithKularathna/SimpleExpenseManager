package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private DatabaseHelper dbHelper;

    public PersistentAccountDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    //Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbers = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor curser = db.query(DatabaseHelper.TABLE_ACCOUNTS, new String[] {DatabaseHelper.KEY_ACCOUNT_NO}, null, null, null, null, null);

        if (curser.moveToFirst()) {
            do {
                accountNumbers.add(curser.getString(0));
            } while (curser.moveToNext());
        }

        curser.close();
        return accountNumbers;
    }

    public List<Account> getAccountsList() {
        List<Account> accountList = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ACCOUNTS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Account account = new Account(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        Double.parseDouble(cursor.getString(3))
                );
                accountList.add(account);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return accountList;
    }

    public Account getAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = db.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_ACCOUNTS, null, DatabaseHelper.KEY_ACCOUNT_NO + "=?",
                new String[] { accountNo }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            Account account = new Account(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    Double.parseDouble(cursor.getString(3))
            );
            cursor.close();
            return account;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    public void addAccount(Account account) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.KEY_ACCOUNT_NO, account.getAccountNo()); 
        values.put(DatabaseHelper.KEY_BANK_NAME, account.getBankName()); 
        values.put(DatabaseHelper.KEY_ACCOUNT_HOLDER_NAME, account.getAccountHolderName()); 
        values.put(DatabaseHelper.KEY_BALANCE, account.getBalance()); 

        
        db.insert(DatabaseHelper.TABLE_ACCOUNTS, null, values);
        db.close(); 

    }

    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_ACCOUNTS, DatabaseHelper.KEY_ACCOUNT_NO + " = ?",
                new String[] { accountNo });
        db.close();
    }

    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Account acc = this.getAccount(accountNo);
        ContentValues values = new ContentValues();
        //Check
        switch (expenseType) {
            case EXPENSE:
                values.put(DatabaseHelper.KEY_BALANCE, acc.getBalance() - amount);
                break;
            case INCOME:
                values.put(DatabaseHelper.KEY_BALANCE, acc.getBalance() + amount);
                break;
        }

        db.update(DatabaseHelper.TABLE_ACCOUNTS, values, DatabaseHelper.KEY_ACCOUNT_NO + " = ?",
                new String[] { accountNo });
    }
}
