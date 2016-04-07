package at.jku.learning.movierating.plot;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import at.jku.learning.movierating.prediction.PrecisePredictor;

public class GnuPlot {
	
	public static void writeGnuPlot(List<Map.Entry<PrecisePredictor, Double>> data, String filename, String title) throws IOException {
		int numOfColumns = data.size();
		double minYValue = Double.POSITIVE_INFINITY;
		double maxYValue = 0;
		
		// gnu plot data
		try (FileWriter fw = new FileWriter(filename + "data.dat", false)) {
			// column titles
			fw.write("-");
			for (Map.Entry<PrecisePredictor, Double> item : data) {
				fw.write(" \"" + item.getKey().toString() + "\"");
			}
			fw.write(System.lineSeparator());
			// data
			fw.write("-"); // this would be the name of the data set (if there were more than one)
			for (Map.Entry<PrecisePredictor, Double> item : data) {
				fw.write(" " + item.getValue().toString());
				
				if (item.getValue() < minYValue) {
					minYValue = item.getValue();
				}
				if (item.getValue() > maxYValue) {
					maxYValue = item.getValue();
				}
			}
			fw.write(System.lineSeparator());
		}
		
		int widthCM = 28;
		// approximate 0.5cm for every column title; definitely not exact, but can be changed afterwards anyway
		int heightCM = 15 + (numOfColumns / 2) * 1;
		
		// actual gnu plot script
		try (FileWriter fw = new FileWriter(filename + ".plt", false)) {
			fw.write(
					"set term pdf font \"Arial,22\" size " + widthCM + "cm," + heightCM + "cm" + System.lineSeparator() +
					"set output \"" + filename + ".pdf\"" + System.lineSeparator() +
					"set title \"" + title + "\"" + System.lineSeparator() +
					"set xtic rotate by -45 scale 0 font \",20\"" + System.lineSeparator() +
					"set grid" + System.lineSeparator() +
					"set key outside above center reverse Left" + System.lineSeparator() +
					"set datafile missing \"-\"" + System.lineSeparator() +
					"load \"colors-qualitative.gnuplot\"" + System.lineSeparator() +
					"set yrange [" + (minYValue - 0.1) + ":" + (maxYValue + 0.1) + "]" + System.lineSeparator() +
					"set format y \"%1.2f\"" + System.lineSeparator() +
					"#set ytics 0.1" + System.lineSeparator() +
					"set style data histograms" + System.lineSeparator() +
					"set style histogram clustered gap 1" + System.lineSeparator() +
					"set style fill solid border -1" + System.lineSeparator() +
					"plot for [k=0:" + (numOfColumns - 1) + "] \"" + filename + "data.dat\" using (column(2+k*1)):xticlabels(1) title columnheader(2+k*1) linestyle k+1" + System.lineSeparator()
			);
		}
	}
	
}