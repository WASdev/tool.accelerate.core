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
package com.ibm.liberty.starter.it.api.v1.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Matcher that retries a delegate matcher every 10 seconds for 3 minutes until it matches.
 */
public class Retry<T> extends BaseMatcher<T> {
    
    private Matcher<T> matcher;
    private Matcher<T> neverMatches;

    public Retry(Matcher<T> matcher) {
        this.matcher = matcher;
    }
    
    public Retry<T> butNot(Matcher<T> matcher) {
        neverMatches = matcher;
        return this;
    }
    
    @Override
    public boolean matches(Object item) {
        int attempts = 0;
        boolean errorMatched = false;
        boolean matched = matcher.matches(item);
        while (!matched && !errorMatched && attempts < 24) {
            errorMatched = errorMatches(item);
            if (errorMatched) {
                return false;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                return matched;
            }
            matched = matcher.matches(item);
            errorMatched = errorMatches(item);
            attempts++;
        }
        return matched && !errorMatched;
    }
    
    private boolean errorMatches(Object item) {
        return (neverMatches == null) ? false : neverMatches.matches(item);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("after waiting up to 3 minutes ");
        description.appendDescriptionOf(matcher);
        if (neverMatches != null) {
            description.appendText(" and not ");
            description.appendDescriptionOf(neverMatches);
        }
    }
    
    @Override
    public void describeMismatch(Object item, Description description) {
        if (errorMatches(item)) {
            description.appendText("did match ");
            description.appendDescriptionOf(neverMatches);
            neverMatches.describeMismatch(item, description);
        } else {
            matcher.describeMismatch(item, description);
        }
    }
    
    public static <E> Retry<E> eventually(Matcher<E> matcher) {
        return new Retry<E>(matcher);
    }
}
