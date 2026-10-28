package LAB2;

import LAB1.Account;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface AccountInterfaceDao {
    void createAccountTable() throws SQLException;
    Account creatingAccount(Account account)throws SQLException;
    boolean updateAccount(String account_id) throws SQLException;
    List<Account> allAccount() throws SQLException;
    Account findAccountById(String account_id)throws SQLException;
    boolean deleteAccount(String account_id)throws SQLException;

}
