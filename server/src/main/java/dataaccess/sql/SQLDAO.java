package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLDAO {

    private static final String[] CREATE_STATEMENTS = {
        """
        CREATE TABLE IF NOT EXISTS `user` (
            `username` VARCHAR(256) NOT NULL,
            `password` VARCHAR(256) NOT NULL,
            `email` VARCHAR(256) NOT NULL,
            PRIMARY KEY (`username`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
        """,
        """
        CREATE TABLE IF NOT EXISTS `auth` (
            `authToken` VARCHAR(256) NOT NULL,
            `username` VARCHAR(256) NOT NULL,
            PRIMARY KEY (`authToken`),
            INDEX (`username`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
        """,
        """
        CREATE TABLE IF NOT EXISTS `game` (
            `gameID` INT NOT NULL AUTO_INCREMENT,
            `whiteUsername` VARCHAR(256) DEFAULT NULL,
            `blackUsername` VARCHAR(256) DEFAULT NULL,
            `gameName` VARCHAR(256) NOT NULL,
            `game` TEXT DEFAULT NULL,
            PRIMARY KEY (`gameID`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
        """
    };
}