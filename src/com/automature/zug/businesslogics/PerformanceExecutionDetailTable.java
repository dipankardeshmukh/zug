package com.automature.zug.businesslogics;

public class PerformanceExecutionDetailTable {
		private String _performanceExecutionDetailId;
	    private int _elapssedMin;
	    private int _elapssedMax;
	    private float _average;
	    private int _95Percentage;
		public String get_performanceExecutionDetailId() {
			return _performanceExecutionDetailId;
		}
		public void set_performanceExecutionDetailId(String executionDetailId) {
			_performanceExecutionDetailId = executionDetailId;
		}
		public int get_elapssedMin() {
			return _elapssedMin;
		}
		public void set_elapssedMin(int min) {
			_elapssedMin = min;
		}
		public int get_elapssedMax() {
			return _elapssedMax;
		}
		public void set_elapssedMax(int max) {
			_elapssedMax = max;
		}
		public float get_average() {
			return _average;
		}
		public void set_average(float _average) {
			this._average = _average;
		}
		public int get_95Percentage() {
			return _95Percentage;
		}
		public void set_95Percentage(int percentage) {
			_95Percentage = percentage;
		}
}
