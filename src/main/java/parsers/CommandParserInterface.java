package parsers;

/** The interface the CommandParser must implement
 *  Some languages might require parsers that are very different from for example the english parser
 *
 *  This interface makes it possible to implement parsers for any language
 *  without having to change any existing parsers
 * */
public interface CommandParserInterface {

    String parseCommand(String command);

}
