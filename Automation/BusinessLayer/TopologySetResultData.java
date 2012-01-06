package BusinessLayer;

import java.util.ArrayList;
import java.util.List;

public class TopologySetResultData {
	  private List<TopologyDetail> _topologyDetailList = new ArrayList<TopologyDetail>();
	    
	    private List<TestCaseResult> _testCaseResultList = new ArrayList<TestCaseResult>();

		public List<TopologyDetail> get_topologyDetailList() {
			return _topologyDetailList;
		}

		public void set_topologyDetailList(List<TopologyDetail> detailList) {
			_topologyDetailList = detailList;
		}

		public List<TestCaseResult> get_testCaseResultList() {
			return _testCaseResultList;
		}

		public void set_testCaseResultList(List<TestCaseResult> caseResultList) {
			_testCaseResultList = caseResultList;
		}
	   
}
