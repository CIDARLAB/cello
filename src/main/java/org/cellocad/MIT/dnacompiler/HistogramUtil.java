package org.cellocad.MIT.dnacompiler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Useful static functions for histogram-based scoring.
 *
 */
public class HistogramUtil {


    /**
     *
     * Allows histogram scoring from discrete input RPU values
     *
     * Uses a representative cytometry distribution, and centers the distribution at the desired mean.
     *
     */
    public static ArrayList<Double> getDefaultHistgramAtSpecifiedMean(double log_mean, String file_name_default) {

        ArrayList<Double> histogram = new ArrayList<Double>();

        ArrayList<String> rpus = Util.fileLines(file_name_default);

        double total_logrpu = 0.0;


        for (int r = 0; r < rpus.size(); ++r) {
            double rpu = Double.valueOf(rpus.get(r));
            double logrpu = Math.log10(rpu);
            total_logrpu += logrpu;
        }


        double avg_logrpu = total_logrpu / rpus.size();

        for (int r = 0; r < rpus.size(); ++r) {
            double rpu = Double.valueOf(rpus.get(r));
            double logrpu = Math.log10(rpu) - avg_logrpu + log_mean; //histogram centered at input value
            histogram.add(logrpu);
        }

        return histogram;
    }



    /**
     *
     * Interpolate cytometry data
     *
     */
    public static void interpolateTransferFunctionTitrations(String gate_name, GateLibrary gate_library) {

        Gate g = gate_library.get_GATES_BY_NAME().get(gate_name);
        HistogramBins hbins = g.get_histogram_bins();

        String var = "";
        if(g.get_variable_thresholds().keySet().size() == 1) {
            ArrayList<String> vars = new ArrayList<String>(g.get_variable_thresholds().keySet());
            var = vars.get(0);
        }
        else {
            return;
        }

        HistogramXfer xfer_hist = g.get_xfer_hist();

        ArrayList<double[]> xfer_interp = new ArrayList<double[]>();
        ArrayList<double[]> xfer_normal = g.get_xfer_hist().get_xfer_binned();

        for(int i=0; i<hbins.get_LOG_BIN_CENTERS().length; ++i) { //scanning x
            double x = hbins.get_LOG_BIN_CENTERS()[i];
            double rpu_x = Math.pow(10, x);

            HashMap<String, Double> variables = new HashMap<String, Double>();
            variables.put(var, rpu_x);

            double rpu_y = ResponseFunction.computeOutput(variables, g.get_params(), g.get_equation());
            double hill_mean = Math.log10(rpu_y);
            int hill_mean_bin = 0;
            for(int ii=0; ii<hbins.get_NBINS(); ++ii) {
                hill_mean_bin = ii;
                if(hbins.get_LOG_BIN_CENTERS()[ii] > hill_mean) {
                    break;
                }
            }

            if(x < xfer_hist.get_xfer_titration().get(0)) {
                double[] interp = new double[hbins.get_NBINS()];

                for(int ii=0; ii<xfer_normal.get(0).length; ++ii) {
                    double z = xfer_normal.get(0)[ii];
                    interp[ii] = z;
                }
                int current_mean_bin = HistogramUtil.bin_median(interp);
                int delta =  - current_mean_bin + hill_mean_bin;

                double[] hill_interp = new double[hbins.get_NBINS()];
                for(int y=0; y<hbins.get_NBINS(); ++y) {
                    if(y - delta < 0 || y - delta >= hbins.get_NBINS())
                        hill_interp[y] = 0.0;
                    else {
                        hill_interp[y] = interp[y-delta];
                    }
                }
                double[] hill_normal = HistogramUtil.normalize(hill_interp);
                xfer_interp.add(hill_normal);
            }
            else if(x > Math.log10(xfer_hist.get_xfer_titration().get( xfer_hist.get_xfer_titration().size()-1 ))) {

                double[] interp = new double[hbins.get_NBINS()];

                for(int ii=0; ii<xfer_normal.get(xfer_normal.size()-1 ).length; ++ii) {
                    double z = xfer_normal.get(xfer_normal.size()-1)[ii];
                    interp[ii] = z;
                }

                int current_mean_bin = HistogramUtil.bin_median(interp);
                int delta =  - current_mean_bin + hill_mean_bin;

                double[] hill_interp = new double[hbins.get_NBINS()];
                for(int y=0; y<hbins.get_NBINS(); ++y) {
                    if(y - delta < 0 || y - delta >= hbins.get_NBINS())
                        hill_interp[y] = 0.0;
                    else {
                        hill_interp[y] = interp[y-delta];
                    }
                }
                double[] hill_normal = HistogramUtil.normalize(hill_interp);
                xfer_interp.add(hill_normal);

            }
            else {
                int indx_low = 0;
                int indx_high = 11;

                for(int j=1; j<xfer_hist.get_xfer_titration().size(); ++j) {
                    if(Math.log10(xfer_hist.get_xfer_titration().get(j)) > x) {
                        indx_high = j;
                        break;
                    }
                }
                indx_low = indx_high - 1;

                //x
                //y is bin
                //z is counts

                double x1 = Math.log10(xfer_hist.get_xfer_titration().get(indx_low));
                double x2 = Math.log10(xfer_hist.get_xfer_titration().get(indx_high));
                double weight = (x - x1) / (x2 - x1);

                int median_bin_y1 = HistogramUtil.bin_median(xfer_normal.get(indx_low));
                int median_bin_y2 = HistogramUtil.bin_median(xfer_normal.get(indx_high));

                Double med_bin_interp = median_bin_y1 * (1 - weight) + median_bin_y2 * weight;
                Integer median_bin_interp = med_bin_interp.intValue();

                /////////////////////////////////////////////////
                median_bin_interp = hill_mean_bin;
                /////////////////////////////////////////////////

                double[] interp = new double[hbins.get_NBINS()];

                for(int y=0; y<hbins.get_LOG_BIN_CENTERS().length; ++y) { //scanning x

                    int delta = median_bin_interp - y;

                    int y1 = median_bin_y1 - delta;
                    int y2 = median_bin_y2 - delta;

                    double z1 = 0.0; //counts, low boundary
                    double z2 = 0.0; //counts, high boundary

                    if(y1 >= 0 && y1 < hbins.get_NBINS()) {
                        z1 = xfer_normal.get(indx_low)[y1];
                    }
                    if(y2 >= 0 && y2 < hbins.get_NBINS()) {
                        z2 = xfer_normal.get(indx_high)[y2];
                    }

                    double z = z1 * (1 - weight) + z2 * weight;
                    interp[y] = z;

                    //double weighted_avg = y1 * (1 - weight) + y2 * weight;
                    //interp[ii] = weighted_avg;
                }
                double[] hill_normal = HistogramUtil.normalize(interp);
                xfer_interp.add( hill_normal );
            }
        }

        String all = "";
        for(int i=0; i<hbins.get_NBINS(); ++i) { //print 500x500 matrix
            String row = "";
            for(int j=0; j<xfer_interp.size(); ++j) {
                row += xfer_interp.get(j)[i] + " ";
            }
            all += row + "\n";
        }

        //write square matrix to text file
        //String outname = "matrix_xfer_" + g.Name + ".txt";
        //Util.fileWriter(Args.output_directory + outname, all, false);


        xfer_hist.set_xfer_interp(xfer_interp);
        g.set_xfer_hist(xfer_hist);

        //make sure each titration sums to 1.000
        /*for (int i = 0; i < hbins.get_NBINS(); ++i) {
            double sum = 0.0;
            for(int ii=0; ii<hbins.get_NBINS(); ++ii) {
                sum += xfer_interp.get(i)[ii];
            }
            Print.message(2, "interp sum " + sum);
        }*/

    }


    public static void getTransferFunctionHistogramTitrations(String gate_name, GateLibrary gate_library, HistogramBins hbins, String filepath) {
        //Print.message(2, "getting transfer function histogram titrations for " + gate_name);

        Gate g = gate_library.get_GATES_BY_NAME().get(gate_name);

        HistogramXfer xfer_hist = g.get_xfer_hist();

        //read raw data
        ArrayList<ArrayList<Double>> xfer_data_raw =  new ArrayList< ArrayList<Double> >();
        for(int i=1; i<=12; ++i) {
            xfer_data_raw.add( new ArrayList<Double>() );

            String file_name = filepath + g.name + "/rpus_" + g.name + "_" + String.format("%02d", i) + ".txt";
            //System.out.println("ls " + file_name);

            File f = new File(file_name);
            if(!f.exists()) {
                System.out.println("cannot find file " + file_name);
                System.exit(-1);
            }

            ArrayList<String> rpu_lines = Util.fileLines(file_name);
            for (String r : rpu_lines) {
                double logrpu = Math.log10(Double.valueOf(r));
                xfer_data_raw.get(i - 1).add(logrpu);
            }
        }
        xfer_hist.set_xfer_data_raw( xfer_data_raw );

        //bin
        ArrayList<double[]> xfer_binned = new ArrayList<double[]>();

        for(int i=0; i<12; ++i) {
            double[] binned = HistogramUtil.placeDataIntoBins(xfer_hist.get_xfer_data_raw().get(i), hbins);
            xfer_binned.add(binned);
        }
        xfer_hist.set_xfer_binned( xfer_binned );

    }


    /**
     * Not in use.
     *
     * generic function to perform binning to generate a histogram from a list of numbers.
     * Instead, see placeDataIntoBins.
     *
     */
    public static double[] calcHistogram(ArrayList<Double> data, double min, double max, int numBins, boolean logrpu) {

        final double[] result = new double[numBins];
        final double binSize = (max - min)/numBins;

        for (double d : data) {

            if(logrpu) {
                d = Math.log10(d);
            }

            int bin = (int) ((d - min) / binSize);

            if (bin < 0) { /* this data is smaller than min */ }
            else if (bin >= numBins) { /* this data point is bigger than max */ }
            else {
                result[bin] += 1.0;
            }
        }
        return result;
    }

    /**
     * normalize histogram, sum of all fractional counts = 1.
     *
     */
    public static double[] normalize(double[] data) {

        double[] norm = new double[ data.length ];
        double total_sum = 0.0;

        for(int i=0; i<data.length; ++i) {
            total_sum += data[i];
        }

        for(int i=0; i<data.length; ++i) {
            norm[i] = data[i]/total_sum;
        }

        return norm;
    }


    //returns RPU, not log(RPU)
    public static double median(ArrayList<Double> m) {

        Collections.sort(m);

        int middle = m.size()/2;

        if (m.size()%2 == 1)
            return m.get(middle);

        else
            return (m.get(middle-1) + m.get(middle)) / 2.0;

    }

    //returns RPU, not log(RPU)
    public static double median(double[] data, HistogramBins hbins) {

        double total_sum = 0.0;

        for(int i=0; i<data.length; ++i) {
            total_sum += data[i];
        }

        double halfway = total_sum / 2;
        double current = 0.0;

        int bin = 0;
        for(int i=0; i<data.length; ++i) {
            current += data[i];
            if(current >= halfway) {
                break;
            }
            ++bin;
        }

        return Math.pow(10, hbins.get_LOG_BIN_CENTERS()[bin]);
    }

    /**
     *
     * returns bin index of median
     *
     */
    public static int bin_median(double[] data) {

        double total_sum = 0.0;

        for(int i=0; i<data.length; ++i) {
            total_sum += data[i];
        }

        double halfway = total_sum / 2;
        double current = 0.0;

        int bin = 0;
        for(int i=0; i<data.length; ++i) {
            current += data[i];
            if(current >= halfway) {
                break;
            }
            ++bin;
        }

        return bin;
    }

    /**
     *
     * Given an RPU, find the bin index of that RPU in the histogram
     *
     */
    public static int bin_of_logrpu(double logrpu, HistogramBins hbins) {

        for(int i=0; i<hbins.get_LOG_BIN_CENTERS().length; ++i) {

            if(hbins.get_LOG_BIN_CENTERS()[i] >= logrpu) {
                return i;
            }
        }

        return hbins.get_NBINS() - 1;
    }


    /**
     *
     * convert list of numbers to histogram
     *
     */
    public static double[] placeDataIntoBins(ArrayList<Double> data, HistogramBins hbins) {

        final double[] result = new double[hbins.get_NBINS()];

        final double binSize = (hbins.get_LOGMAX() - hbins.get_LOGMIN())/hbins.get_NBINS();

        for (double d : data) {
            int bin = (int) ((d - hbins.get_LOGMIN()) / binSize);
            if (bin < 0) { /* this data is smaller than min */ }
            else if (bin >= hbins.get_NBINS()) { /* this data point is bigger than max */ }
            else {
                result[bin] += 1.0;
            }
        }

        return result;
    }

    /**
     *
     * translate a histogram left or right to the specified median
     *
     * compute the bin distance (# bins) separating the current median and new median
     * shift fractional counts by that # of bins
     * renormalize (in case tails are chopped off)
     *
     */
    public static double[] normalizeHistogramToNewMedian(double[] histogram, Double new_median, HistogramBins hbins) {

        int bin_median = HistogramUtil.bin_median(histogram);
        int shift = bin_median - HistogramUtil.bin_of_logrpu(Math.log10(new_median), hbins);
        double[] shifted_histogram = new double[hbins.get_NBINS()];

        for(int bin=0; bin<hbins.get_NBINS(); ++bin) {
            int shifted_bin = bin - shift;
            if(shifted_bin > 0 && shifted_bin < hbins.get_NBINS()) {
                shifted_histogram[shifted_bin] = histogram[bin];
            }
        }

        HistogramUtil.normalize(shifted_histogram);

        return shifted_histogram;
    }

};
