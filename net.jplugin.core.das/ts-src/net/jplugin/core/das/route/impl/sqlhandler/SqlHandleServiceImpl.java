package net.jplugin.core.das.route.impl.sqlhandler;

import java.util.HashMap;
import java.util.List;

import net.jplugin.common.kits.StringKit;
import net.jplugin.core.das.route.api.SqlHandleService;
import net.jplugin.core.das.route.api.TablesplitException;
import net.jplugin.core.das.route.impl.conn.RouterConnection;
import net.jplugin.core.das.route.impl.conn.SqlHandleResult;
import net.jplugin.core.das.route.impl.parser.SqlWordsWalker;

public class SqlHandleServiceImpl implements SqlHandleService {

	HashMap<String, AbstractCommandHandler> map = null;
	
	private  synchronized void init() {
		if (map!=null) return;
		map = new HashMap<>();
		map.put("SELECT", new SelectHandler());
		map.put("UPDATE", new UpdateHandler());
		map.put("DELETE", new DeleteHandler());
		map.put("INSERT", new InsertHandler());
	}
	
	@Override
	public SqlHandleResult handle(RouterConnection conn, String sql, List<Object> params) {

		if (map==null) init();
		sql = sql.trim();
		SqlWordsWalker walker = SqlWordsWalker.createFromSql(sql);
		walker.next();
		if (StringKit.isNull(walker.word))
			throw  new TablesplitException("command is null:"+sql);
		
		AbstractCommandHandler h = map.get(walker.word.toUpperCase());
		if (h==null) throw new TablesplitException("Error command:"+walker.word);
		
		return h.handle(conn,sql,params,walker);
	}

	@Override
	public SqlHandleResult handle(RouterConnection conn, String sql) {
		return handle(conn,sql,null);
	}


}
