package environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class Helpers {
	
	/*
	 * Write a table to a csv file
	 * @param table A map of each string header to a column of data of the same length
	 * @param filename The path of the output csv file
	 */
	
	public static void writeCsvTable(Map<String, double[]> table, File filename) {
		String delimiter = ",";
		String[] headers = table.keySet().toArray(new String[table.keySet().size()]);
		assert headers.length > 0;
		int nrows = table.get(headers[0]).length;
		assert nrows > 0;
		
		FileWriter fileWriter = null;
		BufferedWriter bufferedWriter = null;
		try {
			fileWriter = new FileWriter(filename);
			bufferedWriter = new BufferedWriter(fileWriter);
			
			bufferedWriter.write(String.join(delimiter, headers));
			bufferedWriter.newLine();
			
			for (int i=0; i < nrows; i++) {
				for (int k=0; k < headers.length; k++) {
					bufferedWriter.write(String.format("%.4f", table.get(headers[k])[i]));
					if (k < (headers.length-1)) {
						bufferedWriter.write(delimiter);
					}
				}
				
				bufferedWriter.newLine();
			}
		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter");
			e.printStackTrace();
		} finally {
			try {
				bufferedWriter.close();
				fileWriter.close();
			} catch (Exception e2) {
				System.out.println("Error closing output file");
				e2.printStackTrace();
			}
		}
	}
}
