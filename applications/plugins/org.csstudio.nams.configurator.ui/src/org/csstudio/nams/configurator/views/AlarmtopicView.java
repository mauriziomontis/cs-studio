package org.csstudio.nams.configurator.views;

import org.csstudio.nams.configurator.beans.IConfigurationBean;
import org.csstudio.nams.configurator.composite.FilterableBeanList;

public class AlarmtopicView extends AbstractNamsView {

	public static final String ID = "org.csstudio.nams.configurator.alarmtopic";

	@Override
	protected IConfigurationBean[] getTableContent() {
		return configurationBeanService.getAlarmTopicBeans();
	}

	@Override
	protected void initDragAndDrop(FilterableBeanList filterableBeanList) {
		// TODO Auto-generated method stub
		
	}
}
