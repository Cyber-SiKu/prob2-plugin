package de.prob2.ui.eclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.prob.Main;
import de.prob.testing.TestRunner;

public class BUnitFileDialogHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Display display = Display.getDefault();
		Shell shell = display.getActiveShell();

		// File standard dialog
		DirectoryDialog fileDialog = new DirectoryDialog(shell);
		// Set the text
		fileDialog
		.setText("Select directory where your test file is contained");
		// Set filter on .txt files
		// fileDialog.setFilterExtensions(new String[] { "*.groovy" });
		// Put in a readable name for the filter
		// fileDialog
		// .setFilterNames(new String[] { "Groovy Test files (*.groovy)" });
		// Open Dialog and save result of selection
		String selected = fileDialog.open();
		System.out.println(selected);

		TestRunner tests = Main.getInjector().getInstance(TestRunner.class);
		tests.runTests(selected);
		return null;
	}

}