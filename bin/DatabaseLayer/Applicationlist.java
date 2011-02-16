package DatabaseLayer;

// Generated Nov 2, 2009 5:59:33 PM by Hibernate Tools 3.2.4.GA

import java.util.HashSet;
import java.util.Set;

/**
 * Applicationlist generated by hbm2java
 */
@SuppressWarnings("serial")
public class Applicationlist implements java.io.Serializable {

	int applicationListId;
	Application application;
	Set<Machinecatalog>  machinecatalogs = new HashSet<Machinecatalog> (0);

	public Applicationlist() {
	}

	public Applicationlist(int applicationListId, Application application) {
		this.applicationListId = applicationListId;
		this.application = application;
	}

	public Applicationlist(int applicationListId, Application application,
			Set<Machinecatalog> machinecatalogs) {
		this.applicationListId = applicationListId;
		this.application = application;
		this.machinecatalogs = machinecatalogs;
	}

	public int getApplicationListId() {
		return this.applicationListId;
	}

	public void setApplicationListId(int applicationListId) {
		this.applicationListId = applicationListId;
	}

	public Application getApplication() {
		return this.application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Set<Machinecatalog> getMachinecatalogs() {
		return this.machinecatalogs;
	}

	public void setMachinecatalogs(Set<Machinecatalog> machinecatalogs) {
		this.machinecatalogs = machinecatalogs;
	}

}
