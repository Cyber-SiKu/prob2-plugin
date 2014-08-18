package de.prob2.ui.eclipse.perspectives;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class BugReportPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		final String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.addView("de.prob2.ui.eclipse.bugreport", IPageLayout.LEFT, 1,
				editorArea);
	}

}
