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
import sun.text.normalizer.Trie;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Scott Mansfield
 */

public class TrieNode {
    private TrieNode parent;
    private int diffIndex;
    private String prefix;
    private Rule rule;
    private Map<Character, TrieNode> children;

    public TrieNode(TrieNode parent, int diffIndex, String prefix, Rule rule) {
        this.parent = parent;
        this.diffIndex = diffIndex;
        this.prefix = prefix;
        this.rule = rule;

        this.children = new HashMap<>();
    }

    public TrieNode getParent() {
        return parent;
    }

    public int getDiffIndex() {
        return diffIndex;
    }

    public Rule getRule() {
        return rule;
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

    public TrieNode removeChild(Character character) {
        if (!children.containsKey(character)) {
            throw new IllegalArgumentException("Child for character " + character + " does not exist");
        }

        return children.remove(character);
    }

    public void replaceChild(Character character, TrieNode trieNode) {
        children.put(character, trieNode);
    }

    public String getPrefix() {
        return prefix;
    }
}

