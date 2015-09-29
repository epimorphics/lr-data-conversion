package com.epimorphics.lr.data.ppd;

/**
 * A filter that will return true or false given a PPDCSVLine.
 * 
 * @author bwm
 *
 */
public interface PPDCSVLineFilter {
	public boolean test(PPDCSVLine line);
}
