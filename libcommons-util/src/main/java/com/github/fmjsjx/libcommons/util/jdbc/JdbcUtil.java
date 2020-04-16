package com.github.fmjsjx.libcommons.util.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Supplier;

import com.github.fmjsjx.libcommons.util.function.jdbc.ParametersSetter;
import com.github.fmjsjx.libcommons.util.function.jdbc.ResultExecution;
import com.github.fmjsjx.libcommons.util.function.jdbc.ResultRowExecution;
import com.github.fmjsjx.libcommons.util.function.jdbc.RowMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JdbcUtil {

    public static final void select(Connection conn, String sql, ParametersSetter paramsSetter,
            ResultExecution execution) throws SQLRuntimeException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            paramsSetter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                execution.execute(rs);
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static final <C extends Collection<E>, E> C selectMany(Connection conn, String sql,
            ParametersSetter paramsSetter, RowMapper<E> rowMapper, Supplier<C> factory) throws SQLRuntimeException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            paramsSetter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                C collection = factory.get();
                for (; rs.next();) {
                    collection.add(rowMapper.apply(rs));
                }
                return collection;
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static final <E> List<E> selectList(Connection conn, String sql, ParametersSetter paramsSetter,
            RowMapper<E> rowMapper) throws SQLRuntimeException {
        return selectMany(conn, sql, paramsSetter, rowMapper, ArrayList::new);
    }

    public static final <E> List<E> selectList(Connection conn, String sql, RowMapper<E> rowMapper)
            throws SQLRuntimeException {
        return selectList(conn, sql, ParametersSetter.noParametersSetter(), rowMapper);
    }

    public static final <E> Optional<E> selectOne(Connection conn, String sql, RowMapper<E> mapper)
            throws SQLRuntimeException {
        return selectOne(conn, sql, ParametersSetter.noParametersSetter(), mapper);
    }

    public static final <E> Optional<E> selectOne(Connection conn, String sql, ParametersSetter paramsSetter,
            RowMapper<E> mapper) throws SQLRuntimeException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            paramsSetter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.apply(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static final OptionalInt selectInt(Connection conn, String sql) throws SQLRuntimeException {
        return selectInt(conn, sql, ParametersSetter.noParametersSetter());
    }

    public static final OptionalInt selectInt(Connection conn, String sql, ParametersSetter paramsSetter)
            throws SQLRuntimeException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            paramsSetter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return OptionalInt.of(rs.getInt(1));
                }
                return OptionalInt.empty();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static final OptionalLong selectLong(Connection conn, String sql) throws SQLRuntimeException {
        return selectLong(conn, sql, ParametersSetter.noParametersSetter());
    }

    public static final OptionalLong selectLong(Connection conn, String sql, ParametersSetter paramsSetter)
            throws SQLRuntimeException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            paramsSetter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return OptionalLong.of(rs.getLong(1));
                }
                return OptionalLong.empty();
            }
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static final int update(Connection conn, String sql) {
        return update(conn, sql, ParametersSetter.noParametersSetter());
    }

    public static final int update(Connection conn, String sql, ParametersSetter paramsSetter)
            throws SQLRuntimeException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            paramsSetter.accept(stmt);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

    public static final int update(Connection conn, String sql, ParametersSetter paramsSetter,
            ResultRowExecution keywRowExecution) throws SQLRuntimeException {
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            paramsSetter.accept(stmt);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    for (; rs.next();) {
                        keywRowExecution.execute(rs);
                    }
                }
            }
            return affectedRows;
        } catch (SQLException e) {
            throw new SQLRuntimeException(e);
        }
    }

}
