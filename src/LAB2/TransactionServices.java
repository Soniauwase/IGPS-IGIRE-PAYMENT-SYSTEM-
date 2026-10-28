package LAB2;

import LAB1.Transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static LAB2.CustomException.*;

public class TransactionServices {

    private final TransactionDao transactionDao;
    private final ProcessedRequestDao processedRequestDao;

    public TransactionServices() {
        this.transactionDao = new TransactionDao();
        this.processedRequestDao = new ProcessedRequestDao();
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to establish database connection.", e);
        }
    }

    private boolean accountExists(Connection connection, int accountId) throws SQLException {
        String sql = "SELECT 1 FROM accounts WHERE account_id = ?;";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean deposit(String referenceId, int accountId, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero.");
        }

        try (Connection connection = getConnection()) {
            if (processedRequestDao.existsByReferenceId(referenceId)) {
                throw new DuplicateTransactionException("Duplicate request: Transaction with reference ID '" + referenceId + "' has already been processed.");
            }

            if (!accountExists(connection, accountId)) {
                throw new AccountNotFoundException("Account with ID " + accountId + " does not exist.");
            }

            connection.setAutoCommit(false);
            try {
                String updateBalanceSql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?;";
                try (PreparedStatement pstmt = connection.prepareStatement(updateBalanceSql)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setInt(2, accountId);
                    pstmt.executeUpdate();
                }

                Transaction tx = new Transaction();
                tx.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8));
                tx.setReferenceId(Integer.parseInt(referenceId));
                tx.setAmount(amount);
                tx.setTransactionType("DEPOSIT");
                tx.setAccountId(String.valueOf(accountId));

                transactionDao.createTransaction(connection, tx);
                processedRequestDao.saveProcessedRequest(referenceId);

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw new DatabaseException("SQL error occurred during deposit execution.", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error during deposit.", e);
        }
    }

    public boolean withdraw(String referenceId, int accountId, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        }

        try (Connection connection = getConnection()) {
            if (processedRequestDao.existsByReferenceId(referenceId)) {
                throw new DuplicateTransactionException("Duplicate request: Transaction with reference ID '" + referenceId + "' has already been processed.");
            }

            if (!accountExists(connection, accountId)) {
                throw new AccountNotFoundException("Account with ID " + accountId + " does not exist.");
            }

            connection.setAutoCommit(false);
            try {
                String updateBalanceSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ? AND balance >= ?;";
                int rowsUpdated;
                try (PreparedStatement pstmt = connection.prepareStatement(updateBalanceSql)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setInt(2, accountId);
                    pstmt.setDouble(3, amount);
                    rowsUpdated = pstmt.executeUpdate();
                }

                if (rowsUpdated == 0) {
                    connection.rollback();
                    throw new InsufficientBalanceException("Withdrawal failed: Insufficient funds in account ID " + accountId);
                }

                Transaction tx = new Transaction();
                tx.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8));
                tx.setReferenceId(Integer.parseInt(referenceId));
                tx.setAmount(amount);
                tx.setTransactionType("WITHDRAWAL");
                tx.setAccountId(String.valueOf(accountId));

                transactionDao.createTransaction(connection, tx);
                processedRequestDao.saveProcessedRequest(referenceId);

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw new DatabaseException("SQL error occurred during withdrawal execution.", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error during withdrawal.", e);
        }
    }

    public boolean transfer(String referenceId, int senderAccountId, int receiverAccountId, double amount) {
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be greater than zero.");
        }
        if (senderAccountId == receiverAccountId) {
            throw new IllegalArgumentException("Sender and receiver accounts cannot be the same.");
        }

        try (Connection connection = getConnection()) {
            if (processedRequestDao.existsByReferenceId(referenceId)) {
                throw new DuplicateTransactionException("Duplicate request: Transaction with reference ID '" + referenceId + "' has already been processed.");
            }

            if (!accountExists(connection, senderAccountId) || !accountExists(connection, receiverAccountId)) {
                throw new AccountNotFoundException("One or both account IDs provided do not exist.");
            }

            connection.setAutoCommit(false);
            try {
                String deductSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ? AND balance >= ?;";
                int senderUpdated;
                try (PreparedStatement pstmt = connection.prepareStatement(deductSql)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setInt(2, senderAccountId);
                    pstmt.setDouble(3, amount);
                    senderUpdated = pstmt.executeUpdate();
                }

                if (senderUpdated == 0) {
                    connection.rollback();
                    throw new InsufficientBalanceException("Transfer failed: Insufficient funds in sender account ID " + senderAccountId);
                }

                String addSql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?;";
                try (PreparedStatement pstmt = connection.prepareStatement(addSql)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setInt(2, receiverAccountId);
                    pstmt.executeUpdate();
                }

                Transaction outTx = new Transaction();
                outTx.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8));
                outTx.setReferenceId(Integer.parseInt(referenceId + "-OUT"));
                outTx.setAmount(amount);
                outTx.setTransactionType("TRANSFER_OUT");
                outTx.setAccountId(String.valueOf(senderAccountId));

                Transaction inTx = new Transaction();
                inTx.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8));
                inTx.setReferenceId(Integer.parseInt(referenceId + "-IN"));
                inTx.setAmount(amount);
                inTx.setTransactionType("TRANSFER_IN");
                inTx.setAccountId(String.valueOf(receiverAccountId));

                transactionDao.createTransaction(connection, outTx);
                transactionDao.createTransaction(connection, inTx);
                processedRequestDao.saveProcessedRequest(referenceId);

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw new DatabaseException("SQL error occurred during transfer execution.", e);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error during transfer.", e);
        }
    }
}