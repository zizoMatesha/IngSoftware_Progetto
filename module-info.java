module IngSoftware {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.media;
    requires javafx.web;
    requires javafx.controls;
    requires java.sql;
    requires java.sql.rowset;
    requires java.xml;
    requires org.mariadb.jdbc;
    requires java.instrument;
    requires kernel;
    requires layout;
    requires io;
    requires forms;
    //requires slf4j.log4j12;
    requires slf4j.api;
    requires jdk.jsobject;
    requires log4j;
    requires junit;


    opens Controller;
    opens srcClass;
    //opens srcClass to javafx.base;
}