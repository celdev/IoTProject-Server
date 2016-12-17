import java.util.Optional;

class SweCommandParser extends CommandParser {

    private SweCommandParser(Builder builder) {
        super(builder);
    }

    static Optional<CommandParser> SwedishCommandParserBuilder() {
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