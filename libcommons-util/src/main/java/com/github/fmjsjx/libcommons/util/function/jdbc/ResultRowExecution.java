package com.github.fmjsjx.libcommons.util.function.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultRowExecution {

    void execute(ResultSet rs) throws SQLException;
    
}
