package LAB2;

import LAB1.Account;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class AccountDao implements  AccountInterfaceDao {

    @Override
    public void createAccountTable() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        Statement statement=connection.createStatement();
        String createSql= """
                create table account(
                account_id varchar(50) primary key,
                account_number varchar(50)  unique not null,
                account_type varchar(50) CHECK( account_type IN('saving account','wallet account')),
                balance DOUBLE PRECISION,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                customer_id varchar(50)  not null,
                foreign key(customer_id) references customer(customer_id) ON DELETE CASCADE
             
                )
                """;
        statement.execute(createSql);
        System.out.println("table created successfully");

    }

    @Override
    public Account creatingAccount(Account account) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String insertSql= """
                insert  into account(account_id,account_number,account_type,balance, created_at,customer_id) values (?,?,?,?,?,?);
                """;
        PreparedStatement preparedStatement =connection.prepareStatement(insertSql);
        preparedStatement.setString(1, account.getAccountId());
        preparedStatement.setString(2, account.getAccountNumber());
        preparedStatement.setString(3,account.getAccountType());
        preparedStatement.setDouble(4, account.getBalance());
        preparedStatement.setTimestamp(5,Timestamp.valueOf(LocalDateTime.now()));
        preparedStatement.setString(6,account.getCustomerId());

        preparedStatement.executeUpdate();
        account.setCreatedAt(LocalDateTime.now());

        return account;
    }

    @Override
    public boolean updateAccount(String account_id) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String updateSql= """
                update account set account_name,account_tye,balance,created_at,customer_id
                """;


        return false;
    }

    @Override
    public List<Account> allAccount() throws SQLException {

        return List.of();
    }

    @Override
    public Account findAccountById(String account_id) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String findByIdSql= """
                select  account_id,  account_number,account_type,balance,created_at,customer_id  from account where account_id=?;
                """;
        PreparedStatement preparedStatement =connection.prepareStatement(findByIdSql);
        preparedStatement.setString(1,account_id);
        ResultSet resultSet= preparedStatement.executeQuery();
        while (resultSet.next()){
            Account account= new Account();
            account.setAccountId(resultSet.getString("account_id"));
            account.setAccountNumber(resultSet.getString("account_number"));
            account.setAccountType(resultSet.getString("account_type"));
            account.setBalance(resultSet.getDouble("balance"));
            account.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
            account.setCustomerId(resultSet.getString("customer_id"));
            return  account;
        }



        return null;
    }

    @Override
    public boolean deleteAccount(String account_id) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String deleteAccountSql=" delete account  where account_id=?";
        PreparedStatement preparedStatement= connection.prepareStatement(deleteAccountSql);
        preparedStatement.setString(1,account_id);
        preparedStatement.executeUpdate();
        System.out.println(" the account of this account:"+account_id + "is deleted successfully");
        return true;

    }

    static void main(String[] args) throws SQLException {
        AccountDao accountDao= new AccountDao();
       // accountDao.createAccountTable();
    }
}
