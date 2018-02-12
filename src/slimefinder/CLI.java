package slimefinder;

import java.io.FileWriter;
import java.io.IOException;

import slimefinder.properties.*;

public class CLI {	
	/**
	 * Property file names
	 */
	static final String slimeProperties = "slime.properties";
	static final String searchProperties = "search.properties";
	static final String imageProperties = "image.properties";
	
	private static FileWriter w;
	private static SlimeProperties pSlime;
	private static SearchProperties pSearch;
	private static ImageProperties pImage;
	public static boolean verbose;
	private static final String ln = String.format("%n");
	
	public static void main(String[] args) throws Exception {	
		String arg;
		boolean images = false, search = false;
		for (int i = 0; i < args.length; i++) {
			arg = args[i];
			
			if (arg.equals("--verbose") || arg.equals("-v") ) {
				verbose = true;
			}
			
			if (arg.equals("--images") || arg.equals("-i") ) {
				images = true;
			}
			
			if (arg.equals("--search") || arg.equals("-s") ) {
				search = true;
			}
			
			if (arg.equals("--help") || arg.equals("-h")) {
				helpMessage();
				return;
			}
		}
		
		if (!images && !search) {
			helpMessage();
			return;
		}
		
		pSlime = new SlimeProperties();
		pSearch = new SearchProperties();
		pImage = new ImageProperties();
		
		long time;
		if (search) {
			System.out.println("Searching...");
			pSlime.loadProperties(slimeProperties);
			pSearch.loadProperties(searchProperties);
			w = new FileWriter(pSearch.outputDir, pSearch.append);
			Search s = new Search(pSearch, pSlime);
			
			int count = Math.max(pSearch.maxWidth * pSearch.maxWidth - pSearch.minWidth * pSearch.minWidth, 0);
			if (pSearch.thorough) count *= 256;
			if (count >= 10000000) System.out.println("Estimated time: " + formatTime(s.timeEstimate(100000) * count));
			System.out.println("Searching positions with " + 
					pSearch.minChunkSize + " <= chunkSize <= " + pSearch.maxChunkSize + " or " + 
					pSearch.minBlockSize + " <= blockSize <= " + pSearch.maxBlockSize + "." + ln);
			
			if (verbose) println(data("block-position", "chunk-position", "chunkSize", "blockSize"), "extrema", true, true, true);
			time = System.nanoTime();
			int successCount = s.search();
			time = System.nanoTime() - time;
			if (verbose) System.out.println();
			
			System.out.println("Checked " + count + " position" + ((count == 1) ? ". " : "s. ") + successCount + " matched the search criteria.");
			System.out.println("Took " + CLI.formatTime(time) + ((count > 0) ? " (" + time/count + " nanoseconds per position)." : "."));
			System.out.println("Found the following extrema:");
			println(data("block-position", "chunk-position", "chunkSize", "blockSize"), "extrema", true, true, true);
			println(CLI.data(s.minBlock), "(min blockSize)", true, false, false);
			println(CLI.data(s.maxBlock), "(max blockSize)", true, false, false);
			println(CLI.data(s.minChunk), "(min chunkSize)", true, false, false);
			println(CLI.data(s.maxChunk), "(max chunkSize)", true, false, false);
			w.close();
		}
		
		if (images) {
			if (search) System.out.println();
			System.out.println("Generating images...");
			pImage.loadProperties(imageProperties);
			Image d = new Image(pImage, pSlime);
			
			time = System.nanoTime();
			int count = d.drawImages();
			time = System.nanoTime() - time;
			
			System.out.println( "Generated " + count + " image" + ((count == 1) ? "" : "s") + ".");
			System.out.println("Took " + CLI.formatTime(time) + ((count > 0) ? " (" + time/count/1000000 + " milliseconds per image)." : "."));
		}
	}
	
	public static void helpMessage() {
		System.out.println("--help, -h");
		System.out.println("  Display this message");
		System.out.println();
		System.out.println("--images, -i");
		System.out.println("  Read positions from a file and draw images of them");
		System.out.println();
		System.out.println("--search, -s");
		System.out.println("  Search for positions with specific slime chunk patterns and save them to a file");
		System.out.println();
		System.out.println("--verbose, -v");
		System.out.println("  Print the found positions on the console when in search mode");
	}
	
	public static String formatTime(long nanos) {
		long millis = nanos / 1000000;
		long secs = millis / 1000;
		long mins = secs / 60;
		long hours = mins / 60;
		long days = hours / 24;
		
		String d = (days == 0) ? "" : days + "d";
		String h = (hours == 0) ? "" : hours % 24 + "h "; 
		String m = (mins == 0) ? "" : mins % 60 + "m "; 
		String s = (secs == 0) ? "0." : secs % 60 + "."; 
		String ms = String.format("%1$03d", millis % 1000) + "s";
		return d + h + m + s + ms;
	}
	
	/**
	 * Gives the mask data format of the given strings.
	 */
	private static String data(String... args) {
		String s1 = String.format("%1$-20s", args[0]);
		String s2 = String.format("%1$-20s", args[1]);
		String s3 = String.format("%1$-20s", args[2]);
		String s4 = String.format("%1$-12s", args[3]);
		return s1 + s2 + s3 + s4;
	}
	
	/**
	 * Gives the string representation for mask data.
	 */
	public static String data(Mask m) {
		String s1 = m.posBlock.toString();
		String s2 = m.posChunk.x + ":" + m.posIn.x + "," + m.posChunk.z + ":" + m.posIn.z;
		String s3 = m.getChunkSize() + "/" + m.getChunkSurfaceArea();
		String s4 = m.getBlockSize() + "/" + m.getBlockSurfaceArea();
		return data(s1, s2, s3, s4);
	}
	
	/**
	 * Writes a line on the console and the output file.
	 * @param str - the line to be written
	 * @param extremum - an additional string to be appended containing extremum information
	 * @param writeonConsole - should the line be written on console
	 * @param writeOnFile - should the line be written on the output file
	 * @param comment - should the line on the output file be commented. This should be done to every non-data line.
	 * @throws IOException
	 */
	public static void println(String str, String extremum, boolean writeonConsole, boolean writeOnFile, boolean comment) throws IOException {
		println(str + String.format("%1$-20s", extremum), writeonConsole, writeOnFile, comment);
	}
	
	/**
	 * Writes a line on the console and the output file.
	 * @param str - the line to be written
	 * @param writeonConsole - should the line be written on console
	 * @param writeOnFile - should the line be written on the output file
	 * @param comment - should the line on the output file be commented. This should be done to every non-data line.
	 * @throws IOException
	 */
	public static void println(String str, boolean writeonConsole, boolean writeOnFile, boolean comment) throws IOException {
		if (writeonConsole) System.out.println(str);
		if (writeOnFile) {
			str = str.trim();
			str = comment ? "#" + str : " " + str;
			w.write(str + ln);
			w.flush();
		}
	}
}
