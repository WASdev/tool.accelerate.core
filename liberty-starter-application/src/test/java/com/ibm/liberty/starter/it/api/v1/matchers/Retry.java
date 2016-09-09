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
