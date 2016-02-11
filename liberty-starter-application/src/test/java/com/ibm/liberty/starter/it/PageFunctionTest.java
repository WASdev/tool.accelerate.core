/*******************************************************************************
 * Copyright (c) 2016 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.ibm.liberty.starter.it;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class PageFunctionTest {
    
    @Test
    public void headingTest() {
        final WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        HtmlPage page;
        String port = System.getProperty("liberty.test.port");
        try {
            page = webClient.getPage("http://localhost:" + port + "/start/");
            String title = page.getTitleText();
            assertTrue("Title is " + title, title.equals("Liberty Groove"));
        } catch (Exception e){
            org.junit.Assert.fail("Caught exception: " + e);
        } finally {
            webClient.close();
        }
    }
    
    @Ignore
    @Test
    // TODO: This method of testing does not work for angular, need to find an alternative method of testing
    public void techFormTest() {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        HtmlPage page;
        String port = System.getProperty("liberty.test.port");
        try {
            page = webClient.getPage("http://localhost:" + port + "/start/");
            DomElement techForm = page.getElementById("techTable");
            DomElement formBody = techForm.getFirstElementChild();
            int count = formBody.getChildElementCount();
            // We expect there to be more than one child element, otherwise the 
            // javascript has not created the tech table properly.
            assertTrue("Expected more than one element in the tech table, instead found " + count, count > 1);
        } catch (Exception e){
            org.junit.Assert.fail("Caught exception: " + e.getCause().toString());
        } finally {
            webClient.close();
        }
    }

}
