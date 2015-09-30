package com.epimorphics.lr.data.ppd.verification;

import java.util.Random;

import com.epimorphics.lr.data.ppd.PPDCSVLine;
import com.epimorphics.lr.data.ppd.PPDCSVLineFilter;

/**
 * Filter to select 1 rows in N at random
 * @author bwm
 *
 */
public class RandomFilter implements PPDCSVLineFilter {
	
	private final int frequency;
	Random random = new Random();
	private final int index;
	
	RandomFilter(int frequency) {
		this.frequency = frequency;
		index = random.nextInt(frequency);
	}

	@Override
	public boolean test(PPDCSVLine line) {
		return (random.nextInt(frequency) == index) ;
	}

}
