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

import com.widowcrawler.terminator.model.RobotsTxt;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Scott Mansfield
 */
public class ParserTest {

    @Test(timeout = 1000)
    public void parse_validRobotsTxtSmall_parsesFile() throws Exception {
        InputStream inputStream = new FileInputStream("src/test/resources/example_robots_small.txt");

        Parser parser = new Parser(inputStream);

        long start = System.nanoTime();
        RobotsTxt robotsTxt = parser.parse();
        long duration = System.nanoTime() - start;

        System.out.println("Duration: " + duration);

        robotsTxt.getRuleSets().forEach( (userAgent, rules) -> {
                    System.out.println("User-agent: " + userAgent);

                    rules.forEach(rule -> System.out.println(rule.getRuleType().toString() + ": " + rule.getPathMatch()));
                }
        );

        System.out.println("\n\n");

        robotsTxt.getSiteMapRefs().forEach(ref -> System.out.println("Sitemap: " + ref));
    }

    @Test(timeout = 1000)
    public void parse_validRobotsTxtLarge_parsesFile() throws Exception {
        InputStream inputStream = new FileInputStream("src/test/resources/example_robots.txt");

        Parser parser = new Parser(inputStream);

        long start = System.nanoTime();
        parser.parse();
        long duration = System.nanoTime() - start;

        System.out.println("Duration: " + duration);
    }

    @Test(timeout = 10000)
    public void parse_validRobotsTxtLarge1000Times_parsesFile() throws Exception {
        long totalDuration = 0L;

        InputStream inputStream = new FileInputStream("src/test/resources/example_robots.txt");
        String data = IOUtils.toString(inputStream);

        for (int i = 0; i < 1000; i++) {

            Parser parser = new Parser(data);

            long start = System.nanoTime();
            parser.parse();
            totalDuration += System.nanoTime() - start;
        }

        System.out.println("Total Duration: " + totalDuration);
        System.out.println("Average Duration: " + (totalDuration / 1000D));
    }
}
