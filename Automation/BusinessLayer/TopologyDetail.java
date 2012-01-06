package BusinessLayer;

public class TopologyDetail {

	private String _role;
    private int _topologyId;  
    private String _buildNumber;
	

	public String get_role() {
		return _role;
	}
	public void set_role(String _role) {
		this._role = _role;
	}
	public int get_topologyId() {
		return _topologyId;
	}
	public void set_topologyId(int id) {
		_topologyId = id;
	}
	public String get_buildNumber() {
		return _buildNumber;
	}
	public void set_buildNumber(String number) {
		_buildNumber = number;
	}
	
	
}
