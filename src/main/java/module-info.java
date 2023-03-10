module com.afropolymath.react.editor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires io.javalin;
    requires jdk.jsobject;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.commons.io;
    requires org.apache.commons.text;
    requires org.apache.commons.codec;
    requires org.slf4j;

    opens com.afropolymath.react.editor to javafx.fxml;
    opens com.afropolymath.react.editor.bridge to javafx.web;

    exports com.afropolymath.react.editor;
}
