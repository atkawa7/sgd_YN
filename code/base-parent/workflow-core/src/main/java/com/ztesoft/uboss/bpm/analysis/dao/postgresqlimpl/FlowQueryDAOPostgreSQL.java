package com.ztesoft.uboss.bpm.analysis.dao.postgresqlimpl;

import java.util.List;

import com.ztesoft.uboss.bpm.analysis.dao.abstractimpl.FlowQueryDAO;
import com.ztesoft.uboss.bpm.analysis.model.DelayedFlowQryCondition;
import com.ztesoft.uboss.bpm.analysis.model.DelayedFlows;
import com.ztesoft.zsmart.core.exception.BaseAppException;
import com.ztesoft.uboss.bpm.utils.DateUtil;

public class FlowQueryDAOPostgreSQL extends FlowQueryDAO {

	@Override
	public List<DelayedFlows> selectDelayedFlows(
			DelayedFlowQryCondition qryCondition) throws BaseAppException {
		
		String sql = "SELECT COUNT(1) DELAYED_FLOW_NUMBER, DATE_TRUNC('month', A.START_TIME) ANALYSE_MONTH"
			+ " FROM BPM_TASK_HOLDER A, BPM_PROCESS_TEMP B, BPM_PROCESS_TEMP_VER C"
			+ " WHERE A.PROCESS_VER_ID = C.PROCESS_VER_ID AND A.START_TIME >= :START_YEAR AND A.START_TIME < :END_YEAR"
			+ " [ AND B.PROC_DEF_TYPE_ID = :PROC_DEF_TYPE_ID] [ AND B.PROC_TEMP_ID = :PROC_TEMP_ID] AND B.OVER_TIME IS NOT NULL"
			+ " AND B.PROC_TEMP_ID = C.PROC_TEMP_ID AND (((A.START_TIME + B.OVER_TIME * INTERVAL '1 day') > NOW() AND A.HOLDER_STATE NOT IN ('C', 'T'))"
			+ " OR ((A.START_TIME + B.OVER_TIME * INTERVAL '1 day') > A.END_TIME AND A.HOLDER_STATE = 'C'))"
			+ " GROUP BY DATE_TRUNC('month', A.START_TIME)";
		
		qryCondition.setStartYear(qryCondition.getYear());
		qryCondition.setEndYear(DateUtil.offsetYear(qryCondition.getYear(), 1));
		
		return this.selectList(sql, DelayedFlows.class, qryCondition);
	}
}
