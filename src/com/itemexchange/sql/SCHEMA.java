package com.adamki11s.itemexchange.sql;


public enum SCHEMA {
	
	MySQL,
	SQLite;
	
	@Override
	public String toString(){
		return this.toString().toUpperCase();
	}

}
