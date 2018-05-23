package runners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

public class ParallelRunners extends BlockJUnit4ClassRunner {

	public ParallelRunners(Class<?> klass) throws InitializationError {
		super(klass);
		// criando o Runner paralelo...
		setScheduler(new ThreadPoll());
	}
	
	private static class ThreadPoll implements RunnerScheduler {

		private ExecutorService executorService;
		
		public ThreadPoll() {
			executorService = Executors.newFixedThreadPool(5);
		}
		
		public void schedule(Runnable run) {
			executorService.submit(run);
		}

		public void finished() {
			executorService.shutdown();
			try {
				executorService.awaitTermination(10, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
	}

}
