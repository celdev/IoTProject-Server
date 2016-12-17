import java.util.Optional;

class EngCommandParser extends CommandParser {

    private EngCommandParser(Builder builder) {
        super(builder);
    }

    static Optional<CommandParser> EnglishCommandParserBuilder() {
        Builder builder = new Builder();
        return builder
                .setConditionWords(" when ", " if ")
                .setOnWords("turn on")
                .setOffWords("turn off")
                .setNameOfUnits("light", "heat")
                .setIfWord(" if ")
                .setEqualWords(" less than ", "greater than", " is ")
                .build();
    }
}
