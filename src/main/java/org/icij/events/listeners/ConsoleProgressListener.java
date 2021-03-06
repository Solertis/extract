package org.icij.events.listeners;

import me.tongfei.progressbar.ProgressBar;

import org.icij.events.Listener;
import org.icij.events.Monitorable;

public class ConsoleProgressListener implements Listener {

	private final ProgressBar progress;

	public ConsoleProgressListener(final ProgressBar progress) {
		this.progress = progress;
	}

	@Override
	public void notify(final Monitorable monitorable, final Object arg) {
		notify(arg);
	}

	@Override
	public void notify(final Object arg) {
		if (null != arg) {
			progress.setExtraMessage(arg.toString());
		}

		progress.step();
	}

	@Override
	public void hintRemaining(final int total) {
		progress.maxHint(total);
	}
}
