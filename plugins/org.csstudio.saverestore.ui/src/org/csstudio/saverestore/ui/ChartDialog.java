package org.csstudio.saverestore.ui;

import static org.csstudio.ui.fx.util.FXUtilities.setGridConstraints;

import java.util.List;

import org.csstudio.saverestore.ui.util.VTypeNamePair;
import org.csstudio.ui.fx.util.FXCanvasMaker;
import org.csstudio.ui.fx.util.FXUtilities;
import org.diirt.util.array.ListNumber;
import org.diirt.vtype.VNumberArray;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ChartDialog extends Dialog {

    private final VTypeNamePair value;
    private Composite parent;

    /**
     * Creates a passive dialog that show a chart of a number array.
     *
     * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
     * @param value the value to display
     */
    public ChartDialog(Shell parentShell, VTypeNamePair value) {
        super(parentShell);
        this.value = value;
        setBlockOnOpen(false);
    }

//    @Override
//    protected boolean isResizable() {
//        return true;
//    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (value != null) {
            shell.setText(value.name);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns++;
        new FXCanvasMaker() {
            @Override
            protected Scene createFxScene() {
                Button closeButton = new Button(IDialogConstants.CLOSE_LABEL);
                closeButton.setOnAction(e -> buttonPressed(IDialogConstants.OK_ID));
                GridPane pane = new GridPane();
                pane.setHgap(10);
                setGridConstraints(closeButton, false, false, HPos.RIGHT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
                pane.add(closeButton, 0, 0);
                return new Scene(pane);
            }
        }.createPartControl(parent);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        this.parent = parent;
        Composite composite = (Composite) super.createDialogArea(parent);
        Composite fxComposite = new Composite(composite, SWT.NONE);
        fxComposite
            .setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.VERTICAL_ALIGN_FILL, true, true));
        fxComposite.setLayout(new GridLayout());
        new FXCanvasMaker() {
            @Override
            protected Scene createFxScene() {
                return getScene();
            }
        }.createPartControl(fxComposite);
        applyDialogFont(composite);
        return composite;
    }

    private Scene getScene() {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Array Index");
        LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setTitle(value.name + " @ " + ((VNumberArray) value.value).getTimestamp().toDate());
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(value.name);
        ListNumber data = ((VNumberArray) value.value).getData();
        List<Data<Number, Number>> list = series.getData();
        for (int i = 0; i < data.size(); i++) {
            list.add(new Data<>(i, data.getDouble(i)));
        }
        lineChart.getData().add(series);
        lineChart.getStylesheets()
            .add(SnapshotViewerEditor.class.getResource(SnapshotViewerEditor.STYLE).toExternalForm());
        lineChart.setStyle(FXUtilities.toBackgroundColorStyle(parent.getBackground()));
        Scene scene = new Scene(lineChart, 600, 400);
        return scene;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.dialogs.Dialog#getInitialLocation(org.eclipse.swt.graphics.Point)
     */
    @Override
    protected Point getInitialLocation(Point initialSize) {
        Composite theParent = getShell().getParent();

        Monitor monitor = getShell().getDisplay().getPrimaryMonitor();
        if (theParent != null) {
            monitor = theParent.getMonitor();
        }

        Rectangle monitorBounds = monitor.getClientArea();
        Point centerPoint;
        if (theParent != null) {
            centerPoint = Geometry.centerPoint(theParent.getBounds());
        } else {
            centerPoint = Geometry.centerPoint(monitorBounds);
        }

        return new Point(centerPoint.x - (initialSize.x / 2), Math.max(monitorBounds.y,
            Math.min(centerPoint.y - (initialSize.y / 2), monitorBounds.y + monitorBounds.height - initialSize.y)));
    }
}
