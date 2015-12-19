/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cellocad.MIT.dnacompiler;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author prash
 */

public class PairCyt {

    @Getter @Setter private Double _x;
    @Getter @Setter private List<Pair> _y = new ArrayList<Pair>();
    
    
    public PairCyt(Double _x, List<Pair> _y)
    {
        this._x = _x;
        this._y.addAll(_y);
    }
}
