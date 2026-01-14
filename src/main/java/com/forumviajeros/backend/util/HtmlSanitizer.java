package com.forumviajeros.backend.util;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * Secure HTML sanitization using OWASP Java HTML Sanitizer
 *
 * This replaces the previous regex-based implementation which was vulnerable
 * to bypass attacks. OWASP HTML Sanitizer uses a whitelist-based approach
 * which is more secure.
 *
 * @see <a href="https://owasp.org/www-project-java-html-sanitizer/">OWASP Java HTML Sanitizer</a>
 */
public class HtmlSanitizer {

    // Policy that allows common formatting tags (rich text)
    private static final PolicyFactory RICH_TEXT_POLICY = new HtmlPolicyBuilder()
            .allowElements("p", "br", "b", "i", "u", "strong", "em", "ul", "ol", "li", "blockquote")
            .allowElements("h1", "h2", "h3", "h4", "h5", "h6")
            .allowElements("a")
            .allowAttributes("href").onElements("a")
            .allowStandardUrlProtocols()
            .requireRelNofollowOnLinks()
            .toFactory();

    // Combines multiple standard policies for safer rich text
    private static final PolicyFactory SAFE_FORMATTING_POLICY = Sanitizers.FORMATTING
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.LINKS);

    private HtmlSanitizer() {
        // Utility class - prevent instantiation
    }

    /**
     * Strips all HTML tags from input, leaving only plain text.
     * This is the most restrictive sanitization.
     *
     * @param input the HTML string to sanitize
     * @return plain text with all HTML removed, or null if input is null
     */
    public static String stripAllTags(String input) {
        if (input == null) {
            return null;
        }

        // Use empty policy factory to strip all HTML
        PolicyFactory noHtmlPolicy = new HtmlPolicyBuilder().toFactory();
        return noHtmlPolicy.sanitize(input);
    }

    /**
     * Sanitizes rich text content, allowing safe formatting tags only.
     * Removes all potentially dangerous tags (script, style, iframe, etc.)
     * and attributes (onclick, onerror, etc.).
     *
     * Allowed tags: p, br, b, i, u, strong, em, ul, ol, li, h1-h6, a, blockquote
     *
     * @param input the HTML string to sanitize
     * @return sanitized HTML with only safe formatting tags, or null if input is null
     */
    public static String sanitizeRichText(String input) {
        if (input == null) {
            return null;
        }

        // Use the safe formatting policy
        return SAFE_FORMATTING_POLICY.sanitize(input);
    }

    /**
     * Sanitizes rich text with custom policy allowing more tags.
     * Use this for user-generated content that needs formatting.
     *
     * @param input the HTML string to sanitize
     * @return sanitized HTML, or null if input is null
     */
    public static String sanitizeCustomRichText(String input) {
        if (input == null) {
            return null;
        }

        return RICH_TEXT_POLICY.sanitize(input);
    }
}