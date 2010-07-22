package org.csstudio.swt.widgets.figures;
import org.eclipse.draw2d.Figure;


public class LinkingContainerTest extends AbstractWidgetTest{

	@Override
	public Figure createTestWidget() {
		return new LinkingContainerFigure();
	}
	
	
	@Override
	public String[] getPropertyNames() {
		String[] superProps =  super.getPropertyNames();
		String[] myProps = new String[]{
				"zoomToFitAll"
				
		};
		
		return concatenateStringArrays(superProps, myProps);
	}
	
			
}
