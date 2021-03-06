package jp.co.future.uroborosql;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.JDBCType;
import java.util.Arrays;

import jp.co.future.uroborosql.config.SqlConfig;
import jp.co.future.uroborosql.fluent.SqlQuery;
import jp.co.future.uroborosql.parameter.ReaderParameter;
import jp.co.future.uroborosql.parameter.StreamParameter;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AbstractSqlFluentTest {
	private static SqlConfig config = null;

	@BeforeClass
	public static void setUpClass() {
		config = UroboroSQL.builder("jdbc:h2:mem:SqlContextImplTest", "sa", "").build();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHasParam() throws Exception {
		try (SqlAgent agent = config.agent()) {
			SqlQuery query = null;
			query = agent.query("select * from dummy");
			query.param("key1", "value1");
			assertTrue(query.hasParam("key1"));
			assertFalse(query.hasParam("key2"));
		}
	}

	@Test
	public void testIfAbsent() throws Exception {
		try (SqlAgent agent = config.agent()) {
			SqlQuery query = null;
			query = agent.query("select * from dummy");
			query.paramIfAbsent("key1", "value1");
			assertThat(query.context().getParam("key1").getValue(), is("value1"));
			query.paramIfAbsent("key1", "value2");
			assertThat(query.context().getParam("key1").getValue(), is("value1"));

			query = agent.query("select * from dummy");
			query.paramIfAbsent("key1", "value1", JDBCType.VARCHAR);
			assertThat(query.context().getParam("key1").getValue(), is("value1"));
			query.paramIfAbsent("key1", "value2", JDBCType.VARCHAR);
			assertThat(query.context().getParam("key1").getValue(), is("value1"));

			query = agent.query("select * from dummy");
			query.paramIfAbsent("key1", "value1", JDBCType.VARCHAR.getVendorTypeNumber());
			assertThat(query.context().getParam("key1").getValue(), is("value1"));
			query.paramIfAbsent("key1", "value2", JDBCType.VARCHAR.getVendorTypeNumber());
			assertThat(query.context().getParam("key1").getValue(), is("value1"));

			query = agent.query("select * from dummy");
			query.paramListIfAbsent("key1", "value1", "value2");
			assertThat(query.context().getParam("key1").getValue(), is(Arrays.asList("value1", "value2")));
			query.paramListIfAbsent("key1", "value11", "value22");
			assertThat(query.context().getParam("key1").getValue(), is(Arrays.asList("value1", "value2")));

			query = agent.query("select * from dummy");
			query.inOutParamIfAbsent("key1", "value1", JDBCType.VARCHAR);
			assertThat(query.context().getParam("key1").getValue(), is("value1"));
			query.inOutParamIfAbsent("key1", "value2", JDBCType.VARCHAR);
			assertThat(query.context().getParam("key1").getValue(), is("value1"));

			query = agent.query("select * from dummy");
			query.inOutParamIfAbsent("key1", "value1", JDBCType.VARCHAR.getVendorTypeNumber());
			assertThat(query.context().getParam("key1").getValue(), is("value1"));
			query.inOutParamIfAbsent("key1", "value2", JDBCType.VARCHAR.getVendorTypeNumber());
			assertThat(query.context().getParam("key1").getValue(), is("value1"));

			query = agent.query("select * from dummy");
			InputStream is1 = new ByteArrayInputStream("value1".getBytes());
			query.blobParamIfAbsent("key1", is1, "value1".length());
			StreamParameter stream1 = (StreamParameter) query.context().getParam("key1");
			assertThat(query.context().getParam("key1").getValue(), is("[BLOB]"));
			InputStream is2 = new ByteArrayInputStream("value2".getBytes());
			query.blobParamIfAbsent("key1", is2, "value2".length());
			assertThat(query.context().getParam("key1"), is(stream1));

			query = agent.query("select * from dummy");
			InputStream is11 = new ByteArrayInputStream("value1".getBytes());
			query.blobParamIfAbsent("key1", is11);
			StreamParameter stream11 = (StreamParameter) query.context().getParam("key1");
			assertThat(query.context().getParam("key1").getValue(), is("[BLOB]"));
			InputStream is22 = new ByteArrayInputStream("value2".getBytes());
			query.blobParamIfAbsent("key1", is22);
			assertThat(query.context().getParam("key1"), is(stream11));

			query = agent.query("select * from dummy");
			Reader r1 = new StringReader("value1");
			query.clobParamIfAbsent("key1", r1);
			ReaderParameter reader1 = (ReaderParameter) query.context().getParam("key1");
			assertThat(query.context().getParam("key1").getValue(), is("[CLOB]"));
			Reader r2 = new StringReader("value2");
			query.clobParamIfAbsent("key1", r2);
			assertThat(query.context().getParam("key1"), is(reader1));

			query = agent.query("select * from dummy");
			Reader r11 = new StringReader("value1");
			query.clobParamIfAbsent("key1", r11, "value1".length());
			ReaderParameter reader11 = (ReaderParameter) query.context().getParam("key1");
			assertThat(query.context().getParam("key1").getValue(), is("[CLOB]"));
			Reader r22 = new StringReader("value2");
			query.clobParamIfAbsent("key1", r22, "value1".length());
			assertThat(query.context().getParam("key1"), is(reader11));
		}

	}
}
