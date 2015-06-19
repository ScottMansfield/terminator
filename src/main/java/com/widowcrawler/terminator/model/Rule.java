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
package com.widowcrawler.terminator.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Scott Mansfield
 */
public class Rule {
    private RuleType ruleType;
    private String pathMatch;

    public Rule (RuleType ruleType, String pathMatch) {
        this.ruleType = ruleType;
        this.pathMatch = pathMatch;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public String getPathMatch() {
        return pathMatch;
    }

    @Override
    public int hashCode() {
        int hash = 1;

        if (ruleType != null) {
            hash += 37 * ruleType.hashCode();
        }

        if (getPathMatch() != null) {
            hash += 11 * pathMatch.hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Rule)) {
            return false;
        }

        Rule other = (Rule) obj;

        return this.ruleType == other.getRuleType() &&
                StringUtils.equals(this.getPathMatch(), other.getPathMatch());
    }
}
