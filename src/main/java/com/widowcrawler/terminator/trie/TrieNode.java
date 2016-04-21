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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Scott Mansfield
 */

public class TrieNode {
    private TrieNode parent;
    private int diffIndex;
    private String prefix;
    private Map<Character, TrieNode> children;
    private Map<Character, Rule> rules;

    public TrieNode(TrieNode parent, int diffIndex, String prefix) {
        this.parent = parent;
        this.diffIndex = diffIndex;
        this.prefix = prefix;

        this.children = new HashMap<>();
        this.rules = new HashMap<>();
    }

    public TrieNode getParent() {
        return parent;
    }

    public int getDiffIndex() {
        return diffIndex;
    }

    public String getPrefix() {
        return prefix;
    }

    // Rule methods
    public boolean hasRule(Character character) {
        return rules.containsKey(character);
    }

    public Rule getRule(Character character) {
        return rules.get(character);
    }

    public void addRule(Character character, Rule rule) {
        assert !rules.containsKey(character);
        assert !children.containsKey(character);
        rules.put(character, rule);
    }

    public Rule removeRule(Character character) {
        assert rules.containsKey(character);
        return rules.remove(character);
    }

    // Child methods
    public boolean hasChild(Character character) {
        return children.containsKey(character);
    }

    public TrieNode getChild(Character character) {
        return children.get(character);
    }

    public void addChild(Character character, TrieNode trieNode) {
        assert !children.containsKey(character);
        assert !rules.containsKey(character);
        children.put(character, trieNode);
    }

    public TrieNode removeChild(Character character) {
        assert children.containsKey(character);
        return children.remove(character);
    }

    public void replaceChild(Character character, TrieNode trieNode) {
        children.put(character, trieNode);
    }
}

