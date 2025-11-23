package dev.puzzleshq.jigsaw.bytecode.inject.format;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class InterfaceInjectorFormat {

    static final Pattern INLINE_COMMENT_PATTERN = Pattern.compile("#[^\n]+");
    static final Pattern MULTILINE_COMMENT_PATTERN = Pattern.compile("\\*[^*]+\\*");

    public static List<InterfaceInjectorFormat.InjectionEntry> parseInjector(final String code) {
        String commentPass1 = INLINE_COMMENT_PATTERN.matcher(code).replaceAll("");
        String commentPass2 = MULTILINE_COMMENT_PATTERN.matcher(commentPass1).replaceAll("").trim();

        InterfaceInjectorFormat.Lexer lexer = new InterfaceInjectorFormat.Lexer(commentPass2);
        InterfaceInjectorFormat.Parser parser = new InterfaceInjectorFormat.Parser(lexer);

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
                        '0' <= curChar && curChar <= '9'
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

    public static final class InjectionEntry {

        private final List<String> classes;
        private final List<String> interfaces;

        public InjectionEntry(
                List<String> classes,
                List<String> interfaces
        ) {
            this.classes = classes;
            this.interfaces = interfaces;
        }

        public List<String> getClasses() {
            return classes;
        }

        public List<String> getInterfaces() {
            return interfaces;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (classes.size() == 1) builder.append(classes.get(0));
            else builder.append(classes);
            builder.append(" <- ");
            if (interfaces.size() == 1) builder.append(interfaces.get(0));
            else builder.append(interfaces);
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

        private InjectionEntry nextInjectionEntry() {
            List<String> classes = nextClass();
            expect(TokenType.L_ARROW);
            List<String> interfaces = nextClass();

            return new InjectionEntry(classes, interfaces);
        }

        public List<InjectionEntry> parse() {
            next();

            List<InjectionEntry> entries = new ArrayList<>();

            while (currentToken.getTokenType() != TokenType.EOF) {
                entries.add(nextInjectionEntry());
            }

            stringBuffer.delete(0, stringBuffer.length());

            return entries;
        }

    }

}
