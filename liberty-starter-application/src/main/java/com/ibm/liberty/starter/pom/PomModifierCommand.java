package com.ibm.liberty.starter.pom;

import org.w3c.dom.Document;

public interface PomModifierCommand {

    public void modifyPom(Document pom);
    
}
