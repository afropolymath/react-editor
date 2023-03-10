package com.afropolymath.react.editor.bridge;

import javafx.event.Event;
import javafx.event.EventType;

public class EditorChangedEvent extends Event {
    public static final EventType<EditorChangedEvent> EDITOR_CONTENT_CHANGED = new EventType<>(
            "EDITOR_CONTENT_CHANGED");

    private String content;
    private String path;

    public String getPath() {
        return path;
    }

    public String getContent() {
        return content;
    }

    public EditorChangedEvent(EventType<? extends Event> eventType, String updatedPath, String updatedContent) {
        super(eventType);
        this.path = updatedPath;
        this.content = updatedContent;
    }
}
