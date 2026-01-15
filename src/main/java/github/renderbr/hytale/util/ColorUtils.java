package github.renderbr.hytale.util;

import com.hypixel.hytale.server.core.Message;

import java.awt.*;

public class ColorUtils {
    public static final Color FAKE_COLOR_BOLD = new Color(0x666666);


    public static Color getColorFromChar(char c){
        return switch (c) {
            case '0' -> new Color(0x000000); // Black
            case '1' -> new Color(0x0000AA); // Dark Blue
            case '2' -> new Color(0x00AA00); // Dark Green
            case '3' -> new Color(0x00AAAA); // Dark Aqua
            case '4' -> new Color(0xAA0000); // Dark Red
            case '5' -> new Color(0xAA00AA); // Dark Purple
            case '6' -> new Color(0xFFAA00); // Gold
            case '7' -> new Color(0xAAAAAA); // Gray
            case '8' -> new Color(0x555555); // Dark Gray
            case '9' -> new Color(0x5555FF); // Blue
            case 'a' -> new Color(0x55FF55); // Green
            case 'b' -> new Color(0x55FFFF); // Aqua
            case 'c' -> new Color(0xFF5555); // Red
            case 'd' -> new Color(0xFF55FF); // Light Purple
            case 'e' -> new Color(0xFFFF55); // Yellow
            case 'f' -> new Color(0xFFFFFF); // White
            case 'l' -> FAKE_COLOR_BOLD;
            default -> null;
        };
    }

    public static Message parseColorCodes(String text) {
        // If there's no '&' in the string, just return a raw message
        if (!text.contains("&")) {
            return Message.raw(text);
        }

        // Split by the & symbol
        String[] parts = text.split("&");
        Message root = Message.empty();

        for (String part : parts) {
            if (part.isEmpty()) continue;

            char colorChar = part.charAt(0);
            String content = part.substring(1);
            Color color = getColorFromChar(colorChar);

            if (color != null) {
                if(color.equals(FAKE_COLOR_BOLD)){
                    root = Message.join(root, Message.raw(content).bold(true));
                    continue;
                }

                root = Message.join(root, Message.raw(content).color(color));
            } else {
                // If the code wasn't recognized, just put it back as text
                root = Message.join(root, Message.raw("&" + part));
            }
        }
        return root;
    }

}
