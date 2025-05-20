package com.forumviajeros.backend.util;

public class HtmlSanitizer {
    private HtmlSanitizer() {
    }

    public static String stripAllTags(String input) {
        if (input == null)
            return null;
        return input.replaceAll("<[^>]*>", "");
    }

    public static String sanitizeRichText(String input) {
        if (input == null)
            return null;
        return input.replaceAll("(?i)<script.*?>.*?</script>", "")
                .replaceAll("(?i)<style.*?>.*?</style>", "")
                .replaceAll("<[^>]*>", "");
    }
}