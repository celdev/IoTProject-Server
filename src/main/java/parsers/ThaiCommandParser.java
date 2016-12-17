package parsers;

import java.util.Optional;

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
                .setEqualWords("น้อย", "มาก", "เป็น")
                .build();
    }
}
