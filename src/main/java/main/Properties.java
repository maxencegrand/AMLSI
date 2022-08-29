/**
 *
 */
package main;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * This class allows to read properties files
 * @author Maxence Grand
 *
 */
public class Properties {
    /**
     * The separator
     */
	private static final String separator="::";
    /**
     * The seed id
     */
	private static final String seeds_id = "seeds";
    /**
     * The noise level id
     */
	private static final String noise_id = "noise_threshold";
    /**
     * The partial level id
     */
	private static final String partial_id = "partial_threshold";
	/**
	 * All seeds
	 */
	private static long[] seeds;
	/**
	 * All partial levels
	 */
	private static float[] partial;
	/**
	 * All noise levels
	 */
	private static float[] noise;
	/**
	 * Getter of seeds
	 * @return the seeds
	 */
	public static long[] getSeeds() {
		return seeds;
	}
	/**
	 * Getter of partial
	 * @return the partial
	 */
	public static float[] getPartial() {
		return partial;
	}
	/**
	 * Getter of noise
	 * @return the noise
	 */
	public static float[] getNoise() {
		return noise;
	}

	/**
	 * Read file properties
	 * @param file
	 */
	public static void read(String file) {
		try (InputStream input = new FileInputStream(file)) {

            java.util.Properties prop = new java.util.Properties();

            // load a properties file
            prop.load(input);

            //Set seeds
            String[] str_seed = prop.getProperty(Properties.seeds_id).split(
            											Properties.separator);
            Properties.seeds =  new long[str_seed.length];
            for(int i = 0; i < str_seed.length; i++) {
            	Properties.seeds[i] = Long.parseLong(str_seed[i]);
            }

            //Set Partial
            String[] str_partial = prop.getProperty(Properties.partial_id).split(
            											Properties.separator);
            Properties.partial =  new float[str_partial.length];
            for(int i = 0; i < str_partial.length; i++) {
            	Properties.partial[i] = Float.parseFloat(str_partial[i]);
            }

            //Set Noise
            String[] str_noise = prop.getProperty(Properties.noise_id).split(
            											Properties.separator);
            Properties.noise =  new float[str_noise.length];
            for(int i = 0; i < str_noise.length; i++) {
            	Properties.noise[i] = Float.parseFloat(str_noise[i]);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
}
