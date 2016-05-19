package at.jku.learning.movierating.prediction.test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

import at.jku.learning.movierating.prediction.PrecisePredictor;
import at.jku.learning.movierating.prediction.Predictor;

public class ConfigTester {
	
	private final PrintStream out;

	public ConfigTester() {
		this.out = null;
	}
	
	public ConfigTester(PrintStream out) {
		this.out = out;
	}
	
	public List<Entry<Predictor, Double>> testConfigs(PredictorTester tester, Predictor... predictors) {
		long startTime = System.currentTimeMillis();
		
		int threadPoolSize = Runtime.getRuntime().availableProcessors();
		if (out != null) {
			out.println("ConfigTester: total tasks = " + predictors.length + ", using " + threadPoolSize + " threads");
		}
		final AtomicInteger doneTasks = new AtomicInteger();
		ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
		List<FutureTask<List<Map.Entry<Predictor, Double>>>> tasks = new ArrayList<>();
		
		for (Predictor p : predictors) {
			FutureTask<List<Map.Entry<Predictor, Double>>> task = new FutureTask<>(new Callable<List<Map.Entry<Predictor, Double>>>() {
				@Override
				public List<Entry<Predictor, Double>> call() throws Exception {
					List<Entry<Predictor, Double>> data = null;
					// make sure we don't crash; if there is an exception, just exclude this
					// predictor by returning null
					Exception ex = null;
					try {
						data = tester.comparePredictors(p);
					} catch (Exception e) {
						data = null;
						ex = e;
					}
					doneTasks.incrementAndGet();
					if (out != null) {
						out.print("ConfigTester: " + doneTasks + "/" + predictors.length);
						if (ex != null) {
							out.println(" - failed:");
							ex.printStackTrace(out);
						} else {
							out.println();
						}
					}
					// null in case of exception
					return data;
				}
			});
			executor.execute(task);
			tasks.add(task);
		}
        
        List<Map.Entry<Predictor, Double>> result = new ArrayList<>();
		for (FutureTask<List<Map.Entry<Predictor, Double>>> task : tasks) {
			List<Entry<Predictor, Double>> singleResult = null;
			try {
				singleResult = task.get();
			} catch (InterruptedException | ExecutionException e) {
				singleResult = null;
				if (out != null) {
					out.println("getting task result failed:");
					e.printStackTrace(out);
				}
			}
			// only add the result if it completed successfully (that is, the call "task.get()"
			// itself succeeded as well the executed code ---> see comment above)
			if (singleResult != null) {
				result.addAll(singleResult);
			}
		}
        executor.shutdown();
        
        if (out != null) {
	        long duration = System.currentTimeMillis() - startTime;
			out.println("ConfigTester: completed in " + duration + " ms");
        }
		
		return result;
	}
	
}