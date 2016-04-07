package at.jku.learning.movierating.prediction.test;

import java.io.IOException;
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

public class ConfigTester {
	
	private final PrintStream out;

	public ConfigTester() {
		this.out = null;
	}
	
	public ConfigTester(PrintStream out) {
		this.out = out;
	}
	
	public List<Entry<PrecisePredictor, Double>> testConfigs(PredictorTester tester, PrecisePredictor... predictors) throws IOException, InterruptedException, ExecutionException {
		long startTime = System.currentTimeMillis();
		
		int threadPoolSize = Runtime.getRuntime().availableProcessors();
		if (out != null) {
			out.println("ConfigTester: total tasks = " + predictors.length + ", using " + threadPoolSize + " threads");
		}
		final AtomicInteger doneTasks = new AtomicInteger();
		ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
		List<FutureTask<List<Map.Entry<PrecisePredictor, Double>>>> tasks = new ArrayList<>();
		
		for (PrecisePredictor p : predictors) {
			FutureTask<List<Map.Entry<PrecisePredictor, Double>>> task = new FutureTask<>(new Callable<List<Map.Entry<PrecisePredictor, Double>>>() {
				@Override
				public List<Entry<PrecisePredictor, Double>> call() throws Exception {
					List<Entry<PrecisePredictor, Double>> data = tester.comparePredictors(p);
					doneTasks.incrementAndGet();
					if (out != null) {
						out.println("ConfigTester: " + doneTasks + "/" + predictors.length);
					}
					return data;
				}
			});
			executor.execute(task);
			tasks.add(task);
		}
        
        List<Map.Entry<PrecisePredictor, Double>> result = new ArrayList<>();
		for (FutureTask<List<Map.Entry<PrecisePredictor, Double>>> task : tasks) {
			result.addAll(task.get());
		}
        executor.shutdown();
        
        if (out != null) {
	        long duration = System.currentTimeMillis() - startTime;
			out.println("ConfigTester: completed in " + duration + " ms");
        }
		
		return result;
	}
	
}