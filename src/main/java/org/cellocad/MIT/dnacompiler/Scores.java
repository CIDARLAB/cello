package org.cellocad.MIT.dnacompiler;
/**
 * Created by Bryan Der on 3/26/14.
 */

import lombok.Getter;
import lombok.Setter;

/**
 * Given the different options for score type, this provides a single API for getting a circuit score.
 */
public class Scores {

    public Scores() {}

    public Scores(Scores scores) { // copy constructor
        _onoff_ratio   = new Double( scores.get_onoff_ratio() );
        _conv_overlap  = new Double( scores.get_conv_overlap() );
        _noise_margin  = new Double( scores.get_noise_margin() );
        _snr   = new Double( scores.get_snr() );
        _dsnr  = new Double( scores.get_dsnr() );
        _noise_margin_contract = scores.is_noise_margin_contract();
    }


    /////////////////////////
    //
    // Getters and Setters
    //
    /////////////////////////

    public double get_score() {
        /*if(Args.circuit_score.equals("onoff_ratio")) {
            return _onoff_ratio;
        }
        else if(Args.circuit_score.equals("noise_margin")) {
            return _noise_margin;
        }
        else if(Args.circuit_score.equals("histogram")) {
            return _conv_overlap;
        }
        else {
            return _onoff_ratio;
        }*/

        return _onoff_ratio;
    }


    /////////////////////////
    //
    // Private member data
    //
    /////////////////////////


    @Getter @Setter private double _reg_onoff_ratio  = -1.0;
    @Getter @Setter private double _tp_onoff_ratio  = -1.0;

    @Getter @Setter private double _onoff_ratio  = -1.0;
    @Getter @Setter private double _conv_overlap = -1.0;
    @Getter @Setter private double _noise_margin = -1.0;
    @Getter @Setter private double _snr = -1.0;
    @Getter @Setter private double _dsnr = -1.0;

    //circuit can be asserted as success or failure based on noise margin contract
    //if one or more gates does not have a noise margin > 0, contract = false
    @Getter @Setter private boolean _noise_margin_contract = true;

};
