package me.sakigamiyang.aquarius.common.io;

public class FileUtils {
    public enum NewLines {
        SYSTEM_DEPENDENT(System.getProperty("line.separator")),
        WINDOWS("\r\n"),
        UNIX_AND_MACOS("\n"),
        CLASSIC_MACOS("\r");

        private final String content;

        NewLines(final String content) {
            this.content = content;
        }

        public String getContent() {
            return this.content;
        }
    }
}
