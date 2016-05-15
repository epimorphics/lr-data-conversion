package com.epimorphics.lr.data.ukhpi;

/**
 * A filter that will return true or false given a PPDCSVLine.
 * 
 * @author bwm
 *
 */
public interface UKHPICSVLineFilter {
	public boolean test(UKHPICSVLine line);
}
