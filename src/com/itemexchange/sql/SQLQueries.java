package com.adamki11s.itemexchange.sql;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.adamki11s.itemexchange.database.Database;

public class SQLQueries implements Runnable {

	private static Queue<String> run = new LinkedList<String>();

	private static final Object lock = new Object();

	public static void addQuery(String s) {
		synchronized (lock) {
			run.add(s);
		}
	}

	@Override
	public void run() {
		synchronized (lock) {
			if (!run.isEmpty()) {
				try {
					Database.sql.standardQuery(run.poll());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
