/**
 * 
 */
package com.deviget.minesweeperserver.api;

import java.util.List;

/**
 * @author fernando
 *
 */
public class MongoCustomConversions extends org.springframework.data.mongodb.core.convert.MongoCustomConversions {

	/**
	 * @param converters
	 */
	public MongoCustomConversions(List<?> converters) {
		super(converters);
	}

}
