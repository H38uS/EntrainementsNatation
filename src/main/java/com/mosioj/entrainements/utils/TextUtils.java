package com.mosioj.entrainements.utils;

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
}
