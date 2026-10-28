package LAB2;

import LAB1.ProcessedRequest;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProcessedRequestDao implements  ProcessedRequestInterfaceDAO {

    @Override
    public void createProcessedRequestTable() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        Statement statement=connection.createStatement();
        String createSql = """
            CREATE TABLE IF NOT EXISTS processed_requests (
            id SERIAL PRIMARY KEY,
            reference_id VARCHAR(50) UNIQUE NOT NULL,
            processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;
        statement.execute(createSql);

    }

    @Override
    public boolean saveProcessedRequest(String reference_id) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String insertSql = "INSERT INTO processed_requests (reference_id, processed_at) VALUES (?, ?) ON CONFLICT (reference_id) DO NOTHING;";
        PreparedStatement preparedStatement= connection.prepareStatement(insertSql);
        preparedStatement.setString(1, reference_id);
        preparedStatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
        return preparedStatement.executeUpdate() > 0;

    }

    @Override
    public ProcessedRequest findProcessedRequestByReferenceId(String reference_id) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String findProcessedRequestById = "SELECT * FROM processed_requests WHERE reference_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(findProcessedRequestById);
        preparedStatement.setString(1, reference_id);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                ProcessedRequest request = new ProcessedRequest();
                request.setId(resultSet.getInt("id"));
                request.setReference_id(resultSet.getString("reference_id"));
                Timestamp timestamp = resultSet.getTimestamp("processed_at");
                if (timestamp != null) {
                    request.setProcessed_at(timestamp.toLocalDateTime());
                }
                return request;

            }
        }
        return null;
    };


    @Override
    public List<ProcessedRequest> displayAllProcessedRequests() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String displaySql = "SELECT * FROM processed_requests;";
        List<ProcessedRequest> requestList = new ArrayList<>();
        PreparedStatement preparedStatement= connection.prepareStatement(displaySql);
        ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ProcessedRequest request = new ProcessedRequest();
                request.setId(resultSet.getInt("id"));
                request.setReference_id(resultSet.getString("reference_id"));

                Timestamp timestamp = resultSet.getTimestamp("processed_at");
                if (timestamp != null) {
                    request.setProcessed_at(timestamp.toLocalDateTime());
                }

                requestList.add(request);
            }

        return requestList;
    }

    @Override
    public boolean updateProcessedRequest(String reference_id, String new_status) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String updateSql = "UPDATE processed_requests SET processed_at = ? WHERE reference_id = ?;";
        PreparedStatement preparedStatement= connection.prepareStatement(updateSql);
        preparedStatement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
        preparedStatement.setString(2, reference_id);

        int rowsUpdated = preparedStatement.executeUpdate();
        return rowsUpdated > 0;
    }


    @Override
    public boolean deleteProcessedRequest(String reference_id) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String deleteSql = "DELETE FROM processed_requests WHERE reference_id = ?;";
        PreparedStatement preparedStatement=connection.prepareStatement(deleteSql);
        preparedStatement.setString(1, reference_id);

        int rowsDeleted = preparedStatement.executeUpdate();
        return rowsDeleted > 0;
    }

    @Override
    public boolean existsByReferenceId(String reference_id) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/IgirepaymentSystem", "postgres", "Admin");
        String existSql = "SELECT 1 FROM processed_requests WHERE reference_id = ?;";
        PreparedStatement preparedStatement= connection.prepareStatement(existSql);
        preparedStatement.setString(1, reference_id);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {

            return resultSet.next();
        }
    }

}
