package com.github.fmjsjx.libcommons.util.function.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface RowMapper<E> {

    E apply(ResultSet rs) throws SQLException;

}
