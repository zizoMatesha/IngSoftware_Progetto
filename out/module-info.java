module IngSoftware {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.media;
    requires javafx.controls;
    requires javafx.web;
    requires java.sql;
    requires java.sql.rowset;
    requires java.xml;
    requires org.mariadb.jdbc;
    requires java.instrument;
    requires kernel;
    requires layout;
    requires io;
    requires forms;
    requires junit;
    requires jdk.jsobject;
    requires slf4j.api;
    requires d;


    opens desClass;
    opens srcClass;
}