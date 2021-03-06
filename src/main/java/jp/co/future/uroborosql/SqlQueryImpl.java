/**
 * Copyright (c) 2017-present, Future Corporation
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package jp.co.future.uroborosql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jp.co.future.uroborosql.context.SqlContext;
import jp.co.future.uroborosql.converter.EntityResultSetConverter;
import jp.co.future.uroborosql.converter.MapResultSetConverter;
import jp.co.future.uroborosql.converter.ResultSetConverter;
import jp.co.future.uroborosql.exception.DataNotFoundException;
import jp.co.future.uroborosql.exception.UroborosqlSQLException;
import jp.co.future.uroborosql.fluent.SqlQuery;
import jp.co.future.uroborosql.mapping.mapper.PropertyMapperManager;
import jp.co.future.uroborosql.utils.CaseFormat;

/**
 * SqlQuery実装
 *
 * @author H.Sugimoto
 */
final class SqlQueryImpl extends AbstractSqlFluent<SqlQuery> implements SqlQuery {
	/**
	 * コンストラクタ
	 *
	 * @param agent SqlAgent
	 * @param context SqlContext
	 */
	SqlQueryImpl(final SqlAgent agent, final SqlContext context) {
		super(agent, context);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#stream()
	 */
	@Override
	public Stream<Map<String, Object>> stream() {
		return stream(agent().getDefaultMapKeyCaseFormat());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#stream(jp.co.future.uroborosql.converter.ResultSetConverter)
	 */
	@Override
	public <T> Stream<T> stream(final ResultSetConverter<T> converter) {
		try {
			return agent().query(context(), converter);
		} catch (SQLException e) {
			throw new UroborosqlSQLException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#stream(jp.co.future.uroborosql.utils.CaseFormat)
	 */
	@Override
	public Stream<Map<String, Object>> stream(final CaseFormat caseFormat) {
		return stream(new MapResultSetConverter(caseFormat));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#stream(java.lang.Class)
	 */
	@Override
	public <T> Stream<T> stream(final Class<T> type) {
		return stream(new EntityResultSetConverter<T>(type, new PropertyMapperManager()));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#resultSet()
	 */
	@Override
	public ResultSet resultSet() {
		try {
			return agent().query(context());
		} catch (SQLException e) {
			throw new UroborosqlSQLException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#first()
	 */
	@Override
	public Map<String, Object> first() {
		return first(agent().getDefaultMapKeyCaseFormat());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#first(jp.co.future.uroborosql.utils.CaseFormat)
	 */
	@Override
	public Map<String, Object> first(final CaseFormat caseFormat) {
		return findFirst(caseFormat).orElseThrow(() -> new DataNotFoundException("query result is empty."));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#first(Class)
	 */
	@Override
	public <T> T first(final Class<T> type) {
		return findFirst(type).orElseThrow(() -> new DataNotFoundException("query result is empty."));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#findFirst()
	 */
	@Override
	public Optional<Map<String, Object>> findFirst() {
		return findFirst(agent().getDefaultMapKeyCaseFormat());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#findFirst(jp.co.future.uroborosql.utils.CaseFormat)
	 */
	@Override
	public Optional<Map<String, Object>> findFirst(final CaseFormat caseFormat) {
		try (Stream<Map<String, Object>> stream = stream(new MapResultSetConverter(caseFormat))) {
			return stream.findFirst();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#findFirst(java.lang.Class)
	 */
	@Override
	public <T> Optional<T> findFirst(final Class<T> type) {
		try (Stream<T> stream = stream(new EntityResultSetConverter<T>(type, new PropertyMapperManager()))) {
			return stream.findFirst();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#collect()
	 */
	@Override
	public List<Map<String, Object>> collect() {
		return collect(agent().getDefaultMapKeyCaseFormat());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#collect(jp.co.future.uroborosql.utils.CaseFormat)
	 */
	@Override
	public List<Map<String, Object>> collect(final CaseFormat caseFormat) {
		try {
			return agent().query(context(), caseFormat);
		} catch (SQLException e) {
			throw new UroborosqlSQLException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see jp.co.future.uroborosql.fluent.SqlQuery#collect(java.lang.Class)
	 */
	@Override
	public <T> List<T> collect(final Class<T> type) {
		try (Stream<T> stream = stream(new EntityResultSetConverter<T>(type, new PropertyMapperManager()))) {
			return stream.collect(Collectors.toList());
		}
	}
}
