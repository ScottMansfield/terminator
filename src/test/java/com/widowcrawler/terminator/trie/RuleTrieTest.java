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
package com.widowcrawler.terminator.trie;

import com.widowcrawler.terminator.model.Rule;
import com.widowcrawler.terminator.model.RuleType;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Scott Mansfield
 */
public class RuleTrieTest {

    @Test
    public void build_noRegexRules_evaluatesCorrectly() {
        // Arrange
        Set<Rule> rules = new HashSet<>(5);
        rules.add(new Rule(RuleType.DISALLOW, "/"));
        rules.add(new Rule(RuleType.ALLOW, "/foo"));
        rules.add(new Rule(RuleType.DISALLOW, "/foo/bar"));
        rules.add(new Rule(RuleType.ALLOW, "/fop"));
        rules.add(new Rule(RuleType.ALLOW, "/baz/biz"));

        // Act
        RuleTrie ruleTrie = RuleTrie.build(rules);

        // Assert
        assertEquals(ruleTrie.getRoot().getClass(), TrieNode.class);
    }
}
