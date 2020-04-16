package com.github.fmjsjx.libcommons.util.function.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface ParametersSetter {

    static ParametersSetter NO_PARAMETERS_SETTER = stmt -> {
        // do nothing
    };

    static ParametersSetter noParametersSetter() {
        return NO_PARAMETERS_SETTER;
    }

    void accept(PreparedStatement stmt) throws SQLException;

}
