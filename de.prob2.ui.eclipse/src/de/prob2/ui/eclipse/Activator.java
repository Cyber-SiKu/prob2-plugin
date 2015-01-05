package de.prob2.ui.eclipse;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.bms.prob.ProBServerFactory;
import de.bms.server.BMotionServer;
import de.bms.server.ResourceResolver;
import de.prob.Main;
import de.prob.webconsole.WebConsole;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static volatile boolean ready = false;

	public static BMotionServer bmotionServer;

	public static void runProB(final String... args) {
		Main.restricted = false;
		VersionController.ensureInstalled();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					WebConsole.run("127.0.0.1", new Runnable() {
						@Override
						public void run() {
							Activator.ready = true;
							VersionController.ensureInstalled();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(r).start();

		// Start BMotion Studio for ProB
		new Thread(new Runnable() {
			@Override
			public void run() {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				String[] bmsArgs = { "-workspace",
						workspace.getRoot().getLocation().toOSString() };
				bmotionServer = ProBServerFactory.getServer(bmsArgs);
				bmotionServer.setResourceResolver(new ResourceResolver() {
					@Override
					public URL resolve(final URL url) {
						URL newUrl = url;
						try {
							newUrl = FileLocator.resolve(url);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return newUrl;
					}
				});
				bmotionServer.start();
			}
		}).start();

	}

	// The plug-in ID
	public static final String PLUGIN_ID = "de.prob2.ui.eclipse"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		runProB("-s", "-local");
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
