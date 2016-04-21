package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/***********************************************************************

 Synopsis    [ Stores the data for repressor transfer functions. ]

 _xfer_data_raw is read in from files.
 _xfer_interp is the interpolated 500x500 square matrix representing a probabilistic transfer function.

 ***********************************************************************/

public class HistogramXfer {


    /////////////////////////
    //
    // Private member data
    //
    /////////////////////////
    //for 12 titrations, the outer arraylist size will be 12.
    //The inner arraylist will typically be tens of thousands of values (fluorescence values normalized to RPU)
    @Getter @Setter private ArrayList< ArrayList<Double> > _xfer_data_raw = new ArrayList< ArrayList<Double> >();

    //titration data
    //derived from xfer_data_raw, the values are now counts that correspond to bins.
    //The indexes of double[] map to the indexes of HistogramBins._LOG_BIN_CENTERS
    @Getter @Setter private ArrayList<double[]> _xfer_binned;

    //the length is the same as the number of titrations, i.e. 12.
    //the values are the x-axis input RPU values that correspond to each titration
    @Getter @Setter private ArrayList<Double> _xfer_titration;

    //titration data interpolated based on _xfer_binned and _xfer_titration
    //this is the histogram-based transfer function
    //it's a square matrix, NBINS -by- NBINS
    @Getter @Setter private ArrayList<double[]> _xfer_interp = new ArrayList<double[]>();

}

