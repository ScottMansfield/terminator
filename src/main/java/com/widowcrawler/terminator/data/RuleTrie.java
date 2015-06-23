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
package com.widowcrawler.terminator.data;

import com.widowcrawler.terminator.model.Rule;
import com.widowcrawler.terminator.model.RuleType;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Scott Mansfield
 */
public class RuleTrie {

    private static final Character NULL = '\0';

    public static RuleTrie build(Set<Rule> rules) {
        TrieNode root = new CoreNode();

        rules.forEach(rule -> insert(root, rule));

        return new RuleTrie(root);
    }

    private static void insert(TrieNode node, Rule rule) {
        Rule standardizedRule = ensureRuleHasLeadingSlash(rule);

        insertRec(node, standardizedRule, 0);
    }

    private static void insertRec(TrieNode node, Rule rule, int matchIndex) {
        Character current;

        if (matchIndex >= rule.getPathMatch().length()) {
            current = NULL;
        } else {
            current = rule.getPathMatch().charAt(matchIndex);
        }

        if (node instanceof CoreNode) {
            CoreNode coreNode = (CoreNode) node;

            if (coreNode.hasChild(current)) {
                insertRec(coreNode.getChild(current), rule, matchIndex + 1);
            } else {
                addChild(coreNode, rule, current, matchIndex);
            }
        } else { // node instanceof LeafNode
            LeafNode leafNode = (LeafNode) node;

            splitNode(leafNode, rule);
        }
    }

    private static void addChild(CoreNode coreNode, Rule rule, Character current, int matchIndex) {
        LeafNode leafNode = new LeafNode(rule, matchIndex);

        coreNode.addChild(current, leafNode);
    }

    private static void splitNode(LeafNode leafNode, Rule rule) {
        String prefix = StringUtils.getCommonPrefix(leafNode.getRule().getPathMatch(), rule.getPathMatch());

        int matchIndex = prefix.length() - 1;

        CoreNode coreNode = new CoreNode();

        // Put original node as child of new node
        coreNode.addChild(leafNode.getRule().getPathMatch().charAt(matchIndex), leafNode);
        coreNode.addChild(rule.getPathMatch().charAt(matchIndex), new LeafNode(rule, matchIndex));
    }

    private static Rule ensureRuleHasLeadingSlash(Rule rule) {
        return new Rule(rule.getRuleType(), StringUtils.prependIfMissing(rule.getPathMatch(), "/"));
    }

    private static abstract class TrieNode { }

    private static class LeafNode extends TrieNode {
        private Rule rule;
        private int matchIndex;

        public LeafNode(Rule rule, int matchIndex) {
            this.rule = rule;
            this.matchIndex = matchIndex;
        }

        public Rule getRule() {
            return rule;
        }

        public int getMatchIndex() {
            return matchIndex;
        }
    }

    private static class CoreNode extends TrieNode {
        private Map<Character, TrieNode> children;

        public CoreNode() {
            this.children = new HashMap<>(40);
        }

        public boolean hasChild(Character character) {
            return children.containsKey(character);
        }

        public TrieNode getChild(Character character) {
            return children.get(character);
        }

        public void addChild(Character character, TrieNode trieNode) {
            if (children.containsKey(character)) {
                throw new IllegalArgumentException("Child for character " + character + " already exists");
            }

            children.put(character, trieNode);
        }

        public void replaceChild(Character character, TrieNode trieNode) {
            children.put(character, trieNode);
        }
    }

    private TrieNode root;

    private RuleTrie(TrieNode root) {
        this.root = root;
    }

    public TrieNode getRoot() {
        return root;
    }
}
