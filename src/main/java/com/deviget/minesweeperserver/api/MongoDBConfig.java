/**
 * 
 */
package com.deviget.minesweeperserver.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import com.deviget.minesweeperserver.api.Game.Cell.Coordinates;

/**
 * @author fernando
 *
 */
@Configuration
public class MongoDBConfig {

	@Bean
	public MongoCustomConversions getCustomConversions() {
		@SuppressWarnings("rawtypes")
		List<Converter> customConv = new ArrayList<>();
		customConv.add(new GameCoordinatesConverter());
		customConv.add(new GameCoordinatesReverseConverter());
		return new MongoCustomConversions(customConv);
	}
	
	class GameCoordinatesConverter implements Converter<Game.Cell.Coordinates, String> {

		@Override
		public String convert(Coordinates source) {
			return source.getRow() + "_" + source.getColumn();
		}

	}
	
	class GameCoordinatesReverseConverter implements Converter<String, Game.Cell.Coordinates> {

		@Override
		public Coordinates convert(String source) {
			String[] coordsStr = source.split("_");
			short row = Short.parseShort(coordsStr[0]);
			short column = Short.parseShort(coordsStr[1]);
			return new Coordinates(row, column);
		}



	}
}
