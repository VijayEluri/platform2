package com.idega.data.query;

import com.idega.data.IDOEntity;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.query.output.Outputable;
import com.idega.data.query.output.Output;
import com.idega.data.query.output.ToStringer;

/**
 * @author <a href="joe@truemesh.com">Joe Walnes</a>
 */
public class Table implements Outputable {

	private String name;

	private String alias;

	private IDOEntityDefinition _entityDefinition;

	public Table(String name) {
		this.name = name;
	}

	public Table(String name, String alias) {
		this.name = name;
		this.alias = alias;
	}

	public Table(Class entityClass) {
		_entityDefinition = getEntityDefinition(entityClass);
		if (_entityDefinition != null) {
			this.name = _entityDefinition.getSQLTableName().toLowerCase();
		}
		else {
			this.name = "null";
		}
	}

	public Table(Class entityClass, String alias) {
		this(entityClass);
		this.alias = alias;
	}

	public Table(IDOEntity entity) {
		this(entity.getEntityDefinition(), null);
	}

	public Table(IDOEntity entity, String alias) {
		this(entity.getEntityDefinition(), alias);
	}

	private Table(IDOEntityDefinition entityDefinition, String alias) {
		_entityDefinition = entityDefinition;
		if (_entityDefinition != null) {
			this.name = _entityDefinition.getSQLTableName().toLowerCase();
		}
		else {
			this.name = "null";
		}
		if (alias != null) {
			this.alias = alias;
		}
	}

	/**
	 * Name of table
	 */
	public String getName() {
		return name;
	}

	private IDOEntityDefinition getEntityDefinition(Class entityClass) {
		try {
			return IDOLookup.getEntityDefinitionForClass(entityClass);
		}
		catch (IDOLookupException ile) {
			return null;
		}
	}

	/**
	 * Whether this table has an alias assigned.
	 */
	private boolean hasAlias() {
		return alias != null;
	}

	/**
	 * Short alias of table
	 */
	public String getAlias() {
		return hasAlias() ? alias : name;
	}

	/**
	 * Get a column for a particular table.
	 */
	public Column getColumn(String columnName) {
		return new Column(this, columnName);
	}

	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Table)) return false;
		return getAlias().equals(((Table) o).getAlias());
	}

	public void write(Output out) {
		out.print(getName());
		if (hasAlias()) {
			out.print(' ');
			out.print(getAlias());
		}
	}

	public String toString() {
		return ToStringer.toString(this);
	}

	/**
	 * @return Returns the entityDefinition.
	 */
	public IDOEntityDefinition getEntityDefinition() {
		return _entityDefinition;
	}
	
	public boolean hasEntityDefinition() {
		return getEntityDefinition() != null;
	}

	/**
	 * @param entityDefinition
	 *          The entityDefinition to set.
	 */
	public void setEntityDefinition(IDOEntityDefinition entityDefinition) {
		_entityDefinition = entityDefinition;
	}
}