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
package com.ibm.liberty.starter.unit;

import org.junit.rules.ExternalResource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;
import java.util.Hashtable;

public class SetupInitialConext extends ExternalResource {

    @Override
    protected void before() throws Throwable {
        if (NamingManager.hasInitialContextFactoryBuilder()) {
            return;
        }
        NamingManager.setInitialContextFactoryBuilder(new InitialContextFactoryBuilder() {
            @Override
            public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment) throws NamingException {
                return new InitialContextFactory() {
                    @Override
                    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
                        return new InitialContext(environment) {
                            @Override
                            public Object lookup(String name) throws NamingException {
                                if ("serverOutputDir".equals(name)) {
                                    return "foo";
                                } else {
                                    return super.lookup(name);
                                }
                            }
                        };
                    }
                };
            }
        });
    };
}
