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
