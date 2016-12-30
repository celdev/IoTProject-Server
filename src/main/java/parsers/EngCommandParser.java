package parsers;

import java.util.Optional;


/** This class contains the method for building and returning an
 *  Optional CommandParser for the English language
 * */
public class EngCommandParser extends CommandParser {

    private EngCommandParser(Builder builder) {
        super(builder);
    }

    public static Optional<CommandParser> commandParserBuilder() {
        Builder builder = new Builder();
        return builder
                .setConditionWords(" when ", " if ")
                .setOnWords("turn on")
                .setOffWords("turn off")
                .setNameOfUnits("light", "heat")
                .setIfWord(" if ")
                .setComparisonWords(" less than ", "greater than", " is ")
                .build();
    }
}
