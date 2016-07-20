package com.ibm.liberty.starter.it.api.v1.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Matcher that retries a delegate matcher every 10 seconds for 3 minutes until it matches.
 */
public class Retry<T> extends BaseMatcher<T> {
    
    private Matcher<T> matcher;

    public Retry(Matcher<T> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(Object item) {
        int attempts = 0;
        boolean matched = matcher.matches(item);
        while (!matched && attempts < 24) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                return matched;
            }
            matched = matcher.matches(item);
            attempts++;
        }
        return matched;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("after waiting up to 3 minutes ");
        description.appendDescriptionOf(matcher);
    }
    
    @Override
    public void describeMismatch(Object item, Description description) {
        matcher.describeMismatch(item, description);
    }
    
    public static <E> Matcher<E> eventually(Matcher<E> matcher) {
        return new Retry<E>(matcher);
    }
}