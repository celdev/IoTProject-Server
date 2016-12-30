package parsers;

import java.util.Optional;


/** This class contains the method for building and returning an
 *  Optional CommandParser for the Swedish language
 * */
public class SweCommandParser extends CommandParser {

    private SweCommandParser(Builder builder) {
        super(builder);
    }

    public static Optional<CommandParser> commandParserBuilder() {
        Builder builder = new Builder();
        return builder
                .setConditionWords(" när ", " om ")
                .setOnWords("sätt på", "tänd")
                .setOffWords("stäng av", "släck")
                .setNameOfUnits("lampa", "värme")
                .setIfWord(" om ")
                .setComparisonWords(" mindre ", " mer ", " är ")
                .build();
    }
}