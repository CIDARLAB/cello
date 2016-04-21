package org.cellocad.MIT.dnacompiler;

import lombok.Getter;
import lombok.Setter;

public class HistogramBins {

    
    @Getter @Setter private double _LOGMAX;  //3.0;  // RPU = 1000
    @Getter @Setter private double _LOGMIN;  //-3.0; // RPU = 0.001
    @Getter @Setter private int _NBINS;      //250;

    @Getter @Setter private double[] _LOG_BIN_CENTERS; //double rpu_value = _LOG_BIN_CENTERS[20]
    @Getter @Setter private double _LOG_BIN_WIDTH;

    public void init() {

        _LOGMAX =  3.0;  //RPU = 10^3
        _LOGMIN = -3.0;  //RPU = 10^-3
        _NBINS = 250;

        //set the bin values based on the max, min, and number of bins.
        _LOG_BIN_CENTERS = new double[_NBINS];
        _LOG_BIN_WIDTH = (_LOGMAX - _LOGMIN) / _NBINS;

        for(int i=0; i<_NBINS; ++i) {
            _LOG_BIN_CENTERS[i] = _LOGMIN + i*_LOG_BIN_WIDTH;
        }
    }
}
