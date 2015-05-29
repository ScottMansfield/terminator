/**
 * Copyright 2015 Scott Mansfield
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.widowcrawler.terminator.parse;

import com.widowcrawler.terminator.ParseException;
import com.widowcrawler.terminator.model.RobotsTxt;
import com.widowcrawler.terminator.model.Rule;
import com.widowcrawler.terminator.model.RuleType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Scott Mansfield
 */
public class Parser {

    public static final String USER_AGENT = "User-Agent:";
    public static final String ALLOW = "Allow:";
    public static final String DISALLOW = "Disallow:";
    public static final String SITEMAP = "Sitemap:";

    private static final List<Character> whitespaceChars;
    private static final List<Character> endlineChars;

    static {
        whitespaceChars = Collections.unmodifiableList(Arrays.asList(' ', '\t'));
        endlineChars = Collections.unmodifiableList(Arrays.asList('\r', '\n'));
    }

    private String data;
    private int dataPtr;

    private Map<String, Set<Rule>> ruleSets;
    private Set<String> siteMapRefs;

    public Parser(String data) {
        this.data = data;
        this.dataPtr = 0;
        this.ruleSets = new HashMap<>();
        this.siteMapRefs = new HashSet<>();
    }

    public Parser(InputStream inputStream) throws IOException {
        this(readStreamAlwaysClose(inputStream));
    }

    // ouch, seems hacky but it works
    private static String readStreamAlwaysClose(InputStream inputStream) throws IOException {
        try {
            return IOUtils.toString(inputStream, "UTF-8");
        } finally {
            inputStream.close();
        }
    }

    public RobotsTxt parse() throws ParseException {
        robotsTxt();

        return new RobotsTxt(ruleSets, siteMapRefs);
    }

    //////////////////////////////////
    // Begin recursive descent parser
    //////////////////////////////////

    private void robotsTxt() throws ParseException {
        //System.out.println("robotsTxt()");
        while (!isEndOfFile()) {
            robotsTxtPart();
        }
    }

    private void robotsTxtPart() throws ParseException {
        //System.out.println("robotsTxtPart()");
        whitespace();

        if (isEndline()) {
            endline();
        } else if (isCommentStart()) {
            commentLine();
        } else if (isAgentSpecStart()) {
            agentSpec();
        } else if (isSitemapRefStart()) {
            sitemapRef();
        } else {
            throw new ParseException(dataPtr, "Invalid line");
        }
    }

    private void whitespace() {
        //System.out.println("whitespace()");
        while (!isEndOfFile() && isWhitespace()) {
            next();
        }
    }

    private void commentLine() throws ParseException {
        //System.out.println("commentLine()");
        while (!isEndOfFile() && !isEndline()) {
            next();
        }

        endline();
    }

    private void endline() throws ParseException {
        //System.out.println("endline()");
        if (isEndOfFile()) return;

        if (current() == '\n') {
            next();
        } else if (current() == '\r') {
            next();
            if (current() == '\n') {
                next();
            }
        } else {
            throw new ParseException(dataPtr, "Unexpected character: " + current());
        }
    }

    private void agentSpec() throws ParseException {
        //System.out.println("agentSpec()");
        String userAgent = userAgent();
        Set<Rule> ruleSet = new HashSet<>();

        whitespace();

        while (isRuleLineStart()) {
            Rule rule = ruleLine();

            if (rule != null) {
                ruleSet.add(rule);
                //System.out.println(rule.getRuleType());
                //System.out.println(rule.getPathMatch());
            }

            whitespace();
        }

        ruleSets.put(userAgent, ruleSet);
    }

    private String userAgent() throws ParseException {
        //System.out.println("userAgent()");
        skip(USER_AGENT.length());
        whitespace();

        String userAgent = userAgentIdentifier();

        if (isCommentStart()) {
            commentLine();
        } else if (isEndline()) {
            endline();
        } else {
            throw new ParseException(dataPtr, "Expected a User-agent line.");
        }

        return userAgent;
    }

    private String userAgentIdentifier() {
        //System.out.println("userAgentIdentifier()");
        int start = dataPtr;

        while (!isEndOfFile() && !isCommentStart() && !isEndline()) {
            next();
        }

        return StringUtils.trimToEmpty(data.substring(start, dataPtr));
    }

    private Rule ruleLine() throws ParseException {
        //System.out.println("ruleLine()");
        if (isEndline()) {
            endline();
        } else if (isCommentStart()) {
            commentLine();
        } else if (isRuleStart()) {
            return rule();
        } else {
            throw new ParseException(dataPtr, "Expected either 'Allow:' or 'Disallow:' to start a rule");
        }

        // blank lines return null
        return null;
    }

    private Rule rule() throws ParseException {
        //System.out.println("rule()");
        Rule rule;

        if (isAllowRule()) {
            rule = allowRule();
        } else if (isDisallowRule()) {
            rule = disallowRule();
        } else {
            throw new ParseException(dataPtr, "Expected either 'Allow:' or 'Disallow:' to start a rule");
        }

        if (isEndline()) {
            endline();
        } else if (isCommentStart()) {
            commentLine();
        } else {
            throw new ParseException(dataPtr, "Unexpected text after rule path");
        }

        return rule;
    }

    private Rule allowRule() {
        //System.out.println("allowRule()");
        skip(ALLOW.length());
        whitespace();
        String path = rulePath();

        return new Rule(RuleType.ALLOW, path);
    }

    private Rule disallowRule() {
        //System.out.println("disallowRule()");
        skip(DISALLOW.length());
        whitespace();
        String path = rulePath();

        return new Rule(RuleType.DISALLOW, path);
    }

    private String rulePath() {
        //System.out.println("rulePath()");
        int start = dataPtr;

        while (!isEndOfFile() && !isCommentStart() && !isEndline() && !isWhitespace()) {
            next();
        }

        String path = StringUtils.trimToEmpty(data.substring(start, dataPtr));

        whitespace();

        return path;
    }

    private void sitemapRef() throws ParseException {
        //System.out.println("sitemapRef");
        skip(SITEMAP.length());
        whitespace();

        String url = sitemapRefIdentifier();
        siteMapRefs.add(url);

        endline();
    }

    private String sitemapRefIdentifier() {
        //System.out.println("sitemapRefIdentifier()");
        int start = dataPtr;

        while (!isEndOfFile() && !isCommentStart() && !isEndline() && !isWhitespace()) {
            next();
        }

        String url = StringUtils.trimToEmpty(data.substring(start, dataPtr));

        whitespace();

        return url;
    }

    //////////////////////////
    // Begin helper functions
    //////////////////////////

    private Character current() {
        return data.charAt(dataPtr);
    }

    private void next() {
        dataPtr++;
    }

    private void skip(int numChars) {
        for (int i = 0; i < numChars; i++) {
            next();
        }
    }

    private boolean isWhitespace() {
        return !isEndOfFile() && whitespaceChars.contains(current());
    }

    private boolean isCommentStart() {
        return !isEndOfFile() && current() == '#';
    }

    private boolean isEndline() {
        return !isEndOfFile() && endlineChars.contains(current());
    }

    private boolean isAgentSpecStart() {
        return !isEndOfFile() && matchStringStart(USER_AGENT);
    }

    private boolean isRuleLineStart() {
        return isRuleStart() || isCommentStart() || isEndline();
    }

    private boolean isRuleStart() {
        return isAllowRule() || isDisallowRule();
    }

    private boolean isAllowRule() {
        return !isEndOfFile() && matchStringStart(ALLOW);
    }

    private boolean isDisallowRule() {
        return !isEndOfFile() && matchStringStart(DISALLOW);
    }

    private boolean isEndOfFile() {
        return dataPtr >= data.length();
    }

    private boolean isSitemapRefStart() {
        return !isEndOfFile() && matchStringStart(SITEMAP);
    }

    private boolean matchStringStart(String toMatch) {
        // be permissive with casing
        return StringUtils.equalsIgnoreCase(
                data.substring(dataPtr, dataPtr + toMatch.length()),
                toMatch
        );
    }
}
