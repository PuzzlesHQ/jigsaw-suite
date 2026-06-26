package dev.puzzleshq.jigsaw.bytecode.extension.format;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EnumExtensionFormat {

    static final Pattern INLINE_COMMENT_PATTERN = Pattern.compile("#[^\n]+");
    static final Pattern MULTILINE_COMMENT_PATTERN = Pattern.compile("\\*[^*]+\\*");

    public static List<EnumsExtensionEntry> parseInjector(final String code) {
        String commentPass1 = INLINE_COMMENT_PATTERN.matcher(code).replaceAll("");
        String commentPass2 = MULTILINE_COMMENT_PATTERN.matcher(commentPass1).replaceAll("").trim();

        EnumExtensionFormat.Lexer lexer = new EnumExtensionFormat.Lexer(commentPass2);
        EnumExtensionFormat.Parser parser = new EnumExtensionFormat.Parser(lexer);

        return parser.parse();
    }

    static final class Lexer {

        byte[] bytes;
        int index;

        public Lexer(String source) {
            this.bytes = source.getBytes(StandardCharsets.US_ASCII);
            this.index = 0;
        }

        public Token getNextToken() {
            if (bytes.length == 0 || index >= bytes.length) return Token.EOF_TOKEN;

            char curChar = ((char)bytes[index]);

            while (curChar == ' ' || curChar == '\n' || curChar == '\r' || curChar == '\t'){
                index++;
                curChar = ((char)bytes[index]);
            }

            Token token = null;

            switch (curChar) {
                case '[': {
                    token = Token.L_BRACKET_TOKEN;
                    break;
                }
                case ']': {
                    token = Token.R_BRACKET_TOKEN;
                    break;
                }
                case ',': {
                    token = Token.COMMA_TOKEN;
                    break;
                }
                case '/': {
                    token = Token.FORWARD_SLASH_TOKEN;
                    break;
                }
                case '<': {
                    index++;
                    if (bytes[index] != '-')
                        throw new RuntimeException("Invalid arrow found at index: " + index + ", '-' must appear after '<', not " + ((char)bytes[index]));
                    token = Token.L_ARROW_TOKEN;
                    break;
                }
            }

            if (token != null) {
                index++;
                return token;
            }


            int lastIndex = index;
            StringBuilder contents = new StringBuilder();
            while (
                    (
                        'A' <= curChar && curChar <= 'Z' ||
                        'a' <= curChar && curChar <= 'z' ||
                        '0' <= curChar && curChar <= '9' ||
                        '_' == curChar
                    ) && index < bytes.length
            ) {
                contents.append(curChar);

                index++;
                if (index < bytes.length)
                    curChar = ((char)bytes[index]);
            }

            if (lastIndex == index)
                throw new RuntimeException("Invalid character found at index: " + index + ", '" + ((char)bytes[index]) + "'");
            else
                return new Token(TokenType.IDENTIFIER, contents.toString());
        }

    }

    static final class Token {

        public static final Token EOF_TOKEN = new Token(TokenType.EOF);
        public static final Token L_BRACKET_TOKEN = new Token(TokenType.L_BRACKET);
        public static final Token R_BRACKET_TOKEN = new Token(TokenType.R_BRACKET);
        public static final Token L_ARROW_TOKEN = new Token(TokenType.L_ARROW);
        public static final Token COMMA_TOKEN = new Token(TokenType.COMMA);
        public static final Token FORWARD_SLASH_TOKEN = new Token(TokenType.FORWARD_SLASH);

        private final TokenType tokenType;
        private final String contents;

        public Token(TokenType tokenType) {
            this.tokenType = tokenType;
            this.contents = null;
        }

        public Token(TokenType tokenType, String contents) {
            this.tokenType = tokenType;
            this.contents = contents;
        }

        public TokenType getTokenType() {
            return this.tokenType;
        }

        public String getContents() {
            return contents;
        }

        @Override
        public String toString() {
            return "Token { type: '" + tokenType + ((contents == null) ? "' }" : "', contents: '" + contents + "' }");
        }
    }

    enum TokenType {
        L_BRACKET, R_BRACKET,
        L_ARROW, COMMA,
        IDENTIFIER, FORWARD_SLASH,
        EOF
    }

    public static final class EnumsExtensionEntry {

        private final List<String> classes;
        private final List<String> enums;

        public EnumsExtensionEntry(
                List<String> classes,
                List<String> enums
        ) {
            this.classes = classes;
            this.enums = enums;
        }

        public List<String> getClasses() {
            return classes;
        }

        public List<String> getEnums() {
            return enums;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (classes.size() == 1) builder.append(classes.get(0));
            else builder.append(classes);
            builder.append(" <- ");
            if (enums.size() == 1) builder.append(enums.get(0));
            else builder.append(enums);
            return builder.toString();
        }
    }

    static final class Parser {

        private final Lexer lexer;
        private Token currentToken;

        private final StringBuffer stringBuffer = new StringBuffer();

        public Parser(Lexer lexer) {
            this.lexer = lexer;
        }

        private Token next() {
            return currentToken = lexer.getNextToken();
        }

        private Token expect(TokenType tokenType) {
            if (currentToken.getTokenType() != tokenType) {
                throw new RuntimeException("Expected: " + tokenType + ", got Actual: " + currentToken.getTokenType());
            }

            Token last = currentToken;
            next();
            return last;
        }

        private String nextClassString() {
            stringBuffer.delete(0, stringBuffer.length());

            stringBuffer.append(expect(TokenType.IDENTIFIER).contents);

            while (currentToken.getTokenType() == TokenType.FORWARD_SLASH) {
                next();
                stringBuffer.append("/").append(currentToken.contents);
                next();
            }

            return stringBuffer.toString();
        }

        private List<String> nextClassArray() {
            List<String> strings = new ArrayList<>();

            expect(TokenType.L_BRACKET);

            if (currentToken == Token.R_BRACKET_TOKEN)
                return strings;

            strings.add(nextClassString());

            while (currentToken.getTokenType() == TokenType.COMMA) {
                next();
                String classString = nextClassString();
                strings.add(classString);
            }

            expect(TokenType.R_BRACKET);

            return strings;
        }

        private String nextEnumString() {
            stringBuffer.delete(0, stringBuffer.length());

            stringBuffer.append(expect(TokenType.IDENTIFIER).contents);

            return stringBuffer.toString();
        }

        private List<String> nextEnumArray() {
            List<String> strings = new ArrayList<>();

            expect(TokenType.L_BRACKET);

            if (currentToken == Token.R_BRACKET_TOKEN)
                return strings;

            strings.add(nextEnumString());

            while (currentToken.getTokenType() == TokenType.COMMA) {
                next();
                String enumString = nextEnumString();
                strings.add(enumString);
            }

            expect(TokenType.R_BRACKET);

            return strings;
        }

        private List<String> nextClass() {
            switch (currentToken.getTokenType()) {
                case L_BRACKET: return nextClassArray();
                case IDENTIFIER: return new ArrayList<String>() {{
                        add(nextClassString());
                    }};
                default: {
                    throw new RuntimeException("Did not expect token: " + currentToken);
                }
            }
        }

        private List<String> nextEnum() {
            switch (currentToken.getTokenType()) {
                case L_BRACKET: return nextEnumArray();
                case IDENTIFIER: return new ArrayList<String>() {{
                    add(nextEnumString());
                }};
                default: {
                    throw new RuntimeException("Did not expect token: " + currentToken);
                }
            }
        }

        private EnumsExtensionEntry nextInjectionEntry() {
            List<String> classes = nextClass();
            expect(TokenType.L_ARROW);
            List<String> enums = nextEnum();

            return new EnumsExtensionEntry(classes, enums);
        }

        public List<EnumsExtensionEntry> parse() {
            next();

            List<EnumsExtensionEntry> entries = new ArrayList<>();

            while (currentToken.getTokenType() != TokenType.EOF) {
                entries.add(nextInjectionEntry());
            }

            stringBuffer.delete(0, stringBuffer.length());

            return entries;
        }

    }

}
