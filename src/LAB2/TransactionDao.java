package LAB2;

import LAB1.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao implements TransactionInterfaceDao{
    @Override
    public boolean createTransactionTable() throws SQLException {
     Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        Statement statement=connection.createStatement();
     String createSql= """
             create table transaction(
             transaction_id varchar(50) primary key,
             reference_id  int  unique not null,
             amount DOUBLE PRECISION,
             transaction_type varchar(50)  CHECK (transaction_type IN('deposit','withdraw')),
             time_stamp  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
             account_id varchar(50) not null ,
             foreign key  (account_id) references account(account_id) ON DELETE CASCADE
             
             )
             """;
     statement.execute(createSql);
        return true;
    }

    @Override
    public Transaction createTransaction(Connection connection, Transaction transaction) throws SQLException {
        String insertSql = """
        INSERT INTO transactions (transaction_id, reference_id, amount, transaction_type, time_stamp, account_id) 
        VALUES (?, ?, ?, ?, ?, ?);
    """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            preparedStatement.setString(1, transaction.getTransactionId());
            preparedStatement.setInt(2, transaction.getReferenceId());
            preparedStatement.setDouble(3, transaction.getAmount());
            preparedStatement.setString(4, transaction.getTransactionType());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(6, transaction.getAccountId());
            preparedStatement.executeUpdate();
        }
        return transaction;
    }

    @Override
    public Transaction findTransactionById(String transaction_id) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String findTransactionById= """
                select  * from transaction where transaction_id=?;
                """;
        PreparedStatement preparedStatement=connection.prepareStatement(findTransactionById);
        preparedStatement.setString(1,transaction_id);
        ResultSet resultSet= preparedStatement.executeQuery();
        while (resultSet.next()){
            Transaction transaction= new Transaction();
            transaction.setTransactionId(resultSet.getString("transaction_id"));
            transaction.setReferenceId(resultSet.getInt("reference_id"));
            transaction.setAmount(resultSet.getDouble("amount"));
            transaction.setTransactionType(resultSet.getString("transaction_type"));
            transaction.setTimestamp(resultSet.getTimestamp("time_stamp").toLocalDateTime());
            transaction.setAccountId(resultSet.getString("account_id"));
            return transaction;
        }
        return null;
    }

    @Override
    public List<Transaction> displayAllTransaction() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        List<Transaction> transactionList = new ArrayList<>();
        String displaysql = "SELECT * FROM transaction;";
        PreparedStatement preparedStatement=connection.prepareStatement(displaysql);
        ResultSet resultSet=preparedStatement.executeQuery();
        while (resultSet.next()) {
                Transaction transaction = new Transaction();
                transaction.setTransactionId(resultSet.getString("transaction_id"));
                transaction.setReferenceId(resultSet.getInt("reference_id"));

                transaction.setAmount(resultSet.getDouble("amount"));
                transaction.setTransactionType(resultSet.getString("transaction_type"));

                if (resultSet.getTimestamp("time_stamp") != null) {
                    transaction.setTimestamp(resultSet.getTimestamp("time_stamp").toLocalDateTime());
                }

                transaction.setAccountId(resultSet.getString("account_id"));

                transactionList.add(transaction);
            }

        return transactionList;
    }

    @Override
    public boolean updateTransaction(Transaction transaction) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String updateSql = """
            UPDATE transaction 
            SET reference_id = ?, amount = ?, transaction_type = ?, time_stamp = ?, account_id = ? 
            WHERE transaction_id = ?;
            """;
        PreparedStatement preparedStatement= connection.prepareStatement(updateSql);
        preparedStatement.setInt(1, transaction.getReferenceId());
        preparedStatement.setDouble(2, transaction.getAmount());
        preparedStatement.setString(3, transaction.getTransactionType());
        preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
        preparedStatement.setString(5, transaction.getAccountId());
        preparedStatement.setString(6, transaction.getTransactionId());
        int rowsUpdated=preparedStatement.executeUpdate();
        if(rowsUpdated>0) {
            System.out.println("transaction updated successfully!");
        }
        return false;
    }

    @Override
    public boolean deleteTransaction(String transaction_id) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String deleteSql = "DELETE FROM transaction WHERE transaction_id = ?";
        PreparedStatement preparedStatement= connection.prepareStatement(deleteSql);
            preparedStatement.setString(1, transaction_id);
            if(preparedStatement.executeUpdate() > 0){
                System.out.println("transaction deleted successfully!");
        }
            return false;
    }

    @Override
    public List<Transaction> getTransactionHistory(String account_number) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        List<Transaction> transactionList = new ArrayList<>();
        String sql = "SELECT * FROM transaction WHERE account_id = ? ORDER BY time_stamp DESC;";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, account_number);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setTransactionId(resultSet.getString("transaction_id"));
                    transaction.setReferenceId(resultSet.getInt("reference_id"));
                    transaction.setAmount(resultSet.getDouble("amount"));
                    transaction.setTransactionType(resultSet.getString("transaction_type"));

                    if (resultSet.getTimestamp("time_stamp") != null) {
                        transaction.setTimestamp(resultSet.getTimestamp("time_stamp").toLocalDateTime());
                    }
                    transaction.setAccountId(resultSet.getString("account_id"));
                    transactionList.add(transaction);
                }
            }
        return transactionList;
    }

    @Override
    public boolean validateTransactionReference(String reference_id) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");


        String sql = "SELECT 1 FROM transaction WHERE reference_id = ?;";

             PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, reference_id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }



