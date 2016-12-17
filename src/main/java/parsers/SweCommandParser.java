package parsers;

import parsers.CommandParser;

import java.util.Optional;

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
                .setEqualWords(" mindre ", " mer ", " är ")
                .build();
    }
}