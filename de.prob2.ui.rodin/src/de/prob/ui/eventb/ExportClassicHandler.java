package de.prob.ui.eventb;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import de.prob.scripting.Api;
import de.prob.servlet.Main;
import de.prob.statespace.StateSpace;

public class ExportClassicHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final IEventBRoot root = getSelectedEventBRoot(event);
		if (root != null) {
			final Preferences prefs = Platform.getPreferencesService()
					.getRootNode().node(InstanceScope.SCOPE)
					.node("prob_export_preferences");
			final Shell shell = HandlerUtil.getActiveShell(event);
			final String fileName = askForExportFile(prefs, shell, root);
			if (fileName != null) {
				exportToClassic(fileName, root);
			}
		}
		return null;
	}

	private IEventBRoot getSelectedEventBRoot(final ExecutionEvent event) {
		final ISelection fSelection = HandlerUtil.getCurrentSelection(event);
		IEventBRoot root = null;
		if (fSelection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) fSelection;
			if (ssel.size() == 1
					&& ssel.getFirstElement() instanceof IEventBRoot) {
				root = (IEventBRoot) ssel.getFirstElement();
			}
		}
		return root;
	}

	private String askForExportFile(final Preferences prefs, final Shell shell,
			final IEventBRoot root) {
		final String path = prefs.get("dir", System.getProperty("user.home"));

		final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.eventb" });

		dialog.setFilterPath(path);
		final String subext = (root instanceof IMachineRoot) ? "_mch" : "_ctx";
		dialog.setFileName(root.getComponentName() + subext + ".eventb");
		String result = dialog.open();
		if (result != null) {
			final String newPath = dialog.getFilterPath();
			if (!path.equals(newPath)) {
				prefs.put("dir", newPath);
				try {
					prefs.flush();
				} catch (final BackingStoreException e) {
					// Ignore, if preferences are not stored correctly we simply
					// ignore it (annoying, but not critical)
				}
			}
			if (!result.endsWith(".eventb")) {
				result += ".eventb";
			}
		}
		return result;
	}

	public static void exportToClassic(final String filename,
			final IEventBRoot root) {
		final boolean isSafeToWrite = isSafeToWrite(filename);

		if (isSafeToWrite) {
			String modelFile = root.getResource().getRawLocation()
					.makeAbsolute().toOSString();
			if (modelFile.endsWith(".buc")) {
				modelFile = modelFile.replace(".buc", ".bcc");
			} else {
				modelFile = modelFile.replace(".bum", ".bcm");
			}

			final Api api = Main.getInjector().getInstance(Api.class);

			try {
				final StateSpace s = api.eventb_load(modelFile);
				api.eventb_save(s, filename);
			} catch (final Exception e) {
				final Display display = Display.getDefault();
				final Shell shell = display.getActiveShell();
				MessageDialog.openError(shell, "Error occurred",
						"Exporting the specified file did not succeed because "
								+ e.getMessage());
			}
		}
	}

	// private static String serialize(Project project, String maincomponent) {
	// NewCoreModelTranslation translation = new NewCoreModelTranslation();
	// Model model = translation.translate(project, maincomponent);
	// // XStream xstream = new XStream(new JettisonMappedXmlDriver());
	// XStream xstream = new XStream();
	// String xml = xstream.toXML(model);
	// ByteArrayOutputStream out = new ByteArrayOutputStream();
	// GZIPOutputStream gzip;
	// byte[] bytes;
	// try {
	// gzip = new GZIPOutputStream(out);
	// gzip.write(xml.getBytes());
	// gzip.close();
	// bytes = out.toByteArray();
	// } catch (IOException e) {
	// bytes = xml.getBytes();
	// }
	// return Base64.encodeBase64String(bytes);
	// }

	private static boolean isSafeToWrite(final String filename) {
		if (new File(filename).exists()) {
			final MessageDialog dialog = new MessageDialog(null, "File exists",
					null, "The file exists. Do you want to overwrite it?",
					MessageDialog.QUESTION, new String[] { "Yes", "No" }, 0);
			return dialog.open() == 0;
		} else {
			return true;
		}
	}
}
