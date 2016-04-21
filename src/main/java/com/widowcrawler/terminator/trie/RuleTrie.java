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
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * @author Scott Mansfield
 */
public class RuleTrie {

    private static final Character NULL = '\0';

    public static RuleTrie build(Set<Rule> rules) {
        TrieNode root = new TrieNode(null, 0, "", null);

        rules.forEach(rule -> insert(root, rule));

        return new RuleTrie(root);
    }

    private static void insert(TrieNode node, Rule rule) {
        Rule standardizedRule = ensureRuleHasLeadingSlash(rule);

        insertRec(node, standardizedRule, 0);
    }

    private static void insertRec(TrieNode node, Rule rule, int curIndex) {
        // TODO: Make sure this is the right character. Am I at the previous spot or the current spot?
        Character current = rule.getPathMatch().charAt(curIndex);

        // case 1: No rule or child for the current character
        //   - add rule at current character spot in current node
        // case 2: Child for current character
        //   - recurse to child
        // case 3: Rule for current character
        //   - pull rule out, create a new child node, and re-insert
        //   - make sure to check for equality
        if (!node.hasRule(current) && !node.hasChild(current)) {
            // insert node
            node.addRule(current, rule);
        } else if (node.hasChild(current)) {
            // recurse
            // get common substring
            insertRec(node.getChild(current, rule, ))
        } else if (node.hasRule(current)) {
            // pull rule out, create new child node and re-insert
            Rule existing = node.removeRule(current);
            TrieNode newChild = new TrieNode(node, -1, "")
        } else {
            throw new IllegalStateException("Both a rule and child exist for the same character.");
        }



        // Branch node
        if (node.getRule() == null) {
            Character current = NULL;

            int diffIndex = Math.max(curIndex, node.getDiffIndex());

            // If current is equal to the current node's prefix
            if (diffIndex == rule.getPathMatch().length()) {
                current = NULL;

            // If current is a prefix of the current node's prefix
            } else if (diffIndex > rule.getPathMatch().length()) {
                splitCoreNodeAndInsert(node, rule);

            // If current is longer than the prefix
            } else {
                current = rule.getPathMatch().charAt(diffIndex);
            }

            if (node.hasChild(current)) {
                insertRec(node.getChild(current), rule, curIndex + 1);
            } else {
                addChild(node, rule, current, diffIndex);
            }

        // leaf node
        } else {
            splitLeafNode(node, rule);
        }
    }

    private static void addChild(TrieNode coreNode, Rule rule, Character current, int diffIndex) {
        TrieNode leafNode = new TrieNode(coreNode, diffIndex, rule.getPathMatch(), rule);

        coreNode.addChild(current, leafNode);
    }

    private static void splitCoreNodeAndInsert(TrieNode coreNode, Rule rule) {
        // split node, call back to insertRec on the correct child
        String prefix = StringUtils.getCommonPrefix(coreNode.getPrefix(), rule.getPathMatch());

        // TODO: If coreNode is the root, add new parent (?)

        TrieNode replacement = new TrieNode(coreNode.getParent(), prefix.length(), prefix, null);

        TrieNode parent = coreNode.getParent();
        String parentPrefix = parent.getPrefix();

        parent.removeChild(coreNode.getPrefix().charAt(parentPrefix.length()));
        parent.addChild(prefix.charAt(parentPrefix.length()), replacement);

        //
    }

    private static void splitLeafNode(TrieNode leafNode, Rule rule) {
        String leafPathMatch = leafNode.getRule().getPathMatch();

        // TODO: What if they are the same exact rule?
        // probably log info and drop
        // If they differ by ALLOW / DISALLOW, log warn and pick latest one
        String prefix = StringUtils.getCommonPrefix(leafPathMatch, rule.getPathMatch());

        int diffIndex = prefix.length();

        TrieNode coreNode = new TrieNode(leafNode.getParent(), diffIndex, prefix, null);
        Character leafChar = NULL;
        Character ruleChar;

        // Put original node as child of new node
        if (diffIndex < leafPathMatch.length()) {
            leafChar = leafPathMatch.charAt(diffIndex);
        }

        ruleChar = rule.getPathMatch().charAt(diffIndex);

        coreNode.addChild(leafChar, new TrieNode(coreNode, diffIndex, leafNode.getRule().getPathMatch(), leafNode.getRule()));
        coreNode.addChild(ruleChar, new TrieNode(coreNode, diffIndex, rule.getPathMatch(), rule));

        TrieNode parent = leafNode.getParent();
        parent.replaceChild(leafPathMatch.charAt(leafNode.getDiffIndex()), coreNode);
    }

    private static Rule ensureRuleHasLeadingSlash(Rule rule) {
        return new Rule(rule.getRuleType(), StringUtils.prependIfMissing(rule.getPathMatch(), "/"));
    }

    private TrieNode root;

    private RuleTrie(TrieNode root) {
        this.root = root;
    }

    public TrieNode getRoot() {
        return root;
    }
}
