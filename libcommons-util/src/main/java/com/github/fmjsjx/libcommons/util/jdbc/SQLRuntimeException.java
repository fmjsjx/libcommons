package com.github.fmjsjx.libcommons.util.jdbc;

import java.sql.SQLException;

import com.github.fmjsjx.libcommons.util.exception.WrappedRuntimeException;

public class SQLRuntimeException extends WrappedRuntimeException {

    private static final long serialVersionUID = -9054719718768726902L;

    public SQLRuntimeException(SQLException cause) {
        super(cause);
    }

    public SQLException wrappedSQLException() {
        return (SQLException) wrappedCause();
    }

}
