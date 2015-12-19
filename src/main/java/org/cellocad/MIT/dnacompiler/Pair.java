/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cellocad.MIT.dnacompiler;


/**
 *
 * @author prash
 */

import lombok.Getter;
import lombok.Setter;


public class Pair {

    @Getter @Setter private Double _x;
    @Getter @Setter private Double _y;
    
    public Pair(Double _x, Double _y)
    {
        this._x = _x;
        this._y = _y;
    }
}
