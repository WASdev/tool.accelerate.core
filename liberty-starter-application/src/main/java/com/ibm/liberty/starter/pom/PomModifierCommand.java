package com.ibm.liberty.starter.pom;

import java.io.IOException;

import org.w3c.dom.Document;

public interface PomModifierCommand {

    public void modifyPom(Document pom) throws IOException;

}
