package com.mosioj.entrainements.utils;

import com.vdurmont.emoji.EmojiParser;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class TextUtils {

    /** Markdown processor */
    private static final Parser PARSER = Parser.builder().build();

    /** Markdown renderer */
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().escapeHtml(true).sanitizeUrls(true).build();

    /**
     * Utils class.
     */
    private TextUtils() {
        // Nothing to do
    }

    /**
     * @param source The source string to interpret
     * @return The HTML interpreted mark down text corresponding to the source
     */
    public static String interpreteMarkDown(String source) {
        return RENDERER.render(PARSER.parse(source));
    }


    /**
     *
     * @param initialText The initial text containing smileys.
     * @return The string with all smileys transformed to codes.
     */
    public static String transformSmileyToCode(final String initialText) {
        return EmojiParser.parseToAliases(initialText);
    }

    /**
     *
     * @param initialText The initial text containing codes.
     * @return The string with all codes transformed to smileys.
     */
    public static String transformCodeToSmiley(final String initialText) {
        return EmojiParser.parseToUnicode(initialText);
    }
}
