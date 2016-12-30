package parsers;

import java.util.Optional;

/** This class contains the method for building and returning an
 *  Optional CommandParser for the Thai language
 * */
public class ThaiCommandParser extends CommandParser {

    private ThaiCommandParser(Builder builder) {
        super(builder);
    }

    public static Optional<CommandParser> commandParserBuilder() {
        Builder builder = new Builder();
        return builder
                .setConditionWords("เมื่อ", "ถ้า")
                .setOnWords("เปิด")
                .setOffWords("ปิด")
                .setNameOfUnits("ไฟ", "ความร้อน")
                .setIfWord("ถ้า")
                .setComparisonWords("น้อย", "มาก", "เป็น")
                .build();
    }
}
