package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
		 * The {@link LabelProvider} for the properties table.
		 * 
		 * @author Xihui Chen
		 * 
		 */
public class PropertiesLabelProvider extends LabelProvider implements
				ITableLabelProvider {

			/**
			 * {@inheritDoc}
			 */
			public Image getColumnImage(final Object element,
					final int columnIndex) {
				if (columnIndex == 1 && element instanceof AbstractWidgetProperty) {
					AbstractWidgetProperty property = (AbstractWidgetProperty) element;
					
					if (property != null) {
						if (property.getPropertyDescriptor().getLabelProvider() != null) 
							return property.getPropertyDescriptor().getLabelProvider().
								getImage(property.getPropertyValue());
					}
				}
				return null;
			}

			/**
			 * {@inheritDoc}
			 */
			public String getColumnText(final Object element,
					final int columnIndex) {
				if (element instanceof AbstractWidgetProperty) {
					AbstractWidgetProperty property = (AbstractWidgetProperty) element;
					if (columnIndex == 0) {
						return property.getDescription();
					}
					
					if (property != null && property.getPropertyDescriptor().getLabelProvider() != null) {
						return property.getPropertyDescriptor().getLabelProvider().getText(
								property.getPropertyValue());
					}
				}
				if (element != null) {
					return element.toString();
				}
				return "error";
			}
		

}