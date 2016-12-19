package org.icij.extract.tasks;

import org.icij.extract.document.DocumentFactory;
import org.icij.extract.queue.DocumentQueue;
import org.icij.extract.queue.Scanner;

import org.icij.extract.tasks.factories.DocumentQueueFactory;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.icij.task.MonitorableTask;
import org.icij.task.annotation.Option;
import org.icij.task.annotation.Task;

/**
 * Task that scans paths for files to add to a queue.
 *
 * @author Matthew Caruana Galizia <mcaruana@icij.org>
 * @since 1.0.0-beta
 */
@Task("Queue files for processing later.")
@Option(name = "queue-type", description = "Set the report backend type. For now, the only valid value is \"redis\"" +
		".", parameter = "type", code = "q")
@Option(name = "queue-name", description = "The name of the report, the default of which is type-dependent" +
		".", parameter = "name")
@Option(name = "redis-address", description = "Set the Redis backend address. Defaults to " +
		"127.0.0.1:6379.", parameter = "address")
@Option(name = "include-pattern", description = "Glob pattern for matching files e.g. \"**/*.{tif,pdf}\". " +
		"Files not matching the pattern will be ignored.", parameter = "pattern")
@Option(name = "exclude-pattern", description = "Glob pattern for excluding files and directories. Files " +
		"and directories matching the pattern will be ignored.", parameter = "pattern")
@Option(name = "follow-symlinks", description = "Follow symbolic links, which are not followed by default" +
		".")
@Option(name = "include-hidden-files", description = "Don't ignore hidden files. On DOS file systems, this" +
		" means all files or directories with the \"hidden\" file attribute. On all other file systems, this means " +
		"all file or directories starting with a dot. Hidden files are ignored by default.")
@Option(name = "include-os-files", description = "Include files and directories generated by common " +
		"operating systems. This includes \"Thumbs.db\" and \".DS_Store\". The list is not determined by the current " +
		"operating system. OS-generated files are ignored by default.")
@Option(name = "max-depth", description = "The maximum depth to which the scanner will recurse.", parameter = "integer")
@Option(name = "id-method", description = "The method for determining document IDs, for queues that use them. " +
		"Defaults to using the path as an ID.",
		parameter = "name")
@Option(name = "id-digest-method", description = "For calculating document ID digests, where applicable depending on " +
		"the ID method.", parameter = "name")
@Option(name = "charset", description = "The character set for document attributes stored in the queue.", parameter =
		"name")
public class QueueTask extends MonitorableTask<Long> {

	@Override
	public Long run(final String[] paths) throws Exception {
		if (null == paths || paths.length == 0) {
			throw new IllegalArgumentException("You must pass the paths to scan on the command line.");
		}

		final DocumentFactory factory = new DocumentFactory().configure(options);

		try (final DocumentQueue queue = new DocumentQueueFactory(options)
				.withDocumentFactory(factory)
				.createShared()) {
			return queue(new Scanner(factory, queue, null, monitor).configure(options), paths);
		}
	}

	@Override
	public Long run() throws Exception {
		final String[] paths = new String[1];

		paths[0] = ".";
		return run(paths);
	}

	/**
	 * Submit the given list of paths to the scanner.
	 *
	 * @param scanner the scanner to submit paths to
	 * @param paths the paths to scan
	 * @throws InterruptedException if interrupted while waiting for a scan to complete
	 * @throws ExecutionException if an exception occurs while scanning
	 */
	private long queue(final Scanner scanner, final String... paths) throws InterruptedException, ExecutionException {

		// Block until each scan has completed. These jobs will complete in serial or parallel depending on the
		// executor used by the scanner.
		for (Future<Path> scan : scanner.scan(paths)) {
			scan.get();
		}

		// Shut down the scanner and block until the scanning of each directory has completed in serial.
		// Note that each scan operation is completed in serial before this point is reached, so only a short timeout
		// is needed.
		scanner.shutdown();
		scanner.awaitTermination(1, TimeUnit.MINUTES);
		return scanner.queued();
	}
}
