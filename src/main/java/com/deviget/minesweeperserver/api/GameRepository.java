/**
 * 
 */
package com.deviget.minesweeperserver.api;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author fernando
 *
 */
public interface GameRepository extends MongoRepository<Game, String> {

}
